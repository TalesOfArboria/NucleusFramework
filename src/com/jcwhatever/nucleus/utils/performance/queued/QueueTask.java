/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.nucleus.utils.performance.queued;

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.result.FutureResultAgent.Future;
import com.jcwhatever.nucleus.utils.observer.result.ResultBuilder;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Abstract class that worker tasks should extend so
 * that they can be used by {@code QueueWorker}.
 */
public abstract class QueueTask implements IPluginOwned, Runnable {

    private final Plugin _plugin;
    private final TaskConcurrency _concurrency;
    private final FutureResultAgent<QueueTask> _resultAgent = new FutureResultAgent<>();

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
    }

    /**
     * Get the plugin that owns the task.
     */
    @Override
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
    public final Future<QueueTask> getResult() {
        return _resultAgent.getFuture();
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
     * @param args    Optional message format arguments.
     */
    public final Future<QueueTask> cancel(@Nullable String reason, Object... args) {
        PreCon.notNull(args);

        if (isEnded())
            return _resultAgent.getFuture();

        onCancel();
        onEnd();

        _isRunning = false;
        _isCancelled = true;

        if (reason != null)
            reason = TextUtils.format(reason, args);

        _resultAgent.sendResult(
                new ResultBuilder<QueueTask>().cancel().message(reason).result(this).build());

        if (_parent != null)
            _parent.update(this);

        return _resultAgent.getFuture();
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
    protected final Future<QueueTask> complete() {

        if (isEnded())
            return _resultAgent.getFuture();

        onComplete();
        onEnd();

        _isRunning = false;
        _isComplete = true;

        _resultAgent.sendResult(
                new ResultBuilder<QueueTask>().success().message("Completed.").result(this).build());

        if (_parent != null)
            _parent.update(this);

        return _resultAgent.getFuture();
    }

    /**
     * Call when the task fails.
     *
     * @param reason  Optional message describing the reason the task failed.
     * @param args    Optional message format arguments.
     */
    protected final Future<QueueTask> fail(@Nullable String reason, Object... args) {

        if (isEnded())
            return _resultAgent.getFuture();

        onFail();
        onEnd();

        _isRunning = false;
        _isFailed = true;

        if (reason != null)
            reason = TextUtils.format(reason, args);

        _resultAgent.sendResult(
                new ResultBuilder<QueueTask>().error().message(reason).result(this).build());

        if (_parent != null)
            _parent.update(this);

        return _resultAgent.getFuture();
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
