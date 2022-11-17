package account.util;

public class NumberUtils {
    public static String formatAsMoney(long sum) {
        return String.format("%d dollar(s) %d cent(s)", sum / 100, sum % 100);
    }
}
