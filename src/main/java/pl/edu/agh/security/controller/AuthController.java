package pl.edu.agh.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.edu.agh.user.domain.form.UserCreateForm;
import pl.edu.agh.user.domain.form.validator.UserCreateFormValidator;
import pl.edu.agh.user.service.UserService;

import javax.validation.Valid;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */

@Slf4j
@Controller
public class AuthController {

    private final UserService userService;
    private final UserCreateFormValidator userCreateFormValidator;

    @Autowired
    public AuthController(UserService userService, UserCreateFormValidator userCreateFormValidator) {
        this.userService = userService;
        this.userCreateFormValidator = userCreateFormValidator;
    }

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @GetMapping(value = "/login")
    public ModelAndView getLoginPage(@RequestParam(required = false) String error) {
        log.debug("Getting login page.");
        return new ModelAndView("auth/login", "error", error);
    }

    @GetMapping(value = "/register")
    public ModelAndView getRegisterPage() {
        log.debug("Getting register page.");
        return new ModelAndView("auth/register", "form", new UserCreateForm());
    }

    @PostMapping(value = "/register")
    public ModelAndView handleUserCreateForm(@Valid @ModelAttribute("form") UserCreateForm form, BindingResult bindingResult) {
        log.debug("Handling register post action.");

        if (bindingResult.hasErrors()) {
            log.debug("Form error while handling register post action. Errors: " + bindingResult.getAllErrors().toString());
            return new ModelAndView("auth/register");
        }

        userService.create(form);
        return new ModelAndView("redirect:/login");
    }
}
