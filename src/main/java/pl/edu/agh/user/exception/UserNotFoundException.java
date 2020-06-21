package pl.edu.agh.user.exception;

/**
 * Created by Kamil Jureczka on 2017-08-05.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String userName) {
        super("User: " + userName + " not found!");
    }

    public UserNotFoundException(String userName, Throwable cause) {
        super("User: " + userName + " not found!", cause);
    }
}
