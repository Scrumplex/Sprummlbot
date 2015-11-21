package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public interface SprummlPlugin {

	boolean init(String version);
	
	void end();
	
	void handleEvent(SprummlEventType type, BaseEvent event);
	
	boolean handleCommand(Client sender, String command, String[] args);

}
