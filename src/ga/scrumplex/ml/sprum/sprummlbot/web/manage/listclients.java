package ga.scrumplex.ml.sprum.sprummlbot.web.manage;

import java.util.ArrayList;
import java.util.List;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import ga.scrumplex.ml.sprum.sprummlbot.Config;

public class listclients {

	public String content = new String();
	public StringBuilder sb = new StringBuilder();
	
	public listclients() {
	    List<String> clients = new ArrayList<String>();
	    
	    for(Client c : Config.api.getClients()) {
	    	ClientInfo ci = Config.api.getClientInfo(c.getId());
	    	clients.add("<p>Name=" + c.getNickname() + ", IP=" + ci.getIp() + ", ID=" + c.getId() + ", UID=" + c.getUniqueIdentifier() + "</p>");
	    }
		sb.append("<!DOCTYPE html>");
		sb.append("<head>");
		sb.append("<title>Sprummlbot - Clients</title>");		
		sb.append("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/css/materialize.min.css\"/>");
		sb.append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/js/materialize.min.js\"></script>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<nav>");
		sb.append("<div class=\"nav-wrapper\">");
      	sb.append("<a href=\"#\" class=\"brand-logo\">Schrumpelbot</a>");
      	sb.append("<ul id=\"nav-mobile\" class=\"right hide-on-med-and-down\">");
        sb.append("<li><a href=\"/logout\">Logout</a></li>");
        sb.append("</ul>");
      	sb.append("</div>");
    	sb.append("</nav>");
		sb.append("<center>");
		for(String clientinfo : clients) {
			sb.append(clientinfo);
		}
		sb.append("</body>");
		sb.append("</html>");
		content = sb.toString();
	}
	
}