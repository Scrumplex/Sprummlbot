package net.scrumplex.sprummlbot.plugins;

class PluginInfo {

    private String pluginName;
    private String pluginVersion;
    private String[] pluginAuthors;

    /**
     * This class is for storing some information of an plugin
     *
     * @param pluginName    Name of the plugin
     * @param pluginVersion Version of the plugin
     * @param pluginAuthors Authors of the plugin
     */
    PluginInfo(String pluginName, String pluginVersion, String[] pluginAuthors) {
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
        this.pluginAuthors = pluginAuthors;
    }

    /**
     * @return Returns the Plugin Name defined in plugin.ini
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * @return Returns the Plugin Version defined in plugin.ini
     */
    public String getPluginVersion() {
        return pluginVersion;
    }

    /**
     * @return Returns the Plugin Authors defined in plugin.ini
     */
    public String[] getPluginAuthors() {
        return pluginAuthors;
    }

    /**
     * Same as getPluginAuthors()[0]
     *
     * @return Returns first author.
     */
    public String getPluginAuthor() {
        return getPluginAuthors()[0];
    }
}
