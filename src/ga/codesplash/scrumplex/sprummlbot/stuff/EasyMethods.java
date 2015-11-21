package ga.codesplash.scrumplex.sprummlbot.stuff;

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

}
