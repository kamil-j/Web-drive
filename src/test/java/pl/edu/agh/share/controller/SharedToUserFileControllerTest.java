package pl.edu.agh.share.controller;

import com.amazonaws.services.s3.model.S3Object;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.drive.exception.AmazonServiceException;
import pl.edu.agh.drive.service.DriveService;
import pl.edu.agh.share.ShareTestDataProvider;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.share.service.SharedFileMetadataService;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SharedToUserFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @SpyBean
    private SharedFileMetadataService sharedFileMetadataService;

    @SpyBean
    private DriveService driveService;

    @Test
    @WithUserDetails("test@test")
    public void getAllSharedToUserFilesStatusOk() throws Exception {
        List<SharedFileMetadata> testFileList = ShareTestDataProvider.getTestShareFiles();
        Page<SharedFileMetadata> testPage = new PageImpl<>(testFileList);
        given(sharedFileMetadataService.getAllSharedToUserFiles(any(), any(Pageable.class))).willReturn(testPage);

        this.mockMvc.perform(get("/shared"))
                .andExpect(status().isOk())
                .andExpect(view().name("shared/shared"))
                .andExpect(model().attribute("sharedToUserFiles", Matchers.contains(testFileList.get(0), testFileList.get(1))));
    }

    @Test
    @WithUserDetails("test@test")
    public void serveSharedToUserFileWhenFileNotFound() throws Exception {
        doThrow(new FileNotFoundException()).when(sharedFileMetadataService).getSharedToUserFile(any(), eq("test_cloud_name12356"));

        this.mockMvc.perform(get("/shared/test_cloud_name12356"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shared"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.sharedToUserFileController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void serveSharedToUserFileWhenAmazonException() throws Exception {
        doThrow(new AmazonServiceException("")).when(driveService).getObject(eq("test_file_cloud_name6"));

        this.mockMvc.perform(get("/shared/test_file_cloud_name6"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.global", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void serveSharedToUserFileStatusOk() throws Exception {
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream("file".getBytes()));

        doReturn(s3Object).when(driveService).getObject(eq("test_file_cloud_name5"));

        this.mockMvc.perform(get("/shared/test_file_cloud_name5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain")))
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test4.txt\""));
    }

    @Test
    @WithUserDetails("test@test")
    public void cancelSharedToUserFileWithoutCsrf() throws Exception {
        this.mockMvc.perform(delete("/shared"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void cancelSharedToUserFileWhenFileNotFound() throws Exception {
        this.mockMvc.perform(delete("/shared")
                    .param("cloudFileName", "notExistingFileName").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shared"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.sharedToUserFileController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void cancelSharedToUserFileStatusOk() throws Exception {
        this.mockMvc.perform(delete("/shared")
                    .param("cloudFileName", "test_file_cloud_name6").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shared"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("success.sharedToUserFileController.deleteFile", null, Locale.ENGLISH)));
    }
}
