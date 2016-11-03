package net.scrumplex.sprummlbot.core;

import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import net.scrumplex.sprummlbot.wrapper.State;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class SprummlbotThreadFactory implements ThreadFactory {

    private final SprummlbotPlugin plugin;

    public SprummlbotThreadFactory(SprummlbotPlugin plugin) {
        this.plugin = plugin;
    }

    public SprummlbotThreadFactory() {
        plugin = null;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        if (Sprummlbot.getSprummlbot().getSprummlbotState() == State.STOPPING)
            return null;

        Thread t = new Thread(r);
        t.setName((plugin == null ? "sprummlbot_core_thread" : "plugin_" + plugin.getPluginInfo().getPluginName()) + "#" + t.getId());
        t.setPriority(plugin == null ? 10 : 5);
        return t;
    }
}
