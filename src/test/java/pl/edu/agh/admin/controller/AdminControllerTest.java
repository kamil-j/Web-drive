package pl.edu.agh.admin.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Test
    @WithUserDetails("test@test")
    public void getAdminPageStatusOk() throws Exception {
        this.mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"));
    }

    @Test
    @WithUserDetails("test@test")
    public void getUsersWithoutEmail() throws Exception {
        this.mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithUserDetails("test@test")
    public void getUsersWithEmail() throws Exception {
        this.mockMvc.perform(get("/admin/users").param("email", "test@test"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithUserDetails("test@test")
    public void getUserPageWhenUserNotExists() throws Exception {
        this.mockMvc.perform(get("/admin/user/23142"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.adminController.userNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void getUserPageStatusOk() throws Exception {
        this.mockMvc.perform(get("/admin/user/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    public void editUserWithoutCsrf() throws Exception {
        this.mockMvc.perform(put("/admin/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void editUser() throws Exception {
        RequestBuilder request = put("/admin/user")
                .param("id", "2")
                .param("email", "user@user1")
                .param("name", "Tester")
                .param("surname", "Testowy")
                .param("role", "ADMIN")
                .with(csrf());

        this.mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/2"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("success.adminController.editUser", null, Locale.ENGLISH)));
    }

    @Test
    public void deleteUserWithoutCsrf() throws Exception {
        this.mockMvc.perform(delete("/admin/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void deleteUser() throws Exception {
        RequestBuilder request = delete("/admin/user")
                .param("userId", "2")
                .with(csrf());

        this.mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("success.adminController.deleteUser", null, Locale.ENGLISH)));
    }
}
