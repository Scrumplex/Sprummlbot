package ga.codesplash.scrumplex.sprummlbot.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


import ga.codesplash.scrumplex.sprummlbot.SprummlbotLoader;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.configurations.ConfigException;

/**
 * This class handles Esceptions.
 * It will write them to a file named lasterror.log
 */
public class Exceptions {

    /**
     * Handles Exeptions
     *
     * @param e     Thrown exception
     * @param CAUSE Custom CAUSE message
     */
    public static void handle(Exception e, String CAUSE) {
        handle(e, CAUSE, true);
    }

    /**
     * Handles Exeptions
     *
     * @param e        Thrown exception
     * @param CAUSE    Custom CAUSE message
     * @param shutdown Defines if Bot should shutdown.
     */
    public static void handle(Exception e, String CAUSE, boolean shutdown) {
        if (Vars.DEBUG == 2) {
            e.printStackTrace();
            if (shutdown) {
                System.exit(1);
            }
            return;
        }
        File lasterror = new File("lasterror.log");
        System.out.println(CAUSE + " More information in " + lasterror.getAbsolutePath());
        if (!lasterror.exists()) {
            try {
                lasterror.createNewFile();
            } catch (IOException e1) {
                System.out.println("There was an problem while creating \"lasterror.log\" file! Printing Errors:");
                System.out.println("//MAIN ERROR://");
                e.printStackTrace();
                System.out.println("");
                System.out.println("//WRITE ERROR://");
                e1.printStackTrace();
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(lasterror.getAbsoluteFile());
        } catch (IOException e1) {
            System.out.println("There was an problem while creating \"lasterror.log\" file! Printing Errors:");
            System.out.println("//MAIN ERROR://");
            e.printStackTrace();
            System.out.println("");
            System.out.println("//WRITE ERROR://");
            e1.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            bw.write(sw.toString());
            bw.write("\n\nPlease send this error to the plugin developer with the config file!");
        } catch (IOException e1) {
            System.out.println("There was an problem while creating \"lasterror.log\" file! Printing Errors:");
            System.out.println("//MAIN ERROR://");
            e.printStackTrace();
            System.out.println("");
            System.out.println("//WRITE ERROR://");
            e1.printStackTrace();
        }
        try {
            bw.close();
        } catch (IOException e1) {
            System.out.println("There was an problem while creating \"lasterror.log\" file! Printing Errors:");
            System.out.println("//MAIN ERROR://");
            e.printStackTrace();
            System.out.println("");
            System.out.println("//WRITE ERROR://");
            e1.printStackTrace();
        }
        if (shutdown) {
            if (Vars.DEBUG == 1) {
                System.out.println("Waiting 3 seconds for shutdown. (disable with debug=0)");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            System.exit(1);
        }
    }

    public static void handlePluginError(Exception e, File jarFile) {
        if (Vars.DEBUG == 2) {
            e.printStackTrace();
            return;
        }
        File error = new File(jarFile.getAbsolutePath() + ".log");
        System.out.println("The plugin " + jarFile.getName() + " could not be loaded! An error occurred! More information in " + error.getAbsolutePath());
        if (!error.exists()) {
            try {
                error.createNewFile();
            } catch (IOException e1) {
                System.out.println("There was an problem while creating \"" + error.getName() + "\" file! Printing Errors:");
                System.out.println("//MAIN ERROR://");
                e.printStackTrace();
                System.out.println("");
                System.out.println("//WRITE ERROR://");
                e1.printStackTrace();
            }
        }
        FileWriter fw = null;
        try {
            fw = new FileWriter(error.getAbsoluteFile());
        } catch (IOException e1) {
            System.out.println("There was an problem while creating \"" + error.getName() + "\" file! Printing Errors:");
            System.out.println("//MAIN ERROR://");
            e.printStackTrace();
            System.out.println("");
            System.out.println("//WRITE ERROR://");
            e1.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            bw.write(sw.toString());
            if (e instanceof ConfigException) {
                bw.write("\nCaused by config!");
            }
            bw.write("\n\nPlease send this error to support with the config file!");
        } catch (IOException e1) {
            System.out.println("There was an problem while creating \"" + error.getName() + "\" file! Printing Errors:");
            System.out.println("//MAIN ERROR://");
            e.printStackTrace();
            System.out.println("");
            System.out.println("//WRITE ERROR://");
            e1.printStackTrace();
        }
        try {
            bw.close();
        } catch (IOException e1) {
            System.out.println("There was an problem while creating \"" + error.getName() + "\" file! Printing Errors:");
            System.out.println("//MAIN ERROR://");
            e.printStackTrace();
            System.out.println("");
            System.out.println("//WRITE ERROR://");
            e1.printStackTrace();
        }
        SprummlbotLoader.pl.unload(jarFile);
    }

}
