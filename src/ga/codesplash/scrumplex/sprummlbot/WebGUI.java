package ga.codesplash.scrumplex.sprummlbot;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * Class for running an web-interface
 */
public class WebGUI {
	public static HttpServer SERVER = null;

	/**
	 * Starts Webservice
	 * @throws IOException
     */
	public static void start() throws IOException {
		SERVER = HttpServer.create(new InetSocketAddress(Vars.WEBINTERFACE_PORT), 0);
		HttpContext hc = SERVER.createContext("/", new WebGUIHandler());
		hc.setAuthenticator(new BasicAuthenticator("") {

			@Override
			public boolean checkCredentials(String user, String pw) {
				return (Vars.AVAILABLE_LOGINS.containsKey(user) && Vars.AVAILABLE_LOGINS.get(user).equals(pw));
			}
		});
		SERVER.start();
	}
}