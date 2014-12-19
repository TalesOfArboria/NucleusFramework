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

import com.jcwhatever.bukkit.generic.scheduler.ScheduledTask;
import com.jcwhatever.bukkit.generic.scheduler.TaskHandler;
import com.jcwhatever.bukkit.generic.utils.DependencyRunner.IDependantRunnable;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A utility class to execute code when a dependant condition is met within
 * a timeout period.
 */
public class DependencyRunner<T extends IDependantRunnable> {

    private final Plugin _plugin;
    private final Set<T> _runnables;
    private final LinkedList<IFinishHandler<T>> _onFinished = new LinkedList<>();
    private final Watcher _watcher = new Watcher();
    private final int _timeoutSeconds;

    private ScheduledTask _watcherTask;
    private Date _endTime;

    /**
     * Constructor.
     *
     * <p>Timeout is 20 seconds.</p>
     *
     * @param plugin  The owning plugin.
     */
    public DependencyRunner(Plugin plugin) {
        this(plugin, 20);
    }

    /**
     * Constructor.
     *
     * @param plugin          The owning plugin.
     * @param timeoutSeconds  The timeout in seconds.
     */
    public DependencyRunner(Plugin plugin, int timeoutSeconds) {
        PreCon.notNull(plugin);

        _plugin = plugin;
        _runnables = new HashSet<>(20);
        _timeoutSeconds = timeoutSeconds;
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the number of seconds the {@code DependencyRunner} will run
     * before timing out.
     */
    public int getTimeoutSeconds() {
        return _timeoutSeconds;
    }

    /**
     * Add a dependant to run when its dependency conditions are met.
     *
     * @param runnable  The dependant runnable to add.
     *
     * @return  Self for chaining.
     */
    public DependencyRunner add(T runnable) {
        PreCon.notNull(runnable);
        checkCanUse();

        _runnables.add(runnable);
        return this;
    }

    /**
     * Add a collection of dependants to run when their dependency
     * conditions are met.
     *
     * @param runnables  The dependant runnables collection to add.
     *
     * @return  Self for chaining.
     */
    public DependencyRunner addAll(Collection<? extends T> runnables) {
        PreCon.notNull(runnables);
        checkCanUse();

        _runnables.addAll(runnables);
        return this;
    }

    /**
     * Add an {@code IFinishHandler} to run when all dependant runnables have run
     * or when the timeout is reached.
     *
     * @param onFinish  The handler to add.
     *
     * @return  Self for chaining.
     */
    public DependencyRunner onFinish(IFinishHandler<T> onFinish) {
        PreCon.notNull(onFinish);
        checkCanUse();

        _onFinished.add(onFinish);
        return this;
    }

    /**
     * Begin running dependant runnables when their dependency conditions are met
     * until the timeout is reached.
     *
     * <p>Once this is called, the {@code DependencyRunner} cannot be used anymore.</p>
     */
    public void start() {
        checkCanUse();

        if (_runnables.size() == 0)
            return;

        _endTime = DateUtils.addSeconds(new Date(), _timeoutSeconds);
        _watcherTask = Scheduler.runTaskRepeat(_plugin, 1, 10, _watcher);
    }

    // make sure start method has not been called.
    private void checkCanUse() {
        if (_watcherTask != null)
            throw new RuntimeException("The DependencyRunner cannot be use after the load method is called.");
    }

    /**
     * Interface for a dependant runnable.
     */
    public interface IDependantRunnable extends Runnable {

        /**
         * Determine if the required dependency conditions have been met.
         */
        boolean isDependencyReady();

        /**
         * Run the code that has dependencies.
         */
        @Override
        void run();
    }

    /**
     * Interface for a handler that is called when the {@code DependencyRunner} has
     * finished running all of the dependant runnables or the timeout has been reached.
     *
     * @param <T>  The {@code IDependantRunnable} type.
     */
    public interface IFinishHandler<T extends IDependantRunnable> {
        void onFinish(List<T> notRun);
    }

    /*
     * Periodically checks dependencies and runs dependant runnables whose
     * dependency conditions have been met. Cancels self when timeout is reached.
     */
    private class Watcher extends TaskHandler {

        @Override
        public void run() {
            LinkedList<T> runners = new LinkedList<>(_runnables);

            while (!runners.isEmpty()) {

                T runnable = runners.remove();

                if (runnable.isDependencyReady()) {

                    runnable.run();

                    _runnables.remove(runnable);
                }
            }

            if (_runnables.isEmpty() || new Date().compareTo(_endTime) >= 0)
                cancelTask();
        }

        /*
         * Called when the watcher is finished.
         */
        @Override
        protected void onCancel() {
            while (!_onFinished.isEmpty()) {
                IFinishHandler<T> onFinished = _onFinished.remove();
                onFinished.onFinish(new ArrayList<T>(_runnables));
            }
        }
    }
}
