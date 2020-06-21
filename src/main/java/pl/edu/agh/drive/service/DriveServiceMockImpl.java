package pl.edu.agh.drive.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kamil Jureczka on 2017-08-03.
 */
@Service
@Profile({"DEV", "TEST"})
public class DriveServiceMockImpl implements DriveService {

    @Value("TEST_BUCKET")
    private String bucketName;

    @Override
    public S3Object getObject(String cloudObjectName) {
        S3Object testObject = new S3Object();
        testObject.setBucketName(bucketName);
        testObject.setKey(cloudObjectName);
        testObject.setObjectContent(new ByteArrayInputStream("mock_file".getBytes()));

        return testObject;
    }

    @Override
    public PutObjectResult putObject(String cloudObjectName, InputStream dataStream, ObjectMetadata metadata) {
        return new PutObjectResult();
    }

    @Override
    public PutObjectResult putFile(MultipartFile file, String cloudFileName) throws IOException {
        return new PutObjectResult();
    }

    @Override
    public void deleteFile(String cloudFileName) throws IOException {
        //There is nothing to do!
    }
}
