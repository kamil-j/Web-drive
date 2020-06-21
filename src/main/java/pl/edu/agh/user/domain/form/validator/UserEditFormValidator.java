package pl.edu.agh.user.domain.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.agh.user.domain.form.UserEditForm;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */
@Component
public class UserEditFormValidator implements Validator {

    private static final int PASSWORD_MIN_LEN = 6;
    private static final int PASSWORD_MAX_LEN = 15;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserEditForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserEditForm form = (UserEditForm) target;
        validatePasswords(errors, form);
    }

    private void validatePasswords(Errors errors, UserEditForm form) {
        String newPassword = form.getPassword();
        String newPasswordRepeated = form.getPasswordRepeated();

        if(newPassword != null && !newPassword.isEmpty()) {
            if(!newPassword.equals(newPasswordRepeated)) {
                errors.rejectValue("password", "noMatch.form.password");
            }

            if(newPassword.length() < PASSWORD_MIN_LEN || newPassword.length() > PASSWORD_MAX_LEN) {
                errors.rejectValue("password", "Size.form.password",
                        new Object[]{"password", PASSWORD_MAX_LEN, PASSWORD_MIN_LEN}, null);
            }

            if(!newPassword.matches("^(?=.*[A-Za-z!@#$%^&+=])(?=.*\\d)[A-Za-z!@#$%^&+=\\d]{6,15}$")){
                errors.rejectValue("password", "Pattern.form.password");
            }
        }
    }
}
