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

/* 
 * 
 */

import javax.annotation.Nullable;

/**
 * A task handler {@link java.lang.Runnable} with the ability to cancel itself
 * and run optional code if it is cancelled.
 */
public abstract class TaskHandler implements Runnable {

    private ScheduledTask _task;
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
     * <p>You must still call the child task handlers run method manually.</p>
     *
     * <p>Calling cancelTask method also calls cancelTask on the child
     * method if it's a {@link TaskHandler} instance.</p>
     *
     * <p>Calling cancelTask method on the child also calls cancelTask method.</p>
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
     * Get the {@link ScheduledTask} of the task handler
     */
    @Nullable
    public ScheduledTask getTask() {
        return _task;
    }

    /**
     * Cancel the task.
     */
    public void cancelTask() {
        if (_task != null)
            _task.cancel();
    }

    /**
     * Called when the task is cancelled.
     */
    protected void onCancel() {}

    /**
     * Set the task as cancelled.
     */
    void setCancelled() {
        cancelUp();
        cancelDown();
    }

    /**
     * Set the parent and its parents
     * as cancelled.
     */
    void cancelUp() {
        if (_task != null) {
            onCancel();
            _task = null;
        }

        if (_parent != null) {
            _parent.cancelUp();
        }
    }

    /**
     * Set the children and their children
     * as cancelled.
     */
    void cancelDown() {
        if (_task != null) {
            onCancel();
            _task = null;
        }

        if (_child != null) {
            _child.cancelDown();
        }
    }

    /**
     * Set the scheduled task.
     */
    void setTask(ScheduledTask task) {
        if (_task != null)
            throw new RuntimeException("A TaskHandler cannot be in more than 1 scheduled task at a time.");

        _task = task;

        if (_child != null) {
            _child.setTask(task);
        }
    }
}
