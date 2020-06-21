package pl.edu.agh.user.service;

import pl.edu.agh.user.domain.User;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */
public interface CurrentUserService {
    void setNewCurrentUser(User user);
}
