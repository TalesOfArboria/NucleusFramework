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

import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.managed.scheduler.ITaskScheduler;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Uses Bukkits Task Scheduler and adds use of TaskHandlers which
 * add functionality.
 */
public class InternalTaskScheduler implements ITaskScheduler {

    @Override
    public IScheduledTask runTaskLater(Plugin plugin, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);

        InternalScheduledTask task = new InternalScheduledTask(runnable, false);
        if (!plugin.isEnabled()) {
            pluginDisabledMessage(plugin, Thread.currentThread().getStackTrace());
            return task;
        }

        BukkitTask bukkitTask = Bukkit.getScheduler().runTask(plugin, runnable);
        task.setBukkitTask(bukkitTask);

        return task;
    }

    @Override
    public IScheduledTask runTaskLater(Plugin plugin, int ticks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(ticks);

        InternalScheduledTask task = new InternalScheduledTask(runnable, false);
        if (!plugin.isEnabled()) {
            pluginDisabledMessage(plugin, Thread.currentThread().getStackTrace());
            return task;
        }

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
        task.setBukkitTask(bukkitTask);

        return task;
    }

    @Override
    public IScheduledTask runTaskLaterAsync(Plugin plugin, int ticks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(ticks);

        InternalScheduledTask task = new InternalScheduledTask(runnable, false);
        if (!plugin.isEnabled()) {
            pluginDisabledMessage(plugin, Thread.currentThread().getStackTrace());
            return task;
        }

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks);
        task.setBukkitTask(bukkitTask);

        return task;
    }

    @Override
    public IScheduledTask runTaskRepeat(Plugin plugin, int startTicks, int repeatTicks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(startTicks);
        PreCon.positiveNumber(repeatTicks);

        InternalScheduledTask task = new InternalScheduledTask(runnable, false);
        if (!plugin.isEnabled()) {
            pluginDisabledMessage(plugin, Thread.currentThread().getStackTrace());
            return task;
        }

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, runnable, startTicks, repeatTicks);
        task.setBukkitTask(bukkitTask);

        return task;
    }

    @Override
    public IScheduledTask runTaskRepeatAsync(Plugin plugin, int startTicks, int repeatTicks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(startTicks);
        PreCon.positiveNumber(repeatTicks);

        InternalScheduledTask task = new InternalScheduledTask(runnable, false);
        if (!plugin.isEnabled()) {
            pluginDisabledMessage(plugin, Thread.currentThread().getStackTrace());
            return task;
        }

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin, runnable, startTicks, repeatTicks);
        task.setBukkitTask(bukkitTask);

        return task;
    }

    @Override
    public void runTaskSync(Plugin plugin, final Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);

        Bukkit.getScheduler().callSyncMethod(plugin, new SyncTask(runnable));
    }

    @Override
    public void runTaskSync(Plugin plugin, int ticks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.positiveNumber(ticks);
        PreCon.notNull(runnable);

        Bukkit.getScheduler().runTaskLater(plugin, new DelayedSyncTask(plugin, runnable), ticks);
    }

    private void pluginDisabledMessage(Plugin plugin, StackTraceElement[] stackTrace) {

        if (plugin instanceof NucleusPlugin && !((NucleusPlugin) plugin).isDebugging())
            return;

        StringBuilder sb = new StringBuilder(stackTrace.length * 30);

        try {
            TextUtils.printStackTrace(stackTrace, sb);
        } catch (IOException ignore) {
            // exception not possible with StringBuilder
        }

        NucMsg.debug(plugin, "Attempted to schedule task while plugin disabled.\n{0}", sb.toString());
    }

    private static class SyncTask implements Callable<Void> {

        private final Runnable _task;

        SyncTask(Runnable task) {
            _task = task;
        }

        @Override
        public Void call() throws Exception {
            try {
                _task.run();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class DelayedSyncTask implements Runnable {

        private final Plugin _plugin;
        private final Runnable _task;

        DelayedSyncTask (Plugin plugin, Runnable task) {
            _plugin = plugin;
            _task = task;
        }

        @Override
        public void run() {
            Bukkit.getScheduler().callSyncMethod(_plugin, new SyncTask(_task));
        }
    }
}
