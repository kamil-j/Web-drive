package pl.edu.agh.user.domain.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Kamil Jureczka on 2017-08-23.
 */

@Getter
@Setter
@NoArgsConstructor
public class UserSearchCriteria {
    @NotBlank
    private String userEmail;
}
