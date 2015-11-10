package ga.codesplash.scrumplex.sprummlbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import ga.codesplash.scrumplex.sprummlbot.Configurations.Messages;

public class Commands {

	public static ArrayList<String> DISABLED = new ArrayList<>();
	public static ArrayList<String> COMMANDS = new ArrayList<>();
	public static ArrayList<String> COMMANDSLIST = new ArrayList<>();

	public static void setup() {
		COMMANDSLIST.add("!help");
		COMMANDSLIST.add("!yt");
		COMMANDSLIST.add("!web");
		COMMANDSLIST.add("!skype");
		COMMANDSLIST.add("!ip");
		COMMANDSLIST.add("!support");
		COMMANDSLIST.add("!mute");
		COMMANDS.add("!help");
		COMMANDS.add("!yt");
		COMMANDS.add("!web");
		COMMANDS.add("!skype");
		COMMANDS.add("!ip");
		COMMANDS.add("!login");
		COMMANDS.add("!support");
		COMMANDS.add("!mute");
	}

	public static void setup(String[] disabled) {
		setup();
		for (String cmd : disabled) {
			Commands.DISABLED.add(cmd);
			COMMANDSLIST.remove(cmd);
			COMMANDS.remove(cmd);
		}
	}

	public static boolean handle(String cmd, Client c) {
		if (c == null) {
			if (cmd.equalsIgnoreCase("list")) {
				CONcommandLIST();
				return true;
			} else if (cmd.equalsIgnoreCase("stop")) {
				CONcommandSTOP();
				return true;
			}
			return false;

		} else {
			if (!DISABLED.contains(cmd)) {
				switch (cmd) {

				case "!help":
					return commandHelp(c);

				case "!yt":
					return commandYT(c);

				case "!web":
					return commandWEB(c);

				case "!skype":
					return commandSKYPE(c);

				case "!ip":
					return commandIP(c);

				case "!login":
					return commandLOGIN(c);

				case "!support":
					return commandSUPPORT(c);
					
				case "!mute":
					return commandMUTE(c);
				}
			}
			return false;
		}
	}

	private static boolean commandMUTE(Client c) {
		if(!Vars.BROADCAST_IGNORE.contains(c.getUniqueIdentifier())) {
			Vars.BROADCAST_IGNORE.add(c.getUniqueIdentifier());
			Vars.API.sendPrivateMessage(c.getId(), Messages.get("you-wont-be-notified"));
		} else {
			Vars.BROADCAST_IGNORE.remove(c.getUniqueIdentifier());
			Vars.API.sendPrivateMessage(c.getId(), Messages.get("you-will-be-notified"));
		}
		return true;
	}

	private static boolean commandSUPPORT(Client c) {
		Vars.API.moveClient(c.getId(), Vars.SUPPORTCHANNELID);
		return true;
	}

	public static boolean commandHelp(Client c) {
		Vars.API.sendPrivateMessage(c.getId(), Messages.get("help-dialog"));
		Vars.API.sendPrivateMessage(c.getId(),
				Messages.get("commandslist") + COMMANDSLIST.toString().replace("[", "").replace("]", ""));
		return true;
	}

	private static boolean commandYT(Client c) {
		Vars.API.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("youtube") + "]Youtube Channel[/URL]");
		return true;
	}

	public static boolean commandWEB(Client c) {
		Vars.API.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("website") + "]Website[/URL]");
		return true;
	}

	public static boolean commandSKYPE(Client c) {
		Vars.API.sendPrivateMessage(c.getId(), "Skype ID: " + Messages.get("skype"));
		return true;
	}

	public static boolean commandIP(Client c) {
		Vars.API.sendPrivateMessage(c.getId(), Messages.get("your-ip") + Vars.API.getClientInfo(c.getId()).getIp());
		return true;
	}

	public static boolean commandLOGIN(Client c) {
		if (Vars.LOGINABLE.contains(c.getUniqueIdentifier())) {
			if (Vars.PORT_WI == 0) {
				Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-disabled"));
			} else {
				String user = "user" + c.getDatabaseId();
				String pass = "0";
				if (WebGUILogins.AVAILABLE.containsKey(user)) {
					pass = WebGUILogins.AVAILABLE.get(user);
					Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user") + user);
					Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-pw") + pass);
					Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-login-is-temp"));
				} else {

					pass = randomString(10);
					Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user") + user);
					Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-pw") + pass);
					Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-login-is-temp"));
					WebGUILogins.AVAILABLE.put(user, pass);
				}
			}
			return true;
		}
		return false;
	}

	public static void CONcommandLIST() {
		List<String> clients = new ArrayList<String>();

		for (Client c : Vars.API.getClients()) {
			ClientInfo ci = Vars.API.getClientInfo(c.getId());
			clients.add("Name=" + c.getNickname() + ", IP=" + ci.getIp() + ", ID=" + c.getId() + ", UID="
					+ c.getUniqueIdentifier());
		}
		for (String c : clients) {
			Logger.out(c);
		}
	}

	public static void CONcommandSTOP() {
		System.exit(0);
	}

	final static String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static Random rnd = new Random();

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}
}
