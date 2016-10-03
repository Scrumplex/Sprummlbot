package net.scrumplex.sprummlbot.plugins;

import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.events.EventManager;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.State;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class SprummlbotPlugin {

    private PluginInfo info;
    private Sprummlbot sprummlbot;
    private EventManager eventMgr;
    private File configFile;
    private PluginClassLoader loader;
    private SprummlTasker tasker;

    public SprummlbotPlugin() {
    }

    void initialize(PluginClassLoader loader, SprummlTasker tasker, EventManager eventMgr, PluginInfo info, Sprummlbot sprummlbot) {
        this.info = info;
        this.sprummlbot = sprummlbot;
        this.eventMgr = eventMgr;
        configFile = null;
        this.loader = loader;
        this.tasker = tasker;
        if (info == null)
            throw new NullPointerException("Plugin Info cannot be null!");
        onEnable();
    }

    void unload() {
        onDisable();
        getTasker().shutdown();
        info = null;
        sprummlbot = null;
        eventMgr = null;
        configFile = null;
        tasker = null;
        loader = null;
    }

    @Nullable
    protected final Config getConfig() {
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

    protected final PluginClassLoader getClassLoader() {
        return loader;
    }

    public final Sprummlbot getSprummlbot() {
        return sprummlbot;
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

    public final PluginInfo getPluginInfo() {
        return info;
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof SprummlbotPlugin && getPluginInfo().getPluginName().equals(((SprummlbotPlugin) obj).getPluginInfo().getPluginName());
    }
}
