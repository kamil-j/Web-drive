package pl.edu.agh.user.domain.form.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.edu.agh.user.domain.form.UserCreateForm;
import pl.edu.agh.user.repository.UserRepository;

import java.util.Locale;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */
@Component
public class UserCreateFormValidator implements Validator {

    private final UserRepository userRepository;

    @Autowired
    public UserCreateFormValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserCreateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserCreateForm form = (UserCreateForm) target;
        validatePassword(errors, form);
        validateEmail(errors, form);
    }

    private void validatePassword(Errors errors, UserCreateForm form) {
        if (!form.getPassword().equals(form.getPasswordRepeated())) {
            errors.rejectValue("password", "noMatch.form.password");
        }
    }

    private void validateEmail(Errors errors, UserCreateForm form) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "exist.form.email");
        }
    }
}

