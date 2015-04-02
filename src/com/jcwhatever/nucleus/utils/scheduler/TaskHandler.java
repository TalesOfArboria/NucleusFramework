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

package com.jcwhatever.nucleus.utils.scheduler;

import javax.annotation.Nullable;

/**
 * A task handler {@link java.lang.Runnable} with the ability to cancel itself
 * and run optional code if it's cancelled.
 */
public abstract class TaskHandler implements Runnable {

    private IScheduledTask _task;
    private TaskHandler _child;
    private TaskHandler _parent;

    /**
     * Constructor.
     */
    public TaskHandler() {}

    /**
     * Constructor.
     *
     * <p>Adds TaskHandler as a child.</p>
     *
     * <p>The child task handler is not run in conjunction with the task. The purpose of a
     * parent/child relationship is to cancel all child and parent tasks when any of the tasks
     * in the relationship are cancelled.</p>
     *
     * <p>Invoking {@link #cancelTask} method also invokes {@link #cancelTask} on the child
     * instance method.</p>
     *
     * @param child  The child task handler.
     */
    public TaskHandler(@Nullable TaskHandler child) {
        if (child != null) {
            _child = child;
            _child._parent = this;
        }
    }

    /**
     * Get the {@link IScheduledTask} of the task handler
     */
    @Nullable
    public IScheduledTask getTask() {
        return _task;
    }

    /**
     * Cancel the task.
     */
    public void cancelTask() {
        if (_task != null) {
            _task.cancel();
            onCancel();
            _task = null;
        }
        cancelUp();
        cancelDown();
    }

    /**
     * Set the scheduled task.
     *
     * <p>For use by the {@link ITaskScheduler} implementation.</p>
     */
    public void setTask(IScheduledTask task) {
        if (_task != null)
            throw new IllegalStateException("A TaskHandler cannot be in more than 1 scheduled task at a time.");

        _task = task;

        if (_child != null) {
            _child.setTask(task);
        }
    }

    /**
     * Invoked when the task is cancelled.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onCancel() {}

    /*
     * Set the parent and its parents as cancelled.
     */
    private void cancelUp() {
        if (_parent != null) {
            _parent.cancelUp();
        }
    }

    /*
     * Set the children and their children as cancelled.
     */
    private void cancelDown() {
        if (_child != null) {
            _child.cancelDown();
        }
    }
}
