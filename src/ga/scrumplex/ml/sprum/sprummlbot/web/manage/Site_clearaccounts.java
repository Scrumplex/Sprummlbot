package ga.scrumplex.ml.sprum.sprummlbot.web.manage;

public class Site_clearaccounts {

	public String content = new String();
	public StringBuilder sb = new StringBuilder();

	public Site_clearaccounts() {
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