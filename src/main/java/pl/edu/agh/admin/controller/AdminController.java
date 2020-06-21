package pl.edu.agh.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.domain.form.UserEditForm;
import pl.edu.agh.user.exception.UserNotFoundException;
import pl.edu.agh.user.repository.UserRepository;
import pl.edu.agh.user.service.UserService;
import pl.edu.agh.util.PageWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by Kamil Jureczka on 2017-09-24.
 */

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final MessageSource messageSource;

    @Autowired
    public AdminController(UserRepository userRepository, MessageSource messageSource, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ModelAndView getAdminPage() {
        log.debug("Getting admin page.");
        return new ModelAndView("admin/admin");
    }

    @GetMapping("/users")
    public String getUsers(@PageableDefault(value = 5) Pageable pageable, Model model,
                                 @RequestParam(required = false) String email) {
        log.debug("Getting admin users page.");

        if(email != null && !email.isEmpty()) {
            log.debug("AdminController getUsers: email param is provided. Getting user with email: " + email);
            return getSingleUser(model, email);
        }

        log.debug("AdminController getUsers: email param is empty. Getting all users.");
        return getAllUsers(pageable, model);
    }

    private String getSingleUser(Model model, String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        return userOptional.map(user -> {
                model.addAttribute("users", Collections.singletonList(user));
                return "admin/users";
        }).orElse("admin/users");

    }

    private String getAllUsers(Pageable pageable, Model model) {
        Page<User> users = userRepository.findAllByOrderByEmailAsc(pageable);
        PageWrapper<User> page = new PageWrapper<>(users, "admin/users");

        model.addAttribute("users", page.getContent());
        model.addAttribute("page", page);

        return "admin/users";
    }

    @GetMapping("/user/{id}")
    public ModelAndView getUserPage(@PathVariable Long id) throws UserNotFoundException {
        log.debug("Getting admin user page with id: " + id);
        User user = userRepository.findOne(id);
        if(user == null) {
            throw new UserNotFoundException(id.toString());
        }

        UserEditForm form = new UserEditForm(user);
        return new ModelAndView("admin/user", "form", form);
    }

    @PutMapping("/user")
    public String editUser(@Valid @ModelAttribute("form") UserEditForm form, BindingResult bindingResult,
                           Locale locale, RedirectAttributes redirectAttributes) throws UserNotFoundException {
        log.debug("Handling admin edit user action.");

        if (bindingResult.hasErrors()) {
            log.debug("Form error while handling edit user by admin. Errors: " + bindingResult.getAllErrors().toString());
            return "admin/user";
        }

        User editedUser = userService.edit(form);
        if(form.getRole() != null && !form.getRole().equals(editedUser.getRole())) {
            userService.editRole(form.getId(), form.getRole());
        }

        log.debug("Successfully edited user (id: " + form.getId() + ")");
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("success.adminController.editUser", null, locale));
        return "redirect:/admin/user/"+form.getId();
    }

    @DeleteMapping("/user")
    public ModelAndView deleteUser(@RequestParam Long userId, Locale locale, RedirectAttributes redirectAttributes)
            throws UserNotFoundException {
        log.debug("Handling admin delete user action.");
        userService.deleteUser(userId);

        log.debug("Successfully deleted user (id: " + userId + ")");
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("success.adminController.deleteUser", null, locale));
        return new ModelAndView("redirect:/admin");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundError(HttpServletRequest request, Exception exception, Locale locale,
                                          RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.adminController.userNotFound", null, locale));

        return "redirect:/";
    }
}
