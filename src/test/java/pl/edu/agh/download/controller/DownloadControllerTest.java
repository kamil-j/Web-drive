package pl.edu.agh.download.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Kamil Jureczka on 2017-09-24.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Test
    public void serveNotExistingFile() throws Exception {
        this.mockMvc.perform(get("/download/test123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message",
                messageSource.getMessage("exception.downloadController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    public void serveExistingFileButLinkExpired() throws Exception {
        this.mockMvc.perform(get("/download/test_file_cloud_name3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.downloadController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    public void serveFile() throws Exception {
        this.mockMvc.perform(get("/download/test_file_cloud_name1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"));
    }

    @Test
    public void getDownloadPageNotExistingFile() throws Exception {
        this.mockMvc.perform(get("/download/page/test123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.downloadController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    public void getDownloadPageExistingFileButLinkExpired() throws Exception {
        this.mockMvc.perform(get("/download/page/test_file_cloud_name3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message",
                        messageSource.getMessage("exception.downloadController.fileNotFound", null, Locale.ENGLISH)));
    }

    @Test
    public void getDownloadPageStatusOk() throws Exception {
        this.mockMvc.perform(get("/download/page/test_file_cloud_name1"))
                .andExpect(status().isOk())
                .andExpect(view().name("download/download"))
                .andExpect(model().attributeExists("file"));
    }
}
