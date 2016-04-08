package net.scrumplex.sprummlbot.plugins;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class SprummlTasker {

    private ExecutorService executor;
    private ScheduledExecutorService scheduler;

    SprummlTasker() {
        executor = Executors.newFixedThreadPool(5);
        scheduler = Executors.newScheduledThreadPool(5);
    }

    public Future<?> createThread(@NotNull Runnable r) {
        return executor.submit(r);
    }

    public Future<?> createThread(@NotNull RunnableFuture<?> r) {
        return executor.submit(r);
    }

    public ScheduledFuture<?> createScheduledTimerRunnable(@NotNull Runnable r, @NotNull long delay, @NotNull long tick, @NotNull TimeUnit timeUnit) {
        return scheduler.scheduleAtFixedRate(r, delay, tick, timeUnit);
    }

    public ScheduledFuture<?> createDelayedRunnable(@NotNull Runnable r, @NotNull long delay, @NotNull TimeUnit timeUnit) {
        return scheduler.schedule(r, delay, timeUnit);
    }

    public ScheduledFuture<?> createDelayedCallable(@NotNull Callable<?> c, @NotNull long delay, @NotNull TimeUnit timeUnit) {
        return scheduler.schedule(c, delay, timeUnit);
    }

    void shutdown() {
        executor.shutdown();
        scheduler.shutdown();
    }
}
