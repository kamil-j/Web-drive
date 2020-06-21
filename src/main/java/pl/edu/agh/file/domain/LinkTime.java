package pl.edu.agh.file.domain;

import lombok.Getter;

/**
 * Created by Kamil Jureczka on 2017-08-27.
 */

@Getter
public enum LinkTime {
    HOUR1(1), HOUR3(3), HOUR6(6), HOUR12(12), HOUR24(24);

    private long amountOfHours;

    LinkTime(long amountOfHours) {
        this.amountOfHours = amountOfHours;
    }
}
