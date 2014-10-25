package com.jcwhatever.bukkit.generic.performance.queued;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A project that accepts tasks to be completed in
 * synchronous order.
 *
 * <p>
 *     A project is a task and therefore can be added to
 *     other projects.
 * </p>
 */
public class QueueProject extends QueueTask {

    private QueueTask _currentTask;
    private final ProjectManager _managerTask;

    // tasks to be completed
    private final LinkedList<QueueTask> _tasks = new LinkedList<>();

    // finished tasks
    protected Set<QueueTask> _completed;

    // cancelled tasks
    protected Set<QueueTask> _cancelled;

    // failed tasks
    protected Set<QueueTask> _failed;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public QueueProject(Plugin plugin) {
        super(plugin, TaskConcurrency.MAIN_THREAD);

        _managerTask = new ProjectManager();
    }

    /**
     * Add a task to the project.
     *
     * @param task  The task to add.
     */
    public void addTask(QueueTask task) {
        PreCon.notNull(task);

        if (isRunning())
            throw new IllegalAccessError("Cannot add tasks while the project is running.");

        _tasks.add(task);

        task.setParentProject(this);
    }

    /**
     * Add a collection of tasks.
     *
     * @param tasks  The tasks to add.
     */
    public void addTasks(Collection<QueueTask> tasks) {
        PreCon.notNull(tasks);

        for (QueueTask task : tasks)
            addTask(task);
    }

    /**
     * Get all tasks in the project.
     *
     * <p>
     *     Do no call while the task is running.
     * </p>
     */
    public List<QueueTask> getTasks() {
        if (isRunning())
            throw new IllegalAccessError("Cannot retrieve tasks while the project is running.");

        return new ArrayList<QueueTask>(_tasks);
    }

    @Override
    protected void onRun() {

        _managerTask.run();
    }

    /**
     * Called by child tasks to notify project
     * that the state of the child has changed.
     */
    void update(QueueTask task) {

        if (task.isCancelled()) {
            if (_cancelled == null)
                _cancelled = new HashSet<>(10);

            _cancelled.add(task);
        }

        if (task.isFailed()) {
            if (_failed == null)
                _failed = new HashSet<>(10);

            _failed.add(task);
        }

        if (task.isComplete()) {
            if (_completed == null)
                _completed = new HashSet<>(_tasks.size() + 1);

            _completed.add(task);
        }

        // ensure tasks executed independently are removed.
        if (task.isEnded()) {
            _tasks.remove(task);
        }

        _managerTask.run();
    }

    /**
     * Runnable implementation responsible for
     * running project tasks.
     */
    private class ProjectManager implements Runnable {

        @Override
        public void run() {

            // make sure the current task is finished before
            // starting the next one
            if (_currentTask != null && !_currentTask.isEnded())
                return;

            // check if all tasks are completed
            if (_tasks.isEmpty()) {
                complete();
                _currentTask = null;
                return;
            }

            // get next item in queue
            _currentTask = _tasks.removeFirst();

            // make sure the task project hasn't been cancelled
            if (_currentTask.isCancelled()) {

                _currentTask = null;
                run();
            }
            else {

                switch (_currentTask.getConcurrency()) {
                    case MAIN_THREAD:
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
