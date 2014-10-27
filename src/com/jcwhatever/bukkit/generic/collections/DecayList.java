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


package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.GenericsLib;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A Linked List that begins removing first in items at interval if new
 * items are not added before the interval is reached.
 *
 * <p>Items must be continuously added to prevent items from being removed.</p>
 *
 * <p>The number of items added at one time does not effect the length of the decay interval.</p>
 */
public class DecayList<T> extends LinkedList<T>{

    private static final long serialVersionUID = -532564384179678355L;

    // default removal delay when ticks are not specified
    private int _decayTicks;

    // task that removes items at interval.
    private BukkitTask _decayTask;

    // reference to Rot instance responsible for removing items.
    private Rot _rot;

    // holds actions to be executed whenever an item is removed due to decay
    private List<DecayAction<T>> _onDecay = new ArrayList<DecayAction<T>>(5);

    /**
     * Constructor. Decay interval is every 1 second.
     */
    public DecayList() {
        super();
        _decayTicks = 20;
    }

    /**
     * Constructor.
     *
     * @param decayIntervalTicks  The decay interval in ticks.
     */
    public DecayList(int decayIntervalTicks) {
        super();
        _decayTicks = decayIntervalTicks;
    }

    /**
     * Get the decay interval in ticks.
     */
    public int getDecayInterval() {
        return _decayTicks;
    }

    /**
     * Change the decay interval. Causes the decay interval to reset.
     *
     * @param ticks  The decay interval in ticks.
     */
    public void setDecayInterval(int ticks) {
        _decayTicks = ticks;
        scheduleRemoval();
    }

    /**
     * Add an item. Causes the decay interval to reset.
     *
     * @param item  The item to add.
     */
    @Override
    public boolean add(T item) {
        scheduleRemoval();
        return super.add(item);
    }

    /**
     * Add an item at the specified index.
     * Causes the decay interval to reset.
     *
     * @param index  The index to add the item at.
     * @param item   The item to add.
     */
    @Override
    public void add(int index, T item) {
        scheduleRemoval();
        super.add(index, item);
    }

    /**
     * Add a collection of items.
     * Causes the decay interval to reset.
     *
     * @param c  The collection to add.
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        scheduleRemoval();
        return super.addAll(c);
    }

    /**
     * Add a collection of items starting at the specified index.
     * Causes the decay interval to reset.
     *
     * @param index  The index to insert the items at.
     * @param c      The collection to insert.
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        scheduleRemoval();
        return super.addAll(index, c);
    }

    /**
     * Remove all items.
     *
     * <p>Decay handlers are not executed.</p>
     */
    @Override
    public void clear() {
        if (_decayTask != null) {
            _decayTask.cancel();
            _decayTask = null;
        }
        super.clear();
    }

    /**
     * Add a handler to be executed when an item decays.
     *
     * @param action  The handler to add.
     */
    public boolean addOnDecay(DecayAction<T> action) {
        return _onDecay.add(action);
    }

    /**
     * Remove a decay action handler.
     *
     * @param action  The handler to remove.
     */
    public boolean removeOnDecay(DecayAction<T> action) {
        return _onDecay.remove(action);
    }

    // begins or resets the decay timer
    protected void scheduleRemoval() {
        if (_decayTicks < 0)
            return;

        if (_decayTask != null)
            _decayTask.cancel();

        if (_rot == null)
            _rot = new Rot(this);

        _decayTask = Bukkit.getScheduler().runTaskTimer(GenericsLib.getPlugin(), new Rot(this), _decayTicks, _decayTicks);
    }

    public static abstract class DecayAction<T> {
        public abstract void onDecay(T decayedItem);
    }

    private class Rot implements Runnable {

        // a reference to the parent instance
        private final DecayList<T> _parent;

        Rot(DecayList<T> parent) {
            _parent = parent;
        }

        @Override
        public void run() {
            if (_parent.size() == 0)
                return;

            // remove a single item
            T removed = _parent.remove();

            // execute decay actions
            for (DecayAction<T> action : _onDecay) {
                action.onDecay(removed);
            }
        }
    }

}
