package net.scrumplex.sprummlbot.webinterface;

import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader {

    private static Ini ini = null;

    public static void unpackDefaults() throws IOException {
        File folder = new File("html");
        if (!folder.exists())
            folder.mkdir();

        InputStream in = FileLoader.class.getResourceAsStream("/manage.html");
        File file = new File(folder, "manage.html");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdir();
        FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(in));

        in = FileLoader.class.getResourceAsStream("/bootstrap.min.css");
        file = new File(folder, "bootstrap.min.css");
        FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(in));

        in = FileLoader.class.getResourceAsStream("/bootstrap.min.js");
        file = new File(folder, "bootstrap.min.js");
        FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(in));

        in = FileLoader.class.getResourceAsStream("/jquery.min.js");
        file = new File(folder, "jquery.min.js");
        FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(in));

        in = FileLoader.class.getResourceAsStream("/logout.html");
        file = new File(folder, "logout.html");
        FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(in));

        in = FileLoader.class.getResourceAsStream("/login.html");
        file = new File(folder, "login.html");
        FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(in));

        Ini defaultIni = new Ini();
        Profile.Section sec = defaultIni.add("File Assignments");
        sec.put("manage-site", "manage.html");
        sec.put("css", "bootstrap.min.css");
        sec.put("js", "bootstrap.min.js");
        sec.put("jquery", "jquery.min.js");
        sec.put("login-site", "login.html");
        sec.put("logout-site", "logout.html");

        new Config(new File(folder, "files.ini")).setDefaultConfig(defaultIni).compare();
    }

    public static String getFile(String file) throws IOException {
        File folder = new File("html");
        if(ini == null)
            ini = new Ini(new File(folder, "files.ini"));
        Profile.Section sec = ini.get("File Assignments");
        if (sec.containsKey(file))
            return EasyMethods.readFile(new File(folder, sec.get(file)));
        return "<h2 style=\"text-align: center;\">Error 404</h2>";
    }

}
