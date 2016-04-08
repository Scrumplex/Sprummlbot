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
                WebServerManager.respond(httpRequest, FileLoader.getFile("css"), "text/css");
                return;
            case "/bootstrap.min.js":
                WebServerManager.respond(httpRequest, FileLoader.getFile("js"), "text/javascript");
                return;
            case "/jquery.min.js":
                WebServerManager.respond(httpRequest, FileLoader.getFile("jquery"), "text/javascript");
                return;
            case "/logo-wide.png":
                WebServerManager.respond(httpRequest, FileLoader.getFile("logo"), "image/png");
                return;
            case "/login.html":
                WebServerManager.respond(httpRequest, FileLoader.getFile("login-site"), "text/html");
                return;
            case "/logout.html":
                WebServerManager.respond(httpRequest, FileLoader.getFile("logout-site"), "text/html");
                return;
            case "/favicon.ico":
                WebServerManager.respond(httpRequest, FileLoader.getFile("favicon"), "image/x-icon");
                return;
            case "/robots.txt":
                WebServerManager.respond(httpRequest, 200, Basics.getRobots(), "text/plain");
                return;
        }
        WebServerManager.respond(httpRequest, 200, Basics.getRedirection("/login.html"), "text/html");
    }
}
