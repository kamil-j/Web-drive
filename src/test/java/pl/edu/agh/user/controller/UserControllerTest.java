package pl.edu.agh.user.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Kamil Jureczka on 2017-09-24.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Test
    @WithUserDetails("test@test")
    public void getUserPageStatusOk() throws Exception {
        this.mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/user"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    public void editUserWithoutCsrf() throws Exception {
        this.mockMvc.perform(put("/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void editUser() throws Exception {
        RequestBuilder request = put("/user")
                .param("email", "test@test123")
                .param("name", "Tester")
                .param("surname", "Testowy")
                .with(csrf());

        this.mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("edit.userController.successful", null, Locale.ENGLISH)));
    }
}
