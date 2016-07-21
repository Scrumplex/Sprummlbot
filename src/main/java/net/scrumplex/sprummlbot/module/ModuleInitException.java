package net.scrumplex.sprummlbot.module;

public class ModuleInitException extends ModuleException {

    public ModuleInitException(Class<? extends Module> module, String reason, Exception e) {
        super("Module " + module.getName() + " could not be initialized: " + reason, e);
    }

    public ModuleInitException(String reason) {
        super("A Module could not be initialized: " + reason);
    }

}
