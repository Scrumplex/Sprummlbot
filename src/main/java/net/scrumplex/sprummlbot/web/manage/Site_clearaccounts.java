package net.scrumplex.sprummlbot.web.manage;

public class Site_clearaccounts {

    public String content = "";

    public Site_clearaccounts() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<head>");
        sb.append("<title>Sprummlbot</title>");
        sb.append("<meta http-equiv=\"refresh\" content=\"0; url=/manage\" />");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<script>alert(\"Cleared the list! Usually you will need to create an account!\");</script>");
        sb.append("</body>");
        sb.append("</html>");

        content = sb.toString();
    }

}