package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.event.BaseEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.tools.Exceptions;

import java.io.File;
import java.io.IOException;

public class SprummlbotPlugin {

    private File jarFile;
    private File folder;
    private PluginInfo info;
    private File configFile;
    private Sprummlbot sBot;

    public SprummlbotPlugin() {
    }


    void initialize(File jarFile, File folder, PluginInfo info, Sprummlbot sBot) {

        this.jarFile = jarFile;
        this.folder = folder;
        this.info = info;
        this.sBot = sBot;
        onEnable();
    }

    void unload() {
        onDisable();
    }

    public Config getConfig() {
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

    public File getPluginFolder() {
        return folder;
    }

    public File getPluginFile() {
        return jarFile;
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

    public TS3ApiAsync getAPI() {
        return Vars.API;
    }

    public Sprummlbot getSprummlbot() {
        return this.sBot;
    }

    public PluginInfo getPluginInfo() {
        return info;
    }
}
