package ga.codesplash.scrumplex.sprummlbot.plugins;

import java.io.File;

/**
 * This class is a easier to use SprummlPlugin class
 * This class stores all information for a plugin.
 */
public class Plugin {

    private SprummlPlugin plugin = null;
    private String name = "";
    private boolean listensCommands = false;
    private Thread pluginThread = null;
    private String author = "";
    private String version = "";
    private File file = null;

    public Plugin(SprummlPlugin plugin, File file, String name, boolean listensCommands, Thread pluginThread, String author, String version) {
        this.file = file;
        this.plugin = plugin;
        this.name = name;
        this.listensCommands = listensCommands;
        this.pluginThread = pluginThread;
        this.author = author;
        this.version = version;
    }

    public boolean isListeningCommands() {
        return listensCommands;
    }

    public Thread getPluginThread() {
        return pluginThread;
    }

    public SprummlPlugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }
}
