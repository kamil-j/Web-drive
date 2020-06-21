package pl.edu.agh.file.controller;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.drive.service.DriveService;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.domain.form.LinkGenerateRequest;
import pl.edu.agh.file.domain.form.LinkGenerateResponse;
import pl.edu.agh.file.repository.FileMetadataRepository;
import pl.edu.agh.file.service.FileMetadataService;
import pl.edu.agh.share.service.SharedFileMetadataService;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.util.PageWrapper;
import pl.edu.agh.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Created by Kamil Jureczka on 2017-07-20.
 */

@Slf4j
@Controller
@RequestMapping("/file")
public class FileController {

    private final FileMetadataService fileMetadataService;
    private final SharedFileMetadataService sharedFileMetadataService;
    private final FileMetadataRepository fileMetadataRepository;
    private final DriveService driveService;
    private final MessageSource messageSource;

    @Autowired
    public FileController(FileMetadataRepository fileMetadataRepository, FileMetadataService fileMetadataService,
                          SharedFileMetadataService sharedFileMetadataService, DriveService driveService, MessageSource messageSource) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileMetadataService = fileMetadataService;
        this.sharedFileMetadataService = sharedFileMetadataService;
        this.driveService = driveService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ModelAndView listUploadedFiles(@PageableDefault(value = 5) Pageable pageable, Model model) {
        log.debug("FileController listUploadedFiles: Getting uploaded files.");
        User user = UserUtil.getCurrentUser();

        Page<FileMetadata> files = fileMetadataRepository.findAllByUserOrderByNameAsc(user, pageable);
        PageWrapper<FileMetadata> page = new PageWrapper<>(files, "file");

        model.addAttribute("files", page.getContent());
        model.addAttribute("page", page);

        return new ModelAndView("file/file");
    }

    @GetMapping("/{cloudFileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String cloudFileName) throws FileNotFoundException {
        log.debug("FileController serveFile: Serving file with cloud name: " + cloudFileName);
        User user = UserUtil.getCurrentUser();

        FileMetadata fileMetadata = fileMetadataService.getFile(user, cloudFileName);
        S3Object object = driveService.getObject(fileMetadata.getCloudName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileMetadata.getContentType()))
                .contentLength(fileMetadata.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileMetadata.getName()+"\"")
                .body(new InputStreamResource(object.getObjectContent()));
    }

    @DeleteMapping
    public ModelAndView deleteFile(@RequestParam String cloudFileName, Locale locale,
                             RedirectAttributes redirectAttributes) throws FileNotFoundException {
        log.debug("FileController deleteFile: Deleting file with cloud name: " + cloudFileName);
        User user = UserUtil.getCurrentUser();

        sharedFileMetadataService.cancelAllUserShareFile(user, cloudFileName);
        fileMetadataService.deleteFile(user, cloudFileName);

        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("success.fileController.deleteFile", null, locale));
        log.debug("FileController deleteFile: Successfully deleted file with cloud name: " + cloudFileName);
        return new ModelAndView("redirect:/file");
    }

    @PostMapping("/link")
    @ResponseBody
    public ResponseEntity<?> generateLinkToFile(@Valid @RequestBody LinkGenerateRequest linkGenerateRequest,
                                                BindingResult bindingResult, Locale locale) throws FileNotFoundException {
        log.debug("FileController generateLinkToFile: Generating link to file with cloud name: "
                + linkGenerateRequest.getCloudFileName());
        if (bindingResult.hasErrors()) {
            if(bindingResult.hasFieldErrors()) {
                log.debug("FileController generateLinkToFile: Request fields contain errors: "
                        + bindingResult.getAllErrors().toString());
                return ResponseEntity.badRequest().body(getFieldErrorResponse(locale));
            }
            log.debug("FileController generateLinkToFile: Request contain errors: "
                    + bindingResult.getAllErrors().toString());
            return ResponseEntity.badRequest().body(getErrorResponse(locale));
        }

        User user = UserUtil.getCurrentUser();
        String link = fileMetadataService.generateLinkToFile(user, linkGenerateRequest.getCloudFileName(),
                linkGenerateRequest.getLinkTime());

        log.debug("FileController generateLinkToFile: Successfully generated link to file with cloud name: "
                + linkGenerateRequest.getCloudFileName());
        return ResponseEntity.ok().body(getSuccessResponse(link, locale));
    }

    private LinkGenerateResponse getFieldErrorResponse(Locale locale) {
        String errorMsg = messageSource.getMessage("generateLink.fileController.fieldError", null, locale);
        return new LinkGenerateResponse(true, errorMsg);
    }

    private LinkGenerateResponse getErrorResponse(Locale locale) {
        String errorMsg = messageSource.getMessage("generateLink.fileController.error", null, locale);
        return new LinkGenerateResponse(true, errorMsg);
    }

    private LinkGenerateResponse getSuccessResponse(String link, Locale locale) {
        String successMsg = messageSource.getMessage("generateLink.fileController.successful", null, locale);
        return new LinkGenerateResponse(link, successMsg);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundError(HttpServletRequest request, Exception exception, Locale locale,
                                          RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.fileController.fileNotFound", null, locale));

        return "redirect:/file";
    }
}
