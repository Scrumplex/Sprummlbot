package ga.codesplash.scrumplex.sprummlbot;

import java.util.Scanner;

/**
 * This class reads console input
 */
class Console {

    /**
     * Starts Console Thread
     */
    public static void runReadThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String cmd;
                Scanner txt = new Scanner(System.in);
                while (txt.hasNextLine()) {
                    cmd = txt.nextLine();
                    Commands.handle(cmd, null);
                }
                txt.close();
            }
        });
        t.start();
    }
}
