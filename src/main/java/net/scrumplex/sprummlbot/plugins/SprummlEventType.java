package net.scrumplex.sprummlbot.plugins;

/**
 * This enum is for the event handling, to identify the events
 */
public enum SprummlEventType {
    MESSAGE,
    VIRTUAL_SERVER_EDIT,
    CLIENT_MOVE,
    CLIENT_LEAVE,
    CLIENT_JOIN,
    CHANNEL_EDIT,
    CHANNEL_DESC_CHANGED,
    CHANNEL_CREATE,
    CHANNEL_DELETE,
    CHANNEL_MOVE,
    CHANNEL_PW_CHANGED,
    PRIVILEGE_KEY_USED
}
