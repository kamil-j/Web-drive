package pl.edu.agh.share.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.service.FileMetadataService;
import pl.edu.agh.share.domain.ShareTime;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.share.repository.SharedFileMetadataRepository;
import pl.edu.agh.user.domain.User;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Kamil Jureczka on 2017-08-05.
 */

@Slf4j
@Service
public class SharedFileMetadataServiceImpl implements SharedFileMetadataService {

    private final SharedFileMetadataRepository sharedFileMetadataRepository;
    private final FileMetadataService fileMetadataService;

    @Autowired
    public SharedFileMetadataServiceImpl(SharedFileMetadataRepository sharedFileMetadataRepository,
                                         FileMetadataService fileMetadataService) {
        this.sharedFileMetadataRepository = sharedFileMetadataRepository;
        this.fileMetadataService = fileMetadataService;
    }

    @Override
    public List<SharedFileMetadata> getAllUserSharingFiles(User user) {
        return sharedFileMetadataRepository.findByUserFromAndIsCanceled(user, false);
    }

    @Override
    public Page<SharedFileMetadata> getAllUserSharingFiles(User user, Pageable pageable) {
        return sharedFileMetadataRepository.findAllByUserFromAndIsCanceledOrderByFile_NameAsc(user, false, pageable);
    }

    @Override
    public List<SharedFileMetadata> getAllSharedToUserFiles(User user) {
        return sharedFileMetadataRepository.findByUserToAndIsCanceled(user, false);
    }

    @Override
    public Page<SharedFileMetadata> getAllSharedToUserFiles(User user, Pageable pageable) {
        return sharedFileMetadataRepository.findAllByUserToAndIsCanceledOrderByFile_NameAsc(user, false, pageable);
    }

    @Override
    public List<SharedFileMetadata> getUserSharingFile(User user, String cloudFileName) {
        return sharedFileMetadataRepository.findByUserFromAndIsCanceledAndFile_CloudName(user, false, cloudFileName);
    }

    @Override
    public SharedFileMetadata getSharedToUserFile(User user, String cloudFileName) throws FileNotFoundException {
        return sharedFileMetadataRepository.findByUserToAndIsCanceledAndFile_CloudName(user, false, cloudFileName)
                .orElseThrow(() -> new FileNotFoundException(cloudFileName));
    }

    @Override
    public void cancelAllUserShare(User user) {
        List<SharedFileMetadata> userShare = sharedFileMetadataRepository.findByUserFromAndIsCanceled(user, false);

        userShare.forEach(sharedFileMetadata -> {
            sharedFileMetadata.setIsCanceled(true);
            sharedFileMetadataRepository.save(sharedFileMetadata);
        });

        List<SharedFileMetadata> shareToUser = sharedFileMetadataRepository.findByUserToAndIsCanceled(user, false);

        shareToUser.forEach(sharedFileMetadata -> {
            sharedFileMetadata.setIsCanceled(true);
            sharedFileMetadataRepository.save(sharedFileMetadata);
        });
    }

    @Override
    public void cancelUserSharingFile(User userFrom, User userTo, String cloudFileName) throws FileNotFoundException {
        SharedFileMetadata file = getSharedFromUserToSpecificUserFile(userFrom, userTo, cloudFileName)
                .orElseThrow(() -> new FileNotFoundException(cloudFileName));
        file.setIsCanceled(true);
        sharedFileMetadataRepository.save(file);
    }

    @Override
    public void cancelSharedToUserFile(User user, String cloudFileName) throws FileNotFoundException {
        SharedFileMetadata file = getSharedToUserFile(user, cloudFileName);
        file.setIsCanceled(true);
        sharedFileMetadataRepository.save(file);
    }

    @Override
    public SharedFileMetadata shareFile(User userFrom, User userTo, String cloudFileName, ShareTime shareTime)
            throws FileNotFoundException {
        Optional<SharedFileMetadata> file = getSharedFromUserToSpecificUserFile(userFrom, userTo, cloudFileName);
        if(file.isPresent()){
            changeShareFileTime(file.get(), shareTime);
            return file.get();
        }

        FileMetadata fileMetadata = fileMetadataService.getFile(userFrom, cloudFileName);

        LocalDateTime expirationTime = getExpirationDate(shareTime);
        SharedFileMetadata sharedFileMetadata = new SharedFileMetadata(userFrom, userTo, fileMetadata, expirationTime);

        return sharedFileMetadataRepository.save(sharedFileMetadata);
    };

    @Override
    public Optional<SharedFileMetadata> getSharedFromUserToSpecificUserFile(User userFrom, User userTo, String cloudFileName) {
        return  sharedFileMetadataRepository
                .findByUserFromAndUserToAndIsCanceledAndFile_CloudName(userFrom, userTo, false, cloudFileName);
    }

    @Override
    public SharedFileMetadata changeShareFileTime(SharedFileMetadata file, ShareTime shareTime) {
        LocalDateTime expirationTime = getExpirationDate(shareTime);
        file.setExpirationDate(expirationTime);

        return sharedFileMetadataRepository.save(file);
    }

    @Override
    public void cancelAllUserShareFile(User user, String cloudFileName) {
        List<SharedFileMetadata> files = sharedFileMetadataRepository.findByUserFromAndIsCanceledAndFile_CloudName(user, false, cloudFileName);
        files.forEach(file -> {
            file.setIsCanceled(true);
            sharedFileMetadataRepository.save(file);
        });
    }

    private LocalDateTime getExpirationDate(ShareTime shareTime){
        if(shareTime.getAmountOfHours() == -1) {
            return LocalDateTime.now().plusYears(5000);
        }

        return LocalDateTime.now().plusHours(shareTime.getAmountOfHours());
    }
}
