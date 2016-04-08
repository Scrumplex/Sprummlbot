package net.scrumplex.sprummlbot.plugins;

import java.io.File;

public class PluginInfo {

    private final String pluginName;
    private final String pluginVersion;
    private final String[] pluginAuthors;
    private File pluginFile;
    private File pluginFolder;

    PluginInfo(String pluginName, String pluginVersion, String[] pluginAuthors, File pluginFile, File pluginFolder) {
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.pluginAuthors = pluginAuthors;
        this.pluginFile = pluginFile;
        this.pluginFolder = pluginFolder;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public String[] getPluginAuthors() {
        return pluginAuthors;
    }

    public String getPluginAuthor() {
        return getPluginAuthors()[0];
    }

    public File getPluginFile() {
        return pluginFile;
    }

    public File getPluginFolder() {
        return pluginFolder;
    }
}
