package ga.codesplash.scrumplex.sprummlbot.plugins;

/**
 * This exception should be thrown if a plugin is not compatible or broken.
 */
public class PluginLoadException extends Exception {

    public PluginLoadException(String cause) {
        super(cause);
    }

}
