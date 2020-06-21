package pl.edu.agh.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.user.domain.CurrentUser;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.domain.form.UserEditForm;
import pl.edu.agh.user.domain.form.validator.UserEditFormValidator;
import pl.edu.agh.user.service.CurrentUserService;
import pl.edu.agh.user.service.UserService;
import pl.edu.agh.util.UserUtil;

import javax.validation.Valid;
import java.util.Locale;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */

@Slf4j
@RequestMapping("/user")
@Controller
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final UserEditFormValidator userEditFormValidator;
    private final MessageSource messageSource;

    @Autowired
    public UserController(UserService userService, CurrentUserService currentUserService,
                          UserEditFormValidator userEditFormValidator, MessageSource messageSource) {
        this.userService = userService;
        this.currentUserService = currentUserService;
        this.userEditFormValidator = userEditFormValidator;
        this.messageSource = messageSource;
    }

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userEditFormValidator);
    }

    @GetMapping
    public ModelAndView getUserPage(){
        log.debug("Getting user page.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((CurrentUser)authentication.getPrincipal()).getUser();

        UserEditForm form = new UserEditForm(user);

        return new ModelAndView("user/user", "form", form);
    }

    @PutMapping
    public String editUser(@Valid @ModelAttribute("form") UserEditForm form, BindingResult bindingResult,
                           Locale locale, RedirectAttributes redirectAttributes) {
        log.debug("Handling edit user put action.");

        if (bindingResult.hasErrors()) {
            log.debug("Form error while handling edit user action. Errors: " + bindingResult.getAllErrors().toString());
            return "user/user";
        }

        User user = UserUtil.getCurrentUser();
        form.setId(user.getId()); //Safety purposes
        User editedUser = userService.edit(form);
        currentUserService.setNewCurrentUser(editedUser);

        log.debug("Successfully edited user (email: " + editedUser.getEmail() + ")");
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("edit.userController.successful", null, locale));
        return "redirect:/user";
    }
}
