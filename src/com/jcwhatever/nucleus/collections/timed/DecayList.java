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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

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

    private static final WeakHashMap<DecayList, Void> _instances = new WeakHashMap<>(10);

    // removes a single item from the list when run.
    private static transient Rot _rot;

    private final Plugin _plugin;

    // the decay interval in ticks
    private int _decay;

    // last decay check time
    private transient long _lastDecayCheck;

    // holds subscribers to be notified whenever an item is removed due to decay
    // or the list is empty.
    private transient NamedUpdateAgents _agents = new NamedUpdateAgents();

    /**
     * Constructor. Decay interval is every 1 second.
     */
    public DecayList(Plugin plugin) {
        this(plugin, 1, TimeScale.SECONDS);
    }

    /**
     * Constructor.
     *
     * @param plugin         The owning plugin.
     * @param decayInterval  The decay interval.
     * @param timeScale      The time scale of the specified decay interval.
     *                       Minimum value is 1 tick.
     */
    public DecayList(Plugin plugin, int decayInterval, TimeScale timeScale) {
        super();

        PreCon.notNull(plugin);
        PreCon.greaterThanZero(decayInterval);
        PreCon.notNull(timeScale);

        _plugin = plugin;
        _decay = Math.max(50, decayInterval * timeScale.getTimeFactor());
    }

    /**
     * Constructor.
     *
     * @param plugin         The owning plugin.
     * @param decayInterval  The decay interval.
     * @param timeScale      The time scale of the specified decay interval.
     *                       Minimum value is 1 tick.
     * @param collection     The initial collection of elements.
     */
    public DecayList(Plugin plugin, int decayInterval, TimeScale timeScale, Collection<E> collection) {
        super(collection);

        PreCon.notNull(plugin);
        PreCon.greaterThanZero(decayInterval);
        PreCon.notNull(timeScale);

        _plugin = plugin;
        _decay = Math.max(1, (decayInterval * timeScale.getTimeFactor()) / 50);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the decay interval in ticks.
     */
    public int getDecayInterval() {
        return _decay;
    }

    /**
     * Change the decay interval. Causes the decay interval to reset.
     *
     * @param interval   The decay interval.
     * @param timeScale  The timeScale of the interval. The maximum resolution is 1 tick.
     */
    public void setDecayInterval(int interval, TimeScale timeScale) {
        _decay = Math.max(50, interval * timeScale.getTimeFactor());
        scheduleRemoval();
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

        _agents.getAgent("onDecay").addSubscriber(subscriber);

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

        _agents.getAgent("onEmpty").addSubscriber(subscriber);

        return this;
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
        _instances.remove(this);
        super.clear();
    }

    // begins or resets the decay timer
    protected void scheduleRemoval() {

        // make sure there is a rot instance.
        if (_rot == null) {
            _rot = new Rot();

            Scheduler.runTaskRepeat(Nucleus.getPlugin(), 1, 1, _rot);
        }

        if (!_instances.containsKey(this))
            _instances.put(this, null);

        _lastDecayCheck = System.currentTimeMillis();
    }

    private static class Rot implements Runnable {

        final List<DecayList> lists = new ArrayList<>(10);

        @Override
        public void run() {

            lists.addAll(_instances.keySet());

            long time = System.currentTimeMillis();

            for (DecayList list : lists) {

                if (list.isEmpty()) {
                    _instances.remove(list);
                    continue;
                }

                if (list._lastDecayCheck + list._decay > time)
                    continue;

                Object removed = list.remove();

                list._agents.update("onDecay", removed);

                if (list.isEmpty()) {
                    list._agents.update("onEmpty", list);
                }

                list._lastDecayCheck = time;
            }

            lists.clear();
        }
    }
}
