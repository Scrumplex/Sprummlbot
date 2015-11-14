package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public interface SprummlPlugin {

	public boolean init(String version);
	
	public void end();
	
	public void handleEvent(SprummlEventType type, BaseEvent event);
	
	public boolean handleCommand(Client sender, String command, String[] args);
	
}
