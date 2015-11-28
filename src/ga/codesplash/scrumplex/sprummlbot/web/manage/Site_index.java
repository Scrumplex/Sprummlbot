package ga.codesplash.scrumplex.sprummlbot.web.manage;

import java.util.ArrayList;
import java.util.List;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.codesplash.scrumplex.sprummlbot.Vars;

public class Site_index {

	public String content = "";

    public Site_index() {
		List<String> clients = new ArrayList<>();
		for (Client c : Vars.API.getClients()) {
			if (c.getId() == Vars.QID) {
				clients.add("<tr><td>" + c.getNickname() + "</td><td>" + c.getId() + "</td><td>"
						+ c.getUniqueIdentifier()
						+ "</td><td><a class=\"waves-effect waves-light btn\" onclick=\"doShutdown();\"><i class=\"material-icons right\">power_settings_new</i>Shutdown Sprummlbot</a></td>");
			} else {
				clients.add("<tr><td>" + c.getNickname() + "</td><td>" + c.getId() + "</td><td>"
						+ c.getUniqueIdentifier()
						+ "</td><td><a class=\"waves-effect waves-light btn\" href=\"/manage/action/kick/!msg=Kicked,userid="
						+ c.getId()
						+ "\"><i class=\"material-icons right\">cancel</i>Kick</a> <a class=\"waves-effect waves-light btn\" href=\"/manage/action/ban/!msg=Banned,userid="
						+ c.getId()
						+ ",time=3600\"><i class=\"material-icons right\">report</i>Ban 1 hour</a> <a class=\"waves-effect waves-light btn\" onclick=\"sendPrivateMessage("
						+ c.getId()
						+ ")\" href=\"javascript:void(0);\"><i class=\"material-icons right\">notifications</i>Send Message</a> <a class=\"waves-effect waves-light btn\" onclick=\"poke("
						+ c.getId()
						+ ")\" href=\"javascript:void(0);\"><i class=\"material-icons right\">notifications_active</i>Poke</a></td>");
			}
		}
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
		sb.append("<html>");
		sb.append("<head lang=\"en\">");
		sb.append("    <link href=\"http://fonts.googleapis.com/icon?family=Material+Icons\" rel=\"stylesheet\">");
		sb.append(
				"    <link type=\"text/css\" rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.1/css/materialize.min.css\"  media=\"screen,projection\"/>");
		sb.append("    <meta charset=\"UTF-8\">");
		sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
		sb.append("    <title>Sprummlbot - Home</title>");
		sb.append("</head>");
		sb.append("");
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
		sb.append("<div class=\"fixed-action-btn\" style=\"bottom: 45px; right: 24px;\">");
		sb.append("    <a class=\"btn-floating waves-effect waves-light btn-large red\">");
		sb.append("        <i class=\"large material-icons\">settings</i>");
		sb.append("    </a>");
		sb.append("    <ul>");
		sb.append(
				"        <li><a onclick=\"doShutdown();\" class=\"btn-floating red waves-effect waves-light tooltipped\" data-position=\"left\" data-delay=\"50\" data-tooltip=\"Shutdown Sprummlbot\"><i class=\"material-icons\">power_settings_new</i></a></li>");
		sb.append(
				"        <li><a onclick=\"doReload();\" class=\"btn-floating green darken-1 waves-effect waves-light tooltipped\" data-position=\"left\" data-delay=\"50\" data-tooltip=\"Reload data\"><i class=\"material-icons\">loop</i></a></li>");
		sb.append(
				"        <li><a onclick=\"doLog();\" class=\"btn-floating blue waves-effect waves-light tooltipped\" data-position=\"left\" data-delay=\"50\" data-tooltip=\"Sprummlbot Logs\"><i class=\"material-icons\">warning</i></a></li>");
		sb.append("    </ul>");
		sb.append("</div>");
		sb.append("");
		if (Vars.AVAILABLE_LOGINS.size() > 1) {
			sb.append("<div class=\"col s12 m4 l4 center-align\" id=\"warn_active_acc\">");
			sb.append("    <div class=\"card blue-grey darken-1\">");
			sb.append("        <div class=\"card-content white-text\">");
			sb.append("            <span class=\"card-title\">Warning!</span>");
			sb.append("            <p>There are ").append(Vars.AVAILABLE_LOGINS.size()).append(" Accounts available!</p>");
			sb.append("        </div>");
			sb.append("        <div class=\"card-action\">");
			sb.append("            <a onclick=\"doClearClients()\">Remove all</a>");
			sb.append("            <a onclick=\"$('#warn_active_acc').remove()\">Dismiss</a>");
			sb.append("        </div>");
			sb.append("    </div>");
			sb.append("</div>");
		}
		sb.append("<div class=\"col center\" style=\"    display: flex;");
		sb.append("    flex-direction: row;");
		sb.append("    flex-wrap: wrap;");
		sb.append("    justify-content: center;");
		sb.append("    align-items: center;");
		sb.append("    margin: 1% 1%;\">");
		sb.append(
				"    <div class=\"row teal\" style=\"float: left; width: 250px; height: 250px; border: medium none; border-radius: 20px; display: inline-block; line-height: 36px; text-transform: uppercase; vertical-align: middle; margin: 1% 1%; align-items: center; text-align: center;\">");
		sb.append("        <h5 style=\"color:#fff\">Users</h5>");
		sb.append("        <p style=\"color:#fff\">There are currently ").append(Vars.API.getServerInfo().getClientsOnline() - 1).append(" user(s) and the Sprummlbot on your server!</p>");
		sb.append("    </div>");
		sb.append("");
		sb.append(
				"    <div class=\"row blue-grey\" style=\"color: #fff; float: left; width: 250px; height: 250px; border: medium none; border-radius: 20px; display: inline-block; line-height: 36px; text-transform: uppercase; vertical-align: middle; margin: 1% 1%; align-items: center; text-align: center;\">");
		sb.append("        <h5 style=\"color:#fff\">Banned Users</h5>");
		int bancount = 0;
		if (Vars.API.getBans() != null) {
			bancount = Vars.API.getBans().size();
		}
		sb.append("        <p style=\"color:#fff\">There are currently ").append(bancount).append(" ban entries!</p>");
		if (bancount != 0) {
			sb.append("        <a href=\"/manage/bans/\" style=\"color:#fff\">Click here</a> to delete the bans.");
		}
		sb.append("    </div>");
		sb.append("</div>");
		sb.append("");
		sb.append("<div class=\"row\">");
		sb.append("    <div class=\"col s12\">");
		sb.append("        <table class=\"bordered centered\">");
		sb.append("            <thead>");
		sb.append("            <tr>");
		sb.append("                <th data-field=\"name\">Nickname</th>");
		sb.append("                <th data-field=\"id\">User ID</th>");
		sb.append("                <th data-field=\"uid\">Unique ID</th>");
		sb.append("                <th data-field=\"actions\">Actions</th>");
		sb.append("            </tr>");
		sb.append("            </thead>");
		sb.append("            <tbody>");
		for (String line : clients) {
			sb.append(line);
		}
		sb.append("            </tbody>");
		sb.append("        </table>");
		sb.append("    </div>");
		sb.append("</div>");
		sb.append("");
		sb.append("<script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-2.1.1.min.js\"></script>");
		sb.append(
				"<script type=\"text/javascript\" src=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.1/js/materialize.min.js\"></script>");
		sb.append("<script type=\"text/javascript\">");
		sb.append("    function doClearClients(){");
		sb.append("        location.href = '/manage/action/clearaccounts/';");
		sb.append("    }");
		sb.append("    function sendPrivateMessage(id){");
		sb.append("        var msg = prompt('Please type in the message you want to send', '');");
		sb.append("        msg.replace(/!/g, '%21');");
		sb.append("        console.log(msg);");
		sb.append("        console.log(encodeURIComponent(msg));");
		sb.append(
				"        location.href = '/manage/action/sendpriv/!userid=' + id + ',msg=' + encodeURIComponent(msg);");
		sb.append("    }");
		sb.append("    function poke(id){");
		sb.append("        var msg = prompt('Please type in the message you want to poke', '');");
		sb.append("        msg.replace(/!/g, '%21');");
		sb.append("        console.log(msg);");
		sb.append("        console.log(encodeURIComponent(msg));");
		sb.append("        location.href = '/manage/action/poke/!userid=' + id + ',msg=' + encodeURIComponent(msg);");
		sb.append("    }");
		sb.append("");
		sb.append("    function doShutdown(){");
		sb.append("        location.href = '/manage/shutdown/';");
		sb.append("    }");
		sb.append("");
		sb.append("    function doReload(){");
		sb.append("        location.reload();");
		sb.append("    }");
		sb.append("");
		sb.append("    function doLog(){");
		sb.append("        location.href = '/manage/log/';");
		sb.append("    }");
		sb.append("</script>");
		sb.append("</body>");
		sb.append("</html>");

		content = sb.toString();
	}

}