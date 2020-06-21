package pl.edu.agh.share.domain;

import lombok.Getter;

/**
 * Created by Kamil Jureczka on 2017-08-24.
 */

@Getter
public enum ShareTime {
    HOUR1(1), HOUR3(3), HOUR12(12), HOUR24(24), WEEK(168), MONTH(720), YEAR(8760), NO_LIMIT(-1);

    private long amountOfHours;

    ShareTime(long amountOfHours) {
        this.amountOfHours = amountOfHours;
    }
}
