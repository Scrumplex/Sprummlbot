package ga.scrumplex.ml.sprum.sprummlbot.web.manage;

public class sendmessage {

	public String content = new String();
	public StringBuilder sb = new StringBuilder();
	
	public sendmessage() {
		sb.append("<!DOCTYPE html>");
		sb.append("<head>");
		sb.append("<title>Sprummlbot</title>");
		sb.append("<meta http-equiv=\"refresh\" content=\"0; url=/manage\" />");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<script>alert(\"Message was sent!\");</script>");
		sb.append("</body>");
		sb.append("</html>");
		
		
		content = sb.toString();
	}
	
}