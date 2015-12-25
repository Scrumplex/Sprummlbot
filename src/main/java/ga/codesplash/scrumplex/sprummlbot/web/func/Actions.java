package ga.codesplash.scrumplex.sprummlbot.web.func;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.web.manage.Site_ban;
import ga.codesplash.scrumplex.sprummlbot.web.manage.Site_clearaccounts;
import ga.codesplash.scrumplex.sprummlbot.web.manage.Site_kick;
import ga.codesplash.scrumplex.sprummlbot.web.manage.Site_unban;

public class Actions {

    public static String shutdown() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                System.exit(0);
            }
        });
        t.start();
        return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_shutdown().content;
    }

    public static String kick(int cid, String msg) {
        try {
            return new Site_kick(Vars.API.kickClientFromServer(msg, cid).get()).content;
        } catch (InterruptedException e) {
            return new Site_kick(false).content;
        }
    }

    public static String ban(int cid, String msg, int time) {
        CommandFuture<Integer[]> ban = Vars.API.banClient(cid, time, msg);
        try {
            ban.get();
        } catch (InterruptedException e) {
            return new Site_ban(false).content;
        }
        return new Site_ban(ban.isFailed()).content;
    }

    public static String unban(int id) {
        try {
            return new Site_unban(Vars.API.deleteBan(id).get()).content;
        } catch (InterruptedException e) {
            return new Site_unban(false).content;
        }
    }

    public static String clearAccounts() {
        Vars.AVAILABLE_LOGINS.clear();
        return new Site_clearaccounts().content;
    }

    public static String poke(int userid, String msg) {
        Vars.API.pokeClient(userid, msg);
        return new ga.codesplash.scrumplex.sprummlbot.web.Site_index().content;
    }

    public static String sendpriv(int userid, String msg) {
        Vars.API.sendPrivateMessage(userid, msg);
        return new ga.codesplash.scrumplex.sprummlbot.web.Site_index().content;
    }
}
