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


package com.jcwhatever.nucleus.collections.timed;

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A Linked List that begins removing first in items at interval if new
 * items are not added before the interval is reached.
 *
 * <p>Items must be continuously added to prevent items from being removed.</p>
 *
 * <p>The number of items added at one time does not effect the length of the
 * decay interval.</p>
 *
 * <p>Subscribers that are added to track when an element decays or the collection
 * is empty are notified without delay when the element is removed or the
 * collection becomes empty due to decay.</p>
 *
 * <p>Not thread safe.</p>
 */
public class DecayList<E> extends LinkedList<E> implements IPluginOwned {

    private static final long serialVersionUID = -532564384179678355L;

    private final Plugin _plugin;

    // default removal delay when ticks are not specified
    private int _decayTicks;

    // task that removes items at interval.
    private transient BukkitTask _decayTask;
    private transient Rot _rot;

    // holds subscribers to be notified whenever an item is removed due to decay
    // or the list is empty.
    private transient NamedUpdateAgents _agents = new NamedUpdateAgents();

    /**
     * Constructor. Decay interval is every 1 second.
     */
    public DecayList(Plugin plugin) {
        this(plugin, 20);
    }

    /**
     * Constructor.
     *
     * @param plugin              The owning plugin.
     * @param decayIntervalTicks  The decay interval in ticks.
     */
    public DecayList(Plugin plugin, int decayIntervalTicks) {
        super();

        PreCon.notNull(plugin);
        PreCon.greaterThanZero(decayIntervalTicks);

        _plugin = plugin;
        _decayTicks = decayIntervalTicks;
    }

    /**
     * Constructor.
     *
     * @param plugin              The owning plugin.
     * @param decayIntervalTicks  The decay interval in ticks.
     * @param collection          The initial collection of elements.
     */
    public DecayList(Plugin plugin, int decayIntervalTicks, Collection<E> collection) {
        super(collection);

        PreCon.notNull(plugin);
        PreCon.greaterThanZero(decayIntervalTicks);

        _plugin = plugin;
        _decayTicks = decayIntervalTicks;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
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
     * @param interval   The decay interval.
     * @param timeScale  The timeScale of the interval. The maximum resolution is ticks.
     */
    public void setDecayInterval(int interval, TimeScale timeScale) {
        _decayTicks = (interval * timeScale.getTimeFactor()) / 50;
        scheduleRemoval();
    }

    @Override
    public boolean add(E item) {
        scheduleRemoval();
        return super.add(item);
    }

    @Override
    public void add(int index, E item) {
        scheduleRemoval();
        super.add(index, item);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        scheduleRemoval();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        scheduleRemoval();
        return super.addAll(index, c);
    }

    @Override
    public void clear() {
        if (_decayTask != null) {
            _decayTask.cancel();
            _decayTask = null;
        }
        super.clear();
    }

    /**
     * Register a subscriber to be notified when an item is removed
     * due to decay.
     *
     * @param subscriber  The subscriber to register.
     *
     * @return  Self for chaining.
     */
    public DecayList<E> onDecay(IUpdateSubscriber<E> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onDecay").register(subscriber);

        return this;
    }

    /**
     * Register a subscriber to be notified when the list is empty
     * due to decay.
     *
     * @param subscriber  The subscriber to register.
     *
     * @return  Self for chaining.
     */
    public DecayList<E> onEmpty(IUpdateSubscriber<DecayList<E>> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onEmpty").register(subscriber);

        return this;
    }

    // begins or resets the decay timer
    protected void scheduleRemoval() {
        if (_decayTicks < 0)
            return;

        if (_decayTask != null)
            _decayTask.cancel();

        if (_rot == null)
            _rot = new Rot<E>(this);

        _decayTask = Bukkit.getScheduler().runTaskTimer(
                _plugin, _rot, _decayTicks, _decayTicks);
    }

    private static class Rot<E> implements Runnable {

        // a reference to the parent instance
        private final DecayList<E> parent;

        Rot(DecayList<E> parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            if (parent.isEmpty())
                return;

            // remove a single item
            E removed = parent.remove();

            if (parent._agents.hasAgent("onDecay")) {
                parent._agents.getAgent("onDecay").update(removed);
            }

            if (parent.isEmpty() && parent._agents.hasAgent("onEmpty")) {
                parent._agents.getAgent("onEmpty").update(parent);
            }

            try {
                Thread.sleep(1L);
            } catch (InterruptedException ignore) {}
        }
    }

}
