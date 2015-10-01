package ga.scrumplex.ml.sprum.sprummlbot;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class WebGUI {
	public static HttpServer server = null;
	public static void start() throws IOException {
		server = HttpServer.create(new InetSocketAddress(Config.webport), 0);
		HttpContext hc = server.createContext("/", new WebGUIHandler());
		hc.setAuthenticator(new BasicAuthenticator("") {
			
			@Override
			public boolean checkCredentials(String user, String pw) {
				if(WebGUILogins.available.containsKey(user) && WebGUILogins.available.get(user).equals(pw)) {
					return true;
				}
				return false;
			}
		});
		server.start();
	}
}