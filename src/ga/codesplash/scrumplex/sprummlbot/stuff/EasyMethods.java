package ga.codesplash.scrumplex.sprummlbot.stuff;

public class EasyMethods {

	public static boolean isInteger(String str) {
		try {
			Integer.valueOf(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

}
