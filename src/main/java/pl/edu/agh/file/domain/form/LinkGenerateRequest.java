package pl.edu.agh.file.domain.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import pl.edu.agh.file.domain.LinkTime;

import javax.validation.constraints.NotNull;

/**
 * Created by Kamil Jureczka on 2017-08-27.
 */

@Getter
@Setter
@NoArgsConstructor
public class LinkGenerateRequest {

    @NotBlank
    private String cloudFileName;

    @NotNull
    private LinkTime linkTime;
}
