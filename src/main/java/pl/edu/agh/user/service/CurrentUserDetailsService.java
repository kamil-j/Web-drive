package pl.edu.agh.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.agh.user.domain.CurrentUser;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.repository.UserRepository;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */

@Slf4j
@Service
public class CurrentUserDetailsService implements UserDetailsService, CurrentUserService {

    private final UserRepository userRepository;

    @Autowired
    public CurrentUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email=%s was not found", email)));
        return new CurrentUser(user);
    }

    @Override
    public void setNewCurrentUser(User user) {
        log.debug("Setting new current user - ID: " + user.getId());

        CurrentUser newCurrentUser = new CurrentUser(user);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(newCurrentUser,
                newCurrentUser.getPassword(), newCurrentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}