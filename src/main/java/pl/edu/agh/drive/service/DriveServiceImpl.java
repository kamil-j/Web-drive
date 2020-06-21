package pl.edu.agh.drive.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.drive.exception.AmazonServiceException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kamil Jureczka on 2017-08-03.
 */
@Service
@Profile("PROD")
public class DriveServiceImpl implements DriveService {

    @Value("${drive.bucketName}")
    private String bucketName;

    @Value("${drive.accessKey}")
    private String accessKey;

    @Value("${drive.secretKey}")
    private String secretKey;

    private AmazonS3 getAmazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }

    @Override
    public S3Object getObject(String cloudObjectName) {
        AmazonS3 s3Client = getAmazonS3();

        try {
            return s3Client.getObject(new GetObjectRequest(bucketName, cloudObjectName));
        } catch (Throwable e) {
            throw new AmazonServiceException("Cannot get object with cloud name: "
                    + cloudObjectName + " from Amazon service!", e);
        }
    }

    @Override
    public PutObjectResult putObject(String cloudObjectName, InputStream dataStream, ObjectMetadata metadata) {
        AmazonS3 s3Client = getAmazonS3();
        try {
            return s3Client.putObject(new PutObjectRequest(bucketName, cloudObjectName, dataStream, metadata));
        } catch (Throwable e) {
            throw new AmazonServiceException("Cannot put object with cloud name: "
                    + cloudObjectName + " to Amazon service!", e);
        }
    }

    @Override
    public PutObjectResult putFile(MultipartFile file, String cloudFileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        InputStream dataStream = new ByteArrayInputStream(file.getBytes());

        try {
            return putObject(cloudFileName, dataStream, metadata);
        } catch (Throwable e) {
            throw new AmazonServiceException("Cannot put file with cloud name: "
                    + cloudFileName + " to Amazon service!", e);
        }
    }

    @Override
    public void deleteFile(String cloudFileName) throws IOException {
        AmazonS3 s3Client = getAmazonS3();

        try {
            s3Client.deleteObject(bucketName, cloudFileName);
        } catch (Throwable e) {
            throw new AmazonServiceException("Cannot delete file with cloud name: "
                    + cloudFileName + " from Amazon service!", e);
        }
    }
}
