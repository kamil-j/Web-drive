package pl.edu.agh.share.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.agh.share.domain.ShareTime;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.user.domain.User;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Kamil Jureczka on 2017-08-05.
 */
public interface SharedFileMetadataService {
    List<SharedFileMetadata> getAllUserSharingFiles(User user);
    Page<SharedFileMetadata> getAllUserSharingFiles(User user, Pageable pageable);
    List<SharedFileMetadata> getAllSharedToUserFiles(User user);
    Page<SharedFileMetadata> getAllSharedToUserFiles(User user, Pageable pageable);
    void cancelUserSharingFile(User userFrom, User userTo, String cloudFileName) throws FileNotFoundException;
    void cancelSharedToUserFile(User user, String cloudFileName) throws FileNotFoundException;
    SharedFileMetadata shareFile(User userFrom, User userTo, String cloudFileName, ShareTime shareTime) throws FileNotFoundException;
    Optional<SharedFileMetadata> getSharedFromUserToSpecificUserFile(User userFrom, User userTo, String cloudFileName);
    SharedFileMetadata changeShareFileTime(SharedFileMetadata file, ShareTime shareTime);
    void cancelAllUserShareFile(User user, String cloudFileName);
    List<SharedFileMetadata> getUserSharingFile(User user, String cloudFileName) throws FileNotFoundException;
    SharedFileMetadata getSharedToUserFile(User user, String cloudFileName) throws FileNotFoundException;
    void cancelAllUserShare(User user);
}
