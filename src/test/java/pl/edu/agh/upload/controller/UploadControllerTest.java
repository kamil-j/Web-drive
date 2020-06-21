package pl.edu.agh.upload.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.service.FileMetadataService;
import pl.edu.agh.share.service.SharedFileMetadataService;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Kamil Jureczka on 2017-08-22.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileMetadataService fileMetadataService;

    @MockBean
    private SharedFileMetadataService sharedFileMetadataService;

    @Autowired
    private MessageSource messageSource;

    @Test
    @WithUserDetails("test@test")
    public void getFileUploadPageStatusOk() throws Exception {
        this.mockMvc.perform(get("/upload"))
                .andExpect(status().isOk())
                .andExpect(view().name("upload/file"));
    }

    @Test
    @WithUserDetails("test@test")
    public void uploadFileWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/upload"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void uploadEmptyFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", new ByteArrayInputStream(new byte[]{}));

        this.mockMvc.perform(fileUpload("/upload").file(multipartFile).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/upload"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("emptyFile.uploadController.handleFileUpload", null, Locale.ENGLISH)));

    }

    @Test
    @WithUserDetails("test@test")
    public void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Test content".getBytes());

        given(fileMetadataService.saveNewFile(any(), eq(multipartFile), any())).willReturn(getTestFile());

        this.mockMvc.perform(fileUpload("/upload").file(multipartFile).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/file"));

        then(fileMetadataService).should().saveNewFile(any(), eq(multipartFile), any());
    }

    private FileMetadata getTestFile() {
        FileMetadata firstFileMetadata = new FileMetadata();
        firstFileMetadata.setName("test1.txt");
        firstFileMetadata.setCloudName("testcloud1");

        return firstFileMetadata;
    }

    @Test
    @WithUserDetails("test@test")
    public void getShareUploadPageStatusOk() throws Exception {
        this.mockMvc.perform(get("/upload/share"))
                .andExpect(status().isOk())
                .andExpect(view().name("upload/share"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    @WithUserDetails("test@test")
    public void uploadShareFileWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/upload/share"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void uploadNullShareFile() throws Exception {
        this.mockMvc.perform(fileUpload("/upload/share")
                    .param("email", "user@user")
                    .param("shareTime", "HOUR24").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("upload/share"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithUserDetails("test@test")
    public void uploadShareFileWithNotExistingUser() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", new ByteArrayInputStream("file body".getBytes()));

        this.mockMvc.perform(fileUpload("/upload/share")
                    .file(multipartFile)
                    .param("email", "user123123@user123")
                    .param("shareTime", "HOUR24").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.uploadController.userNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void uploadEmptyShareFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", new ByteArrayInputStream(new byte[]{}));

        this.mockMvc.perform(fileUpload("/upload/share")
                    .file(multipartFile)
                    .param("email", "user@user")
                    .param("shareTime", "HOUR24").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/upload/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("NotNull.form.file", null, Locale.ENGLISH)));

    }

    @Test
    @WithUserDetails("test@test")
    public void uploadShareFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "full file".getBytes());

        given(fileMetadataService.saveNewFile(any(), eq(multipartFile), any())).willReturn(getTestFile());

        this.mockMvc.perform(fileUpload("/upload/share")
                    .file(multipartFile)
                    .param("email", "user@user")
                    .param("shareTime", "HOUR24").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/share"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("success.uploadController.handleShareFileUpload", null, Locale.ENGLISH)));

        then(fileMetadataService).should().saveNewFile(any(), eq(multipartFile), any());
        then(sharedFileMetadataService).should().shareFile(any(), any(), eq(getTestFile().getCloudName()), any());
    }
}
