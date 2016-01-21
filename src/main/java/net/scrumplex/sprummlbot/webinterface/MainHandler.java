package net.scrumplex.sprummlbot.webinterface;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

class MainHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpRequest) throws IOException {
        String url = httpRequest.getRequestURI().toString().split("\\?", 2)[0];
        switch (url) {
            case "/bootstrap.min.css":
                WebServerManager.respond(httpRequest, 200, FileLoader.getFile("css"), "text/css");
                return;
            case "/bootstrap.min.js":
                WebServerManager.respond(httpRequest, 200, FileLoader.getFile("js"), "text/javascript");
                return;
            case "/jquery.min.js":
                WebServerManager.respond(httpRequest, 200, FileLoader.getFile("jquery"), "text/javascript");
                return;
            case "/login.html":
                WebServerManager.respond(httpRequest, 200, FileLoader.getFile("login-site"), "text/html");
                return;
            case "/logout.html":
                WebServerManager.respond(httpRequest, 200, FileLoader.getFile("logout-site"), "text/html");
                return;
        }
        WebServerManager.respond(httpRequest, 200, Basics.getRedirector("/login.html"), "text/html");
    }
}
