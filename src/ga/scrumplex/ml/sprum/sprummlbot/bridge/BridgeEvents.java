package ga.scrumplex.ml.sprum.sprummlbot.bridge;

import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

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
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

import ga.scrumplex.ml.sprum.sprummlbot.Config;

public class BridgeEvents {

	private static TS3Listener listener = null;

	public static void createEvents(final DataOutputStream out) {
		listener = new TS3Listener() {

			@Override
			public void onTextMessage(TextMessageEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getInvokerId()));
				props.put("uid", e.getInvokerUniqueId());
				props.put("nick", e.getInvokerName());
				props.put("msg", e.getMessage());
				props.put("type", "text");
				TCPServer.sendEvent(out, TS3EventType.TEXT_PRIVATE, props);
			}

			@Override
			public void onServerEdit(ServerEditedEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getInvokerId()));
				props.put("uid", e.getInvokerUniqueId());
				props.put("nick", e.getInvokerName());
				props.put("type", "editserver");
				TCPServer.sendEvent(out, TS3EventType.SERVER, props);
			}

			@Override
			public void onClientMoved(ClientMovedEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("mcid", String.valueOf(e.getInvokerId()));
				props.put("muid", e.getInvokerUniqueId());
				props.put("mnick", e.getInvokerName());
				props.put("cid", String.valueOf(e.getClientId()));
				props.put("type", "clientmove");
				TCPServer.sendEvent(out, TS3EventType.SERVER, props);
			}

			@Override
			public void onClientLeave(ClientLeaveEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getClientId()));
				props.put("type", "clientleave");
				TCPServer.sendEvent(out, TS3EventType.SERVER, props);
			}

			@Override
			public void onClientJoin(ClientJoinEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getClientId()));
				props.put("uid", e.getInvokerUniqueId());
				props.put("nick", e.getInvokerName());
				props.put("type", "clientjoin");
				TCPServer.sendEvent(out, TS3EventType.SERVER, props);
			}

			@Override
			public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getInvokerId()));
				props.put("uid", e.getInvokerUniqueId());
				props.put("nick", e.getInvokerName());
				props.put("channelid", String.valueOf(e.getChannelId()));
				props.put("type", "channelpwchange");
				TCPServer.sendEvent(out, TS3EventType.CHANNEL, props);
			}

			@Override
			public void onChannelMoved(ChannelMovedEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getInvokerId()));
				props.put("uid", e.getInvokerUniqueId());
				props.put("nick", e.getInvokerName());
				props.put("channelid", String.valueOf(e.getChannelId()));
				props.put("type", "channelmove");
				TCPServer.sendEvent(out, TS3EventType.CHANNEL, props);
			}

			@Override
			public void onChannelEdit(ChannelEditedEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getInvokerId()));
				props.put("uid", e.getInvokerUniqueId());
				props.put("nick", e.getInvokerName());
				props.put("channelid", String.valueOf(e.getChannelId()));
				props.put("type", "channeledit");
				TCPServer.sendEvent(out, TS3EventType.CHANNEL, props);
			}

			@Override
			public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
			}

			@Override
			public void onChannelDeleted(ChannelDeletedEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getInvokerId()));
				props.put("uid", e.getInvokerUniqueId());
				props.put("nick", e.getInvokerName());
				props.put("channelid", String.valueOf(e.getChannelId()));
				props.put("type", "channeldel");
				TCPServer.sendEvent(out, TS3EventType.CHANNEL, props);
			}

			@Override
			public void onChannelCreate(ChannelCreateEvent e) {
				Map<String, String> props = new HashMap<>();
				props.put("cid", String.valueOf(e.getInvokerId()));
				props.put("uid", e.getInvokerUniqueId());
				props.put("nick", e.getInvokerName());
				props.put("channelid", String.valueOf(e.getChannelId()));
				props.put("type", "channelcreate");
				TCPServer.sendEvent(out, TS3EventType.CHANNEL, props);
			}
		};
	}

	public static void registerEvents() {
		Config.API.addTS3Listeners(listener);
	}

	public static void unregisterEvents() {
		Config.API.removeTS3Listeners(listener);
	}

}
