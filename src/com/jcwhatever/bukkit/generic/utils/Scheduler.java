/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;

/**
 * Task scheduler.
 */
public class Scheduler {

    private Scheduler() {}

    /**
     * Run a task later.
     *
     * <p>A {@code TaskHandler} instance can be used in place of a {@code Runnable} to
     * add the ability to cancel the task from within the task handler and to run
     * optional code if the task is cancelled.</p>
     *
     * @param plugin    The owning plugin.
     * @param runnable  The {@code Runnable} to run later.
     *
     * @return  A {@code ScheduledTask} instance to keep track of the task.
     */
    public static ScheduledTask runTaskLater(Plugin plugin, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);

        BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
        return new ScheduledTask(runnable, task, false);
    }

    /**
     * Run a task after a specified number of ticks have elapsed.
     *
     * <p>A {@code TaskHandler} instance can be used in place of a {@code Runnable} to
     * add the ability to cancel the task from within the task handler and to run
     * optional code if the task is cancelled.</p>
     *
     * @param plugin    The owning plugin.
     * @param ticks     The number of ticks to wait before running the task.
     * @param runnable  The {@code Runnable} to run later.
     *
     * @return  A {@code ScheduledTask} instance to keep track of the task.
     */
    public static ScheduledTask runTaskLater(Plugin plugin, int ticks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(ticks);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
        return new ScheduledTask(runnable, task, false);
    }

    /**
     * Run a task on an asynchronous thread after a specified number
     * of ticks have elapsed.
     *
     * <p>A {@code TaskHandler} instance can be used in place of a {@code Runnable} to
     * add the ability to cancel the task from within the task handler and to run
     * optional code if the task is cancelled.</p>
     *
     * @param plugin    The owning plugin.
     * @param ticks     The number of ticks to wait before running the task.
     * @param runnable  The {@code Runnable} to run later.
     *
     * @return  A {@code ScheduledTask} instance to keep track of the task.
     */
    public static ScheduledTask runTaskLaterAsync(Plugin plugin, int ticks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(ticks);

        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks);
        return new ScheduledTask(runnable, task, false);
    }

    /**
     * Run a task on a repeating schedule after a specified number of ticks
     * have elapsed and repeating after the specified repeat ticks have elapsed.
     *
     * <p>A {@code TaskHandler} instance can be used in place of a {@code Runnable} to
     * add the ability to cancel the task from within the task handler and to run
     * optional code if the task is cancelled.</p>
     *
     * @param plugin       The owning plugin.
     * @param startTicks   The number of ticks to wait before first running the task.
     * @param repeatTicks  The number of ticks to wait between each repeat of the task.
     * @param runnable     The {@code Runnable} to run later.
     *
     * @return  A {@code ScheduledTask} instance to keep track of the task.
     */
    public static ScheduledTask runTaskRepeat(Plugin plugin, int startTicks, int repeatTicks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(startTicks);
        PreCon.positiveNumber(repeatTicks);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, startTicks, repeatTicks);
        return new ScheduledTask(runnable, task, true);
    }

    /**
     * Run a task on an asynchronous repeating schedule after a specified number
     * of ticks have elapsed and repeating after the specified repeat ticks have
     * elapsed.
     *
     * <p>A {@code TaskHandler} instance can be used in place of a {@code Runnable} to
     * add the ability to cancel the task from within the task handler and to run
     * optional code if the task is cancelled.</p>
     *
     * @param plugin       The owning plugin.
     * @param startTicks   The number of ticks to wait before first running the task.
     * @param repeatTicks  The number of ticks to wait between each repeat of the task.
     * @param runnable     The {@code Runnable} to run later.
     *
     * @return  A {@code ScheduledTask} instance to keep track of the task.
     */
    public static ScheduledTask runTaskRepeatAsync(Plugin plugin, int startTicks, int repeatTicks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(startTicks);
        PreCon.positiveNumber(repeatTicks);

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, startTicks, repeatTicks);
        return new ScheduledTask(runnable, task, true);
    }

    /**
     * Run a task on the main thread at the next available chance.
     *
     * @param plugin    The owning plugin.
     * @param runnable  The {@code Runnable} to run later.
     */
    public static void runTaskSync(Plugin plugin, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
    }

    /**
     * Run a task on the main thread after the specified number of ticks have
     * elapsed.
     *
     * @param plugin    The owning plugin.
     * @param ticks     The number of ticks to wait before running the task.
     * @param runnable  The {@code Runnable} to run later.
     */
    public static void runTaskSync(Plugin plugin, int ticks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.positiveNumber(ticks);
        PreCon.notNull(runnable);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, ticks);
    }


    /**
     * A task handler {@code Runnable} with the ability to cancel itself
     * and run optional code if it is cancelled.
     */
    public static abstract class TaskHandler implements Runnable {

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
         * method if it's a {@code TaskHandler} instance.</p>
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
         * Get the {@code ScheduledTask} of the task handler
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
                throw new RuntimeException("A TaskHandler cannot be in more than 1 scheduled task at a time. " +
                        "Use a Runnable instance instead.");

            _task = task;

            if (_child != null) {
                _child.setTask(task);
            }
        }

    }

    /**
     * Return object to keep reference to
     * scheduled tasks.
     */
    public static class ScheduledTask {

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
        ScheduledTask(Runnable runnable, BukkitTask task, boolean isRepeating) {
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

        /**
         * Get the runnable that the task runs.
         */
        public Runnable getRunnable() {
            return _runnable;
        }

        /**
         * Determine if the cancel method was called
         * on the task.
         */
        public boolean isCancelled() {
            return _isCancelled;
        }

        /**
         * Determine if the scheduled task is a repeating
         * task.
         */
        public boolean isRepeating() {
            return _isRepeating;
        }

        /**
         * Cancels the scheduled task.
         * <p>If the task is already cancelled or has
         * already executed, nothing happens.</p>
         */
        public void cancel() {
            _isCancelled = true;
            _task.cancel();

            if (_runnable instanceof TaskHandler) {
                ((TaskHandler) _runnable).setCancelled();
            }
        }
    }

}
