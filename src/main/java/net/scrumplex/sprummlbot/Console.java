package net.scrumplex.sprummlbot;

import net.scrumplex.sprummlbot.tools.EasyMethods;

import java.util.Scanner;

class Console {

    public static void runReadThread() {
        Vars.EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                String cmd;
                Scanner txt = new Scanner(System.in);
                while (txt.hasNextLine()) {
                    cmd = txt.nextLine();
                    if (cmd.equalsIgnoreCase("login")) {
                        String user = "admin";
                        String pass = EasyMethods.randomString();
                        if (Vars.AVAILABLE_LOGINS.containsKey(user)) {
                            pass = Vars.AVAILABLE_LOGINS.get(user);
                        } else {
                            Vars.AVAILABLE_LOGINS.put(user, pass);
                        }
                        System.out.println("Username: " + user + " Password: " + pass + " APIKey: " + EasyMethods.md5Hex(user + ":" + pass));
                    } else if (cmd.equalsIgnoreCase("stop")) {
                        System.exit(0);
                    }
                }
                txt.close();
            }
        });
    }
}
