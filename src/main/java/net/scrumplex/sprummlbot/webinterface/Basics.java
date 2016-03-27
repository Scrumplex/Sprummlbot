package net.scrumplex.sprummlbot.webinterface;

class Basics {

    public static String getRedirection(String to) {
        String sb = "<!DOCTYPE html><html>" + "<head>\n</head>\n" +
                "<body>\n" +
                "<script type=\"text/javascript\">\n" +
                "location.replace(\"" + to + "\");\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
        return sb;
    }

}
