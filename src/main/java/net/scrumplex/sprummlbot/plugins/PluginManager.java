package net.scrumplex.sprummlbot.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {

    final Map<File, SprummlbotPlugin> plugins = new HashMap<>();
    final Map<SprummlbotPlugin, ClassLoader> classLoaders = new HashMap<>();

    public List<SprummlbotPlugin> getPlugins() {
        return new ArrayList<>(plugins.values());
    }

    public SprummlbotPlugin getPluginByFile(File pluginJarFile) {
        return plugins.get(pluginJarFile);
    }

    public SprummlbotPlugin getPluginByName(String pluginName) {
        for (SprummlbotPlugin plugin : getPlugins())
            if (plugin.getPluginInfo().getPluginName().equalsIgnoreCase(pluginName))
                return plugin;
        return null;
    }

    public boolean isLoaded(SprummlbotPlugin plugin) {
        return plugins.containsValue(plugin);
    }

    public boolean isLoaded(String pluginName) {
        return (getPluginByName(pluginName) != null);
    }

}
