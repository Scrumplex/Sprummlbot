package net.scrumplex.sprummlbot.webinterface;

class Basics {

    public static String getRedirector(String to) {
        StringBuilder sb = new StringBuilder("<!doctype html>");
        sb.append("<head>\n</head>\n");
        sb.append("<body>\n");
        sb.append("<script> location.href = \"").append(to).append("\";\n");
        sb.append("</script>\n");
        sb.append("</body>\n");
        sb.append("</html>");
        return sb.toString();
    }

}
