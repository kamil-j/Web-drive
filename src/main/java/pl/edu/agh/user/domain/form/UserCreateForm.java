package pl.edu.agh.user.domain.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */

@Getter
@Setter
@NoArgsConstructor
public class UserCreateForm {

    @Email
    @NotEmpty
    private String email;

    @Size(max = 50)
    @Pattern(regexp="^[A-Za-ząćęłńóśźżĄĘŁŃÓŚŹŻ]*$")
    private String name;

    @Size(max = 50)
    @Pattern(regexp="^[A-Za-ząćęłńóśźżĄĘŁŃÓŚŹŻ]*$")
    private String surname;

    @NotEmpty
    @Size(min = 6, max = 15)
    @Pattern(regexp="^(?=.*[A-Za-z!@#$%^&+=])(?=.*\\d)[A-Za-z!@#$%^&+=\\d]{6,}$")
    private String password;

    @NotEmpty
    @Size(min = 6, max = 15)
    @Pattern(regexp="^(?=.*[A-Za-z!@#$%^&+=])(?=.*\\d)[A-Za-z!@#$%^&+=\\d]{6,}$")
    private String passwordRepeated;
}
