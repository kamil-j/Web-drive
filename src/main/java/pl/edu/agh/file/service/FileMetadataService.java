package pl.edu.agh.file.service;

import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.domain.LinkTime;
import pl.edu.agh.user.domain.User;

import java.io.FileNotFoundException;

/**
 * Created by Kamil Jureczka on 2017-07-20.
 */
public interface FileMetadataService {
    FileMetadata saveNewFile(User user, MultipartFile file, String description);
    FileMetadata getFile(User user, String cloudFileName) throws FileNotFoundException;
    FileMetadata getFileFromLink(String cloudFileName) throws FileNotFoundException;
    String generateLinkToFile(User user, String cloudFileName, LinkTime linkTime) throws FileNotFoundException;
    void deleteFile(User user, String cloudFileName) throws FileNotFoundException;
    void deleteAllUserFiles(User user);
}
