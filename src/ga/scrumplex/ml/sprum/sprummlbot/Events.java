package ga.scrumplex.ml.sprum.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

public class Events extends Config{
	
	public Events() {
		api.registerAllEvents();
		api.addTS3Listeners(new TS3Listener() 
		{
			public void onTextMessage(TextMessageEvent e) {
				if (e.getInvokerId() != qID) {
					String message = e.getMessage().toLowerCase();
					Client c = getClientbyID(e.getInvokerId());
					message = message.replace("<video", "");
					if(message.startsWith("!")) {
						if(!Commands.handle(message, c)) {
							api.sendPrivateMessage(c.getId(), Messages.get("unknown-command"));
						}
						Logger.out(message + " received from " + e.getInvokerName());
					}
				}
			}

			public void onServerEdit(ServerEditedEvent e) {
				if(Config.qID != e.getInvokerId()) {
					Client cl = getClientbyID(e.getInvokerId());
					ClientInfo cli = api.getClientInfo(cl.getId());
					Logger.warn("The user " + e.getInvokerName() + " edited the Server! User info: uid=" + cl.getUniqueIdentifier() + " ip=" + cli.getIp() + " country=" + cl.getCountry() + ".");
				}
			}

			public void onClientMoved(ClientMovedEvent e) {
				// ...
			}

			public void onClientLeave(ClientLeaveEvent e) {
				if(idle.containsKey(e.getInvokerId())) {
					idle.remove(e.getInvokerId());
				}
			}

			public void onClientJoin(ClientJoinEvent e) {
				api.sendPrivateMessage(e.getClientId(), Messages.get("welcome") + e.getClientNickname());
				api.sendPrivateMessage(e.getClientId(), Messages.get("commandslist") + Commands.commandslist.toString().replace("[", "").replace("]", ""));
			}

			public void onChannelEdit(ChannelEditedEvent e) {
				// ...
			}

			public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
				// ...
			}

			public void onChannelCreate(ChannelCreateEvent e) {
				// ...
			}

			public void onChannelDeleted(ChannelDeletedEvent e) {
				// ...
			}

			public void onChannelMoved(ChannelMovedEvent e) {
				// ...
			}

			public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
				// ...
			}
		});
	}
}
