package pl.edu.agh.share.controller;

import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.drive.service.DriveService;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.share.service.SharedFileMetadataService;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.exception.UserNotFoundException;
import pl.edu.agh.util.PageWrapper;
import pl.edu.agh.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Created by Kamil Jureczka on 2017-08-25.
 */

@Slf4j
@Controller
@RequestMapping("/shared")
public class SharedToUserFileController {

    private final SharedFileMetadataService sharedFileMetadataService;
    private final DriveService driveService;
    private final MessageSource messageSource;

    @Autowired
    public SharedToUserFileController(SharedFileMetadataService sharedFileMetadataService, DriveService driveService,
                               MessageSource messageSource) {
        this.sharedFileMetadataService = sharedFileMetadataService;
        this.driveService = driveService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String listSharedToUserFiles(@PageableDefault(value = 5) Pageable pageable, Model model) {
        log.debug("SharedToUserFileController listSharedToUserFiles: Getting shared to user files.");
        User user = UserUtil.getCurrentUser();

        Page<SharedFileMetadata> sharedToUserFiles = sharedFileMetadataService.getAllSharedToUserFiles(user, pageable);
        PageWrapper<SharedFileMetadata> page = new PageWrapper<>(sharedToUserFiles, "shared");

        model.addAttribute("page", page);
        model.addAttribute("sharedToUserFiles", page.getContent());
        return "shared/shared";
    }

    @GetMapping("/{cloudFileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveSharedToUserFile(@PathVariable String cloudFileName) throws FileNotFoundException {
        log.debug("SharedToUserFileController serveSharedToUserFile: Serving file with cloud name: " + cloudFileName);
        User user = UserUtil.getCurrentUser();

        FileMetadata fileMetadata = sharedFileMetadataService.getSharedToUserFile(user, cloudFileName).getFile();
        S3Object object = driveService.getObject(fileMetadata.getCloudName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileMetadata.getContentType()))
                .contentLength(fileMetadata.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileMetadata.getName()+"\"")
                .body(new InputStreamResource(object.getObjectContent()));
    }

    @DeleteMapping
    public String cancelSharedToUserFile(@RequestParam String cloudFileName, Locale locale,
                                        RedirectAttributes redirectAttributes) throws FileNotFoundException {
        log.debug("SharedToUserFileController cancelSharedToUserFile: Canceling shared to user file with cloud name: " + cloudFileName);
        User user = UserUtil.getCurrentUser();

        sharedFileMetadataService.cancelSharedToUserFile(user, cloudFileName);

        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("success.sharedToUserFileController.deleteFile", null, locale));
        log.debug("SharedToUserFileController cancelSharedToUserFile: Successfully canceled shared to user file with cloud name: " + cloudFileName);
        return "redirect:/shared";
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundError(HttpServletRequest request, Exception exception, Locale locale,
                                          RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.sharedToUserFileController.fileNotFound", null, locale));

        return "redirect:/shared";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundError(HttpServletRequest request, Exception exception, Locale locale,
                                          RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.sharedToUserFileController.userNotFound", null, locale));

        return "redirect:/shared";
    }
}
