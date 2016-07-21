package net.scrumplex.sprummlbot.plugins;

import net.scrumplex.sprummlbot.core.SprummlbotThreadFactory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class SprummlTasker {

    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;

    SprummlTasker(SprummlbotPlugin plugin) {
        SprummlbotThreadFactory factory = new SprummlbotThreadFactory(plugin);
        executor = Executors.newFixedThreadPool(1, factory);
        scheduler = Executors.newScheduledThreadPool(1, factory);
    }

    public Future<?> createThread(@NotNull Runnable r) {
        return executor.submit(r);
    }

    public Future<?> createThread(@NotNull RunnableFuture<?> r) {
        return executor.submit(r);
    }

    public ScheduledFuture<?> createScheduledTimerRunnable(@NotNull Runnable r, long delay, long tick, @NotNull TimeUnit timeUnit) {
        return scheduler.scheduleAtFixedRate(r, delay, tick, timeUnit);
    }

    public ScheduledFuture<?> createDelayedRunnable(@NotNull Runnable r, long delay, @NotNull TimeUnit timeUnit) {
        return scheduler.schedule(r, delay, timeUnit);
    }

    public ScheduledFuture<?> createDelayedCallable(@NotNull Callable<?> c, long delay, @NotNull TimeUnit timeUnit) {
        return scheduler.schedule(c, delay, timeUnit);
    }

    void shutdown() {
        executor.shutdown();
        scheduler.shutdown();
    }
}
