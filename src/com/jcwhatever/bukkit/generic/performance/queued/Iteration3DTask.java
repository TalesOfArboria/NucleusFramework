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


package com.jcwhatever.bukkit.generic.performance.queued;

import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.plugin.Plugin;

/**
 * Abstract queue worker task designed to add functionality
 * to an implementation making it easier to iterate over 3D 
 * coordinates in a cuboid area.
 *
 * <p>Note: The 3D data is iterated from smallest value to largest value.</p>
 *
 */
public abstract class Iteration3DTask extends QueueTask {

    private long _segmentSize;
    private long _segmentsCompleted;
    private long _iterations;

    private int _xStart, _yStart, _zStart, _xEnd, _yEnd, _zEnd;

    private int _xCurrent, _yCurrent, _zCurrent;

    private long _volume;
    private final Object _sync = new Object();
    private int _delay = 10;

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
        Scheduler.runTaskLater(getPlugin(), _delay, new Iterator3D());
    }

    /**
     * Called for each 3D value set that is iterated over.
     *
     * @param x  The current x value
     * @param y  The current y value
     * @param z  The current z value
     */
    protected abstract void onIterateItem(final int x, final int y, final int z);

    /**
     * Called when the task is first run.
     *
     * <p>Implementation can optionally override this to perform
     * actions when the task begins.</p>
     */
    protected void onIterateBegin() {}

    /**
     * Called when iteration begins on a new segment of the iteration.
     *
     * <p>Implementation can optionally override this to perform
     * actions when a segment begins.</p>     
     *
     * @param x  The current x value
     * @param y  The current y value
     * @param z  The current z value
     */
    protected void onSegmentStart(int x, int y, int z) {}

    /**
     * Called when iteration over a segment ends.
     *
     * <p>Implementation can optionally override this to perform
     * actions when a segment ends.</p>
     *
     * @param x  The ending x value
     * @param y  The ending y value
     * @param z  The ending z value
     */
    protected void onSegmentEnd(int x, int y, int z) {}

    /**
     * Called just before the task finishes.
     *
     * <p>Implementation can optionally override this to perform
     * actions before a task finishes.</p>
     */
    protected void onPreComplete() {}



    // The worker that performs the iterations
    private class Iterator3D implements Runnable {

        @Override
        public void run() {

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
                                Scheduler.runTaskLater(getPlugin(), _delay, this);
                                return;
                            }

                            // check if this is the start of the segment
                            if (isStart) {
                                onSegmentStart(x, y, z);
                            }


                            isStart = false;

                            onIterateItem(x, y, z);

                            if (!isRunning())
                                return;

                            _iterations++;
                            completed++;
                        }
                    }
                }

                onPreComplete();
            }
            complete();

        }
    }
}

