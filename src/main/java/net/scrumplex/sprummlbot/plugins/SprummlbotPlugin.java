package net.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.Startup;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import net.scrumplex.sprummlbot.wrapper.State;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SprummlbotPlugin {

    private PluginInfo info;
    private File configFile;
    private SprummlTasker tasker;

    public SprummlbotPlugin() {
    }

    void initialize(SprummlTasker tasker, PluginInfo info) {
        this.tasker = tasker;
        this.info = info;
        if (info == null)
            throw new NullPointerException("Plugin Info cannot be null!");
        onEnable();
    }

    void unload() {
        onDisable();
        info = null;
        configFile = null;
    }

    public final Config getConfig() {
        if (!getPluginInfo().getPluginFolder().exists()) {
            getPluginInfo().getPluginFolder().mkdirs();
        }
        if (configFile == null) {
            configFile = new File(getPluginInfo().getPluginFolder(), "config.ini");
        }
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                Exceptions.handlePluginError(e, this);
            }
        }
        try {
            return new Config(configFile);
        } catch (IOException e) {
            Exceptions.handlePluginError(e, this);
        }
        return null;
    }

    @Deprecated
    public final File getPluginFolder() {
        System.err.println("[Plugins][" + getPluginInfo().getPluginName() + "] The method getPluginFolder() is deprecated! Please use getPluginInfo().getPluginFolder() instead!");
        return getPluginInfo().getPluginFolder();
    }

    @Deprecated
    public final File getPluginFile() {
        System.err.println("[Plugins][" + getPluginInfo().getPluginName() + "] The method getPluginFile() is deprecated! Please use getPluginInfo().getPluginFile() instead!");
        return getPluginInfo().getPluginFile();
    }

    public final CommandManager getCommandManager() {
        return Vars.COMMAND_MGR;
    }

    public final PluginLoader getPluginLoader() {
        return Startup.pluginLoader;
    }

    public final PluginManager getPluginManager() {
        return Startup.pluginManager;
    }

    public final SprummlTasker getTasker() {
        return tasker;
    }

    public final State getSprummlbotState() {
        return Vars.SPRUMMLBOT_STATUS;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public boolean onCommand(String command, String[] args, Client sender) {
        return false;
    }

    public void onEvent(SprummlEventType eventType, BaseEvent event) {
    }

    public final TS3ApiAsync getAPI() {
        return Vars.API;
    }

    public final Map<String, PermissionGroup> getPermissionGroups() {
        return Vars.PERMGROUPS;
    }


    public final PluginInfo getPluginInfo() {
        return info;
    }
}
