package pl.edu.agh.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.agh.user.domain.CurrentUser;
import pl.edu.agh.user.domain.User;

/**
 * Created by Kamil Jureczka on 2017-08-01.
 */
public class UserUtil {
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((CurrentUser)authentication.getPrincipal()).getUser();
    }
}
