package net.scrumplex.sprummlbot.webinterface;

import com.github.theholywaffle.teamspeak3.api.VirtualServerStatus;
import com.github.theholywaffle.teamspeak3.api.wrapper.Ban;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.VirtualServerInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.scrumplex.sprummlbot.Main;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.tools.EasyMethods;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ApiHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpRequest) throws IOException {
        String url = httpRequest.getRequestURI().toString();
        StringBuilder sb = new StringBuilder("");
        String raw = httpRequest.getPrincipal().getRealm();
        String[] rawParts = {raw};
        Map<String, String> args = new HashMap<>();
        if (raw.contains("&"))
            rawParts = raw.split("&");
        for (String part : rawParts) {
            part = EasyMethods.decodeHTTPString(part);
            String[] splitted = part.split("=", 2);
            args.put(splitted[0], splitted[1]);
        }

        VirtualServerInfo serverInfo = Vars.QUERY.getApi().getServerInfo();
        switch (url) {
            case "/api/clientscount.get":
                WebServerManager.respond(httpRequest, 200, serverInfo.getClientsOnline() + "/" + serverInfo.getMaxClients() + " Users online", "text/plain");
                return;

            case "/api/banscount.get":
                List<Ban> banList = Vars.QUERY.getApi().getBans();
                int count = 0;
                if (banList != null)
                    count = banList.size();
                WebServerManager.respond(httpRequest, 200, count + " Bans active", "text/plain");
                return;

            case "/api/username.get":
                String username = httpRequest.getPrincipal().getUsername();
                if (username.startsWith("user")) {
                    username = username.substring(4);
                    username = Vars.QUERY.getApi().getDatabaseClientInfo(Integer.valueOf(username)).getNickname();
                } else
                    username = Character.toUpperCase(username.charAt(0)) + username.substring(1);
                WebServerManager.respond(httpRequest, 200, username, "text/plain");
                return;

            case "/api/clients.get":
                for (Client client : Vars.QUERY.getApi().getClients()) {
                    sb.append("<tr>\n");
                    sb.append("<td>").append(client.getNickname()).append("</td>\n");
                    sb.append("<td>").append(client.getUniqueIdentifier()).append("</td>\n");
                    if (!client.isServerQueryClient())
                        sb.append("<td>").append(client.getIp()).append("</td>\n");
                    else
                        sb.append("<td>unknown</td>\n");

                    if (!client.isServerQueryClient())
                        sb.append("<td>" + "<div class=\"btn-group\" role=\"group\" aria-label=\"Client actions\">" + "<button onclick=\"kick('").append(client.getId()).append("')\" type=\"button\" class=\"btn btn-warning\"><i class=\"fa fa-times-circle\"></i> Kick Client</button>").append("<button onclick=\"ban('").append(client.getId()).append("')\" type=\"button\" class=\"btn btn-danger\"><i class=\"fa fa-ban\"></i> Ban Client (1 hour)</button>").append("</div>").append("<div class=\"btn-group\" role=\"group\" aria-label=\"Client actions\">\n").append("<button onclick=\"sendMessage('").append(client.getId()).append("')\" type=\"button\" class=\"btn btn-primary\"><i class=\"fa fa-comment\"></i> Send message</button>\n").append("<button onclick=\"poke('").append(client.getId()).append("')\" type=\"button\" class=\"btn btn-primary\"><i class=\"fa fa-commenting-o\"></i> Poke</button>\n").append("</div>").append("</td>\n");
                    else
                        sb.append("<td>" +
                                "<button onclick=\"shutdown()\" type=\"button\" class=\"btn btn-danger\"><i class=\"fa fa-times-circle\"></i> Shutdown</button>" +
                                "</td>\n");
                    sb.append("</tr>\n");
                }
                WebServerManager.respond(httpRequest, 200, sb.toString(), "text/plain");
                return;

            case "/api/bans.get":
                if (Vars.QUERY.getApi().getBans() == null) {
                    WebServerManager.respond(httpRequest, 200, "No Bans", "text/plain");
                    return;
                }
                for (Ban ban : Vars.QUERY.getApi().getBans()) {
                    sb.append("<tr>\n");
                    sb.append("<td>").append(ban.getInvokerName()).append("</td>\n");
                    sb.append("<td>").append(ban.getBannedUId()).append("</td>\n");
                    sb.append("<td>").append(ban.getBannedIp()).append("</td>\n");
                    sb.append("<td>").append(new Date(ban.getCreatedDate().getTime() + ban.getDuration() * 1000).toString()).append("</td>\n");
                    sb.append("<td>").append(ban.getReason()).append("</td>\n");
                    sb.append("<td>" + "<button onclick=\"unban('").append(ban.getId()).append("')\" type=\"button\" class=\"btn btn-success\"><i class=\"fa fa-check\"></i> Unban</button>").append("</td>\n");
                    sb.append("</tr>\n");
                }
                WebServerManager.respond(httpRequest, 200, sb.toString(), "text/plain");
                return;

            case "/api/server/running.get":
                String resp = "0";
                if (Vars.QUERY.getApi().getServerInfo().getStatus() == VirtualServerStatus.ONLINE)
                    resp = "1";
                WebServerManager.respond(httpRequest, 200, resp, "text/plain");
                return;

            case "/api/client/ban.do":
                System.out.println("ban");
                int clientId = Integer.parseInt(args.get("cid"));
                String reason = args.get("reason");
                int[] createdBan = Vars.QUERY.getApi().banClient(clientId, 3600, reason);
                if (createdBan == null)
                    System.out.println("null");
                if (createdBan != null)
                    WebServerManager.respond(httpRequest, 200, "ok", "text/plain");
                return;

            case "/api/client/kick.do":
                if (Vars.QUERY.getApi().kickClientFromServer(args.get("reason"), Integer.parseInt(args.get("cid"))))
                    WebServerManager.respond(httpRequest, 200, "ok", "text/plain");
                return;

            case "/api/client/unban.do":
                if (Vars.QUERY.getApi().deleteBan(Integer.parseInt(args.get("id"))))
                    WebServerManager.respond(httpRequest, 200, "ok", "text/plain");
                return;

            case "/api/client/sendmessage.do":
                if (Vars.QUERY.getApi().sendPrivateMessage(Integer.parseInt(args.get("cid")), args.get("msg")))
                    WebServerManager.respond(httpRequest, 200, "ok", "text/plain");
                return;

            case "/api/client/poke.do":
                if (Vars.QUERY.getApi().pokeClient(Integer.parseInt(args.get("cid")), args.get("msg")))
                    WebServerManager.respond(httpRequest, 200, "ok", "text/plain");
                return;

            case "/api/special/shutdown.do":
                WebServerManager.respond(httpRequest, 200, " Shutting down", "text/plain");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        System.exit(0);
                    }
                }).start();
                return;

            case "/api/special/kickall.do":
                for (Client client : Vars.QUERY.getApi().getClients()) {
                    Vars.QUERY.getApi().kickClientFromServer("Kicked by Sprummlbot webinterface", client);
                }
                WebServerManager.respond(httpRequest, 200, "ok", "text/plain");
                return;

            case "/api/client/sendpublicmessage.do":
                if (Vars.QUERY.getApi().sendServerMessage(args.get("msg")))
                    WebServerManager.respond(httpRequest, 200, "ok", "text/plain");
                return;

            case "/api/server/log.get":
                WebServerManager.respond(httpRequest, 200, Main.out.getHTMLLog(), "text/plain");
                return;
        }
        WebServerManager.respond(httpRequest, 500, "Error", "text/plain");
    }
}
