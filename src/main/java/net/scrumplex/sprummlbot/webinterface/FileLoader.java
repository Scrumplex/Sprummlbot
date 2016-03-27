package net.scrumplex.sprummlbot.webinterface;

import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileLoader {

    private static final Map<String, byte[]> files = new HashMap<>();

    public static void unpackDefaults() throws IOException {
        File folder = new File("html");
        if (!folder.exists())
            folder.mkdir();
        Ini defaultIni = new Ini();
        Profile.Section sec = defaultIni.add("File Assignments");
        sec.put("manage-site", "manage.html");
        sec.put("css", "bootstrap.min.css");
        sec.put("js", "bootstrap.min.js");
        sec.put("jquery", "jquery.min.js");
        sec.put("login-site", "login.html");
        sec.put("logout-site", "logout.html");
        sec.put("logo", "logo-wide.png");
        sec.put("favicon", "favicon.ico");
        sec = defaultIni.add("Settings");
        sec.put("auto-update-html-files", true);

        Ini ini = new Config(new File(folder, "files.ini")).setDefaultConfig(defaultIni).compare().getIni();
        if (ini.get("Settings", "auto-update-html-files", boolean.class)) {
            InputStream in = FileLoader.class.getResourceAsStream("/manage.html");
            File file = new File(folder, "manage.html");
            EasyMethods.writeByteArrayToFile(file, EasyMethods.convertStreamToByteArray(in));

            in = FileLoader.class.getResourceAsStream("/bootstrap.min.css");
            file = new File(folder, "bootstrap.min.css");
            EasyMethods.writeByteArrayToFile(file, EasyMethods.convertStreamToByteArray(in));

            in = FileLoader.class.getResourceAsStream("/bootstrap.min.js");
            file = new File(folder, "bootstrap.min.js");
            EasyMethods.writeByteArrayToFile(file, EasyMethods.convertStreamToByteArray(in));

            in = FileLoader.class.getResourceAsStream("/jquery.min.js");
            file = new File(folder, "jquery.min.js");
            EasyMethods.writeByteArrayToFile(file, EasyMethods.convertStreamToByteArray(in));

            in = FileLoader.class.getResourceAsStream("/logout.html");
            file = new File(folder, "logout.html");
            EasyMethods.writeByteArrayToFile(file, EasyMethods.convertStreamToByteArray(in));

            in = FileLoader.class.getResourceAsStream("/logo-wide.png");
            file = new File(folder, "logo-wide.png");
            EasyMethods.writeByteArrayToFile(file, EasyMethods.convertStreamToByteArray(in));

            in = FileLoader.class.getResourceAsStream("/favicon.ico");
            file = new File(folder, "favicon.ico");
            EasyMethods.writeByteArrayToFile(file, EasyMethods.convertStreamToByteArray(in));

            in = FileLoader.class.getResourceAsStream("/login.html");
            file = new File(folder, "login.html");
            EasyMethods.writeByteArrayToFile(file, EasyMethods.convertStreamToByteArray(in));
        }

        Profile.Section read = ini.get("File Assignments");
        for (String fname : read.keySet()) {
            files.put(fname, EasyMethods.readByteArrayFromFile(new File(folder, read.get(fname))));
        }
    }

    static byte[] getFile(String file) {
        if (files.containsKey(file))
            return files.get(file);
        return new byte[1];
    }

}
