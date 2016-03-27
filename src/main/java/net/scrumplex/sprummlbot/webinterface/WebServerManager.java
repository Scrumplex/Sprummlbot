package net.scrumplex.sprummlbot.webinterface;

import com.sun.net.httpserver.*;
import net.scrumplex.sprummlbot.Startup;
import net.scrumplex.sprummlbot.Tasks;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebServerManager {

    private static HttpServer server;
    private static final List<String> blocked = new ArrayList<>();

    public static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(Vars.WEBINTERFACE_PORT), 0);

        server.createContext("/", new MainHandler());
        server.createContext("/api/1.0/", new RESTHandler()).setAuthenticator(new Authenticator() {
            @Override
            public Result authenticate(final HttpExchange httpRequest) {
                try {
                    String raw;
                    if (blocked.contains(httpRequest.getRemoteAddress().getHostString())) {
                        respond(httpRequest, 403, "Cooling down...", "text/plain");
                        return new Retry(403);
                    }
                    if (!httpRequest.getRequestHeaders().containsKey("Authorization"))
                        return new Retry(401);
                    String requestLine = httpRequest.getRequestHeaders().getFirst("Authorization");
                    String key = requestLine.split("=", 2)[1];
                    raw = key;
                    if (httpRequest.getRequestMethod().equalsIgnoreCase("POST"))
                        raw = EasyMethods.convertStreamToString(httpRequest.getRequestBody());
                    for (String user : Vars.AVAILABLE_LOGINS.keySet()) {
                        String pass = Vars.AVAILABLE_LOGINS.get(user);
                        String apiKey = EasyMethods.md5Hex(user + ":" + pass);
                        if (key.equals(apiKey)) {
                            return new Success(new HttpPrincipal(user, raw));
                        }
                    }
                    blocked.add(httpRequest.getRemoteAddress().getHostString());
                    Tasks.service.schedule(new Runnable() {
                        @Override
                        public void run() {
                            blocked.remove(httpRequest.getRemoteAddress().getHostString());
                        }
                    }, 3, TimeUnit.SECONDS);
                    String response = "Not Authorized!";
                    respond(httpRequest, 401, response, "text/plain");
                    return new Retry(401);
                } catch (Exception ignored) {
                }
                return new Failure(500);
            }
        });
        server.createContext("/manage/", new ManageHandler()).setAuthenticator(new Authenticator() {
            @Override
            public Result authenticate(final HttpExchange httpRequest) {
                try {
                    if (blocked.contains(httpRequest.getRemoteAddress().getHostString())) {
                        String response = Basics.getRedirection("/login.html?error=cooldown");
                        respond(httpRequest, 403, response, "text/html");
                        return new Retry(403);
                    }
                    String url = httpRequest.getRequestURI().toString().split("\\?", 2)[0];
                    if (url.equalsIgnoreCase("/manage/index.html") || url.equalsIgnoreCase("/manage/")) {
                        if (httpRequest.getRequestMethod().equalsIgnoreCase("POST")) {
                            String raw = EasyMethods.convertStreamToString(httpRequest.getRequestBody());
                            String[] rawParts = raw.split("&");
                            String user = rawParts[0].split("=")[1];
                            String pass = rawParts[1].split("=")[1];
                            if (Vars.AVAILABLE_LOGINS.containsKey(user))
                                if (Vars.AVAILABLE_LOGINS.get(user.toLowerCase()).equals(pass)) {
                                    System.out.println("Allowed web interface access to " + user.toLowerCase() + " from ip " + httpRequest.getRemoteAddress().getHostString());
                                    return new Success(new HttpPrincipal(user, pass));
                                }
                        }
                        blocked.add(httpRequest.getRemoteAddress().getHostString());
                        Tasks.service.schedule(new Runnable() {
                            @Override
                            public void run() {
                                blocked.remove(httpRequest.getRemoteAddress().getHostString());
                            }
                        }, 3, TimeUnit.SECONDS);
                        String response = Basics.getRedirection("/login.html?error=login");
                        respond(httpRequest, 200, response, "text/html");
                        return new Retry(401);
                    }

                } catch (Exception ignored) {
                }
                return new Failure(500);
            }
        });

        if (Vars.DYNBANNER_ENABLED && Startup.banner != null) {
            server.createContext("/f/", new HttpHandler() {
                @Override
                public void handle(final HttpExchange httpRequest) {
                    try {
                        if (blocked.contains(httpRequest.getRemoteAddress().getHostString())) {
                            String response = Basics.getRedirection("/login.html?error=cooldown");
                            respond(httpRequest, 403, response, "text/html");
                            return;
                        }
                    } catch (IOException ignored) {
                        httpRequest.close();
                        return;
                    }
                    if (!httpRequest.getRequestHeaders().getFirst("User-Agent")
                            .equalsIgnoreCase("TeamSpeak3-ImageFetcher-1.0")) {
                        httpRequest.close();
                        return;
                    }
                    Vars.EXECUTOR.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                byte[] bytes = Startup.banner.getNewImageAsBytes();
                                httpRequest.getResponseHeaders().add("Content-type", "image/png");
                                httpRequest.sendResponseHeaders(200, bytes.length);
                                OutputStream out = httpRequest.getResponseBody();
                                out.write(bytes);
                                out.close();
                            } catch (InterruptedException e) {
                                Exceptions.handle(e, "Error while creating Dynamic banner", false);
                            } catch (IOException ignored) {

                            }
                            blocked.add(httpRequest.getRemoteAddress().getHostString());
                            Tasks.service.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    blocked.remove(httpRequest.getRemoteAddress().getHostString());
                                }
                            }, 30, TimeUnit.SECONDS);
                        }
                    });
                }
            });
        }
        server.start();
    }

    public static void stop() {
        server.stop(0);
    }

    static void respond(HttpExchange httpRequest, int statusCode, String response, String contentType) throws IOException {
        byte[] responseBytes = response.getBytes();
        Headers headers = httpRequest.getResponseHeaders();
        headers.add("Content-type", contentType);
        headers.add("Server", "Sprummlbot Webserver v" + Vars.VERSION + " by Scrumplex");
        httpRequest.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream out = httpRequest.getResponseBody();
        out.write(responseBytes);
        out.close();
    }

    static void respond(HttpExchange httpRequest, byte[] response, String contentType) throws IOException {
        Headers headers = httpRequest.getResponseHeaders();
        headers.add("Content-type", contentType);
        headers.add("Server", "Sprummlbot Webserver v" + Vars.VERSION + " by Scrumplex");
        httpRequest.sendResponseHeaders(200, response.length);
        OutputStream out = httpRequest.getResponseBody();
        out.write(response);
        out.close();
    }

}
