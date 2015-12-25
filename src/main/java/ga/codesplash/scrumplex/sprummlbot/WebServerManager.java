package ga.codesplash.scrumplex.sprummlbot;

import com.sun.net.httpserver.*;
import ga.codesplash.scrumplex.sprummlbot.tools.Exceptions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Class for running an web-interface
 */
class WebServerManager {

    private static HttpServer server = null;

    /**
     * Starts Webservice
     *
     * @throws IOException
     */
    public static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(Vars.WEBINTERFACE_PORT), 0);
        HttpContext hc = server.createContext("/", new WebHandler());
        hc.setAuthenticator(new BasicAuthenticator("") {
            @Override
            public boolean checkCredentials(String user, String pw) {
                return (Vars.AVAILABLE_LOGINS.containsKey(user) && Vars.AVAILABLE_LOGINS.get(user).equals(pw));
            }
        });
        if (Main.banner != null) {
            server.createContext("/f/", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpRequest) {
                    try {
                        byte[] bytes = Main.banner.getNewImageAsBytes();
                        httpRequest.getResponseHeaders().add("Content-type", "image/png");
                        httpRequest.sendResponseHeaders(200, bytes.length);
                        OutputStream out = httpRequest.getResponseBody();
                        out.write(bytes);
                        out.close();
                    } catch (IOException | InterruptedException e) {
                        Exceptions.handle(e, "Error while creating Interactive banner", false);
                    }
                }
            }).setAuthenticator(null);
        }
        server.start();
    }

    /**
     * Stops Webservice
     */
    public static void stop() {
        server.stop(0);
    }
}