package net.scrumplex.sprummlbot.module;

public class ModuleInitializationException extends ModuleException {

    public ModuleInitializationException(Class<? extends Module> module, String reason, Exception e) {
        super("Module " + module.getName() + " could not be initialized: " + reason, e);
    }

    public ModuleInitializationException(String reason) {
        super("A Module could not be initialized: " + reason);
    }

}
