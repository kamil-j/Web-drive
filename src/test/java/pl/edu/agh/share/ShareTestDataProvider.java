package pl.edu.agh.share;

import pl.edu.agh.file.domain.FileMetadata;
import pl.edu.agh.share.domain.SharedFileMetadata;
import pl.edu.agh.user.domain.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ShareTestDataProvider {
    public static List<SharedFileMetadata> getTestShareFiles() {
        SharedFileMetadata firstFile = new SharedFileMetadata();
        firstFile.setFile(new FileMetadata());
        firstFile.setUserTo(new User());
        firstFile.setUserFrom(new User());
        firstFile.setExpirationDate(LocalDateTime.now().plusYears(10));
        firstFile.getFile().setSize(10L);

        SharedFileMetadata secondFile = new SharedFileMetadata();
        secondFile.setFile(new FileMetadata());
        secondFile.setUserTo(new User());
        secondFile.setUserFrom(new User());
        secondFile.setExpirationDate(LocalDateTime.now().plusYears(10));
        secondFile.getFile().setSize(10L);

        return Arrays.asList(firstFile, secondFile);
    }
}
