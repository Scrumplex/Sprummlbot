package net.scrumplex.sprummlbot.plugins;

public class PluginInfo {

    private final String pluginName;
    private final String pluginVersion;
    private final String[] pluginAuthors;

    PluginInfo(String pluginName, String pluginVersion, String[] pluginAuthors) {
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.pluginAuthors = pluginAuthors;
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
}
