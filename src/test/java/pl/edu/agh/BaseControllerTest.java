package pl.edu.agh;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by Kamil Jureczka on 2017-07-16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void statusIsRedirect() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails("test@test")
    public void responseForLoggedUser() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/file"))
                .andExpect(view().name("redirect:/file"));
    }
}
