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

package com.jcwhatever.nucleus.internal.scheduler;

import com.jcwhatever.nucleus.utils.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.utils.scheduler.TaskHandler;

import org.bukkit.scheduler.BukkitTask;

/**
 * Internal {@link IScheduledTask} implementation.
 */
public class InternalScheduledTask implements IScheduledTask {

    protected BukkitTask _task;
    protected Runnable _runnable;
    protected boolean _isCancelled;
    protected boolean _isRepeating;

    /**
     * Constructor.
     *
     * @param runnable     The scheduled task handler.
     * @param task         The Bukkit task.
     * @param isRepeating  Determine if the task is repeating.
     */
    public InternalScheduledTask(Runnable runnable, BukkitTask task, boolean isRepeating) {
        _runnable = runnable;
        _task = task;
        _isRepeating = isRepeating;

        if (runnable instanceof TaskHandler) {
            TaskHandler handler = (TaskHandler)runnable;
            if (handler.getTask() != null)
                throw new RuntimeException("A TaskHandler cannot be in more than 1 scheduled task at a time. " +
                        "Use a Runnable instance instead.");

            ((TaskHandler) runnable).setTask(this);
        }
    }

    /**
     * Get the task used by the implementation scheduler.
     */
    @SuppressWarnings("unchecked")
    public <T> T getHandle() {
        return (T)_task;
    }

    @Override
    public Runnable getRunnable() {
        return _runnable;
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public boolean isRepeating() {
        return _isRepeating;
    }

    @Override
    public void cancel() {

        if (_isCancelled)
            return;

        _isCancelled = true;
        _task.cancel();

        if (_runnable instanceof TaskHandler) {
            ((TaskHandler) _runnable).cancelTask();
        }
    }
}
