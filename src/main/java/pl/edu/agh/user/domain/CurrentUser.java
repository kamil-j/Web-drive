package pl.edu.agh.user.domain;

import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    @Getter
    private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public UserRole getRole() {
        return user.getRole();
    }
}
