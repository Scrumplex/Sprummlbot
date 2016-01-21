package net.scrumplex.sprummlbot;

import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is an update chacker.
 */
class Updater {

    private String link = null;
    private int currentVersion = 0;

    /**
     * Creates an Updater
     *
     * @param link           Link to the newest versionid file
     * @param currentversion Local versionid
     */
    public Updater(String link, int currentversion) {
        this.link = link;
        this.currentVersion = currentversion;
    }

    /**
     * Checks if update is available.
     *
     * @return Returns if update is available
     * @throws IOException
     */
    public boolean isUpdateAvailable() throws IOException {
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) {
            throw new HTTPException(conn.getResponseCode());
        }
        conn.connect();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            int remoteVersion = Integer.parseInt(line);
            if (currentVersion < remoteVersion) {
                return true;
            }
        }
        rd.close();
        return false;
    }

}