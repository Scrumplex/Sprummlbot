package ga.codesplash.scrumplex.sprummlbot.configurations;

public enum Language {

    DE("de"), EN("en");

    private final String lang;

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