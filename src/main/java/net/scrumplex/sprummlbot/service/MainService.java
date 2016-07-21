package net.scrumplex.sprummlbot.service;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.module.Module;

import java.util.HashMap;
import java.util.List;
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
        service.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Sprummlbot.getSprummlbot().getDefaultAPI().getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                    @Override
                    public void handleSuccess(List<Client> result) {
                        for (ServiceHook hook : hooks.values()) {
                            hook.handle(result);
                        }
                    }
                });
            }

        }, tick, tick, TimeUnit.MILLISECONDS);
    }

    public void hook(Module module, ServiceHook hook) {
        hooks.put(module, hook);
    }

    public void unhook(Module module) {
        hooks.remove(module);
    }

}
