package net.scrumplex.sprummlbot.module;

public class ModuleLoadException extends ModuleException {

    public ModuleLoadException(Module module, String reason) {
        super("Module " + module.getType() + " could not be loaded: " + reason);
    }

}
