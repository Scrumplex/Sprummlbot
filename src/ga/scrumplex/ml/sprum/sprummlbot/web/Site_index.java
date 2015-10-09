package ga.scrumplex.ml.sprum.sprummlbot.web;

public class Site_index {

	public String content = new String();
	public StringBuilder sb = new StringBuilder();

	public Site_index() {
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

}// 666