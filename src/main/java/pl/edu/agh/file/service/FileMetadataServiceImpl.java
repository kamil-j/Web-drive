package pl.edu.agh.file.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.edu.agh.drive.exception.AmazonServiceException;
import pl.edu.agh.drive.service.DriveService;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.domain.LinkTime;
import pl.edu.agh.file.repository.FileMetadataRepository;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.util.CloudIdGenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Kamil Jureczka on 2017-07-20.
 */

@Slf4j
@Service
public class FileMetadataServiceImpl implements FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;
    private final DriveService driveService;
    private final CloudIdGenerator cloudIdGenerator;

    @Autowired
    public FileMetadataServiceImpl(FileMetadataRepository fileMetadataRepository, CloudIdGenerator cloudIdGenerator,
                                   DriveService driveService) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.cloudIdGenerator = cloudIdGenerator;
        this.driveService = driveService;
    }

    @Override
    public FileMetadata saveNewFile(User user, MultipartFile file, String description) {
        String cloudName = cloudIdGenerator.generateCloudObjectName(user);
        FileMetadata fileMetadata = new FileMetadata(user, file, cloudName, description);

        log.debug("FileMetadataServiceImpl: Saving new file - User id: " + user.getId() + " - cloud name: " + cloudName);
        return fileMetadataRepository.save(fileMetadata);
    }

    @Override
    public FileMetadata getFile(User user, String cloudFileName) throws FileNotFoundException {
        log.debug("FileMetadataServiceImpl: Getting file - User id: " + user.getId() + " - cloud name: " + cloudFileName);
        List<FileMetadata> metadataObjects = fileMetadataRepository.findByUserAndCloudName(user, cloudFileName);
        if(metadataObjects.isEmpty()) {
            log.debug("FileMetadataServiceImpl: File not found! User id: " + user.getId() + " - cloud name: " + cloudFileName);
            throw new FileNotFoundException();
        }

        return metadataObjects.get(0);
    }

    @Override
    public FileMetadata getFileFromLink(String cloudFileName) throws FileNotFoundException {
        log.debug("FileMetadataServiceImpl: Getting file from link - cloud name: " + cloudFileName);
        FileMetadata file = fileMetadataRepository.findOneByCloudName(cloudFileName)
                .orElseThrow(FileNotFoundException::new);

        if(file.getLinkExpiredDate().isBefore(LocalDateTime.now())) {
            throw new FileNotFoundException();
        }

        return file;
    }

    @Override
    public String generateLinkToFile(User user, String cloudFileName, LinkTime linkTime) throws FileNotFoundException {
        log.debug("FileMetadataServiceImpl: Generating link to file - User id: " + user.getId()
                + " - Cloud name: " + cloudFileName);

        FileMetadata file = getFile(user, cloudFileName);
        file.setLinkExpiredDate(LocalDateTime.now().plusHours(linkTime.getAmountOfHours()));
        fileMetadataRepository.save(file);

        return getDownloadString(cloudFileName);
    }

    private String getDownloadString(String cloudFileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()
                + "/download/page/" + cloudFileName;
    }

    @Override
    public void deleteFile(User user, String cloudFileName) throws FileNotFoundException {
        log.debug("FileMetadataServiceImpl: Deleting file - User id: " + user.getId()
                + " - Cloud name: " + cloudFileName);

        FileMetadata file = getFile(user, cloudFileName);
        try {
            driveService.deleteFile(cloudFileName);
        } catch (IOException e) {
            log.error("Error while deleting file from drive: " + e.getMessage());
            throw new AmazonServiceException("Error while deleting file from drive", e);
        }

        fileMetadataRepository.delete(file);
    }

    @Override
    public void deleteAllUserFiles(User user) {
        log.debug("FileMetadataServiceImpl: Deleting all user files - User id: " + user.getId());

        List<FileMetadata> userFiles = fileMetadataRepository.findByUser(user);
        userFiles.forEach(fileMetadata -> {
            try {
                driveService.deleteFile(fileMetadata.getCloudName());
            } catch (IOException e) {
                log.error("Error while deleting file from drive: " + e.getMessage());
                throw new AmazonServiceException("Error while deleting file from drive", e);
            }
            fileMetadataRepository.delete(fileMetadata.getId());
        });
    }
}
