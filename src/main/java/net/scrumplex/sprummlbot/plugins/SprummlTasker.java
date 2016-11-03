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

    /**
     * Creates a {@link java.lang.Thread Thread} with the specified Runnable.
     *
     * @param r The {@link java.lang.Runnable Runnable} which is being executed.
     * @return a Future representing pending completion of the task
     */
    public Future<?> createThread(@NotNull Runnable r) {
        return executor.submit(r);
    }

    /**
     * Creates a {@link java.lang.Thread Thread} with the specified RunnableFuture.
     * @param r The {@link java.util.concurrent.RunnableFuture RunnableFuture} which is being executed.
     * @return a Future representing pending completion of the task
     */
    public Future<?> createThread(@NotNull RunnableFuture<?> r) {
        return executor.submit(r);
    }

    /**
     * Creates a timer like thread, that will run all x seconds/milliseconds...
     *
     * @param r        The {@link java.lang.Runnable Runnable}, that will be executed in this timer.
     * @param delay    This delay defines, when the {@link java.lang.Runnable Runnable} will be executed the first time.
     * @param tick     This value defines, in which interval the {@link java.lang.Runnable Runnable} will be executed.
     * @param timeUnit This parameter defines the time unit used for the delay and tick.
     * @return Returns a ScheduledFuture instance, to track your scheduled timer.
     * @see java.util.concurrent.TimeUnit
     */
    public ScheduledFuture<?> scheduleTimerTask(@NotNull Runnable r, long delay, long tick, @NotNull TimeUnit timeUnit) {
        return scheduler.scheduleAtFixedRate(r, delay, tick, timeUnit);
    }

    /**
     * Creates a task, which will run once after the specified delay.
     *
     * @param r        The {@link java.lang.Runnable Runnable}, that will be executed <strong>once</strong> in this task.
     * @param delay    This delay defines, when the {@link java.lang.Runnable Runnable} will be executed.
     * @param timeUnit This parameter defines the time unit used for the delay.
     * @return Returns a ScheduledFuture instance, to track your scheduled task.
     * @see java.util.concurrent.TimeUnit
     */
    public ScheduledFuture<?> scheduleTask(@NotNull Runnable r, long delay, @NotNull TimeUnit timeUnit) {
        return scheduler.schedule(r, delay, timeUnit);
    }

    /**
     * Creates a task, which will run once after the specified delay.
     *
     * @param c        The {@link java.util.concurrent.Callable Callable}, that will be executed <strong>once</strong> in this task.
     * @param delay    This delay defines, when the {@link java.util.concurrent.Callable Callable} will be executed.
     * @param timeUnit This parameter defines the time unit used for the delay.
     * @return Returns a ScheduledFuture instance, to track your scheduled task.
     * @see java.util.concurrent.TimeUnit
     */
    public ScheduledFuture<?> scheduleTask(@NotNull Callable<?> c, long delay, @NotNull TimeUnit timeUnit) {
        return scheduler.schedule(c, delay, timeUnit);
    }

    void shutdown() {
        executor.shutdown();
        scheduler.shutdown();
    }
}
