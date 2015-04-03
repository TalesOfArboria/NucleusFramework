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


package com.jcwhatever.nucleus.utils.performance.queued;

import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.utils.scheduler.TaskHandler;

import org.bukkit.plugin.Plugin;

/**
 * Abstract implementation of a {@link QueueTask} designed to make it easier to
 * perform iterative tasks over 3D arrays/coordinates.
 *
 * <p>Iterative tasks can be broken up into segments which are performed like individual
 * tasks; run in consecutive order with a delay between each segment run.</p>
 *
 * <p>Note: The 3D data is iterated from smallest value to largest value.</p>
 */
public abstract class Iteration3DTask extends QueueTask {

    private long _segmentSize;
    private long _segmentsCompleted;
    private long _iterations;

    private int _xStart, _yStart, _zStart, _xEnd, _yEnd, _zEnd;

    private int _xCurrent, _yCurrent, _zCurrent;

    private long _volume;
    private final Object _sync = new Object();
    private IScheduledTask _task;

    /**
     * Constructor. Initializes the 3D task parameters.
     *
     * <p>Note: The start values for the 3D points must be smaller than the end values</p>
     *
     * @param plugin       The owning plugin
     * @param concurrency  The task concurrency. (Main thread or Async)
     * @param segmentSize  The number of iterations to perform before pausing
     * @param xStart       The x value to start from
     * @param yStart       The y value to start from
     * @param zStart       The z value to start from
     * @param xEnd         The x value to end at
     * @param yEnd         The y value to end at
     * @param zEnd         The z value to end at
     */
    public Iteration3DTask(Plugin plugin, TaskConcurrency concurrency,
                           long segmentSize, int xStart, int yStart, int zStart,
                           int xEnd, int yEnd, int zEnd) {
        super(plugin, concurrency);

        _xCurrent = _xStart = Math.min(xStart, xEnd);
        _xEnd = Math.max(xStart, xEnd);

        _yCurrent = _yStart = Math.min(yStart, yEnd);
        _yEnd = Math.max(yStart, yEnd);

        _zCurrent =_zStart = Math.min(zStart, zEnd);
        _zEnd = Math.max(zStart, zEnd);

        _volume = (_xEnd - _xStart) * (_yEnd - _yStart) * (_zEnd - _zStart);
        _segmentSize = segmentSize;
    }

    /**
     * Get the starting coordinates X value.
     */
    public final int getXStart() {
        return _xStart;
    }

    /**
     * Get the starting coordinates Y value.
     */
    public final int getYStart() {
        return _yStart;
    }

    /**
     * Get the starting coordinates Z value.
     */
    public final int getZStart() {
        return _zStart;
    }


    /**
     * Get the end coordinates X value.
     */
    public final int getXEnd() {
        return _xEnd;
    }

    /**
     * Get the end coordinates Y value.
     */
    public final int getYEnd() {
        return _yEnd;
    }

    /**
     * Get the end coordinates Z value.
     */
    public final int getZEnd() {
        return _zEnd;
    }

    /**
     * Get the current X index.
     */
    public final int getXCurrent() {
        return _xCurrent;
    }

    /**
     * Get the current Y index.
     */
    public final int getYCurrent() {
        return _yCurrent;
    }

    /**
     * Get the current Z index.
     */
    public final int getZCurrent() {
        return _zCurrent;
    }

    /**
     * Get the total number of segments completed.
     */
    public final long getSegmentsCompleted() {
        return _segmentsCompleted;
    }

    /**
     * Get the size of an iterated segment
     */
    public final long getSegmentSize() {
        return _segmentSize;
    }

    /**
     * Get the total number of iterations completed.
     */
    public final long getIterations() {
        return _iterations;
    }

    /**
     * Get the total volume of the 3D cuboid.
     */
    public final long getVolume() {
        return _volume;
    }

    @Override
    protected final void onRun() {
        onIterateBegin();

        if (_task != null)
            _task.cancel();

        _task = Scheduler.runTaskRepeat(getPlugin(), 1, 10, new Iterator3D());
    }

    /**
     * Invoked for each 3D value set that is iterated over.
     *
     * @param x  The current x value.
     * @param y  The current y value.
     * @param z  The current z value.
     */
    protected abstract void onIterateItem(final int x, final int y, final int z);

    /**
     * Invoked before the task is first run.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onIterateBegin() {}

    /**
     * Invoked before iteration begins on a new segment.
     *
     * <p>Intended for optional override.</p>
     *
     * @param x  The current x value.
     * @param y  The current y value.
     * @param z  The current z value.
     */
    protected void onSegmentStart(int x, int y, int z) {}

    /**
     * Invoked after iteration over a segment ends.
     *
     * <p>Intended for optional override.</p>
     *
     * @param x  The ending x value.
     * @param y  The ending y value.
     * @param z  The ending z value.
     */
    protected void onSegmentEnd(int x, int y, int z) {}

    /**
     * Invoked just before the task finishes.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onPreComplete() {}

    // The worker that performs the iterations
    private class Iterator3D extends TaskHandler {

        @Override
        public void run() {

            if (isEnded()) {
                cancelTask();
                return;
            }

            synchronized (_sync) {

                boolean isStart = true;
                int completed = 0;

                for (int y = isStart ? _yCurrent : _yStart; y <= _yEnd; y++) {

                    for (int x = isStart ? _xCurrent : _xStart; x <= _xEnd; x++) {

                        for (int z = isStart ? _zCurrent : _zStart; z <= _zEnd; z++) {

                            // check for end of segment
                            if (_segmentSize > 0 && completed >= _segmentSize) {
                                _segmentsCompleted ++;

                                _xCurrent = x;
                                _yCurrent = y;
                                _zCurrent = z;

                                onSegmentEnd(x, y, z);

                                // schedule next segment
                                // Scheduler.runTaskLater(getPlugin(), _delay, this);
                                return;
                            }

                            // check if this is the start of the segment
                            if (isStart) {
                                onSegmentStart(x, y, z);
                            }


                            isStart = false;

                            onIterateItem(x, y, z);

                            if (!isRunning()) {
                                cancelTask();
                                return;
                            }

                            _iterations++;
                            completed++;
                        }
                    }
                }

                onPreComplete();
            }
            complete();
            cancelTask();
        }
    }
}

