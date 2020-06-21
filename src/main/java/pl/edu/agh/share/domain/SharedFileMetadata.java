package pl.edu.agh.share.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Kamil Jureczka on 2017-08-05.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shared_files_metadata")
public class SharedFileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_from", nullable = false)
    private User userFrom;

    @ManyToOne
    @JoinColumn(name = "user_to", nullable = false)
    private User userTo;

    @ManyToOne
    @JoinColumn(name = "shared_file", nullable = false)
    private FileMetadata file;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "is_canceled")
    private Boolean isCanceled = false;

    public SharedFileMetadata(User userFrom, User userTo, FileMetadata file, LocalDateTime expirationDate) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.file = file;
        this.expirationDate = expirationDate;
    }
}
