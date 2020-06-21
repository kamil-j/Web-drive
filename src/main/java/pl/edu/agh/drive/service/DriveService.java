package pl.edu.agh.drive.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kamil Jureczka on 2017-08-03.
 */
public interface DriveService {
    S3Object getObject(String cloudObjectName);
    PutObjectResult putObject(String cloudObjectName, InputStream dataStream, ObjectMetadata metadata);
    PutObjectResult putFile(MultipartFile file, String cloudFileName) throws IOException;
    void deleteFile(String cloudFileName) throws IOException;
}
