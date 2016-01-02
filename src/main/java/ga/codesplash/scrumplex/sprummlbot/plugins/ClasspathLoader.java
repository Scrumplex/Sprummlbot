package ga.codesplash.scrumplex.sprummlbot.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ClasspathLoader {

    /**
     * This method loads jar files into classpath (e.g. libraries)
     *
     * @param fileToLoad The jar file, which will be loaded to classpath
     * @return returns if the loading was successful
     */
    public boolean load(File fileToLoad) {
        try {
            System.out.println("[Libraries] Trying to load library " + fileToLoad.getName() + ".");
            URLClassLoader.newInstance(new URL[]{fileToLoad.toURI().toURL()},
                    getClass().getClassLoader());
            System.out.println("[Libraries] Library " + fileToLoad.getName() + " was successfully loaded.");
            return true;
        } catch (MalformedURLException ignored) {
            System.out.println("[Libraries] Library " + fileToLoad.getName() + " could not be loaded!.");
        }
        return false;
    }
}
