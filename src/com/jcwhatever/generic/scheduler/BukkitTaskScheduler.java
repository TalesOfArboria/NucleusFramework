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

package com.jcwhatever.generic.scheduler;

import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;

/**
 * Uses Bukkits Task Scheduler and adds use of TaskHandlers which
 * add functionality.
 */
public class BukkitTaskScheduler implements ITaskScheduler{

    @Override
    public ScheduledTask runTaskLater(Plugin plugin, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);

        BukkitTask task = Bukkit.getScheduler().runTask(plugin, runnable);
        return new ScheduledTask(runnable, task, false);
    }

    @Override
    public ScheduledTask runTaskLater(Plugin plugin, int ticks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(ticks);

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
        return new ScheduledTask(runnable, task, false);
    }

    @Override
    public ScheduledTask runTaskLaterAsync(Plugin plugin, int ticks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(ticks);

        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks);
        return new ScheduledTask(runnable, task, false);
    }

    @Override
    public ScheduledTask runTaskRepeat(Plugin plugin, int startTicks, int repeatTicks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(startTicks);
        PreCon.positiveNumber(repeatTicks);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, startTicks, repeatTicks);
        return new ScheduledTask(runnable, task, true);
    }

    @Override
    public ScheduledTask runTaskRepeatAsync(Plugin plugin, int startTicks, int repeatTicks, Runnable runnable) {
        PreCon.notNull(plugin);
        PreCon.notNull(runnable);
        PreCon.positiveNumber(startTicks);
        PreCon.positiveNumber(repeatTicks);

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, startTicks, repeatTicks);
        return new ScheduledTask(runnable, task, true);
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
