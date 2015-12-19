package ga.codesplash.scrumplex.sprummlbot.plugins;

public class PluginInfo {

    private String pluginName;
    private String pluginVersion;
    private String[] pluginAuthors;

    public PluginInfo(String pluginName, String pluginVersion, String[] pluginAuthors) {
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
