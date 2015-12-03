package ga.codesplash.scrumplex.sprummlbot.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClasspathLoader {

    private List<File> libraries = new ArrayList<>();

    public boolean load(File fileToLoad) {
        try {
            System.out.println("[Libraries] Trying to load library " + fileToLoad.getName() + ".");
            URLClassLoader.newInstance(new URL[]{fileToLoad.toURI().toURL()},
                    getClass().getClassLoader());
            System.out.println("[Libraries] Library " + fileToLoad.getName() + " was successfully loaded.");
        } catch (MalformedURLException ignored) {
            System.out.println("[Libraries] Library " + fileToLoad.getName() + " could not be loaded!.");
        }
        return false;
    }

    public void loadAll() {
        File plugins = new File("libs");
        if (!plugins.exists()) {
            if (!plugins.mkdir()) {
                System.out.println("Could not create libs folder.");
                return;
            }
        }
        File[] files = plugins.listFiles();

        if (files == null) {
            System.out.println("Could not load libs folder.");
            return;
        }

        for (File f : files)
            if (f.isFile() && f.getName().endsWith(".jar"))
                load(f);
    }
}
