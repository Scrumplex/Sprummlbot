package ga.codesplash.scrumplex.sprummlbot.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Random;


/**
 * This class has some easy to use methods.
 * It is only for effective use.
 */
public class EasyMethods {

    /**
     * Checks if String is Integer
     *
     * @param str String
     * @return True if it is an Integer
     */
    public static boolean isInteger(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * This method converts http links to nromal links.
     * Example: Hello%20Alex -> Hello Alex
     *
     * @param query The string which will be converted
     * @return Returns the converted string
     */
    public static String decodeHTTPString(String query) {
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            /**
             * Won't happen :O
             */
            Exceptions.handle(e, "Encoding not supported!");
        }
        return query;
    }

    /**
     * Generates a random string without speical chars
     *
     * @param len Length of the generated string
     * @return returns generated string
     */
    public static String randomString(int len) {
        final String alphabeticRange = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(alphabeticRange.charAt(random.nextInt(alphabeticRange.length())));
        return sb.toString();
    }

    /**
     * This method returns the full stacktrace of an exception
     * @param e Exception, which will be converted
     * @return Full Stacktrace
     */
    public static String convertExceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
