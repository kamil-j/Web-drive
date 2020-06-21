package pl.edu.agh.share.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.user.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by Kamil Jureczka on 2017-08-05.
 */

@Transactional
public interface SharedFileMetadataRepository extends JpaRepository<SharedFileMetadata, Long> {
    List<SharedFileMetadata> findByFile(FileMetadata file);
    Optional<SharedFileMetadata> findByUserToAndIsCanceledAndFile_CloudName(User userTo, Boolean isCanceled, String fileCloudName);
    List<SharedFileMetadata> findByUserFromAndIsCanceledAndFile_CloudName(User userFrom, Boolean isCanceled, String fileCloudName);
    Optional<SharedFileMetadata> findByUserFromAndUserToAndIsCanceledAndFile_CloudName(User userFrom, User userTo, Boolean isCanceled, String fileCloudName);
    List<SharedFileMetadata> findByUserFromAndIsCanceled(User userFrom, Boolean isCanceled);
    Page<SharedFileMetadata> findAllByUserFromAndIsCanceledOrderByFile_NameAsc(User userFrom, Boolean isCanceled, Pageable pageable);
    List<SharedFileMetadata> findByUserToAndIsCanceled(User userTo, Boolean isCanceled);
    Page<SharedFileMetadata> findAllByUserToAndIsCanceledOrderByFile_NameAsc(User userTo, Boolean isCanceled, Pageable pageable);
}
