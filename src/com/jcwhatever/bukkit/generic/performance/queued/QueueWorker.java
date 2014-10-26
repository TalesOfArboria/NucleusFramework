/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.performance.queued;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.Scheduler.ScheduledTask;
import com.jcwhatever.bukkit.generic.utils.Scheduler.TaskHandler;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Ensures large tasks/operations happen in
 * synchronous order, even if the task is asynchronous.
 */
public class QueueWorker {

    private static QueueWorker _globalWorker;

    /**
     * Get the global singleton {@code QueueWorker} instance.
     */
    public static QueueWorker get() {
        if (_globalWorker == null)
            _globalWorker = new QueueWorker();

        return _globalWorker;
    }

    private final LinkedList<QueueTask> _queue = new LinkedList<>();
    private final Worker _worker;

    private QueueTask _currentTask;
    private ScheduledTask _workerTask;

    private QueueWorker() {
        _worker = new Worker();
    }

    /**
     * Add a task to the queue worker.
     *
     * <p>If the queue worker is not already running, it
     * will automatically start after the task is added.</p>
     *
     * @param task  The task to add.
     */
    public void addTask(QueueTask task) {
        PreCon.notNull(task);

        _queue.add(task);

        run();
    }

    /**
     * Add a collection of tasks to the queue worker.
     *
     * <p>If the queue worker is not already running, it
     * will automatically start after tasks are added.</p>
     *
     * @param tasks  The tasks to add.
     */
    public void addTasks(Collection<QueueTask> tasks) {
        PreCon.notNull(tasks);

        _queue.addAll(tasks);

        run();
    }

    // called when a task is added.
    private void run() {

        if (_workerTask == null || _workerTask.isCancelled())
            _workerTask = Scheduler.runTaskRepeat(GenericsLib.getPlugin(), 10, 20, _worker);
    }

    /**
     * Runnable implementation responsible for
     * running project tasks.
     */
    private class Worker extends TaskHandler {

        @Override
        public void run() {

            // make sure the current task is finished before
            // starting the next one
            if (_currentTask != null && !_currentTask.isEnded())
                return;

            // check if all tasks are completed
            if (_queue.isEmpty()) {
                _currentTask = null;
                cancelTask();
                return;
            }

            // get next item in queue
            _currentTask = _queue.removeFirst();

            // make sure the task project hasn't been cancelled
            if (_currentTask.isCancelled()) {

                _currentTask = null;
                run(); // try the next task
            }
            else {

                switch (_currentTask.getConcurrency()) {
                    case MAIN_THREAD:
                        // fall through

                    case CURRENT_THREAD:
                        _currentTask.run();
                        break;

                    case ASYNC:
                        Scheduler.runTaskLaterAsync(_currentTask.getPlugin(), 1, new Runnable() {
                            @Override
                            public void run() {
                                _currentTask.run();
                            }
                        });
                        break;
                }
            }
        }
    }


}
