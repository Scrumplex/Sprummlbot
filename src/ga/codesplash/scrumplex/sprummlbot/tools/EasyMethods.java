package ga.codesplash.scrumplex.sprummlbot.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

    public static String decodeHTTPString(String query) {
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Exceptions.handle(e, "Encoding not supported!");
        }
        return query;
    }

}
