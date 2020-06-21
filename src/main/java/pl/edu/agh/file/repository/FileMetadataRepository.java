package pl.edu.agh.file.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.user.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by Kamil Jureczka on 2017-07-20.
 */

@Transactional
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByName(String name);
    Optional<FileMetadata> findOneByCloudName(String cloudName);
    List<FileMetadata> findByContentType(String contentType);
    List<FileMetadata> findByUserAndCloudName(User user, String cloudName);
    List<FileMetadata> findByUser(User user);
    Page<FileMetadata> findAllByUserOrderByNameAsc(User user, Pageable pageable);
}
