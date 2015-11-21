package ga.codesplash.scrumplex.sprummlbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.ws.http.HTTPException;

public class Updater {

	String link = null;
	int current = 0;
	int remote = 0;

	/**
	 * Creates an Updater
	 * @param link
	 * Link to the newest versionid file
	 * @param currentversion
	 * Local versionid
     */
	public Updater(String link, int currentversion) {
		this.link = link;
		this.current = currentversion;
	}

	/**
	 * Checks if update is available.
	 * @return
	 * Returns if update is available
	 * @throws Exception
     */
	boolean isupdateavailable() throws Exception {
		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		if(conn.getResponseCode() == 403) {
			throw new HTTPException(403);
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
