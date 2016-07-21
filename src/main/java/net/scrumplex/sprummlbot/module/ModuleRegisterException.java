package net.scrumplex.sprummlbot.module;

public class ModuleRegisterException extends ModuleException {

    public ModuleRegisterException(Class<? extends Module> clazz, String reason, Exception e) {
        super("Module " + clazz.getName() + " could not be registered: " + reason, e);
    }

    public ModuleRegisterException(Class<? extends Module> clazz, String reason) {
        super("Module " + clazz.getName() + " could not be registered: " + reason);
    }
}
