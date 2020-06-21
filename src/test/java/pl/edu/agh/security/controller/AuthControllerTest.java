package pl.edu.agh.security.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import pl.edu.agh.user.domain.User;
import pl.edu.agh.user.repository.UserRepository;

import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Kamil Jureczka on 2017-07-19.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void getLoginPageStatusOk() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    public void getRegisterPageStatusOk() throws Exception {
        this.mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    public void registerUserWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/register"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void registerUserWithoutForm() throws Exception {
        this.mockMvc.perform(post("/register").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.global", null, Locale.ENGLISH)));
    }

    @Test
    public void registerUserEmptyForm() throws Exception {
        RequestBuilder request = post("/register")
                .param("email", "")
                .param("name", "")
                .param("surname", "")
                .param("password", "")
                .param("passwordRepeated", "")
                .with(csrf());

        this.mockMvc.perform(request)
                .andExpect(view().name("auth/register"));
    }

    @Test
    public void registerUser() throws Exception {
        RequestBuilder request = post("/register")
                .param("email", "test123@test123")
                .param("name", "Testowy")
                .param("surname", "Tester")
                .param("password", "test1234")
                .param("passwordRepeated", "test1234")
                .with(csrf());

        this.mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Optional<User> user = userRepository.findByEmail("test123@test123");
        assertTrue(user.isPresent());
    }
}
