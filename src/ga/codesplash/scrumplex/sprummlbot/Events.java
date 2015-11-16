package ga.codesplash.scrumplex.sprummlbot;

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

import ga.codesplash.scrumplex.sprummlbot.configurations.Messages;
import ga.codesplash.scrumplex.sprummlbot.plugins.SprummlEventType;
import ga.codesplash.scrumplex.sprummlbot.plugins.SprummlPlugin;

public class Events {

	public static void start() {
		Vars.API.registerAllEvents();
		Vars.API.addTS3Listeners(new TS3Listener() {
			public void onTextMessage(TextMessageEvent e) {

				if (e.getInvokerId() != Vars.QID) {
					String message = e.getMessage().toLowerCase();
					Client c = Vars.API.getClientInfo(e.getInvokerId());
					message = message.replace("<video", "");
					if (message.startsWith("!")) {
						if (!Commands.handle(message, c)) {
							Vars.API.sendPrivateMessage(c.getId(), Messages.get("unknown-command"));
						}
						System.out.println(message + " received from " + e.getInvokerName());
					} else {
						for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
							plugin.handleEvent(SprummlEventType.MESSAGE, e);
						}
					}
				}
			}

			public void onServerEdit(ServerEditedEvent e) {
				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.VIRTUAL_SERVER_EDIT, e);
				}

				if (Vars.QID != e.getInvokerId()) {
					Client cl = Vars.API.getClientInfo(e.getInvokerId());
					ClientInfo cli = Vars.API.getClientInfo(cl.getId());
					System.out.println("The user " + e.getInvokerName() + " edited the Server! User info: uid="
							+ cl.getUniqueIdentifier() + " ip=" + cli.getIp() + " country=" + cl.getCountry() + ".");
				}
			}

			public void onClientMoved(ClientMovedEvent e) {
				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CLIENT_MOVE, e);
				}
			}

			public void onClientLeave(ClientLeaveEvent e) {
				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CLIENT_LEAVE, e);
				}
			}

			public void onClientJoin(ClientJoinEvent e) {
				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CLIENT_JOIN, e);
				}
				Vars.API.sendPrivateMessage(e.getClientId(), Messages.get("welcome") + e.getClientNickname());
				Vars.API.sendPrivateMessage(e.getClientId(), Messages.get("commandslist") + Commands.AVAILABLE_COMMANDS);
			}

			public void onChannelEdit(ChannelEditedEvent e) {

				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CHANNEL_EDIT, e);
				}
			}

			public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {

				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CHANNEL_DESC_CHANGED, e);
				}
			}

			public void onChannelCreate(ChannelCreateEvent e) {

				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CHANNEL_CREATE, e);
				}
			}

			public void onChannelDeleted(ChannelDeletedEvent e) {

				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CHANNEL_DELETE, e);
				}
			}

			public void onChannelMoved(ChannelMovedEvent e) {

				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CHANNEL_MOVE, e);
				}
			}

			public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {

				for (SprummlPlugin plugin : SprummlbotLoader.pl.plugins.values()) {
					plugin.handleEvent(SprummlEventType.CHANNEL_PW_CHANGED, e);
				}
			}
		});
	}
}
