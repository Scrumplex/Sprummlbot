package ga.scrumplex.ml.sprum.sprummlbot.web.manage;

import ga.scrumplex.ml.sprum.sprummlbot.Logger;

public class Site_log {

	public String content = new String();
	public StringBuilder sb = new StringBuilder();

	public Site_log() {
		sb.append("<!DOCTYPE html>");
		sb.append("<html lang=\"en\">");
		sb.append("<head>");
		sb.append("    <link href=\"http://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">");
		sb.append(
				"    <link type=\"text/css\" rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.1/css/materialize.min.css\"  media=\"screen,projection\"/>");
		sb.append("    <meta charset=\"UTF-8\">");
		sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
		sb.append("    <title>Sprummlbot - Log</title>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<div class=\"navbar-fixed\">");
		sb.append("    <nav>");
		sb.append("        <div class=\"nav-wrapper teal lighten-2\">");
		sb.append("            <a href=\"/\" class=\"brand-logo\">Sprummlbot</a>");
		sb.append("            <ul class=\"right hide-on-med-and-down\">");
		sb.append(
				"                <li><a href=\"/logout/\"><i class=\"material-icons left\">settings_power</i>Logout</a></li>");
		sb.append("            </ul>");
		sb.append("        </div>");
		sb.append("    </nav>");
		sb.append("</div>");
		sb.append("");
		sb.append("<div class=\"col\" style=\"margin: 1% 1%;\">");
		sb.append("");
		sb.append(Logger.log.toString());
		sb.append("");
		sb.append("</div>");
		sb.append("");
		sb.append("<script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-2.1.1.min.js\"></script>");
		sb.append(
				"<script type=\"text/javascript\" src=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.1/js/materialize.min.js\"></script>");
		sb.append("</body>");
		sb.append("</html>");

		content = sb.toString();
	}

}