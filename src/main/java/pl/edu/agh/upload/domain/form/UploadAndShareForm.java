package pl.edu.agh.upload.domain.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.share.domain.ShareTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Kamil Jureczka on 2017-08-26.
 */

@Getter
@Setter
@NoArgsConstructor
public class UploadAndShareForm {
    @Email
    private String email;

    @NotNull
    private MultipartFile file;

    @Size(max=20)
    private String description;

    @NotNull
    private ShareTime shareTime;
}
