package ga.scrumplex.ml.sprum.sprummlbot.web.manage;

public class ban {

	public String content = new String();
	public StringBuilder sb = new StringBuilder();
	
	public ban(boolean done) {
		sb.append("<!DOCTYPE html>");
		sb.append("<head>");
		sb.append("<title>Sprummlbot</title>");
		sb.append("<meta http-equiv=\"refresh\" content=\"0; url=/manage\" />");
		sb.append("</head>");
		sb.append("<body>");
		if(done) {
			sb.append("<script>alert(\"Banned!\");</script>");
			
		} else {
			sb.append("<script>alert(\"Error!\");</script>");
		}
		sb.append("</body>");
		sb.append("</html>");
		
		
		content = sb.toString();
	}
	
}