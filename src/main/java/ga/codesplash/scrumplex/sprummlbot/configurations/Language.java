package ga.codesplash.scrumplex.sprummlbot.configurations;

public enum Language {

    DE_DE("de_DE"),
    EN_US("en_US"),
    PT_BR("pt_BR");

    private final String lang;

    Language(String str) {
        lang = str;
    }

    public static Language fromID(String id) {
        for (Language lang : Language.values()) {
            if (lang.getID().equalsIgnoreCase(id)) {
                return lang;
            }
        }
        return EN_US;
    }

    public String getID() {
        return lang;
    }
}
