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


package com.jcwhatever.nucleus.managed.scheduler;

import com.jcwhatever.nucleus.Nucleus;

import org.bukkit.plugin.Plugin;

/**
 * Utility to reduce the amount of code needed to use the NucleusFramework task scheduler.
 */
public final class Scheduler {

    private Scheduler() {}

    /**
     * Run a task later.
     *
     * <p>A {@link TaskHandler} instance can be used in place
     * of a {@link java.lang.Runnable} to add the ability to cancel the task from within the task
     * handler and to run optional code if the task is cancelled.</p>
     *
     * @param plugin    The owning plugin.
     * @param runnable  The {@link java.lang.Runnable} to run later.
     *
     * @return  A {@link IScheduledTask} instance to keep track of the task.
     */
    public static IScheduledTask runTaskLater(Plugin plugin, Runnable runnable) {
        return Nucleus.getScheduler().runTaskLater(plugin, runnable);
    }

    /**
     * Run a task after a specified number of ticks have elapsed.
     *
     * <p>A {@link TaskHandler} instance can be used in place
     * of a {@link java.lang.Runnable} to add the ability to cancel the task from within the task
     * handler and to run optional code if the task is cancelled.</p>
     *
     * @param plugin    The owning plugin.
     * @param ticks     The number of ticks to wait before running the task.
     * @param runnable  The {@link java.lang.Runnable} to run later.
     *
     * @return  A {@link IScheduledTask} instance to keep track of the task.
     */
    public static IScheduledTask runTaskLater(Plugin plugin, int ticks, Runnable runnable) {
        return Nucleus.getScheduler().runTaskLater(plugin, ticks, runnable);
    }

    /**
     * Run a task on a new asynchronous thread after a specified number
     * of ticks have elapsed.
     *
     * <p>A {@link TaskHandler} instance can be used in place
     * of a {@link java.lang.Runnable} to add the ability to cancel the task from within the task
     * handler and to run optional code if the task is cancelled.</p>
     *
     * @param plugin    The owning plugin.
     * @param ticks     The number of ticks to wait before running the task.
     * @param runnable  The {@link Runnable} to run later.
     *
     * @return  A {@link IScheduledTask} instance to keep track of the task.
     */
    public static IScheduledTask runTaskLaterAsync(Plugin plugin, int ticks, Runnable runnable) {
        return Nucleus.getScheduler().runTaskLaterAsync(plugin, ticks, runnable);
    }

    /**
     * Run a task on a repeating schedule after a specified number of ticks
     * have elapsed and repeating after the specified repeat ticks have elapsed.
     *
     * <p>A {@link TaskHandler} instance can be used in place
     * of a {@link java.lang.Runnable} to add the ability to cancel the task from within the task
     * handler and to run optional code if the task is cancelled.</p>
     *
     * @param plugin       The owning plugin.
     * @param startTicks   The number of ticks to wait before first running the task.
     * @param repeatTicks  The number of ticks to wait between each repeat of the task.
     * @param runnable     The {@link java.lang.Runnable} to run later.
     *
     * @return  A {@link IScheduledTask} instance to keep track of the task.
     */
    public static IScheduledTask runTaskRepeat(Plugin plugin, int startTicks, int repeatTicks, Runnable runnable) {
        return Nucleus.getScheduler().runTaskRepeat(plugin, startTicks, repeatTicks, runnable);
    }

    /**
     * Run a task on a new asynchronous repeating schedule after a specified number
     * of ticks have elapsed and repeating after the specified repeat ticks have
     * elapsed.
     *
     * <p>A {@link TaskHandler} instance can be used in place
     * of a {@link java.lang.Runnable} to add the ability to cancel the task from within the task
     * handler and to run optional code if the task is cancelled.</p>
     *
     * @param plugin       The owning plugin.
     * @param startTicks   The number of ticks to wait before first running the task.
     * @param repeatTicks  The number of ticks to wait between each repeat of the task.
     * @param runnable     The {@link java.lang.Runnable} to run later.
     *
     * @return  A {@link IScheduledTask} instance to keep track of the task.
     */
    public static IScheduledTask runTaskRepeatAsync(Plugin plugin,
                                                    int startTicks, int repeatTicks,
                                                    Runnable runnable) {
        return Nucleus.getScheduler().runTaskRepeatAsync(plugin, startTicks, repeatTicks, runnable);
    }

    /**
     * Run a task on the main thread at the next available chance.
     *
     * @param plugin    The owning plugin.
     * @param runnable  The {@link java.lang.Runnable} to run later.
     */
    public static void runTaskSync(Plugin plugin, Runnable runnable) {
        Nucleus.getScheduler().runTaskSync(plugin, runnable);
    }

    /**
     * Run a task on the main thread after the specified number of ticks have
     * elapsed.
     *
     * @param plugin    The owning plugin.
     * @param ticks     The number of ticks to wait before running the task.
     * @param runnable  The {@link java.lang.Runnable} to run later.
     */
    public static void runTaskSync(Plugin plugin, int ticks, Runnable runnable) {
        Nucleus.getScheduler().runTaskSync(plugin, ticks, runnable);
    }
}
