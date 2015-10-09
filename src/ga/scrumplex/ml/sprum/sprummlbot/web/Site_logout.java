package ga.scrumplex.ml.sprum.sprummlbot.web;

import ga.scrumplex.ml.sprum.sprummlbot.WebGUILogins;

public class Site_logout {

	public String content = new String();
	public StringBuilder sb = new StringBuilder();

	public Site_logout(String user) {
		sb.append("<!DOCTYPE html>");
		sb.append("<head>");
		sb.append("<title>Logout - Sprummlbot</title>");
		sb.append(
				"<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.1/css/materialize.min.css\"/>");
		sb.append(
				"<script src=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/js/materialize.min.js\"></script>");
		sb.append("</head>");
		sb.append("<center>");
		sb.append("<h2>You have been logged out!</h2>");
		sb.append("Your account has been deleted please request a new one! <p>");
		sb.append("<a href=\"/\">Login</a> again");
		sb.append("</body>");
		sb.append("</html>");
		content = sb.toString();
		WebGUILogins.AVAILABLE.remove(user);
	}

}
