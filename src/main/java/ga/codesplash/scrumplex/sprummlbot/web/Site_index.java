package ga.codesplash.scrumplex.sprummlbot.web;

public class Site_index {

    public String content = "";

    public Site_index() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<head>");
        sb.append("<title>Sprummlbot</title>");
        sb.append("<meta http-equiv=\"refresh\" content=\"0; url=/manage\" />");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("</body>");
        sb.append("</html>");

        content = sb.toString();
    }

}