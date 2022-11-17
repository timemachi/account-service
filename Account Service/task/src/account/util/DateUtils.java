package account.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static LocalDate parseDateFromMonthYear(String rawString) {
        String input = rawString.trim();
        if (!input.matches("^([1-9]|0[1-9]|1[012])-\\d{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date format: " + rawString);
        }
        String[] dateArray = input.split("-");
        String dateString = String.format("%s-%s-01", dateArray[1], dateArray[0]);
        LocalDate date;
        try {
            date = LocalDate.parse(dateString);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date format: " + rawString);
        }
        return date;
    }

    public static String formatDateAsMonthYear(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-yyyy");
        return date.format(formatter);
    }
}
