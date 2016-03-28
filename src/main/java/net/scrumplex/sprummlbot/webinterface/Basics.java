package net.scrumplex.sprummlbot.webinterface;

class Basics {

    static String getRedirection(String to) {
        return "<!DOCTYPE html><html>" + "<head>\n</head>\n" +
                "<body>\n" +
                "<script type=\"text/javascript\">\n" +
                "location.replace(\"" + to + "\");\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }

}
