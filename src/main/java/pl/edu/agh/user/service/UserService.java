package pl.edu.agh.user.service;

import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.domain.UserRole;
import pl.edu.agh.user.domain.form.UserCreateForm;
import pl.edu.agh.user.domain.form.UserEditForm;
import pl.edu.agh.user.exception.UserNotFoundException;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */
public interface UserService {
    User create(UserCreateForm form);
    User edit(UserEditForm form);
    User editRole(long userId, UserRole role) throws UserNotFoundException;
    void deleteUser(long userId) throws UserNotFoundException;
}
