package pl.edu.agh.util

import pl.edu.agh.file.repository.FileMetadataRepository
import pl.edu.agh.user.domain.User
import pl.edu.agh.user.repository.UserRepository
import spock.lang.Specification

class CloudIdGeneratorTest extends Specification {

    private UserRepository userRepository = Mock(UserRepository)

    private FileMetadataRepository fileMetadataRepository = Mock(FileMetadataRepository)

    private CloudIdGenerator cloudIdGenerator = new CloudIdGenerator(userRepository, fileMetadataRepository)

    def "should generate cloud id when cloud id not exists yet"() {
        given:
            userRepository.findOneByCloudId(_) >> Optional.empty()
        when:
            String generatedId = cloudIdGenerator.generateCloudId()
        then:
            !generatedId.isEmpty()
    }

    def "should generate cloud id when cloud id already exists"() {
        given:
        1 * userRepository.findOneByCloudId(_) >> Optional.of(new User(1))
        1 * userRepository.findOneByCloudId(_) >> Optional.empty()
        when:
        String generatedId = cloudIdGenerator.generateCloudId()
        then:
        !generatedId.isEmpty()
    }
}
