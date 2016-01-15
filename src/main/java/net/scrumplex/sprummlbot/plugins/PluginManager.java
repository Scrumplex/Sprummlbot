package net.scrumplex.sprummlbot.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a plugin manager.
 * After plugins got loaded in PluginLoader class they are stored in this class and can be used from here.
 */
public class PluginManager {

    final Map<File, SprummlbotPlugin> plugins = new HashMap<>();

    /**
     * Returns a list of all active plugins
     *
     * @return Returns all loaded Plugin s
     */
    public List<SprummlbotPlugin> getPlugins() {
        return new ArrayList<>(plugins.values());
    }

    /**
     * Returns a Plugin by a File
     *
     * @param pluginJarFile File of tha Plugin
     * @return Returns a Plugin
     */
    public SprummlbotPlugin getPluginByFile(File pluginJarFile) {
        return plugins.get(pluginJarFile);
    }

    /**
     * Returns a Plugin by a name
     *
     * @param pluginName Id of tha plugin, which will be returned
     * @return Returns a Plugin or null
     */
    public SprummlbotPlugin getPluginByName(String pluginName) {
        for (SprummlbotPlugin plugin : getPlugins())
            if (plugin.getPluginInfo().getPluginName().equalsIgnoreCase(pluginName))
                return plugin;
        return null;
    }

    /**
     * Returns if Plugin is loaded or not
     *
     * @param plugin Plugin, which will be checked
     * @return Returns if plugin is loaded
     */
    public boolean isLoaded(SprummlbotPlugin plugin) {
        return plugins.containsValue(plugin);
    }

    /**
     * Returns if Plugin is loaded or not
     *
     * @param pluginName Plugin, which will be checked
     * @return Returns if plugin is loaded
     */
    public boolean isLoaded(String pluginName) {
        return (getPluginByName(pluginName) != null);
    }

}
