package pl.edu.agh.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.repository.FileMetadataRepository;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.repository.UserRepository;

import java.util.UUID;

/**
 * Created by Kamil Jureczka on 2017-07-25.
 */

@Component
public class CloudIdGenerator {

    private final UserRepository userRepository;
    private final FileMetadataRepository fileMetadataRepository;

    @Autowired
    public CloudIdGenerator(UserRepository userRepository, FileMetadataRepository fileMetadataRepository) {
        this.userRepository = userRepository;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public String generateCloudId() {
        String cloudId = UUID.randomUUID().toString();
        while(userRepository.findOneByCloudId(cloudId).isPresent()){
            cloudId = UUID.randomUUID().toString();
        }

        return cloudId;
    }

    public String generateCloudObjectName(User user) {
        String cloudObjectName = user.getCloudId() + "-" + UUID.randomUUID().toString();
        while(fileMetadataRepository.findOneByCloudName(cloudObjectName).isPresent()){
            cloudObjectName = user.getCloudId() + "-" + UUID.randomUUID().toString();
        }

        return cloudObjectName;
    }
}
