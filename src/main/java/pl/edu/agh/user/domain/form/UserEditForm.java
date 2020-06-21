package pl.edu.agh.user.domain.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.domain.UserRole;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */

@Getter
@Setter
@NoArgsConstructor
public class UserEditForm {

    private Long id;

    @Email
    private String email;

    @Size(max = 50)
    @Pattern(regexp="^[A-Za-ząćęłńóśźżĄĘŁŃÓŚŹŻ]*$")
    private String name;

    @Size(max = 50)
    @Pattern(regexp="^[A-Za-ząćęłńóśźżĄĘŁŃÓŚŹŻ]*$")
    private String surname;

    private String password;

    private String passwordRepeated;

    private UserRole role;

    public UserEditForm(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
