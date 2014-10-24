package com.jcwhatever.bukkit.generic.performance.queued;

import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Abstract class that worker tasks should extend so
 * that they can be used by {@code QueueWorker}.
 */
public abstract class QueueTask implements Runnable {

    private final Plugin _plugin;
    private final TaskConcurrency _concurrency;
    private final QueueResult _result;
    private final Future _future;

    private volatile boolean _isRunning = false;
    private volatile boolean _isComplete = false;
    private volatile boolean _isCancelled = false;
    private volatile boolean _isFailed = false;

    private QueueProject _parent;

    public QueueTask (Plugin plugin, TaskConcurrency concurrency) {
        PreCon.notNull(plugin);
        PreCon.notNull(concurrency);

        _plugin = plugin;
        _concurrency = concurrency;
        _result = new QueueResult(this);
        _future = _result.getFuture();
    }

    /**
     * Get the plugin that owns the task.
     */
    public final Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the preferred concurrency of the task.
     */
    public final TaskConcurrency getConcurrency() {
        return _concurrency;
    }

    /**
     * Get the task result future.
     */
    public final Future getResult() {
        return _future;
    }

    /**
     * Determine if the task is currently running.
     */
    public final boolean isRunning() {
        return _isRunning;
    }

    /**
     * Determine if the task has successfully completed.
     */
    public final boolean isComplete() {
        return _isComplete;
    }

    /**
     * Determine if the task is cancelled.
     */
    public final boolean isCancelled() {
        return _isCancelled;
    }

    /**
     * Determine if the task is ended due to a failure.
     */
    public final boolean isFailed() {
        return _isFailed;
    }

    /**
     * Determine if the task has ended for any reason.
     */
    public final boolean isEnded() {
        return _isComplete || _isCancelled || _isFailed;
    }

    /**
     * Call to cancel the task. Does not guarantee the task will end
     * immediately.
     *
     * @param reason  Optional message describing the reason the task was cancelled.
     */
    public final Future cancel(@Nullable String reason) {

        if (isEnded())
            return _future;

        onCancel();
        onEnd();

        _isRunning = false;
        _isCancelled = true;

        _result.setCancelled(reason);

        if (_parent != null)
            _parent.update(this);

        return _future;
    }

    /**
     * Run the task.
     */
    @Override
    public final void run() {
        if (_isRunning)
            return;

        _isRunning = _plugin.isEnabled();

        if (_isRunning)
            onRun();
    }


    protected abstract void onRun();

    protected void onComplete() {}
    protected void onFail() {}
    protected void onCancel() {}
    protected void onEnd() {}


    /**
     * Call when the task is completed successfully.
     */
    protected final Future complete() {

        if (isEnded())
            return _future;

        onComplete();
        onEnd();

        _isRunning = false;
        _isComplete = true;

        _result.setComplete();

        if (_parent != null)
            _parent.update(this);

        return _future;
    }

    /**
     * Call when the task fails.
     *
     * @param reason  Optional message describing the reason the task failed.
     */
    protected final Future fail(String reason) {

        if (isEnded())
            return _future;

        onFail();
        onEnd();

        _isRunning = false;
        _isFailed = true;

        _result.setFailed(reason);

        if (_parent != null)
            _parent.update(this);

        return _future;
    }

    /**
     * Used by the parent project to set itself as parent.
     */
    void setParentProject(QueueProject project) {
        if (_parent != null)
            throw new IllegalStateException("A task cannot have more than one project parent.");

        _parent = project;
    }

}
