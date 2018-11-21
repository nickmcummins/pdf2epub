package net.nickmcummins.pdf;

public class StringUtils {
    public static String removeWhitespaces(String string) {
        return string.replaceAll("\\s+","");
    }
}
