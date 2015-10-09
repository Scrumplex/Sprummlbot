package ga.scrumplex.ml.sprum.sprummlbot.stuff;

public enum Language {

	// TODO: More Languages!
	DE("de"), EN("en");

	String lang;

	Language(String str) {
		lang = str;
	}

	public String getID() {
		return lang;
	}

	public static Language fromID(String id) {
		if (id.equalsIgnoreCase("de")) {
			return DE;
		} else if (id.equalsIgnoreCase("en")) {
			return EN;
		} else {
			return null;
		}
	}
}
