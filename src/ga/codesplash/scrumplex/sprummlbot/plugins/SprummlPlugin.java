package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public interface SprummlPlugin {

	/**
	 * Fires if plugin loads
	 * @param version
	 * Version of current Sprummlbot
	 * @return
	 * If it returns false the plugin will be unloaded!
     */
	boolean init(String version);

	/**
	 * Fires if Sprummlbots shuts down / plugin gets unloaded
	 */
	void end();

	/**
	 * Fires if an event happens
	 * @param type
	 * Defines the Event Type
	 * @param event
	 * Defines the Event (You will need to cast it)
     */
	void handleEvent(SprummlEventType type, BaseEvent event);

	/**
	 * Fires if an non default command has to be handled (Will only fire if this plugin has registered any command)
	 * @param sender
	 * Invoker
	 * @param command
	 * Requested Command
	 * @param args
	 * Arguments of the command
     * @return
	 * Retruns if command was handled
     */
	boolean handleCommand(Client sender, String command, String[] args);
}