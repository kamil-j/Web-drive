package pl.edu.agh;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Kamil Jureczka on 2017-07-14.
 */
@Controller
public class BaseController {

    @GetMapping(value = "/")
    public ModelAndView getStartPage(@ModelAttribute("message") String message, RedirectAttributes redirectAttributes) {
        if(message != null && !message.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", message);
        }

        return new ModelAndView("redirect:/file");
    }
}
