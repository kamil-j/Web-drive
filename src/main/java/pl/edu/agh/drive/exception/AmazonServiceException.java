package pl.edu.agh.drive.exception;

public class AmazonServiceException extends RuntimeException {
    public AmazonServiceException(String message) {
        super(message);
    }

    public AmazonServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
