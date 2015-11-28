package ga.codesplash.scrumplex.sprummlbot.web.manage;

public class Site_ban {

    public String content = "";

    public Site_ban(boolean done) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<head>");
        sb.append("<title>Sprummlbot</title>");
        sb.append("<meta http-equiv=\"refresh\" content=\"0; url=/manage\" />");
        sb.append("</head>");
        sb.append("<body>");
        if (done) {
            sb.append("<script>alert(\"Banned!\");</script>");

        } else {
            sb.append("<script>alert(\"Error!\");</script>");
        }
        sb.append("</body>");
        sb.append("</html>");

        content = sb.toString();
    }

}