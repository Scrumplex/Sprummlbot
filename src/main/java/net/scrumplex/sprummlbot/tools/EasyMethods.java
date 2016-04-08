package net.scrumplex.sprummlbot.tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class EasyMethods {

    public static boolean isInteger(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static String decodeHTTPString(String query) {
        try {
            query = URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Exceptions.handle(e, "Encoding not supported!");
        }
        return query;
    }

    public static String randomString() {
        return randomString(10);
    }

    private static String randomString(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }

    static String convertThrowableToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static void writeToFile(File file, String contents) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(contents);
        bw.close();
    }

    public static String getPublicIP() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) {
            return "0.0.0.0";
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

    public static String md5Hex(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        return new String(convertStreamToByteArray(is));
    }

    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[65535];
        for (int len; (len = is.read(buffer)) != -1; )
            baos.write(buffer, 0, len);
        baos.flush();
        is.close();
        return baos.toByteArray();
    }

    public static void writeByteArrayToFile(File f, byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bytes);
        fos.close();
    }
    public static byte[] readByteArrayFromFile(File f) throws IOException {
        if(!f.exists()) {
            f.createNewFile();
            return new byte[0];
        }
        FileInputStream fos = new FileInputStream(f);
        return convertStreamToByteArray(fos);
    }
}
