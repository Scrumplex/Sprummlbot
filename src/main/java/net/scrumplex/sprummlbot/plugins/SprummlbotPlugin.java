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
import java.util.concurrent.ExecutorService;

public class SprummlbotPlugin {

    private File jarFile;
    private File folder;
    private PluginInfo info;
    private File configFile;

    public SprummlbotPlugin() {
    }

    void initialize(File jarFile, File folder, PluginInfo info) {
        this.jarFile = jarFile;
        this.folder = folder;
        this.info = info;
        if (jarFile == null)
            throw new NullPointerException("JarFile cannot be null!");
        if (folder == null)
            throw new NullPointerException("Plugin Folder cannot be null!");
        if (info == null)
            throw new NullPointerException("Plugin Info cannot be null!");
        onEnable();
    }

    void unload() {
        onDisable();
        jarFile = null;
        folder = null;
        info = null;
        configFile = null;
    }

    public final Config getConfig() {
        if (!getPluginFolder().exists()) {
            getPluginFolder().mkdirs();
        }
        if (configFile == null) {
            configFile = new File(getPluginFolder(), "config.ini");
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

    public final File getPluginFolder() {
        return folder;
    }

    public final File getPluginFile() {
        return jarFile;
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

    public final ExecutorService getThreadExecutor() {
        return Vars.EXECUTOR;
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
