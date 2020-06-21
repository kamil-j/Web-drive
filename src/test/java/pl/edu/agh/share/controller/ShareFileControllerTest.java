package pl.edu.agh.share.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.share.ShareTestDataProvider;
import pl.edu.agh.share.domain.ShareTime;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.share.service.SharedFileMetadataService;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ShareFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @SpyBean
    private SharedFileMetadataService sharedFileMetadataService;

    @Test
    @WithUserDetails("test@test")
    public void getAllUserSharingFilesStatusOk() throws Exception {
        List<SharedFileMetadata> testFileList = ShareTestDataProvider.getTestShareFiles();
        Page<SharedFileMetadata> testPage = new PageImpl<>(testFileList);
        given(sharedFileMetadataService.getAllUserSharingFiles(any(), any(Pageable.class))).willReturn(testPage);

        this.mockMvc.perform(get("/share"))
                .andExpect(status().isOk())
                .andExpect(view().name("share/share"))
                .andExpect(model().attribute("userSharingFiles", Matchers.contains(testFileList.get(0), testFileList.get(1))));
    }

    @Test
    @WithUserDetails("test@test")
    public void shareFileWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/share"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void shareFileFilesWhenShareTimeNotProvided() throws Exception {
        this.mockMvc.perform(post("/share")
                    .param("cloudFileNameShare", "test_cloud_file_name")
                    .param("email", "user@user").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("NotNull.shareFileController.shareTime", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void shareFileWhenFileNotExists() throws Exception {
        doThrow(new FileNotFoundException()).when(sharedFileMetadataService).shareFile(any(), any(),
                eq("test_cloud_name_1243"), eq(ShareTime.HOUR12));

        this.mockMvc.perform(post("/share")
                    .param("cloudFileNameShare", "test_cloud_name_1243")
                    .param("email", "user@user")
                    .param("shareTime", "HOUR12").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.shareFileController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void shareFileWhenUserNotFound() throws Exception {
        this.mockMvc.perform(post("/share")
                    .param("cloudFileNameShare", "test_cloud_file_name")
                    .param("email", "notExistingUserEmail")
                    .param("shareTime", "HOUR12").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.sharedFileController.userNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void shareFileStatusOk() throws Exception {
        this.mockMvc.perform(post("/share")
                    .param("cloudFileNameShare", "test_file_cloud_name7")
                    .param("email", "user@user")
                    .param("shareTime", "HOUR12").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("success.shareFileController.shareFile", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void cancelSharingWithoutCsrf() throws Exception {
        this.mockMvc.perform(delete("/share"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void cancelSharingWhenUserNotFound() throws Exception {
        this.mockMvc.perform(delete("/share")
                    .param("cloudFileName", "test_file_cloud_name7")
                    .param("emailUserTo", "notExistingUserName").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.sharedFileController.userNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void cancelSharingWhenFileNotExists() throws Exception {
        doThrow(new FileNotFoundException()).when(sharedFileMetadataService).cancelUserSharingFile(any(), any(),
                eq("test_file_cloud_name"));

        this.mockMvc.perform(delete("/share")
                    .param("cloudFileName", "test_file_cloud_name")
                    .param("emailUserTo", "user@user").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.shareFileController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void cancelSharingStatusOk() throws Exception {
        this.mockMvc.perform(delete("/share")
                    .param("cloudFileName", "test_file_cloud_name8")
                    .param("emailUserTo", "user@user").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("success.shareFileController.deleteFile", null, Locale.ENGLISH)));
    }
}
