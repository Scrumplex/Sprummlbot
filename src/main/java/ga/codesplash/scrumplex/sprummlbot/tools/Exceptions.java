package ga.codesplash.scrumplex.sprummlbot.tools;

import ga.codesplash.scrumplex.sprummlbot.plugins.SprummlbotPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class handles Esceptions.
 * It will write them to a file named lasterror.log
 */
public class Exceptions {

    /**
     * Handles Exeptions
     *
     * @param throwable Thrown throwable
     * @param CAUSE     Custom CAUSE message
     */
    public static void handle(Throwable throwable, String CAUSE) {
        handle(throwable, CAUSE, true);
    }

    /**
     * Handles Exeptions
     *
     * @param throwable Thrown throwable
     * @param cause     Custom CAUSE message
     * @param shutdown  Defines if Bot should shutdown.
     */
    public static void handle(Throwable throwable, String cause, boolean shutdown) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d_M_Y__HH_mm_ss");
        File directory = new File("logs");
        directory.mkdir();
        File file = new File(directory, "error_" + sdf.format(cal.getTime()) + ".log");
        int i = 1;
        while (file.exists()) {
            file = new File(directory, "error_" + sdf.format(cal.getTime()) + "." + i + ".log");
            i++;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("Couldn't write to " + file.getName());
            e.printStackTrace();
            System.err.println("Printing main error...");
            throwable.printStackTrace();
        }

        System.err.println(cause + " More information in " + file.getAbsolutePath());

        StringBuilder contents = new StringBuilder();
        contents.append("Error Log from ").append(sdf.format(cal.getTime())).append(".\n");
        contents.append("Custom message: ").append(cause).append("\n");
        contents.append(EasyMethods.convertThrowableToString(throwable));
        contents.append("\n\nPlease contact support!");
        try {
            EasyMethods.writeToFile(file, contents.toString());
        } catch (IOException e) {
            System.err.println("Couldn't write to " + file.getName());
            e.printStackTrace();
            System.err.println("Printing main error...");
            throwable.printStackTrace();
        }

        if (shutdown) {
            System.exit(1);
        }
    }

    public static void handlePluginError(Throwable throwable, SprummlbotPlugin plugin) {
        handlePluginError(throwable, plugin.getPluginFile());
    }

    /**
     * This puts the stacktrace ofa throwable into a file under ./logs/plugins/pluginname_error_date.log
     * @param throwable Throwable which will be handled.
     * @param jarFile Plugin's file
     */
    public static void handlePluginError(Throwable throwable, File jarFile) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d_M_Y__HH_mm_ss");
        File directory = new File("logs", "plugins");
        directory.mkdirs();
        File file = new File(directory, jarFile.getName() + "_error_" + sdf.format(cal.getTime()) + ".log");
        int i = 1;
        while (file.exists()) {
            file = new File(directory, jarFile.getName() + "_error_" + sdf.format(cal.getTime()) + "." + i + ".log");
            i++;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("Couldn't write to " + file.getName());
            e.printStackTrace();
            System.err.println("Printing main error...");
            throwable.printStackTrace();
        }
        System.err.println("[Plugins] ERROR! More information in " + file.getAbsolutePath());

        StringBuilder contents = new StringBuilder();
        contents.append("Error Log from ").append(sdf.format(cal.getTime())).append(".\n");
        contents.append(EasyMethods.convertThrowableToString(throwable));
        contents.append("\n\nPlease contact support!");

        try {
            EasyMethods.writeToFile(file, contents.toString());
        } catch (IOException e) {
            System.err.println("Couldn't write to " + file.getName());
            e.printStackTrace();
            System.err.println("Printing main error...");
            throwable.printStackTrace();
        }
    }


}
