package net.scrumplex.sprummlbot.plugins;

class PluginLoadException extends Exception {

    PluginLoadException(String cause) {
        super(cause);
    }
    PluginLoadException(String cause, Exception ex) {
        super(cause, ex);
    }

}
