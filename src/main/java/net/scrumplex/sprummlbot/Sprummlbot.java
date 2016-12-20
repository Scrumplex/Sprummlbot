package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import net.scrumplex.sprummlbot.core.Clients;
import net.scrumplex.sprummlbot.module.ModuleManager;
import net.scrumplex.sprummlbot.plugins.CommandManager;
import net.scrumplex.sprummlbot.plugins.PluginManager;
import net.scrumplex.sprummlbot.plugins.events.EventManager;
import net.scrumplex.sprummlbot.service.MainService;
import net.scrumplex.sprummlbot.wrapper.State;

import java.util.concurrent.TimeUnit;

public class Sprummlbot {

    private static Sprummlbot sprummlbot;
    private CommandManager commandManager;
    private ModuleManager moduleManager;
    private PluginManager pluginManager;
    private TS3ApiAsync ts3ApiAsync;
    private TS3Api ts3Api;
    private MainService service;
    private EventManager mainEventManager;
    private State currentState;
    private TS3Connection ts3Connection;
    private Clients clientManager;

    public static Sprummlbot getSprummlbot() {
        return sprummlbot == null ? sprummlbot = new Sprummlbot() : sprummlbot;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public TS3ApiAsync getAsyncAPI() {
        return ts3ApiAsync;
    }

    public TS3Api getSyncAPI() {
        return ts3Api;
    }

    public TS3Connection getTS3Connection() {
        return ts3Connection;
    }

    public void setTS3Connection(TS3Connection ts3Connection) {
        this.ts3Connection = ts3Connection;
    }

    public TS3ApiAsync getDefaultAPI() {
        return getAsyncAPI();
    }

    public MainService getMainService() {
        return service;
    }

    public void setMainService(MainService s) {
        this.service = s;
    }

    public State getSprummlbotState() {
        return currentState;
    }

    public void setSprummlbotState(State currentState) {
        this.currentState = currentState;
    }

    public EventManager getMainEventManager() {
        return mainEventManager;
    }

    public void setMainEventManager(EventManager mainEventManager) {
        this.mainEventManager = mainEventManager;
    }

    public void shutdown() {
        Main.shutdown(0);
    }

    public void shutdown(int code) {
        Main.shutdown(code);
    }

    public void shutdown(long delay, TimeUnit timeUnit) {
        Vars.SERVICE.schedule(() -> Main.shutdown(0), delay, timeUnit);
    }

    public void shutdown(final int code, long delay, TimeUnit timeUnit) {
        Vars.SERVICE.schedule(() -> Main.shutdown(code), delay, timeUnit);
    }

    public void setTS3ApiAsync(TS3ApiAsync ts3ApiAsync) {
        this.ts3ApiAsync = ts3ApiAsync;
    }

    public void setTS3Api(TS3Api ts3Api) {
        this.ts3Api = ts3Api;
    }

    public Clients getClientManager() {
        return clientManager;
    }

    public void setClientManager(Clients clientManager) {
        this.clientManager = clientManager;
    }
}
