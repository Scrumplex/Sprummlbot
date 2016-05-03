package net.scrumplex.sprummlbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class Updater {

    private String link = null;
    private int currentVersion = 0;

    Updater() {
        this.link = "http://nossl.sprum.ml/version.txt";
        this.currentVersion = Vars.BUILD_ID;
    }

    boolean isUpdateAvailable() throws Exception {
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(200);
        conn.setReadTimeout(200);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 404 || conn.getResponseCode() == 403 || conn.getResponseCode() == 500)
            throw new Exception("Couldn't get newest version: HTTP Code: " + conn.getResponseCode());

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
