package ga.scrumplex.ml.sprum.sprummlbot.bridge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.scrumplex.ml.sprum.sprummlbot.Config;
import ga.scrumplex.ml.sprum.sprummlbot.Logger;

public class TCPServer {

	private static ArrayList<String> white = new ArrayList<>();

	public static void addToWhitelist(String ip) {
		white.add(ip);
	}

	public static void start() throws IOException {
		ServerSocket welcomeSocket = new ServerSocket(Config.PORT_BRIDGE);
		while (Config.BRIDGE_ENABLED) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			Logger.out("[Bridge] Connection incoming! " + connectionSocket.getInetAddress().getHostAddress());
			BridgeEvents.createEvents(out);
			if (!white.contains(connectionSocket.getInetAddress().getHostAddress())) {
				sendError(out, 11);
				connectionSocket.close();
			} else {
				sendToClient(out, "welcome");
			}
			while (connectionSocket != null) {
				try {
					handleIncome(in, out, connectionSocket);
				} catch (NumberFormatException nf) {
					nf.printStackTrace();
					sendError(out, 33, "intnull");
				} catch (IndexOutOfBoundsException ioob) {
					ioob.printStackTrace();
					sendError(out, 33, "syntaxerr");
				} catch (IOException io) {
					BridgeEvents.registerEvents();
					if (Config.DEBUG >= 1)
						Logger.warn("Connection down. Closing Socket.");
					break;
				} catch (NullPointerException np) {
					BridgeEvents.registerEvents();
					if (Config.DEBUG >= 1)
						Logger.warn("Connection down. Closing Socket.");
					break;
				}
			}
		}
		welcomeSocket.close();
	}

	private static void handleIncome(BufferedReader in, DataOutputStream out, Socket con)
			throws NumberFormatException, IndexOutOfBoundsException, IOException {
		String msg = in.readLine().toLowerCase();
		String command = msg.split(" ")[0];
		if (command == "" || command == null) {
			sendError(out, 32);
		} else {
			String[] args;
			if (msg.split(" ").length >= 2) {
				args = msg.split(" ", 2)[1].split(",");
				for (int i = 0; i < args.length; i++) {
					args[i] = args[i].replace("\"", "");
				}
			} else {
				args = null;
			}
			boolean happen = false;
			switch (command) {
			case "disc":
				con.close();
				happen = true;
				break;

			case "sendserv":
				happen = true;
				if (args != null) {
					Config.API.sendServerMessage(args[0]);
					sendToClient(out, "sent");
				} else {
					sendError(out, 33);
				}
				break;

			case "sendpriv":
				happen = true;
				if (args != null) {
					Config.API.sendPrivateMessage(Integer.parseInt(args[0]), args[1]);
					sendToClient(out, "sent");
				} else {
					sendError(out, 33);
				}
				break;

			case "clientlist":
				happen = true;
				StringBuilder sb = new StringBuilder("[");
				for (Client c : Config.API.getClients()) {
					sb.append("nick=\"" + c.getNickname() + "\",cid=\"" + c.getId() + "\",uid=\""
							+ c.getUniqueIdentifier() + "\",dbid=\"" + c.getDatabaseId() + "\";");
				}

				sendToClient(out, sb.toString().substring(0, sb.toString().length() - 1) + "]");
				break;
			case "kick":
				happen = true;
				if (args != null) {
					if (Config.API.kickClientFromServer(args[1], Integer.parseInt(args[0]))) {
						sendToClient(out, "kicked.");
					} else {
						sendError(out, 21);
					}
				} else {
					sendError(out, 33);
				}
				break;

			case "regevents":
				happen = true;
				BridgeEvents.registerEvents();
				sendToClient(out, "eventreg");
				break;
			case "unregevents":
				happen = true;
				BridgeEvents.unregisterEvents();
				sendToClient(out, "eventunreg");
				break;
			}
			if (!happen) {
				sendError(out, 31);
			}
		}
	}

	private static boolean sendError(DataOutputStream out, int i) {
		return sendError(out, i, "error");
	}

	private static boolean sendError(DataOutputStream out, int i, String msg) {
		try {
			out.writeBytes("!e=\"" + i + "\", msg=\"" + msg + "\"\n");
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public static boolean sendEvent(DataOutputStream out, TS3EventType type, Map<String, String> props) {
		StringBuilder sb = new StringBuilder(">event=\"");
		switch (type) {
		case SERVER:
			sb.append("server");
			break;
		case CHANNEL:
			sb.append("channel");
			break;

		case TEXT_CHANNEL:
		case TEXT_SERVER:
		case TEXT_PRIVATE:
			sb.append("text");
			break;
		}
		sb.append("\",properties=\"");
		for (String key : props.keySet()) {
			String val = props.get(key);
			sb.append(key + "=\"" + val + "\",");
		}
		String base = sb.toString().substring(0, sb.length() - 1) + "\"";
		base += ",msg=\"ok\"\n";
		try {
			out.writeBytes(base);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private static boolean sendToClient(DataOutputStream out, String msg) {
		try {
			out.writeBytes(">output=\"" + msg + "\",msg=\"ok\"\n");
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
