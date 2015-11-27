package ga.codesplash.scrumplex.sprummlbot.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager {

    public Map<File, Plugin> plugins = new HashMap<>();

    public List<Plugin> getPlugins() {
        return new ArrayList<>(plugins.values());
    }

    public Plugin getPluginByFile(File f) {
        return plugins.get(f);
    }

    public Plugin getPluginBySprummlPlugin(SprummlPlugin sprummlPlugin) {
        for (Plugin plugin : getPlugins()) {
            if (plugin.getPlugin().equals(sprummlPlugin))
                return plugin;
        }
        return null;
    }

    public Plugin getPluginByName(String pluginName) {
        for (Plugin plugin : getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(pluginName))
                return plugin;
        }
        return null;
    }

    public boolean isLoaded(Plugin plugin) {
        return plugins.containsValue(plugin);
    }

    public boolean isLoaded(String pluginName) {
        return (getPluginByName(pluginName) != null);
    }

}
