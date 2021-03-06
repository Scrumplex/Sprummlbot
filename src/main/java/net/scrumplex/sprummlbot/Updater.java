package net.scrumplex.sprummlbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class Updater {

    private final String link;
    private final double currentVersion;

    Updater() {
        this.link = "http://nossl.sprum.ml/version.php?getNewestBuild";
        this.currentVersion = Vars.BUILD_ID;
    }

    boolean isUpdateAvailable() throws Exception {
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(500);
        conn.setReadTimeout(500);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 404 || conn.getResponseCode() == 403 || conn.getResponseCode() == 500)
            throw new Exception("Couldn't get newest version: HTTP Code: " + conn.getResponseCode());

        conn.connect();
        String line;
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            while ((line = rd.readLine()) != null) {
                double remote = Double.parseDouble(line);
                if (currentVersion < remote) {
                    return true;
                }
            }
        } finally {
            conn.disconnect();
        }
        return false;
    }

}
