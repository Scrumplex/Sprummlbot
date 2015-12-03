package ga.codesplash.scrumplex.sprummlbot.plugins;

/**
 * This enum is for the event handling, to identify the events
 */
public enum SprummlEventType {
    MESSAGE("MESSAGE"),
    VIRTUAL_SERVER_EDIT("VIRTUAL_SERVER_EDIT"),
    CLIENT_MOVE("CLIENT_MOVE"),
    CLIENT_LEAVE("CLIENT_LEAVE"),
    CLIENT_JOIN("CLIENT_JOIN"),
    CHANNEL_EDIT("CHANNEL_EDIT"),
    CHANNEL_DESC_CHANGED("CHANNEL_DESC_CHANGED"),
    CHANNEL_CREATE("CHANNEL_CREATE"),
    CHANNEL_DELETE("CHANNEL_DELETE"),
    CHANNEL_MOVE("CHANNEL_MOVE"),
    CHANNEL_PW_CHANGED("CHANNEL_PW_CHANGED");

    private String name = "";

    SprummlEventType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
