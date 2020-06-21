package pl.edu.agh.file.domain.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kamil Jureczka on 2017-08-27.
 */

@Getter
@Setter
@NoArgsConstructor
public class LinkGenerateResponse {
    private Boolean isFound = false;
    private Boolean isError = false;
    private String message;
    private String link;

    public LinkGenerateResponse(boolean isError, String message){
        this.isError = isError;
        this.message = message;
    }

    public LinkGenerateResponse(String link, String message){
        this.isFound = true;
        this.message = message;
        this.link = link;
    }

    public LinkGenerateResponse(String message) {
        this.message = message;
    }
}
