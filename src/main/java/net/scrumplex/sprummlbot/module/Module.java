package net.scrumplex.sprummlbot.module;

import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import net.scrumplex.sprummlbot.plugins.events.EventManager;
import net.scrumplex.sprummlbot.service.MainService;
import org.ini4j.Profile;
import org.jetbrains.annotations.NotNull;

public class Module implements Comparable<Module> {

    private String type;
    private SprummlbotPlugin plugin;
    private Profile.Section section;
    private ModuleManager m;

    public Module() {

    }

    void initialize(String type, Profile.Section section) {
        this.type = type;
        this.plugin = null;
        this.section = section;
    }


    public final void initialize(@NotNull String type, @NotNull SprummlbotPlugin plugin, @NotNull Profile.Section section) {
        initialize(type, section);
        this.plugin = plugin;
    }

    final void preload() throws InvalidModuleException {
        if (!section.containsKey("type"))
            throw new InvalidModuleException(this, section.getName() + " does not contain type property");
        if (!section.get("type").equalsIgnoreCase(getType()))
            throw new InvalidModuleException(this, section.getName() + " is not a(n) " + getType() + " type!");
        System.out.println("[Modules] Loading module " + getType() + "...");
        try {
            load(section);
        } catch (Exception e) {
            throw new InvalidModuleException(this, e);
        }
        System.out.println("[Modules] Module " + getType() + " has been loaded...");
    }

    protected void load(Profile.Section iniSection) throws Exception {

    }

    protected void start() {
    }

    protected void stop() {
    }

    protected final MainService getMainService() {
        return Sprummlbot.getSprummlbot().getMainService();
    }

    protected final EventManager getEventManager() {
        if (plugin == null)
            return Sprummlbot.getSprummlbot().getMainEventManager();

        return plugin.getEventManager();
    }

    final void setModuleManager(ModuleManager m) {
        this.m = m;
    }

    protected final void terminate() {
        m.stopModule(this);
    }

    public final SprummlbotPlugin getPlugin() {
        return plugin;
    }

    public String getType() {
        return type;
    }

    @Override
    public int compareTo(@NotNull Module module) {
        return getType().compareTo(getType());
    }

}
