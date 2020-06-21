package pl.edu.agh.user.domain.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kamil Jureczka on 2017-08-24.
 */

@Getter
@Setter
@NoArgsConstructor
public class UserSearchResponse {
    Boolean isFound = false;
    Boolean isError = false;
    String message;
    String email;

    public UserSearchResponse(boolean isError, String message){
        this.isError = isError;
        this.message = message;
    }

    public UserSearchResponse(String email, String message){
        this.isFound = true;
        this.message = message;
        this.email = email;
    }

    public UserSearchResponse(String message) {
        this.message = message;
    }
}
