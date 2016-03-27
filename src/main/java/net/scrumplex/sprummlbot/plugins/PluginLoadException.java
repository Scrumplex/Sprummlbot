package net.scrumplex.sprummlbot.plugins;

class PluginLoadException extends Exception {

    private static final long serialVersionUID = 1L;

    public PluginLoadException(String cause) {
        super(cause);
    }

}
