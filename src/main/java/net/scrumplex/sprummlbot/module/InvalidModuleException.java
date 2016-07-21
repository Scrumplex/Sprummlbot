package net.scrumplex.sprummlbot.module;

public class InvalidModuleException extends Exception {

    public InvalidModuleException(Module module, String reason) {
        super(module.getType() + ": " + reason);
    }

    public InvalidModuleException(Module module, Exception ex) {
        super(module.getType(), ex);
    }

}
