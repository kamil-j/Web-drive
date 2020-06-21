package pl.edu.agh.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.drive.service.DriveService;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.service.FileMetadataService;
import pl.edu.agh.file.util.FileUtil;
import pl.edu.agh.share.service.SharedFileMetadataService;
import pl.edu.agh.upload.domain.form.UploadAndShareForm;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.exception.UserNotFoundException;
import pl.edu.agh.user.repository.UserRepository;
import pl.edu.agh.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Kamil Jureczka on 2017-08-19.
 */

@Slf4j
@Controller
@RequestMapping("/upload")
public class UploadController {

    private final FileMetadataService fileMetadataService;
    private final SharedFileMetadataService sharedFileMetadataService;
    private final UserRepository userRepository;
    private final DriveService driveService;
    private final MessageSource messageSource;

    @Autowired
    public UploadController(FileMetadataService fileMetadataService, DriveService driveService,
                            MessageSource messageSource, SharedFileMetadataService sharedFileMetadataService,
                            UserRepository userRepository) {
        this.fileMetadataService = fileMetadataService;
        this.driveService = driveService;
        this.messageSource = messageSource;
        this.sharedFileMetadataService = sharedFileMetadataService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String getFileUploadPage() {
        log.debug("Getting file upload page.");
        return "upload/file";
    }

    @PostMapping
    public String handleFileUpload(@RequestParam MultipartFile file, @RequestParam(required = false) String description,
                                   Locale locale, RedirectAttributes redirectAttributes) throws IOException {
        log.debug("Handling file upload.");

        if(!validateUploadFileRequestParams(file, description, redirectAttributes, locale)) {
            log.warn("Incorrect upload file request params.");
            return "redirect:/upload";
        }

        User user = UserUtil.getCurrentUser();

        FileMetadata fileMetadata = fileMetadataService.saveNewFile(user, file, description);
        driveService.putFile(file, fileMetadata.getCloudName());

        log.debug("File uploaded successfully");
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("success.uploadController.handleFileUpload", null, locale));
        return "redirect:/file";
    }

    private boolean validateUploadFileRequestParams(MultipartFile file, String description,
                                          RedirectAttributes redirectAttributes, Locale locale) {
        if (file.isEmpty()) {
            log.error("Uploaded file: " + file.getName() + " - is empty!");
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("emptyFile.uploadController.handleFileUpload", null, locale));
            return false;
        }

        if(FileUtil.isNotValidDescription(description)){
            log.error("Upload file description: " + description + " - is not valid!");
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("Size.description.uploadController.handleFileUpload", null, locale));
            return false;
        }

        return true;
    }

    @GetMapping("/share")
    public ModelAndView getShareUploadPage() {
        log.debug("Getting share file upload page.");
        return new ModelAndView("upload/share", "form", new UploadAndShareForm());
    }

    @PostMapping("/share")
    public String handleShareFileUpload(@Valid @ModelAttribute("form") UploadAndShareForm form, BindingResult bindingResult,
                                        Locale locale, RedirectAttributes redirectAttributes) throws IOException, UserNotFoundException {
        log.debug("Handling share file upload.");

        if (bindingResult.hasErrors()) {
            log.warn("Share file upload form contains errors: " + bindingResult.getAllErrors().toString());
            return "upload/share";
        }

        User user = UserUtil.getCurrentUser();
        User userTo = userRepository.findByEmail(form.getEmail()).orElseThrow(() ->
                new UserNotFoundException(form.getEmail())
        );

        if(!validateShareFileRequestParams(form.getFile(), redirectAttributes, locale)) {
            log.warn("Incorrect share file upload request params.");
            return "redirect:/upload/share";
        }

        if(!validateUsers(user, userTo, redirectAttributes, locale)){
            log.debug("User with id " + user.getId() + " try to share the file to yourself!");
            return "redirect:/upload/share";
        }

        FileMetadata fileMetadata = fileMetadataService.saveNewFile(user, form.getFile(), form.getDescription());
        driveService.putFile(form.getFile(), fileMetadata.getCloudName());
        sharedFileMetadataService.shareFile(user, userTo, fileMetadata.getCloudName(), form.getShareTime());

        log.debug("File shared successfully");
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("success.uploadController.handleShareFileUpload", null, locale));
        return "redirect:/share";
    }

    private boolean validateShareFileRequestParams(MultipartFile file, RedirectAttributes redirectAttributes, Locale locale) {
        if (file.isEmpty()) {
            log.error("Uploaded file: " + file.getName() + " - is empty!");
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("emptyFile.uploadController.handleShareFileUpload", null, locale));
            return false;
        }

        return true;
    }

    private boolean validateUsers(User userFrom, User userTo, RedirectAttributes redirectAttributes, Locale locale) {
        if(userFrom.getCloudId().equals(userTo.getCloudId())) {
            log.error("validateUsers: The same users cannot share files!");
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("sameUser.uploadController.handleShareFileUpload", null, locale));
            return false;
        }

        return true;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundError(HttpServletRequest request, Exception exception, Locale locale,
                                          RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.uploadController.userNotFound", null, locale));

        return "redirect:/share";
    }
}