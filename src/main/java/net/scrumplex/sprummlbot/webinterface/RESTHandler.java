package net.scrumplex.sprummlbot.webinterface;

import com.github.theholywaffle.teamspeak3.api.wrapper.Ban;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.VirtualServerInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

class RESTHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpRequest) throws IOException {
        try {
            Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
            String url = httpRequest.getRequestURI().toString();
            String raw = httpRequest.getPrincipal().getRealm();
            String[] rawParts = {raw};
            Map<String, String> args = new HashMap<>();
            if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                if (raw.contains("=")) {
                    if (raw.contains("&"))
                        rawParts = raw.split("&");
                    for (String part : rawParts) {
                        part = EasyMethods.decodeHTTPString(part);
                        String[] split = part.split("=", 2);
                        args.put(split[0], split[1]);
                    }
                }
            }
            if (url.startsWith("/api/1.0/clients")) {
                if (url.equalsIgnoreCase("/api/1.0/clients")) {
                    JSONObject json = new JSONObject();
                    List<Client> clients = sprummlbot.getSyncAPI().getClients();
                    if (clients == null) {
                        String[] nothing = new String[0];
                        json.put("error", true);
                        json.put("msg", "server_side_err");
                        json.put("clients", nothing);
                        WebServerManager.respond(httpRequest, 200, json.toString(), "application/json");
                        return;
                    }
                    Collections.sort(clients, new Comparator<Client>() {
                        public int compare(Client one, Client two) {
                            return one.getNickname().compareTo(two.getNickname());
                        }
                    });
                    json.put("error", false);
                    json.put("msg", "success");
                    JSONArray array = new JSONArray();
                    for (Client c : clients) {
                        array.put(c.getMap());
                    }
                    json.put("clients", array);
                    WebServerManager.respond(httpRequest, 200, json.toString(), "application/json");
                    return;
                }
                String[] urls = url.split("/", 6);
                System.arraycopy(urls, 4, urls, 0, 2);
                if (urls[1].equalsIgnoreCase("kick")) {
                    JSONObject obj = new JSONObject();
                    boolean error;
                    if (httpRequest.getRequestMethod().equalsIgnoreCase("GET")) {
                        error = !sprummlbot.getSyncAPI().kickClientFromServer(Integer.parseInt(urls[0]));
                    } else {
                        error = !sprummlbot.getSyncAPI().kickClientFromServer(args.get("reason"), Integer.parseInt(urls[0]));
                    }
                    obj.put("error", error);
                    obj.put("msg", error ? "server_side_err" : "success");
                    WebServerManager.respond(httpRequest, error ? 500 : 200, obj.toString(), "application/json");
                    return;
                } else if (urls[1].equalsIgnoreCase("ban")) {
                    JSONObject obj = new JSONObject();
                    if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                        int[] bans;
                        if (!args.containsKey("reason")) {
                            bans = sprummlbot.getSyncAPI().banClient(Integer.parseInt(urls[0]), Integer.parseInt(args.get("duration")));
                        } else {
                            bans = sprummlbot.getSyncAPI().banClient(Integer.parseInt(urls[0]), Integer.parseInt(args.get("duration")), args.get("reason"));
                        }
                        boolean error = bans == null;
                        obj.put("error", false);
                        obj.put("msg", error ? "server_side_err" : "success");
                        if (bans != null) {
                            obj.put("ids", bans);
                        }
                        WebServerManager.respond(httpRequest, error ? 500 : 200, obj.toString(), "application/json");
                        return;
                    }
                } else if (urls[1].equalsIgnoreCase("poke")) {
                    JSONObject obj = new JSONObject();
                    if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                        boolean error = !sprummlbot.getSyncAPI().pokeClient(Integer.parseInt(urls[0]), args.get("message"));
                        obj.put("error", error);
                        obj.put("msg", error ? "server_side_err" : "success");
                        WebServerManager.respond(httpRequest, error ? 500 : 200, obj.toString(), "application/json");
                        return;
                    }
                } else if (urls[1].equalsIgnoreCase("sendmsg")) {
                    JSONObject obj = new JSONObject();
                    if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                        boolean error = !sprummlbot.getSyncAPI().sendPrivateMessage(Integer.parseInt(urls[0]), args.get("message"));
                        obj.put("error", error);
                        obj.put("msg", error ? "server_side_err" : "success");
                        WebServerManager.respond(httpRequest, error ? 500 : 200, obj.toString(), "application/json");
                        return;
                    }
                }
                return;
            } else if (url.startsWith("/api/1.0/bans")) {
                if (url.equalsIgnoreCase("/api/1.0/bans")) {
                    List<Ban> bans = sprummlbot.getSyncAPI().getBans();
                    JSONObject json = new JSONObject();
                    if (bans == null) {
                        json.put("error", false);
                        json.put("msg", "success");
                        json.put("bans", new JSONArray());
                        WebServerManager.respond(httpRequest, 200, json.toString(), "application/json");
                        return;
                    }
                    Collections.sort(bans, new Comparator<Ban>() {
                        public int compare(Ban one, Ban two) {
                            return one.getId() > two.getId() ? +1 : one.getId() < two.getId() ? -1 : 0;
                        }
                    });
                    json.put("error", false);
                    json.put("msg", "success");
                    JSONArray array = new JSONArray();
                    for (Ban b : bans) {
                        JSONObject obj = new JSONObject(b.getMap());
                        obj.put("expires", new Date(b.getCreatedDate().getTime() + b.getDuration() * 1000).toString());
                        array.put(obj);
                    }
                    json.put("bans", array);
                    WebServerManager.respond(httpRequest, 200, json.toString(), "application/json");
                    return;
                }
                String[] urls = url.split("/", 6);
                System.arraycopy(urls, 4, urls, 0, 2);
                if (urls[1].equalsIgnoreCase("unban")) {
                    JSONObject obj = new JSONObject();
                    if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                        boolean error = !sprummlbot.getSyncAPI().deleteBan(Integer.parseInt(urls[0]));
                        obj.put("error", error);
                        obj.put("msg", error ? "server_side_err" : "success");
                        WebServerManager.respond(httpRequest, error ? 500 : 200, obj.toString(), "application/json");
                        return;
                    }
                }
            } else if (url.startsWith("/api/1.0/server")) {
                if (url.equalsIgnoreCase("/api/1.0/server")) {
                    JSONObject json = new JSONObject();
                    VirtualServerInfo info = sprummlbot.getSyncAPI().getServerInfo();
                    if (info == null) {
                        json.put("error", true);
                        json.put("msg", "Error while retrieving server info!");
                        WebServerManager.respond(httpRequest, 500, json.toString(), "application/json");
                        return;
                    }
                    json.put("error", false);
                    json.put("msg", "success");
                    json.put("server", info.getMap());
                    WebServerManager.respond(httpRequest, 200, json.toString(), "application/json");
                    return;
                }
                if (url.equalsIgnoreCase("/api/1.0/server/sendmsg")) {
                    JSONObject obj = new JSONObject();
                    if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                        boolean error = !sprummlbot.getSyncAPI().sendServerMessage(args.get("message"));
                        obj.put("error", error);
                        obj.put("msg", error ? "server_side_err" : "success");
                        WebServerManager.respond(httpRequest, error ? 500 : 200, obj.toString(), "application/json");
                        return;
                    }
                }
            }
            switch (url.toLowerCase()) {
                case "/api/1.0/sprummlbot":
                    JSONObject json = new JSONObject();
                    json.put("error", false);
                    json.put("msg", "success");
                    JSONObject bot = new JSONObject();
                    bot.put("version", Vars.VERSION);
                    bot.put("update_available", Vars.UPDATE_AVAILABLE);
                    bot.put("webinterface_listening_address", Vars.IP + ":" + Vars.WEBINTERFACE_PORT);
                    bot.put("public_ip", Vars.IP);
                    String username = httpRequest.getPrincipal().getUsername();
                    if (username.startsWith("user")) {
                        username = username.substring(4);
                        username = sprummlbot.getSyncAPI().getDatabaseClientInfo(Integer.valueOf(username)).getNickname();
                    } else
                        username = Character.toUpperCase(username.charAt(0)) + username.substring(1);
                    bot.put("request_user", username);
                    json.put("bot", bot);
                    WebServerManager.respond(httpRequest, 200, json.toString(), "application/json");
                    return;

                case "/api/1.0/sprummlbot/shutdown":
                    JSONObject obj = new JSONObject();
                    obj.put("error", false);
                    obj.put("msg", "shutting_down");
                    WebServerManager.respond(httpRequest, 200, obj.toString(), "application/json");
                    Sprummlbot.getSprummlbot().shutdown(1000, TimeUnit.SECONDS);
                    return;

                case "/api/1.0/sprummlbot/qrcode":
                    String requestLine = httpRequest.getRequestHeaders().getFirst("Authorization");
                    String key = requestLine.split("=", 2)[1];
                    String img = "data:image/png;base64," + DatatypeConverter.printBase64Binary(QRCodeGenerator.getQRCode("sprummlbot://" + key + "@" + Vars.IP + ":" + Vars.WEBINTERFACE_PORT));
                    WebServerManager.respond(httpRequest, 200, img, "text/plain");
                    return;
            }
            JSONObject json = new JSONObject();
            json.put("error", true);
            json.put("msg", "Bad Request");
            WebServerManager.respond(httpRequest, 400, json.toString(), "application/json");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
