package ga.codesplash.scrumplex.sprummlbot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ga.codesplash.scrumplex.sprummlbot.tools.EasyMethods;
import ga.codesplash.scrumplex.sprummlbot.web.func.Actions;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * This class handles all incoming http requests
 */
class WebHandler implements HttpHandler {
    /**
     * Handles incomingweb requests
     *
     * @param httpExchange HttpExchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";

        String requestURI = httpExchange.getRequestURI().toString();
        HashMap<String, String> args = new HashMap<>();
        String url = requestURI;

        if (requestURI.contains("!")) {
            String argsraw = requestURI.split("!")[1];
            String[] notargs = argsraw.split(",");

            for (String argsy : notargs) {
                String[] arg = argsy.split("=");
                args.put(arg[0], arg[1]);
            }

            url = requestURI.split("!")[0];
        }

        if (!url.endsWith("/")) {
            url = url + "/";
        }

        switch (url) {
            case "/":
                response = new ga.codesplash.scrumplex.sprummlbot.web.Site_index().content;
                break;

            case "/manage/":
                response = new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_index().content;
                break;

            case "/manage/shutdown/":
                response = Actions.shutdown();
                break;

            case "/manage/bans/":
                response = new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_bans().content;
                break;

            case "/manage/action/":
                response = new ga.codesplash.scrumplex.sprummlbot.web.Site_index().content;
                break;

            case "/manage/action/ban/":
                response = Actions.ban(Integer.parseInt(args.get("userid")), EasyMethods.decodeHTTPString(args.get("msg")),
                        Integer.parseInt(args.get("time")));
                break;

            case "/manage/action/unban/":
                response = Actions.unban(Integer.parseInt(args.get("id")));
                break;

            case "/manage/log/":
                response = new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_log().content;
                break;

            case "/manage/action/kick/":
                response = Actions.kick(Integer.parseInt(args.get("userid")), EasyMethods.decodeHTTPString(args.get("msg")));
                break;

            case "/manage/action/sendpriv/":
                response = Actions.sendpriv(Integer.parseInt(args.get("userid")), EasyMethods.decodeHTTPString(args.get("msg")));
                break;

            case "/manage/action/poke/":
                response = Actions.poke(Integer.parseInt(args.get("userid")), EasyMethods.decodeHTTPString(args.get("msg")));
                break;

            case "/manage/action/clearaccounts/":
                response = Actions.clearAccounts();
                break;

            case "/logout/":
                response = new ga.codesplash.scrumplex.sprummlbot.web.Site_logout(httpExchange.getPrincipal().getUsername()).content;
                break;
        }
        int statusCode = 200;
        if (response.equalsIgnoreCase("")) {
            statusCode = 404;
            response = "<h2 style=\"text-align:center\">404 - Resource not found</h2>";
        }
        sendResponse(httpExchange, statusCode, response);
    }

    private void sendResponse(HttpExchange httpRequest, int statusCode, String response) throws IOException {
        httpRequest.getResponseHeaders().add("Content-type", "text/html");
        httpRequest.sendResponseHeaders(statusCode, response.length());
        OutputStream out = httpRequest.getResponseBody();
        out.write(response.getBytes());
        out.close();
    }
}