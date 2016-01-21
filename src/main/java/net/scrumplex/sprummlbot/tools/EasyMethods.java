package net.scrumplex.sprummlbot.tools;

import javax.xml.ws.http.HTTPException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Random;


/**
 * This class has some easy to use methods.
 * It is only for effective use.
 */
public class EasyMethods {

    /**
     * Checks if String is Integer
     *
     * @param str String
     * @return True if it is an Integer
     */
    public static boolean isInteger(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * This method converts http links to nromal links.
     * Example: Hello%20Alex -> Hello Alex
     *
     * @param query The string which will be converted
     * @return Returns the converted string
     */
    public static String decodeHTTPString(String query) {
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            /**
             * Won't happen :O
             */
            Exceptions.handle(e, "Encoding not supported!");
        }
        return query;
    }

    /**
     * Generates a random string without speical chars
     *
     * @param len Length of the generated string
     * @return returns generated string
     */
    public static String randomString(int len) {
        return randomString(len, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static String randomString(int len, String contains) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(contains.charAt(random.nextInt(contains.length())));
        return sb.toString();
    }

    /**
     * This method returns the full stacktrace of an Throwable
     *
     * @param e Throwable, which will be converted
     * @return Full Stacktrace
     */
    public static String convertThrowableToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Writes defined String into File
     *
     * @param file     File which will be changed
     * @param contents Contents which will be put in the file
     * @throws IOException
     */
    public static void writeToFile(File file, String contents) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(contents);
        bw.close();
    }

    /**
     * Reads the contants of an file and returns it.
     *
     * @param file File which will be read.
     * @return Contents of file.
     */
    public static String readFile(File file) throws IOException {
        String read = "";
        for (String line : Files.readAllLines(file.toPath(), Charset.defaultCharset())) {
            read += line + "\n";
        }
        return read;
    }

    /**
     * Returns the WAN IP of this Bot
     *
     * @return Returns the WAN IP of this Bot
     * @throws IOException
     */
    public static String getPublicIP() throws IOException {
        URL url = new URL("https://api.ipify.org/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) {
            throw new HTTPException(conn.getResponseCode());
        }
        conn.connect();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = rd.readLine();
        rd.close();
        if (line == null) {
            throw new IOException("No Response");
        }
        return line;
    }
}
