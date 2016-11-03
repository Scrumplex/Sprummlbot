package net.scrumplex.sprummlbot.plugins;

import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.plugins.events.EventManager;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.ChatCommand;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager {


    private final Map<File, SprummlbotPlugin> plugins = new HashMap<>();
    private final Map<SprummlbotPlugin, PluginClassLoader> classLoaders = new HashMap<>();
    private final Sprummlbot sprummlbot;

    public PluginManager(Sprummlbot sprummlbot) {
        this.sprummlbot = sprummlbot;
    }

    public boolean load(File fileToLoad) {
        try {
            System.out.println("[Plugins] Loading plugin " + fileToLoad.getName() + "...");
            JarFile jarFile = new JarFile(fileToLoad);
            JarEntry pluginIniEntry = jarFile.getJarEntry("plugin.ini");
            if (pluginIniEntry == null) {
                Exceptions.handlePluginError(new PluginLoadException("Ini file not found"), fileToLoad);
                jarFile.close();
                return false;
            }
            InputStream input = jarFile.getInputStream(pluginIniEntry);
            String mainClassPath;
            Ini ini = new Ini(input);
            jarFile.close();
            if (!ini.containsKey("Plugin")) {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), fileToLoad);
                jarFile.close();
                return false;
            }
            Section pluginSection = ini.get("Plugin");
            if (!pluginSection.containsKey("main")) {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), fileToLoad);
                return false;
            }
            mainClassPath = pluginSection.get("main");

            String pluginName = fileToLoad.getName();
            String pluginAuthors[] = {"undefined"};
            String pluginVersion = "1.0.0";
            if (ini.containsKey("Information")) {
                pluginSection = ini.get("Information");
                if (pluginSection.containsKey("name"))
                    pluginName = pluginSection.get("name").replace(" ", "_");
                if (pluginSection.containsKey("author"))
                    pluginAuthors[0] = pluginSection.get("author");
                else if (pluginSection.containsKey("authors"))
                    pluginAuthors = pluginSection.getAll("authors", String[].class);
                if (pluginSection.containsKey("version"))
                    pluginVersion = pluginSection.get("version");
            }

            PluginInfo info = new PluginInfo(pluginName, pluginVersion, pluginAuthors, fileToLoad, new File("plugins", pluginName), mainClassPath);

            if (isLoaded(pluginName))
                throw new PluginLoadException("Plugin already loaded!");

            System.out.println("[Plugins][" + info.getPluginName() + "] Enabling plugin " + info.getPluginName() + " version " + info.getPluginVersion() + " by " + info.getPluginAuthor() + "...");

            PluginClassLoader loader = new PluginClassLoader(getClass().getClassLoader(), info);
            SprummlbotPlugin sprummlPlugin = loader.getPlugin();

            SprummlTasker tasker = new SprummlTasker(sprummlPlugin);
            EventManager eventManager = new EventManager(sprummlPlugin);

            sprummlPlugin.initialize(loader, tasker, eventManager, info, sprummlbot);

            System.out.println("[Plugins][" + info.getPluginName() + "] Plugin " + pluginName + " successfully enabled!");
            plugins.put(fileToLoad, sprummlPlugin);
            classLoaders.put(sprummlPlugin, loader);
            return true;
        } catch (Throwable e) {
            Exceptions.handlePluginError(e, fileToLoad);
        }
        return false;
    }

    public boolean unload(File fileToUnLoad) {
        try {
            if (plugins.containsKey(fileToUnLoad)) {
                SprummlbotPlugin plugin = getPluginByFile(fileToUnLoad);
                Collection<ChatCommand> commands = sprummlbot.getCommandManager().getCommands().values();
                for (ChatCommand cmd : commands) {
                    SprummlbotPlugin commandPlugin = cmd.getCommandPlugin();
                    if (commandPlugin == null)
                        continue;

                    if (commandPlugin.equals(plugin))
                        sprummlbot.getCommandManager().unregisterCommand(cmd.getCommandName());
                }
                sprummlbot.getModuleManager().stopAllFromPlugin(plugin);
                plugin.unload();
                plugins.remove(fileToUnLoad);
                ClassLoader loader = classLoaders.remove(plugin);
                if (loader != null) {
                    PluginClassLoader pluginClassLoader = (PluginClassLoader) loader;
                    pluginClassLoader.unload();
                }
            }
            return true;
        } catch (Throwable e) {
            Exceptions.handlePluginError(e, fileToUnLoad);
        }
        return false;
    }

    public void loadAll() {
        System.out.println("[Plugins] Loading plugins...");
        File plugins = new File("plugins");
        if (!plugins.exists()) {
            if (!plugins.mkdir()) {
                System.err.println("Could not create plugins folder.");
                return;
            }
        }
        File[] files = plugins.listFiles();

        if (files == null) {
            System.err.println("Could not load plugins folder.");
            return;
        }

        for (File f : files)
            if (f.isFile() && f.getName().endsWith(".jar"))
                load(f);
    }

    public void unloadAll() {
        System.out.println("[Plugins] Unloading plugins...");
        for (SprummlbotPlugin plugin : getPlugins()) {
            unload(plugin.getPluginInfo().getPluginFile());
        }
    }

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
