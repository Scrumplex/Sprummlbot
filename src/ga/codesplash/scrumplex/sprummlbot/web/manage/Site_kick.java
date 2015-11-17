package ga.codesplash.scrumplex.sprummlbot.web.manage;

public class Site_kick {

	public String content = "";
	public StringBuilder sb = new StringBuilder();

	public Site_kick(boolean done) {
		sb.append("<!DOCTYPE html>");
		sb.append("<head>");
		sb.append("<title>Sprummlbot</title>");
		sb.append("<meta http-equiv=\"refresh\" content=\"0; url=/manage\" />");
		sb.append("</head>");
		sb.append("<body>");
		if (done) {
			sb.append("<script>alert(\"Kicked!\");</script>");

		} else {
			sb.append("<script>alert(\"Error! Does the Server Query has rights?\");</script>");
		}
		sb.append("</body>");
		sb.append("</html>");

		content = sb.toString();
	}

}