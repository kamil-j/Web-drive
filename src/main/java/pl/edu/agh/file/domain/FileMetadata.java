package pl.edu.agh.file.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Kamil Jureczka on 2017-07-20.
 */

@Getter
@Setter
@Entity
@Table(name = "files_metadata")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cloud_name", nullable = false, unique = true)
    private String cloudName;

    @Column(name = "description")
    private String description;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "link_expired_date", nullable = false)
    private LocalDateTime linkExpiredDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lazy
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private List<SharedFileMetadata> shareList;

    public FileMetadata() {
    }

    public FileMetadata(User user, MultipartFile multipartFile, String cloudName, String description) {
        this.user = user;
        this.name = multipartFile.getOriginalFilename();
        this.size = multipartFile.getSize();
        this.contentType = multipartFile.getContentType();
        this.cloudName = cloudName;
        this.description = description;
        this.linkExpiredDate = LocalDateTime.now();
    }
}
