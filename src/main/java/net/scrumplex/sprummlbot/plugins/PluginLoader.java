package net.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.events.EventManager;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.ChatCommand;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {

    private final PluginManager pluginManager;

    public PluginLoader(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
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

            PluginInfo info = new PluginInfo(pluginName, pluginVersion, pluginAuthors, fileToLoad, new File("plugins", pluginName));

            if (pluginManager.isLoaded(pluginName))
                throw new PluginLoadException("Plugin already loaded!");

            System.out.println("[Plugins][" + info.getPluginName() + "] Enabling plugin " + info.getPluginName() + " version " + info.getPluginVersion() + " by " + info.getPluginAuthor() + "...");
            ClassLoader loader = URLClassLoader.newInstance(new URL[]{fileToLoad.toURI().toURL()},
                    getClass().getClassLoader());
            Class<?> rawClass = loader.loadClass(mainClassPath);
            SprummlTasker tasker = new SprummlTasker();
            SprummlbotPlugin sprummlPlugin = (SprummlbotPlugin) rawClass.newInstance();
            try {
                if (rawClass.getMethod("onEvent", SprummlEventType.class, BaseEvent.class).getDeclaringClass() == rawClass)
                    System.err.println("[Plugins][" + info.getPluginName() + "] The method onEvent is deprecated! Please use getEventManager().addEventListener() instead!");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            EventManager eventManager = new EventManager(sprummlPlugin);
            sprummlPlugin.initialize(tasker, info, eventManager);
            System.out.println("[Plugins][" + info.getPluginName() + "] Plugin " + pluginName + " successfully enabled!");
            pluginManager.plugins.put(fileToLoad, sprummlPlugin);
            pluginManager.classLoaders.put(sprummlPlugin, loader);
            return true;
        } catch (Throwable e) {
            Exceptions.handlePluginError(e, fileToLoad);
        }
        return false;
    }

    public boolean unload(File fileToUnLoad) {
        try {
            if (pluginManager.plugins.containsKey(fileToUnLoad)) {
                SprummlbotPlugin plugin = pluginManager.getPluginByFile(fileToUnLoad);
                Collection<ChatCommand> commands = Vars.COMMAND_MGR.getCommands().values();
                for (ChatCommand cmd : commands) {
                    SprummlbotPlugin commandPlugin = cmd.getCommandPlugin();
                    if (commandPlugin == null)
                        continue;

                    if (commandPlugin.equals(plugin))
                        Vars.COMMAND_MGR.disableCommand(cmd, true);
                }
                plugin.unload();
                pluginManager.plugins.remove(fileToUnLoad);
                ClassLoader loader = pluginManager.classLoaders.remove(plugin);
                if (loader != null) {
                    URLClassLoader urlLoader = (URLClassLoader) loader;
                    urlLoader.close();
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
        for (SprummlbotPlugin plugin : pluginManager.getPlugins()) {
            unload(plugin.getPluginInfo().getPluginFile());
        }
    }
}
