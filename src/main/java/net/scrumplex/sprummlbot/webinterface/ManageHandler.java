package net.scrumplex.sprummlbot.webinterface;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.scrumplex.sprummlbot.tools.EasyMethods;

import java.io.IOException;

class ManageHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpRequest) throws IOException {
        String url = httpRequest.getRequestURI().toString();
        if (url.equalsIgnoreCase("/manage/") || url.equalsIgnoreCase("/manage/index.html")) {
            String user = httpRequest.getPrincipal().getUsername();
            String pass = httpRequest.getPrincipal().getRealm();
            String apiKey = EasyMethods.md5Hex(user + ":" + pass);
            WebServerManager.respond(httpRequest, 200, new String(FileLoader.getFile("manage-site")).replace("%%%APIKEY%%%", apiKey), "text/html");
        }
        WebServerManager.respond(httpRequest, 200, Basics.getRedirection("/"), "text/html");
    }
}
