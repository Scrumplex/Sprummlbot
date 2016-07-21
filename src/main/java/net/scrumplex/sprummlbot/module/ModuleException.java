package net.scrumplex.sprummlbot.module;

public class ModuleException extends Exception {

    public ModuleException(String reason) {
        super(reason);
    }

    public ModuleException(Exception e) {
        super(e);
    }

    public ModuleException(String reason, Exception e) {
        super(reason, e);
    }

}
