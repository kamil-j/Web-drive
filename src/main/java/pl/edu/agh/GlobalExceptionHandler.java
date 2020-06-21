package pl.edu.agh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.drive.exception.AmazonServiceException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by Kamil Jureczka on 2017-09-04.
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    public String handleError(HttpServletRequest request, Exception exception, Locale locale,
                              RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.global", null, locale));

        return "redirect:/";
    }

    @ExceptionHandler(AmazonServiceException.class)
    public String handleAmazonError(HttpServletRequest request, Exception exception, Locale locale,
                              RedirectAttributes redirectAttributes) {
        log.error("Request: " + request.getRequestURL() + " raised: ", exception);
        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("exception.global", null, locale));

        return "redirect:/";
    }
}
