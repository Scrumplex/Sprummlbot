package net.scrumplex.sprummlbot.webinterface;

import com.sun.net.httpserver.*;
import net.scrumplex.sprummlbot.Main;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.vpn.VPNChecker;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class WebServerManager {

    private static HttpServer server;

    public static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(Vars.WEBINTERFACE_PORT), 0);

        server.createContext("/", new MainHandler());
        server.createContext("/api/", new ApiHandler()).setAuthenticator(new Authenticator() {
            @Override
            public Result authenticate(HttpExchange httpRequest) {
                try {
                    if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                        String raw = IOUtils.toString(httpRequest.getRequestBody());
                        String[] rawParts = {raw};
                        if (raw.contains("&"))
                            rawParts = raw.split("&");
                        Map<String, String> args = new HashMap<>();
                        for (String part : rawParts) {
                            part = EasyMethods.decodeHTTPString(part);
                            String[] splitted = part.split("=", 2);
                            args.put(splitted[0], splitted[1]);
                        }
                        String key = args.get("api_key");
                        for (String user : Vars.AVAILABLE_LOGINS.keySet()) {
                            String pass = Vars.AVAILABLE_LOGINS.get(user);
                            String apiKey = DigestUtils.md5Hex(user + ":" + pass);
                            if (key.equalsIgnoreCase(apiKey)) {
                                return new Success(new HttpPrincipal(user, raw));
                            }
                        }
                    }
                    String response = "Not Authenticated!";
                    httpRequest.getResponseHeaders().add("Content-type", "text/html");
                    httpRequest.sendResponseHeaders(403, response.length());
                    OutputStream out = httpRequest.getResponseBody();
                    out.write(response.getBytes());
                    out.close();
                    return new Retry(0);
                } catch (Exception ignored) {
                }
                return new Failure(0);
            }
        });
        server.createContext("/manage/", new ManageHandler()).setAuthenticator(new Authenticator() {
            @Override
            public Result authenticate(HttpExchange httpRequest) {
                try {
                    if (new VPNChecker(httpRequest.getRemoteAddress().getHostString(), null).isBlocked()) {
                        String response = Basics.getRedirector("/login.html?error=vpn");
                        httpRequest.getResponseHeaders().add("Content-type", "text/html");
                        httpRequest.sendResponseHeaders(200, response.length());
                        OutputStream out = httpRequest.getResponseBody();
                        out.write(response.getBytes());
                        out.close();
                        return new Retry(0);
                    }
                    String url = httpRequest.getRequestURI().toString().split("\\?", 2)[0];
                    if (url.equalsIgnoreCase("/manage/index.html") || url.equalsIgnoreCase("/manage/")) {
                        if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                            String raw = IOUtils.toString(httpRequest.getRequestBody());
                            String[] rawParts = raw.split("&");
                            String user = rawParts[0].split("=")[1];
                            String pass = rawParts[1].split("=")[1];
                            if (Vars.AVAILABLE_LOGINS.containsKey(user)) {
                                if (Vars.AVAILABLE_LOGINS.get(user).equals(pass))
                                    return new Success(new HttpPrincipal(user, pass));
                            }
                        }
                        String response = Basics.getRedirector("/login.html?error=login");
                        httpRequest.getResponseHeaders().add("Content-type", "text/html");
                        httpRequest.sendResponseHeaders(200, response.length());
                        OutputStream out = httpRequest.getResponseBody();
                        out.write(response.getBytes());
                        out.close();
                        return new Retry(0);
                    }

                } catch (Exception ignored) {
                }
                return new Failure(0);
            }
        });

        if (Vars.INTERACTIVEBANNER_ENABLED && Main.banner != null) {
            server.createContext("/f/", new HttpHandler() {
                @Override
                public void handle(final HttpExchange httpRequest) {
                    if (!httpRequest.getRequestHeaders().getFirst("User-Agent")
                            .equalsIgnoreCase("TeamSpeak3-ImageFetcher-1.0")) {
                        httpRequest.close();
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                byte[] bytes = Main.banner.getNewImageAsBytes();
                                httpRequest.getResponseHeaders().add("Content-type", "image/png");
                                httpRequest.sendResponseHeaders(200, bytes.length);
                                OutputStream out = httpRequest.getResponseBody();
                                out.write(bytes);
                                out.close();
                            } catch (InterruptedException e) {
                                Exceptions.handle(e, "Error while creating Interactive banner", false);
                            } catch (IOException ignored) {

                            }
                        }
                    }).start();
                }
            });
        }

        server.start();
    }

    public static void stop() {
        server.stop(0);
    }

    static void respond(HttpExchange httpRequest, int statusCode, String response, String contentType) throws IOException {
        httpRequest.getResponseHeaders().add("Content-type", contentType);
        httpRequest.sendResponseHeaders(statusCode, response.length());
        OutputStream out = httpRequest.getResponseBody();
        out.write(response.getBytes());
        out.close();
    }

}
