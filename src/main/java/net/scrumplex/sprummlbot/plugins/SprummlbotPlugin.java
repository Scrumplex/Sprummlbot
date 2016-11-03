package net.scrumplex.sprummlbot.plugins;

import net.scrumplex.sprummlbot.Sprummlbot;
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

    final void initialize(PluginClassLoader loader, SprummlTasker tasker, EventManager eventMgr, PluginInfo info, Sprummlbot sprummlbot) {
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

    final void unload() {
        onDisable();
        getTasker().shutdown();
        info = null;
        sprummlbot = null;
        eventMgr = null;
        configFile = null;
        tasker = null;
        loader = null;
    }

    final PluginClassLoader getClassLoader() {
        return loader;
    }

    /**
     * Returns a {@link net.scrumplex.sprummlbot.plugins.Config Config} instance.
     * The file attached to the Config instance will be crated if it does not exist.
     *
     * @return a {@link net.scrumplex.sprummlbot.plugins.Config Config} instance attached to plugin's config file.
     */
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

    /**
     * Returns the global {@link net.scrumplex.sprummlbot.Sprummlbot Sprummlbot} instance used in the Sprummlbot JVM.
     * The {@link net.scrumplex.sprummlbot.Sprummlbot Sprummlbot} class contains many instances of core components of the Sprummlbot.
     *
     * @return the global {@link net.scrumplex.sprummlbot.Sprummlbot Sprummlbot} instance.
     * @see net.scrumplex.sprummlbot.Sprummlbot
     */
    public final Sprummlbot getSprummlbot() {
        return sprummlbot;
    }

    /**
     * Returns the {@link net.scrumplex.sprummlbot.plugins.SprummlTasker SprummlTasker} created for this specific {@link net.scrumplex.sprummlbot.plugins.SprummlTasker SprummlTasker} instance.
     * The {@link net.scrumplex.sprummlbot.plugins.SprummlTasker SprummlTasker} can be used to schedule tasks and create threads.
     * <p>
     * You should not create your own threads or schedulers.
     *
     * @return a {@link net.scrumplex.sprummlbot.plugins.SprummlTasker SprummlTasker}
     * @see net.scrumplex.sprummlbot.plugins.SprummlTasker
     */
    public final SprummlTasker getTasker() {
        return tasker;
    }

    /**
     * @return the bot's state.
     * @see net.scrumplex.sprummlbot.wrapper.State;
     * @deprecated Use getSprummlbot().getSprummlbotState()
     */
    @Deprecated
    public final State getSprummlbotState() {
        return getSprummlbot().getSprummlbotState();
    }

    /**
     * Returns the {@link net.scrumplex.sprummlbot.plugins.events.EventManager EventManager} instance, used by this plugin.
     *
     * You can add event listeners to handle events.
     *
     * @return a {@link net.scrumplex.sprummlbot.plugins.events.EventManager EventManager} instance for the plugin.
     * @see net.scrumplex.sprummlbot.plugins.events.EventManager
     */
    public final EventManager getEventManager() {
        return eventMgr;
    }

    /**
     * Overridable default method.
     *
     * Fired when loading plugin.
     */
    public void onEnable() {
    }

    /**
     * Overridable default method.
     *
     * Fired while unloading plugin.
     */
    public void onDisable() {
    }

    /**
     * Returns the {@link net.scrumplex.sprummlbot.plugins.PluginInfo PluginInfo} for this plugin.
     *
     * @return the {@link net.scrumplex.sprummlbot.plugins.PluginInfo PluginInfo} for this plugin.
     * @see net.scrumplex.sprummlbot.plugins.PluginInfo
     */
    public final PluginInfo getPluginInfo() {
        return info;
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof SprummlbotPlugin && getPluginInfo().getPluginName().equals(((SprummlbotPlugin) obj).getPluginInfo().getPluginName());
    }
}
