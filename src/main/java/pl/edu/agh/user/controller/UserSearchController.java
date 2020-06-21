package pl.edu.agh.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.domain.form.UserSearchCriteria;
import pl.edu.agh.user.domain.form.UserSearchResponse;
import pl.edu.agh.user.repository.UserRepository;
import pl.edu.agh.util.UserUtil;

import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by Kamil Jureczka on 2017-08-23.
 */

@Slf4j
@RequestMapping("/search/user")
@RestController
public class UserSearchController {

    private UserRepository userRepository;
    private final MessageSource messageSource;

    @Autowired
    public UserSearchController(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @PostMapping
    public ResponseEntity<?> getSearchResultViaAjax(@Valid @RequestBody UserSearchCriteria searchCriteria,
                                                    BindingResult bindingResult, Locale locale) {
        if (bindingResult.hasErrors()) {
            if(bindingResult.hasFieldErrors()) {
                return ResponseEntity.badRequest().body(getFieldErrorResponse(locale));
            }
            return ResponseEntity.badRequest().body(getErrorResponse(locale));
        }

        Optional<User> userOptional = userRepository.findByEmail(searchCriteria.getUserEmail());
        if(userOptional.isPresent()) {
            User currentUser = UserUtil.getCurrentUser();
            if(!validateUsers(currentUser, userOptional.get())) {
                return ResponseEntity.ok().body(getTheSameUserResponse(locale));
            }

            return ResponseEntity.ok().body(getSuccessResponse(userOptional.get(), locale));
        }

        return ResponseEntity.ok().body(getUserNotFoundResponse(locale));
    }

    private boolean validateUsers(User currentUser, User foundUser) {
        return !currentUser.getCloudId().equals(foundUser.getCloudId());
    }

    private UserSearchResponse getTheSameUserResponse(Locale locale) {
        String errorMsg = messageSource.getMessage("search.userSearchController.sameUser", null, locale);
        return new UserSearchResponse(true, errorMsg);
    }

    private UserSearchResponse getFieldErrorResponse(Locale locale) {
        String errorMsg = messageSource.getMessage("search.userSearchController.fieldError", null, locale);
        return new UserSearchResponse(true, errorMsg);
    }

    private UserSearchResponse getErrorResponse(Locale locale) {
        String errorMsg = messageSource.getMessage("search.userSearchController.error", null, locale);
        return new UserSearchResponse(true, errorMsg);
    }

    private UserSearchResponse getSuccessResponse(User user, Locale locale) {
        String successMsg = messageSource.getMessage("search.userSearchController.success", null, locale);
        return new UserSearchResponse(user.getEmail(), successMsg);
    }

    private UserSearchResponse getUserNotFoundResponse(Locale locale) {
        String notFoundMsg = messageSource.getMessage("search.userSearchController.userNotFound", null, locale);
        return new UserSearchResponse(notFoundMsg);
    }
}
