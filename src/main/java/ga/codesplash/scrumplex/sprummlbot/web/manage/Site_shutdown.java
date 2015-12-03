package ga.codesplash.scrumplex.sprummlbot.web.manage;

public class Site_shutdown {

    public String content = "";

    public Site_shutdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<head>");
        sb.append("<title>Shutting down...</title>");
        sb.append(
                "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.1/css/materialize.min.css\"/>");
        sb.append(
                "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/js/materialize.min.js\"></script>");
        sb.append("</head>");
        sb.append("<center>");
        sb.append("<h2>The Sprummlbot is shutting down...!</h2>");
        sb.append("</body>");
        sb.append("</html>");
        content = sb.toString();
    }

}
