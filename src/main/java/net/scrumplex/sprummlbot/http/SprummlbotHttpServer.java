package net.scrumplex.sprummlbot.http;

import com.sun.net.httpserver.HttpServer;
import net.scrumplex.sprummlbot.tools.Validate;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SprummlbotHttpServer {

    private int port;
    private HttpServer httpServer;

    public SprummlbotHttpServer(int port) throws SprummlbotHttpException {
        if (Validate.Network.port(port)) {
            try {
                this.port = port;
                this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            } catch (IOException e) {
                throw new SprummlbotHttpException(e);
            }
        } else
            throw new IllegalArgumentException("The web interface port has to be between 0 and 65535!");
    }

    public void startServer() {
        httpServer.createContext("/", new RootHandler());
        httpServer.createContext("/control/", new ControlHandler());
    }


}
