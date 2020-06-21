package pl.edu.agh.file.util;

/**
 * Created by Kamil Jureczka on 2017-08-21.
 */
public class FileUtil {

    private static final int MAX_DESCRIPTION_LENGTH = 20;

    public static boolean isNotValidDescription(String description) {
        return (description != null && !description.isEmpty()) && description.length() > MAX_DESCRIPTION_LENGTH;
    }
}
