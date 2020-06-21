package pl.edu.agh.download.controller;

import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.drive.service.DriveService;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.service.FileMetadataService;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Created by Kamil Jureczka on 2017-08-27.
 */

@Slf4j
@Controller
@RequestMapping("/download")
public class DownloadController {

    private final FileMetadataService fileMetadataService;
    private final DriveService driveService;
    private final MessageSource messageSource;

    @Autowired
    public DownloadController(FileMetadataService fileMetadataService, DriveService driveService,
                              MessageSource messageSource) {
        this.fileMetadataService = fileMetadataService;
        this.driveService = driveService;
        this.messageSource = messageSource;
    }

    @GetMapping("/{cloudFileName:.+}")
    @ResponseBody
    public ResponseEntity<?> serveFileViaLink(@PathVariable String cloudFileName) throws FileNotFoundException {
        log.debug("Serving file with cloudFileName: " + cloudFileName);
        FileMetadata file = fileMetadataService.getFileFromLink(cloudFileName);
        S3Object object = driveService.getObject(file.getCloudName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(file.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getName()+"\"")
                .body(new InputStreamResource(object.getObjectContent()));
    }

    @GetMapping("/page/{cloudFileName:.+}")
    public ModelAndView getDownloadFilePage(@PathVariable String cloudFileName) throws FileNotFoundException {
        log.debug("Getting download file page for file with cloudFileName: " + cloudFileName);
        FileMetadata file = fileMetadataService.getFileFromLink(cloudFileName);

        return new ModelAndView("download/download", "file", file);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundError(HttpServletRequest request, Exception exception, Locale locale,
                                          RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.downloadController.fileNotFound", null, locale));

        return "redirect:/";
    }
}
