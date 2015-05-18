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

package com.jcwhatever.nucleus.internal.managed.scripting.api;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.managed.scheduler.TaskHandler;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SAPI_Scheduler implements IDisposable {

    private final Plugin _plugin;
    private final Set<IScheduledTask> _taskReferences = new HashSet<>(25);
    private boolean _isDisposed;

    public SAPI_Scheduler(Plugin plugin) {
        _plugin = plugin;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        List<IScheduledTask> tasks = new ArrayList<>(_taskReferences);

        for (IScheduledTask task : tasks) {
            task.cancel();
        }

        _taskReferences.clear();

        _isDisposed = true;
    }

    /**
     * Run a task after a specified number of ticks have elapsed.
     *
     * <p>A {@link TaskHandler} instance can be used in place of a
     * {@link java.lang.Runnable} to add the ability to cancel the task
     * from within the task handler and to run optional code if the task is
     * cancelled.</p>
     *
     * @param delay     The number of ticks to wait before running the task.
     * @param runnable  The {@link java.lang.Runnable} to run later.
     *
     * @return  A {@link IScheduledTask} instance to keep track of the task.
     */
    public IScheduledTask runTaskLater(int delay, final Runnable runnable) {
        PreCon.notNull(runnable);

        ScriptTaskWrapper taskHandler = new ScriptTaskWrapper(runnable);

        IScheduledTask task = Scheduler.runTaskLater(_plugin, delay, taskHandler);

        // add reference so it can be cancelled if api is disposed
        _taskReferences.add(task);

        return task;
    }

    /**
     * Run a task on a repeating schedule after a specified number of ticks
     * have elapsed and repeating after the specified repeat ticks have elapsed.
     *
     * <p>A {@link TaskHandler} instance can be used in place of a
     * {@link java.lang.Runnable} to add the ability to cancel the task from
     * within the task handler and to run optional code if the task is cancelled.</p>
     *
     * @param initialDelay  The number of ticks to wait before first running the task.
     * @param repeatDelay   The number of ticks to wait between each repeat of the task.
     * @param runnable      The {@link java.lang.Runnable} to run later.
     *
     * @return  A {@link IScheduledTask} instance to keep track of the task.
     */
    public IScheduledTask runTaskRepeat(int initialDelay, int repeatDelay, final Runnable runnable) {
        PreCon.notNull(runnable);

        ScriptTaskWrapper taskHandler = new ScriptTaskWrapper(runnable);

        IScheduledTask task = Scheduler.runTaskRepeat(_plugin, initialDelay, repeatDelay, taskHandler);

        // add reference so it can be cancelled if api is disposed
        _taskReferences.add(task);

        return task;
    }

    private class ScriptTaskWrapper extends TaskHandler {

        Runnable runnable;

        ScriptTaskWrapper(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                this.runnable.run();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancel() {

            // remove task from reference collection if a script
            // cancels the task
            IScheduledTask task = getTask();
            if (task != null)
                _taskReferences.remove(task);
        }
    }
}

