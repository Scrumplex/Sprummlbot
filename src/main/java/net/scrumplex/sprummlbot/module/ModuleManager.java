package net.scrumplex.sprummlbot.module;

import net.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import net.scrumplex.sprummlbot.tools.Exceptions;
import org.ini4j.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {

    private final Map<String, Class<? extends Module>> moduleAssignments;
    private final Map<String, SprummlbotPlugin> pluginModules;

    private final Map<String, List<Module>> modules;

    public ModuleManager() {
        modules = new HashMap<>();
        moduleAssignments = new HashMap<>();
        pluginModules = new HashMap<>();
    }

    public void registerModuleType(Class<? extends Module> clazz, String moduleType, SprummlbotPlugin plugin) throws ModuleException {
        if (moduleAssignments.containsKey(moduleType))
            throw new ModuleRegisterException(clazz, "Module type " + moduleType + " has already been registered by " + moduleAssignments.get(moduleType).getName());

        moduleAssignments.put(moduleType, clazz);
        pluginModules.put(moduleType, plugin);
    }

    void handleConfigSection(Profile.Section section) throws ModuleInitializationException {
        if (!section.getName().toLowerCase().startsWith("module_"))
            throw new ModuleInitializationException("The section (" + section.getName() + ") does not start with module_: " + section.getName());
        if (!section.containsKey("type"))
            throw new ModuleInitializationException("The section (" + section.getName() + ") does not contain a type field: " + section.getName());
        String type = section.get("type");
        if (!moduleAssignments.containsKey(type))
            throw new ModuleInitializationException("The type of the section (" + section.getName() + ") could not be found: " + section.getName());
        Class<? extends Module> clazz = moduleAssignments.get(type);
        Module module;
        try {
            module = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ModuleInitializationException(clazz, "There was a problem while constructing the module from the section (" + section.getName() + ").", e);
        }
        if (pluginModules.get(type) == null) {
            module.initialize(type, section);
        } else {
            module.initialize(type, section, pluginModules.get(type));
        }
        module.setModuleManager(this);
        List<Module> list = new ArrayList<>();
        if (modules.containsKey(type))
            list = modules.get(type);
        list.add(module);
        modules.put(type, list);
    }

    public void startAllModules() throws ModuleLoadException {
        System.out.println("[Modules] Starting modules...");
        for (List<Module> list : modules.values())
            for (Module module : list)
                try {
                    startModule(module);
                } catch (ModuleLoadException ex) {
                    if (module.getPlugin() != null)
                        Exceptions.handlePluginError(ex, module.getPlugin(), "Module " + module.getClass().getName() + " could not be loaded");
                    else
                        Exceptions.handle(ex, "Module " + module.getClass().getName() + " could not be loaded", false);
                }
    }

    public void startModule(Module module) throws ModuleLoadException {
        if (!modules.containsKey(module.getType()))
            throw new ModuleLoadException(module, "Module type not registered!");
        try {
            module.preload();
            module.start();
        } catch (InvalidModuleException e) {
            if (module.getPlugin() == null)
                Exceptions.handle(e, "Module " + module.getType() + " could not be loaded! Section: " + module.getSectionName(), false);
            else
                Exceptions.handlePluginError(e, module.getPlugin(), "Module " + module.getType() + " could not be loaded! Section: " + module.getSectionName());
        }

    }

    public void stopAllModules() {
        System.out.println("[Modules] Stopping modules...");
        for (List<Module> list : modules.values())
            for (Module module : list)
                stopModule(module);
    }

    public void stopAllFromPlugin(SprummlbotPlugin plugin) {
        for (List<Module> list : modules.values())
            for (Module module : list)
                if (module.getPlugin() != null && module.getPlugin().equals(plugin))
                    stopModule(module);
    }

    public void stopModule(Module module) {
        module.stop();
        System.out.println("[Modules] Module " + module.getType() + " has been stopped...");
    }
}
