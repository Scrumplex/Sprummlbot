package ga.codesplash.scrumplex.sprummlbot.plugins;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ga.codesplash.scrumplex.sprummlbot.Commands;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.stuff.Exceptions;

public class PluginLoader {

    public Map<File, SprummlPlugin> plugins = new HashMap<>();
    public Map<SprummlPlugin, Thread> pluginThreads = new HashMap<>();
    public List<SprummlPlugin> pluginCommands = new ArrayList<>();

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

    public void unloadAll() {
        for(File plugin : plugins.keySet()) {
            unload(plugin);
        }
    }

    public boolean load(File jarFile) {
        try {
            String path;
            JarFile jar = new JarFile(jarFile);
            JarEntry entry = jar.getJarEntry("plugin.ini");
            InputStream input = jar.getInputStream(entry);
            Ini ini = new Ini(input);
            if (!ini.containsKey("Plugin")) {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), jarFile);
                jar.close();
                return false;
            }
            Section sec = ini.get("Plugin");
            if (sec.containsKey("main")) {
                path = sec.get("main");
            } else {
                Exceptions.handlePluginError(new PluginLoadException("Ini file is incompatible"), jarFile);
                jar.close();
                return false;
            }
            String name = jarFile.getName();
            String author = "UNKNOWN";
            String version = "UNKNOWN";
            if (ini.containsKey("Information")) {
                sec = ini.get("Information");
                if (sec.containsKey("name"))
                    name = sec.get("name");
                if (sec.containsKey("author"))
                    author = sec.get("author");
                if (sec.containsKey("version"))
                    version = sec.get("version");
            }
            System.out.println("Loading " + name + " version " + version + " by " + author + "...");
            ClassLoader loader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()},
                    getClass().getClassLoader());
            final SprummlPlugin plugin = (SprummlPlugin) loader.loadClass(path).newInstance();
            plugins.put(jarFile, plugin);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    plugin.init(Vars.VERSION);
                }
            });
            if (ini.containsKey("Commands")) {
                pluginCommands.add(plugin);
                sec = ini.get("Commands");
                for (String command : sec.keySet()) {
                    Commands.registerCommand(command, sec.get(command, boolean.class));
                }
            }
            System.out.println("[" + name + "] Running plugin!");
            t.start();
            pluginThreads.put(plugin, t);
            jar.close();
            return true;
        } catch (Exception ex) {
            Exceptions.handlePluginError(ex, jarFile);
            return false;
        }
    }

    public boolean unload(File jarFile) {
        if (plugins.containsKey(jarFile)) {
            SprummlPlugin plugin = plugins.get(jarFile);
            plugin.end();
            pluginThreads.get(plugin).interrupt();
            plugins.remove(jarFile);
            return true;
        }
        return false;
    }
}
