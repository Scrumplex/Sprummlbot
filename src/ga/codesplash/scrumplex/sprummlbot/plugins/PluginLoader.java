package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.sun.istack.internal.NotNull;
import ga.codesplash.scrumplex.sprummlbot.Commands;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.tools.Exceptions;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class loads plugins.
 */
public class PluginLoader {

    private PluginManager pluginManager = null;

    public PluginLoader(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @NotNull
    public boolean load(File fileToLoad) {
        try {
            JarFile jarFile = new JarFile(fileToLoad);
            JarEntry pluginIniEntry = jarFile.getJarEntry("plugin.ini");
            InputStream input = jarFile.getInputStream(pluginIniEntry);
            String mainClassPath;
            Ini ini = new Ini(input);
            if (!ini.containsKey("Plugin")) {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), fileToLoad);
                jarFile.close();
                return false;
            }
            Section pluginSection = ini.get("Plugin");
            if (pluginSection.containsKey("main")) {
                mainClassPath = pluginSection.get("main");
            } else {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), fileToLoad);
                jarFile.close();
                return false;
            }

            String pluginName = fileToLoad.getName();
            String pluginAuthor = "undefined";
            String pluginVersion = "1.0.0";
            if (ini.containsKey("Information")) {
                pluginSection = ini.get("Information");
                if (pluginSection.containsKey("name"))
                    pluginName = pluginSection.get("name").replace(" ", "_");
                if (pluginSection.containsKey("author"))
                    pluginAuthor = pluginSection.get("author");
                if (pluginSection.containsKey("version"))
                    pluginVersion = pluginSection.get("version");
            }

            System.out.println("[Plugins] Loading plugin: " + pluginName + " by " + pluginAuthor + " version " + pluginVersion + "...");
            if (pluginManager.isLoaded(pluginName)) {
                System.out.println("[Plugins] The plugin " + fileToLoad.getName() + " could not be loaded. Because a plugin with that name is already running!");
                return false;
            }

            ClassLoader loader = URLClassLoader.newInstance(new URL[]{fileToLoad.toURI().toURL()},
                    getClass().getClassLoader());
            final SprummlPlugin sprummlPlugin = (SprummlPlugin) loader.loadClass(mainClassPath).newInstance();
            if (!sprummlPlugin.init(Vars.VERSION)) {
                unLoad(fileToLoad);
            }

            boolean commandHandler = false;
            if (ini.containsKey("Commands")) {
                commandHandler = true;
                pluginSection = ini.get("Commands");
                for (String command : pluginSection.keySet()) {
                    Commands.registerCommand(command, pluginSection.get(command, boolean.class));
                }
            }

            System.out.println("[Plugins] The plugin " + pluginName + " was loaded successfully and is running!");

            pluginManager.plugins.put(fileToLoad, new Plugin(sprummlPlugin, fileToLoad, pluginName, commandHandler, pluginAuthor, pluginVersion));
            jarFile.close();
            return true;
        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            Exceptions.handlePluginError(e, fileToLoad);
        }
        return false;
    }

    @NotNull
    public void unLoad(File fileToUnLoad) {
        if (pluginManager.plugins.containsKey(fileToUnLoad)) {
            Plugin plugin = pluginManager.getPluginByFile(fileToUnLoad);
            plugin.getPlugin().end();
            pluginManager.plugins.remove(fileToUnLoad);
        }
    }

    @NotNull
    public void loadAll() {
        File plugins = new File("plugins");
        if (!plugins.exists()) {
            if (!plugins.mkdir()) {
                System.out.println("Could not create plugins folder.");
                return;
            }
        }
        File[] files = plugins.listFiles();

        if (files == null) {
            System.out.println("Could not load plugins folder.");
            return;
        }

        for (File f : files)
            if (f.isFile() && f.getName().endsWith(".jar"))
                load(f);
    }

    @NotNull
    public void unLoadAll() {
        for (Plugin plugin : pluginManager.getPlugins()) {
            unLoad(plugin.getFile());
        }
    }
}
