package net.scrumplex.sprummlbot.plugins;

import net.scrumplex.sprummlbot.tools.Exceptions;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {

    private PluginManager pluginManager = null;

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
            if (!ini.containsKey("Plugin")) {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), fileToLoad);
                jarFile.close();
                return false;
            }
            Section pluginSection = ini.get("Plugin");
            if (!pluginSection.containsKey("main")) {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), fileToLoad);
                jarFile.close();
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

            PluginInfo info = new PluginInfo(pluginName, pluginVersion, pluginAuthors);

            if (pluginManager.isLoaded(pluginName)) {
                jarFile.close();
                throw new PluginLoadException("Plugin already loaded");
            }

            System.out.println("[Plugins] Enabling plugin " + info.getPluginName() + " version " + info.getPluginVersion() + " by " + info.getPluginAuthor() + "...");
            ClassLoader loader = URLClassLoader.newInstance(new URL[]{fileToLoad.toURI().toURL()},
                    getClass().getClassLoader());
            Class<?> rawClass = loader.loadClass(mainClassPath);
            SprummlbotPlugin sprummlPlugin = (SprummlbotPlugin) rawClass.newInstance();
            sprummlPlugin.initialize(fileToLoad, new File("plugins", info.getPluginName()), info);

            System.out.println("[Plugins] Plugin " + pluginName + " successfully enabled!");
            pluginManager.plugins.put(fileToLoad, sprummlPlugin);
            pluginManager.classLoaders.put(sprummlPlugin, loader);
            jarFile.close();
            return true;
        } catch (Throwable e) {
            Exceptions.handlePluginError(e, fileToLoad);
        }
        return false;
    }

    public boolean unLoad(File fileToUnLoad) {
        try {
            if (pluginManager.plugins.containsKey(fileToUnLoad)) {
                SprummlbotPlugin plugin = pluginManager.getPluginByFile(fileToUnLoad);
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

    public void unLoadAll() {
        System.out.println("[Plugins] Unloading plugins...");
        for (SprummlbotPlugin plugin : pluginManager.getPlugins()) {
            unLoad(plugin.getPluginFile());
        }
    }
}
