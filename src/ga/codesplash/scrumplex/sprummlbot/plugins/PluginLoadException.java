package ga.codesplash.scrumplex.sprummlbot.plugins;

/**
 * This exception should be thrown if a plugin is not compatible or broken.
 */
public class PluginLoadException extends Exception {

	private static final long serialVersionUID = 1L;

	public PluginLoadException(String cause) {
		super(cause);
	}

}
