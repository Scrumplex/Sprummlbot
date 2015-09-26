package ga.scrumplex.ml.sprum.sprummlbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

public class Commands extends Config {
	
	public static ArrayList<String> disabled = new ArrayList<>();
	public static ArrayList<String> commands = new ArrayList<>();
	public static ArrayList<String> commandslist = new ArrayList<>();
	
	public static void setup() {
		commandslist.add("!help");
		commandslist.add("!yt");
		commandslist.add("!web");
		commandslist.add("!skype");
		commandslist.add("!ip");
		commandslist.add("!support");
		commands.add("!help");
		commands.add("!yt");
		commands.add("!web");
		commands.add("!skype");
		commands.add("!ip");
		commands.add("!login");
		commands.add("!support");
	}
	public static void setup(String[] disabled) {
		setup();
		for(String cmd : disabled) {
			Commands.disabled.add(cmd);
			commandslist.remove(cmd);
			commands.remove(cmd);
		}
	}
	
	public static boolean handle(String cmd, Client c) {
		if(c == null) {
			
			if(cmd.equalsIgnoreCase("login")) {
				CONcommandLOGIN();
				return true;
			} else if(cmd.equalsIgnoreCase("list")) {
				CONcommandLIST();
				return true;
			} else if(cmd.equalsIgnoreCase("stop")) {
				CONcommandSTOP();
				return true;
			}
			return false;
			
		} else {
			if(!disabled.contains(cmd)) {
				switch(cmd) {
				
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
				}
			}
			return false;
		}
	}
	
	private static boolean commandSUPPORT(Client c) {
		api.moveClient(c.getId(), Config.supportchannelid);
		return true;
	}

	public static boolean commandHelp(Client c) {
		api.sendPrivateMessage(c.getId(), Messages.get("help-dialog"));
		api.sendPrivateMessage(c.getId(), Messages.get("commandslist") + commandslist.toString().replace("[", "").replace("]", ""));
		return true;
	}
	
	private static boolean commandYT(Client c) {
		api.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("youtube") + "]Youtube Channel[/URL]");
		return true;
	}
	
	public static boolean commandWEB(Client c) {
		api.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("website") + "]Website[/URL]");
		return true;
	}
	
	public static boolean commandSKYPE(Client c) {
		api.sendPrivateMessage(c.getId(), "Skype ID: " + Messages.get("skype"));
		return true;
	}
	
	public static boolean commandIP(Client c) {
		api.sendPrivateMessage(c.getId(), Messages.get("your-ip") + api.getClientInfo(c.getId()).getIp());
		return true;
	}
	
	public static boolean commandLOGIN(Client c) {
		if(Config.admins.contains(c.getUniqueIdentifier())) {
			if(Config.webport == 0) {
				api.sendPrivateMessage(c.getId(), Messages.get("webinterface-disabled"));
			} else {
				String user = "user" + c.getDatabaseId();
				String pass = "0";
				if(WebGUILogins.available.containsKey(user)) {
					pass = WebGUILogins.available.get(user);
					api.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user") + user);
					api.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-pw") + pass);
					api.sendPrivateMessage(c.getId(), Messages.get("webinterface-login-is-temp"));
				} else {

					pass = randomString(10);
					api.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user") + user);
					api.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-pw") + pass);
					api.sendPrivateMessage(c.getId(), Messages.get("webinterface-login-is-temp"));
					WebGUILogins.available.put(user, pass);
				}
			}
			return true;
		}
		return false;
	}
	
	public static void CONcommandLOGIN() {
			if(Config.webport == 0) {
				Logger.out("Webinterface is not activated!");
			} else {
				String user = "admin";
				String pass = "0";
				if(WebGUILogins.available.containsKey(user)) {
					pass = WebGUILogins.available.get(user);
					Logger.out("Credentials: u=" + user + ",p=" + pass);
				} else {
					pass = randomString(10);
					Logger.out("Credentials: u=" + user + ",p=" + pass);
					WebGUILogins.available.put(user, pass);
				}
			}
	}
	public static void CONcommandLIST() {
	    List<String> clients = new ArrayList<String>();
    
	    for(Client c : Config.api.getClients()) {
	    	ClientInfo ci = Config.api.getClientInfo(c.getId());
	    	clients.add("Name=" + c.getNickname() + ", IP=" + ci.getIp() + ", ID=" + c.getId() + ", UID=" + c.getUniqueIdentifier());
	    }
		for(String c : clients) {
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
	   for(int i = 0; i < len; i++) 
		   sb.append(AB.charAt(rnd.nextInt(AB.length())));
	   return sb.toString();
	}
}
