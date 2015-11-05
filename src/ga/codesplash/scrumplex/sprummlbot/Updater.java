package ga.codesplash.scrumplex.sprummlbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

	String link = null;
	int current = 0;
	int remote = 0;

	public Updater(String link, int currentversion) {
		this.link = link;
		this.current = currentversion;
	}

	boolean isupdateavailable() throws Exception {
		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		if(conn.getResponseCode() == 403) {
			throw new IOException("Server returned 403");
		}
		conn.connect();
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			remote = Integer.parseInt(line);
			if (current < remote) {
				return true;
			}
		}
		rd.close();
		return false;
	}

}
