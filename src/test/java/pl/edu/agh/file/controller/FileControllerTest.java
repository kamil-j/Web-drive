package pl.edu.agh.file.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.file.domain.LinkTime;
import pl.edu.agh.file.domain.form.LinkGenerateRequest;
import pl.edu.agh.file.domain.form.LinkGenerateResponse;
import pl.edu.agh.file.repository.FileMetadataRepository;
import pl.edu.agh.file.service.FileMetadataService;
import pl.edu.agh.share.service.SharedFileMetadataService;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Kamil Jureczka on 2017-08-04.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileMetadataRepository fileMetadataRepository;

    @SpyBean
    private FileMetadataService fileMetadataService;

    @SpyBean
    private SharedFileMetadataService sharedFileMetadataService;

    @SpyBean
    private DriveService driveService;

    @Autowired
    private MessageSource messageSource;

    @Test
    @WithUserDetails("test@test")
    public void getAllFilesList() throws Exception {
        List<FileMetadata> testFileList = getTestFileList();
        Page<FileMetadata> testPage = new PageImpl<>(testFileList);
        given(fileMetadataRepository.findAllByUserOrderByNameAsc(any(), any(Pageable.class))).willReturn(testPage);

        this.mvc.perform(get("/file"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("files", Matchers.contains(testFileList.get(0), testFileList.get(1))));
    }

    private List<FileMetadata> getTestFileList() {
        FileMetadata firstFileMetadata = new FileMetadata();
        firstFileMetadata.setName("test1.txt");
        firstFileMetadata.setCloudName("testcloud1");
        firstFileMetadata.setSize(10L);

        FileMetadata secondFileMetadata = new FileMetadata();
        secondFileMetadata.setName("test2.txt");
        secondFileMetadata.setCloudName("testcloud2");
        secondFileMetadata.setSize(10L);

        return Arrays.asList(firstFileMetadata, secondFileMetadata);
    }

    @Test
    @WithUserDetails("test@test")
    public void serveFileWhenFileNotExist() throws Exception {
        this.mvc.perform(get("/file/test12343214231"))
                .andExpect(redirectedUrl("/file"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.fileController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void serveFileWhenAmazonNotAvailable() throws Exception {
        doReturn(new FileMetadata()).when(fileMetadataService).getFile(any(), eq("test12343214231"));
        given(driveService.getObject(any())).willThrow(new AmazonServiceException(""));

        this.mvc.perform(get("/file/test12343214231"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.global", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void serveFileStatusOk() throws Exception {
        FileMetadata file = new FileMetadata();
        file.setCloudName("test_file_cloud_name7");
        file.setContentType("text/plain");
        file.setSize(10L);
        file.setName("test5");

        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream("file".getBytes()));

        doReturn(file).when(fileMetadataService).getFile(any(), eq("test_file_cloud_name7"));
        doReturn(s3Object).when(driveService).getObject(eq("test_file_cloud_name7"));

        this.mvc.perform(get("/file/test_file_cloud_name7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain")))
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test5\""));
    }

    @Test
    @WithUserDetails("test@test")
    public void deleteFileWithoutCsrf() throws Exception {
        this.mvc.perform(delete("/file/test_file_cloud_name"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void deleteFileStatusOk() throws Exception {

        doNothing().when(sharedFileMetadataService).cancelAllUserShareFile(any(), eq("test_file_cloud_name"));
        doNothing().when(fileMetadataService).deleteFile(any(), eq("test_file_cloud_name"));

        this.mvc.perform(delete("/file")
                    .param("cloudFileName", "test_file_cloud_name").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/file"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("success.fileController.deleteFile", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void deleteFileWhenFileNotFound() throws Exception {
        doThrow(new FileNotFoundException()).when(fileMetadataService).deleteFile(any(), eq("test_file_cloud_name"));

        this.mvc.perform(delete("/file")
                    .param("cloudFileName", "test_file_cloud_name").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/file"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.fileController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void generateLinkToFileWithoutCsrf() throws Exception {
        this.mvc.perform(post("/file/link"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("test@test")
    public void generateLinkToNotExistingFile() throws Exception {
        LinkGenerateRequest request = new LinkGenerateRequest();
        request.setCloudFileName("test_file_cloud_name45363");
        request.setLinkTime(LinkTime.HOUR6);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        this.mvc.perform(post("/file/link")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(request)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/file"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.fileController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void generateLinkToFileWhenTimeNotProvided() throws Exception {
        LinkGenerateRequest request = new LinkGenerateRequest();
        request.setCloudFileName("test_cloud_file_name");

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        LinkGenerateResponse response = new LinkGenerateResponse(true,
                messageSource.getMessage("generateLink.fileController.fieldError", null, Locale.ENGLISH));

        this.mvc.perform(post("/file/link")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(request)).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    @WithUserDetails("test@test")
    public void generateLinkToFileWhenFileNotExists() throws Exception {
        LinkGenerateRequest request = new LinkGenerateRequest();
        request.setCloudFileName("test_cloud_file_name21345");
        request.setLinkTime(LinkTime.HOUR6);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        this.mvc.perform(post("/file/link")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(request)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/file"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.fileController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    @WithUserDetails("test@test")
    public void generateLinkToFile() throws Exception {
        LinkGenerateRequest request = new LinkGenerateRequest();
        request.setCloudFileName("test_file_cloud_name1");
        request.setLinkTime(LinkTime.HOUR6);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String link = "test_link";
        doReturn(link).when(fileMetadataService).generateLinkToFile(any(), eq(request.getCloudFileName()), eq(request.getLinkTime()));

        LinkGenerateResponse response = new LinkGenerateResponse(link,
                messageSource.getMessage("generateLink.fileController.successful", null, Locale.ENGLISH));

        this.mvc.perform(post("/file/link")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(request)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }
}
