package ga.codesplash.scrumplex.sprummlbot.bridge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.Logger;

public class TCPServer {

	private static ArrayList<String> white = new ArrayList<>();

	public static void addToWhitelist(String ip) {
		white.add(ip);
	}

	public static void start() throws IOException {
		ServerSocket welcomeSocket = new ServerSocket(Vars.PORT_BRIDGE);
		while (Vars.BRIDGE_ENABLED) {
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
					if (Vars.DEBUG >= 1)
						Logger.warn("Connection down. Closing Socket.");
					break;
				} catch (NullPointerException np) {
					BridgeEvents.registerEvents();
					if (Vars.DEBUG >= 1)
						Logger.warn("Connection down. Closing Socket.");
					break;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		welcomeSocket.close();
	}

	private static void handleIncome(BufferedReader in, DataOutputStream out, Socket con)
			throws NumberFormatException, IndexOutOfBoundsException, IOException, JSONException {
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
					Vars.API.sendServerMessage(args[0]);
					sendToClient(out, "sent");
				} else {
					sendError(out, 33);
				}
				break;

			case "sendpriv":
				happen = true;
				if (args != null) {
					Vars.API.sendPrivateMessage(Integer.parseInt(args[0]), args[1]);
					sendToClient(out, "sent");
				} else {
					sendError(out, 33);
				}
				break;

			case "clientlist":
				happen = true;
				JSONArray arr = new JSONArray();
				for (Client c : Vars.API.getClients()) {
					JSONObject obj = new JSONObject();
					obj.put("nick", c.getNickname());
					obj.put("cid", c.getId());
					obj.put("uid", c.getUniqueIdentifier());
					obj.put("dbid", c.getDatabaseId());
					arr.put(obj);
				}
				sendToClient(out, arr);
				break;
			case "kick":
				happen = true;
				if (args != null) {
					if (Vars.API.kickClientFromServer(args[1], Integer.parseInt(args[0]))) {
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
		JSONObject obj = new JSONObject();
		try {
			obj.put("error", i);
			obj.put("error-msg", msg);
			obj.put("type", "error");
			obj.put("msg", "");
			out.writeBytes(obj.toString());
		} catch (IOException | JSONException e) {
			return false;
		}
		return true;
	}

	public static boolean sendEvent(DataOutputStream out, TS3EventType type, Map<String, String> props) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("error", 0);
			obj.put("type", "event");
			switch (type) {
			case SERVER:
				obj.put("event-type", "server");
				break;
			case CHANNEL:
				obj.put("event-type", "channel");
				break;

			case TEXT_CHANNEL:
			case TEXT_SERVER:
			case TEXT_PRIVATE:
				obj.put("event-type", "text");
				break;
			}
			JSONArray arr = new JSONArray();
			for(String key : props.keySet()) {
				JSONObject arrk = new JSONObject();
				arrk.put(key, props.get(key));
				arr.put(arrk);
			}
			obj.put("event-properties", arr);
			obj.put("msg", "");
			out.writeBytes(obj.toString());
		} catch (IOException | JSONException e) {
			return false;
		}
		return true;
	}

	private static boolean sendToClient(DataOutputStream out, String msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("error", 0);
			obj.put("type", "out");
			obj.put("out", msg);
			obj.put("msg", "ok");
			out.writeBytes(obj.toString());
		} catch (IOException | JSONException e) {
			return false;
		}
		return true;
	}
	
	private static boolean sendToClient(DataOutputStream out, JSONArray msg) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("error", 0);
			obj.put("type", "out");
			obj.put("out", msg);
			obj.put("msg", "ok");
			out.writeBytes(obj.toString());
		} catch (IOException | JSONException e) {
			return false;
		}
		return true;
	}

}
