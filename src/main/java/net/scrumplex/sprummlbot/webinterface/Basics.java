package net.scrumplex.sprummlbot.webinterface;

class Basics {

    public static String getRedirection(String to) {
        String sb = "<!doctype html><html>" + "<head>\n</head>\n" +
                "<body>\n" +
                "<script>location.replace(\"" + to + "\");\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
        return sb;
    }

}
