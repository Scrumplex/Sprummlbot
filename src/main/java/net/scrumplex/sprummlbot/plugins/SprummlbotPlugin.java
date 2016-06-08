package net.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import net.scrumplex.sprummlbot.Startup;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.events.ClientJoinEventHandler;
import net.scrumplex.sprummlbot.plugins.events.EventManager;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import net.scrumplex.sprummlbot.wrapper.State;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SprummlbotPlugin {

    private PluginInfo info;
    private EventManager eventMgr;
    private File configFile;
    private PluginClassLoader loader;
    private SprummlTasker tasker;

    public SprummlbotPlugin() {
    }

    void initialize(PluginClassLoader loader, SprummlTasker tasker, EventManager eventMgr, PluginInfo info) {
        this.loader = loader;
        this.tasker = tasker;
        this.eventMgr = eventMgr;
        this.info = info;
        if (info == null)
            throw new NullPointerException("Plugin Info cannot be null!");
        onEnable();
    }

    void unload() {
        onDisable();
        getTasker().shutdown();
        info = null;
        eventMgr = null;
        configFile = null;
        tasker = null;
        loader = null;
    }

    @Nullable
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

    public final PluginClassLoader getClassLoader() {
        return loader;
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

    public final EventManager getEventManager() {
        return eventMgr;
    }

    public void onEnable() {
    }

    public void onDisable() {
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
