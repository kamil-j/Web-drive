package pl.edu.agh.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.share.domain.SharedFileMetadata;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "cloud_id", nullable = false, unique = true)
    private String cloudId;

    @Lazy
    @OrderBy("name")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FileMetadata> files;

    @Lazy
    @OneToMany(mappedBy = "userFrom", cascade = CascadeType.ALL)
    private List<SharedFileMetadata> myShareFiles;

    @Lazy
    @OneToMany(mappedBy = "userTo", cascade = CascadeType.ALL)
    private List<SharedFileMetadata> sharedFiles;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(Long id) {
        this.id = id;
    }

    public User(String email) {
        this.email = email;
    }
}