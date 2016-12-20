package net.scrumplex.sprummlbot.service;

import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.module.Module;
import net.scrumplex.sprummlbot.tools.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainService {

    private final long tick;
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    private final Map<Module, ServiceHook> hooks = new HashMap<>();

    public MainService(long tick) {
        this.tick = tick;
    }

    public void start() {
        service.scheduleAtFixedRate(() -> Sprummlbot.getSprummlbot().getDefaultAPI().getClients().onSuccess(result -> {
            for (ServiceHook hook : hooks.values()) {
                try {
                    hook.handle(result);
                } catch (Exception e) {
                    Exceptions.handle(e, "An error occurred while performing client checks!", false);
                }
            }
        }), tick, tick, TimeUnit.MILLISECONDS);
    }

    public void hook(Module module, ServiceHook hook) {
        hooks.put(module, hook);
    }

    public void unhook(Module module) {
        hooks.remove(module);
    }

}
