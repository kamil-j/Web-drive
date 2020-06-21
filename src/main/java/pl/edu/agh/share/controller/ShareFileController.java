package pl.edu.agh.share.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.share.domain.ShareTime;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.share.service.SharedFileMetadataService;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.exception.UserNotFoundException;
import pl.edu.agh.user.repository.UserRepository;
import pl.edu.agh.util.PageWrapper;
import pl.edu.agh.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Created by Kamil Jureczka on 2017-08-05.
 */

@Slf4j
@Controller
@RequestMapping("/share")
public class ShareFileController {

    private final SharedFileMetadataService sharedFileMetadataService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Autowired
    public ShareFileController(SharedFileMetadataService sharedFileMetadataService, UserRepository userRepository,
                               MessageSource messageSource) {
        this.sharedFileMetadataService = sharedFileMetadataService;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String listUserSharingFiles(@PageableDefault(value = 5) Pageable pageable, Model model) {
        log.debug("ShareFileController listUserSharingFiles: Getting sharing files.");
        User user = UserUtil.getCurrentUser();

        Page<SharedFileMetadata> userSharingFiles = sharedFileMetadataService.getAllUserSharingFiles(user, pageable);
        PageWrapper<SharedFileMetadata> page = new PageWrapper<>(userSharingFiles, "share");

        model.addAttribute("page", page);
        model.addAttribute("userSharingFiles", page.getContent());
        return "share/share";
    }

    @PostMapping
    public String shareFile(@RequestParam String cloudFileNameShare, @RequestParam String email,
                            @RequestParam(required = false) ShareTime shareTime, //Required false because of message
                            RedirectAttributes redirectAttributes, Locale locale)
            throws UserNotFoundException, FileNotFoundException {
        log.debug("ShareFileController shareFile: Sharing file with cloud name: " + cloudFileNameShare
                + " - to user with email: " + email);
        User userFrom = UserUtil.getCurrentUser();
        User userTo = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(email)
        );

        if(!validateShareFileRequestParams(shareTime, redirectAttributes, locale)) {
            log.debug("ShareFileController shareFile: Incorrect share file request params.");
            return "redirect:/share";
        }

        if(!validateUsers(userFrom, userTo, redirectAttributes, locale)){
            log.debug("ShareFileController shareFile: User with id " + userFrom.getId() + " try to share the file to yourself!");
            return "redirect:/share";
        }

        sharedFileMetadataService.shareFile(userFrom, userTo, cloudFileNameShare, shareTime);

        log.info("File " + cloudFileNameShare + " has been successfully shared to " + userTo.getCloudId());
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("success.shareFileController.shareFile", null, locale));
        return "redirect:/share";
    }

    private boolean validateShareFileRequestParams(ShareTime shareTime, RedirectAttributes redirectAttributes, Locale locale) {
        if (shareTime == null) {
            log.error("validateShareFileRequestParams: Share time not provided!");
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("NotNull.shareFileController.shareTime", null, locale));
            return false;
        }

        return true;
    }

    private boolean validateUsers(User userFrom, User userTo, RedirectAttributes redirectAttributes, Locale locale) {
        if(userFrom.getCloudId().equals(userTo.getCloudId())) {
            log.error("validateUsers: The same users cannot share files!");
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("sameUser.shareFileController.shareFile", null, locale));
            return false;
        }

        return true;
    }

    @DeleteMapping
    public String cancelUserSharingFile(@RequestParam String cloudFileName, @RequestParam String emailUserTo,
                                        Locale locale, RedirectAttributes redirectAttributes) throws UserNotFoundException, FileNotFoundException {
        log.debug("ShareFileController cancelUserSharingFile: Canceling sharing file with cloud name: " + cloudFileName
                + " - to user with email: " + emailUserTo);

        User userFrom = UserUtil.getCurrentUser();
        User userTo = userRepository.findByEmail(emailUserTo).orElseThrow(() ->
                new UserNotFoundException(emailUserTo)
        );

        sharedFileMetadataService.cancelUserSharingFile(userFrom, userTo, cloudFileName);

        log.info("Successfully canceled sharing file with cloud name = " + cloudFileName
                + " - to user with id = " + userTo.getId());
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("success.shareFileController.deleteFile", null, locale));
        return "redirect:/share";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundError(HttpServletRequest request, Exception exception, Locale locale,
                                          RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.shareFileController.fileNotFound", null, locale));

        return "redirect:/share";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundError(HttpServletRequest request, Exception exception, Locale locale,
                                          RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.sharedFileController.userNotFound", null, locale));

        return "redirect:/share";
    }
}
