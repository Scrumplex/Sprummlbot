package ga.codesplash.scrumplex.sprummlbot.plugins;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ga.codesplash.scrumplex.sprummlbot.Commands;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.tools.Exceptions;

/**
 * This class loads plugins.
 */
public class PluginLoader {

    private PluginManager pluginManager;

    /**
     * Creates new PluginLoader instance
     * @param pluginManager
     * Needs this for easier plugin handling
     */
    public PluginLoader(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    /**
     * Loads All plugins in /plugins/
     */
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
            if (f.isFile())
                load(f);
    }

    /**
     * Unloads All Plugins
     */
    public void unloadAll() {
        for (File plugin : pluginManager.plugins.keySet()) {
            unload(plugin);
        }
    }

    /**
     * Loads a plugin
     * @param pluginJarFile
     * Plugin, which will be loaded
     * @return
     * Returns if it was successfully loaded
     */
    public boolean load(final File pluginJarFile) {
        try {
            String path;
            JarFile jar = new JarFile(pluginJarFile);
            JarEntry entry = jar.getJarEntry("plugin.ini");
            InputStream input = jar.getInputStream(entry);
            Ini ini = new Ini(input);
            if (!ini.containsKey("Plugin")) {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), pluginJarFile);
                jar.close();
                return false;
            }
            Section sec = ini.get("Plugin");
            if (sec.containsKey("main")) {
                path = sec.get("main");
            } else {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), pluginJarFile);
                jar.close();
                return false;
            }
            String name = pluginJarFile.getName();
            String author = "UNKNOWN";
            String version = "UNKNOWN";
            if (ini.containsKey("Information")) {
                sec = ini.get("Information");
                if (sec.containsKey("name"))
                    name = sec.get("name").replace(" ", "_");
                if (sec.containsKey("author"))
                    author = sec.get("author");
                if (sec.containsKey("version"))
                    version = sec.get("version");
            }
            System.out.println("Loading " + name + " version " + version + " by " + author + "...");
            if (pluginManager.isLoaded(name)) {
                System.out.println("The plugin " + pluginJarFile.getName() + " could not be loaded. Because a plugin with that name is already running!");
                return false;
            }
            ClassLoader loader = URLClassLoader.newInstance(new URL[]{pluginJarFile.toURI().toURL()},
                    getClass().getClassLoader());
            final SprummlPlugin sprummlPlugin = (SprummlPlugin) loader.loadClass(path).newInstance();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!sprummlPlugin.init(Vars.VERSION)) {
                        unload(pluginJarFile);
                    }
                }
            });
            boolean commandHandler = false;
            if (ini.containsKey("Commands")) {
                commandHandler = true;
                sec = ini.get("Commands");
                for (String command : sec.keySet()) {
                    Commands.registerCommand(command, sec.get(command, boolean.class));
                }
            }
            System.out.println("[" + name + "] Running plugin!");
            pluginManager.plugins.put(pluginJarFile, new Plugin(sprummlPlugin, pluginJarFile, name, commandHandler, t, author, version));
            t.start();
            jar.close();
            return true;
        } catch (Exception ex) {
            Exceptions.handlePluginError(ex, pluginJarFile);
            return false;
        }
    }

    /**
     * Unloads a plugin
     *
     * @param pluginJarFile
     * Plugin, which will be unloaded
     * @return
     * Returns if it was successfully unloaded
     */
    public boolean unload(File pluginJarFile) {
        if (pluginManager.plugins.containsKey(pluginJarFile)) {
            Plugin plugin = pluginManager.getPluginByFile(pluginJarFile);
            plugin.getPlugin().end();
            plugin.getPluginThread().interrupt();
            pluginManager.plugins.remove(pluginJarFile);
            return true;
        }
        return false;
    }
}
