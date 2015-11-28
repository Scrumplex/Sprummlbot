package ga.codesplash.scrumplex.sprummlbot.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * This class has some easy to use methods.
 * It is only for effective use.
 */
public class EasyMethods {

	/**
	 * Checks if String is Integer
	 * @param str
	 * String
	 * @return
	 * True if it is an Integer
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
     * @param query
     * The string which will be converted
     * @return
     * Retruns the converted string
     */
    public static String decodeHTTPString(String query) {
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Exceptions.handle(e, "Encoding not supported!");
        }
        return query;
    }

}
