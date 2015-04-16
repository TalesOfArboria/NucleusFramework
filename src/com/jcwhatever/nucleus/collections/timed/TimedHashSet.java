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
import com.jcwhatever.nucleus.collections.wrap.IteratorWrapper;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;
import com.jcwhatever.nucleus.utils.performance.pool.IPoolElementFactory;
import com.jcwhatever.nucleus.utils.performance.pool.SimpleConcurrentPool;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A hash set where each item has its own lifespan. When an items lifespan ends,
 * it is removed from the hash set.
 *
 * <p>If a duplicate item is added, the items lifespan is reset, in addition to normal
 * hash set operations.</p>
 *
 * <p>Subscribers that are added to track when an item expires or the collection is
 * empty will have a varying degree of resolution up to 10 ticks, meaning the subscriber
 * may be notified up to 10 ticks after an element expires (but not before).</p>
 *
 * <p>Getter operations cease to return an element within approximately 50 milliseconds
 * (1 tick) of expiring.</p>
 *
 * <p>Thread safe.</p>
 *
 * <p>The sets iterators must be used inside a synchronized block which locks the
 * set instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class TimedHashSet<E> implements Set<E>, IPluginOwned {

    // The minimum interval the cleanup is allowed to run at.
    // Used to prevent cleanup from being run too often.
    private static final int MIN_CLEANUP_INTERVAL_MS = 50;

    // The interval the janitor runs at
    private static final int JANITOR_INTERVAL_TICKS = 10;

    // random initial delay interval for janitor, random to help spread out
    // task execution in relation to other scheduled tasks
    private static final int JANITOR_INITIAL_DELAY_TICKS = Rand.getInt(1, 9);

    private final static Map<TimedHashSet, Void> _instances = new WeakHashMap<>(10);
    private static IScheduledTask _janitor;

    private final Plugin _plugin;
    private final Map<E, ExpireInfo> _expireMap;

    private final int _lifespan; // milliseconds
    private final TimeScale _timeScale;

    private final transient Object _sync;
    private transient long _nextCleanup;

    private final transient NamedUpdateAgents _agents = new NamedUpdateAgents();
    private final transient List<Entry<E, ExpireInfo>> _cleanupList = new ArrayList<>(20);

    private final transient SimpleConcurrentPool<ExpireInfo> _expirePool;

    /**
     * Constructor. Default lifespan is 20 ticks.
     */
    public TimedHashSet(Plugin plugin) {
        this(plugin, 10, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Default lifespan is 20 ticks.
     *
     * @param size  The initial capacity.
     */
    public TimedHashSet(Plugin plugin, int size) {
        this(plugin, size, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Default lifespan is 20 ticks.
     *
     * @param size             The initial capacity.
     * @param defaultLifespan  The default lifespan.
     */
    public TimedHashSet(Plugin plugin, int size, int defaultLifespan) {
        this(plugin, size, defaultLifespan, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param size             The initial capacity.
     * @param defaultLifespan  The default lifespan.
     * @param timeScale        The lifespan time scale.
     */
    public TimedHashSet(Plugin plugin, int size, int defaultLifespan, TimeScale timeScale) {
        PreCon.notNull(plugin);
        PreCon.positiveNumber(defaultLifespan);
        PreCon.notNull(timeScale);

        _plugin = plugin;
        _sync = this;
        _lifespan = defaultLifespan * timeScale.getTimeFactor();
        _timeScale = timeScale;
        _expireMap = new HashMap<>(size);

        _expirePool = new SimpleConcurrentPool<ExpireInfo>(ExpireInfo.class, size,
                new IPoolElementFactory<ExpireInfo>() {
                    @Override
                    public ExpireInfo create() {
                        return new ExpireInfo();
                    }
                });

        synchronized (_instances) {
            _instances.put(this, null);
        }

        startJanitor();
    }

    /**
     * Determine if the set contains the specified item and
     * if present, reset the items lifespan using the time scale
     * specified in the constructor.
     *
     * @param item       The item to check.
     * @param lifespan   The new lifespan of the item.
     */
    public boolean contains(E item, int lifespan) {
        return contains(item, lifespan, _timeScale);
    }

    /**
     * Determine if the set contains the specified item and
     * if present, reset the items lifespan using the specified
     * time scale.
     *
     * @param item       The item to check.
     * @param lifespan   The new lifespan of the item.
     * @param timeScale  The time scale of the specified lifespan.
     */
    public boolean contains(E item, int lifespan, TimeScale timeScale) {
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        synchronized (_sync) {
            ExpireInfo info = _expireMap.get(item);
            if (info == null)
                return false;

            if (info.isExpired()) {
                _expireMap.remove(item);
                onLifespanEnd(item);
                _expirePool.recycle(info);
                return false;
            }

            ExpireInfo expireInfo = _expirePool.retrieve();
            assert expireInfo != null;

            _expireMap.put(item, expireInfo.set(lifespan, timeScale));

            return true;
        }
    }

    /**
     * Add an item to the hash set with the specified lifespan
     * using the time scale specified in the constructor.
     *
     * <p>If the item is already present, the lifespan is reset
     * using the new lifespan value.</p>
     *
     * @param item       The item to add.
     * @param lifespan   The lifespan of the item.
     *
     * @return  True if added.
     */
    public boolean add(E item, int lifespan) {
        return add(item, lifespan, _timeScale);
    }

    /**
     * Add an item to the hash set with the specified lifespan
     * using the time scale specified.
     *
     * <p>If the item is already present, the lifespan is reset
     * using the new lifespan value and the item is replaced with
     * the new value.</p>
     *
     * @param item       The item to add.
     * @param lifespan   The lifespan of the item.
     * @param timeScale  The time scale of the specified lifespan.
     *
     * @return  True if added.
     */
    public boolean add(E item, int lifespan, TimeScale timeScale) {
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        synchronized (_sync) {

            ExpireInfo expireInfo = _expirePool.retrieve();
            assert expireInfo != null;

            _expireMap.put(item, expireInfo.set(lifespan, timeScale));
            return true;
        }
    }

    /**
     * Add items from a collection using the specified lifespan in
     * the time scale specified in the constructor.
     *
     * <p>Any item that is already present will have its lifespan reset
     * using the specified lifespan value and the item is replaced with
     * the new element.</p>
     *
     * @param collection  The collection to add.
     * @param lifespan    The lifespan of the item.
     *
     * @return  True if the internal collection was modified.
     */
    public boolean addAll(Collection<? extends E> collection, int lifespan) {
        return addAll(collection, lifespan, _timeScale);
    }

    /**
     * Add items from a collection using the specified lifespan in
     * the time scale specified.
     *
     * <p>Any item that is already present will have its lifespan reset
     * using the specified lifespan value and the item is replaced with
     * the new element.</p>
     *
     * @param collection  The collection to add.
     * @param lifespan    The lifespan of the item.
     * @param timeScale   The time scale of the specified lifespan.
     */
    public boolean addAll(Collection<? extends E> collection, int lifespan, TimeScale timeScale) {
        PreCon.notNull(collection);
        PreCon.positiveNumber(lifespan);

        synchronized (_sync) {
            for (E item : collection) {

                ExpireInfo expireInfo = _expirePool.retrieve();
                assert expireInfo != null;

                _expireMap.put(item, expireInfo.set(lifespan, timeScale));
            }
        }

        return true;
    }

    /**
     * Set the maximum size of the internal object pool used
     * for pooling internal instances.
     *
     * @param poolSize  The maximum pool size. -1 for "infinite".
     *
     * @return  Self for chaining.
     */
    public TimedHashSet<E> setMaxPoolSize(int poolSize) {
        _expirePool.setMaxSize(poolSize);
        return this;
    }

    /**
     * Get the maximum size of the internal object pool used
     * for pooling internal instances.
     */
    public int getMaxPoolSize() {
        return _expirePool.maxSize();
    }

    /**
     * Register a subscriber to be notified when an elements lifespan
     * ends.
     *
     * @param subscriber  The subscriber to register.
     *
     * @return  Self for chaining.
     */
    public TimedHashSet<E> onLifespanEnd(IUpdateSubscriber<E> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onLifespanEnd").addSubscriber(subscriber);

        return this;
    }

    /**
     * Register a subscriber to be notified when the collection is empty
     * due to elements expiring.
     *
     * @param subscriber  The subscriber to register.
     *
     * @return  Self for chaining.
     */
    public TimedHashSet<E> onEmpty(IUpdateSubscriber<TimedHashSet<E>> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onEmpty").addSubscriber(subscriber);

        return this;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public int size() {
        synchronized (_sync) {
            cleanup();
            return _expireMap.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            cleanup();
            return _expireMap.isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        PreCon.notNull(o);

        synchronized (_sync) {

            //noinspection SuspiciousMethodCalls
            ExpireInfo info = _expireMap.get(o);
            if (info == null)
                return false;

            if (info.isExpired()) {
                //noinspection SuspiciousMethodCalls
                _expireMap.remove(o);
                _expirePool.recycle(info);
                return false;
            }

            return true;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        synchronized (_sync) {
            cleanup();
            return _expireMap.keySet().toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        PreCon.notNull(a);

        synchronized (_sync) {
            cleanup();
            //noinspection SuspiciousToArrayCall
            return _expireMap.keySet().toArray(a);
        }
    }

    @Override
    public boolean add(E item) {
        PreCon.notNull(item);

        return add(item, _lifespan, TimeScale.MILLISECONDS);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        PreCon.notNull(collection);

        return addAll(collection, _lifespan, TimeScale.MILLISECONDS);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        PreCon.notNull(c);

        synchronized (_sync) {
            return _expireMap.keySet().retainAll(c);
        }
    }

    @Override
    public void clear() {
        synchronized (_sync) {

            for (Entry<E, ExpireInfo> entry : _expireMap.entrySet()) {
                _expirePool.recycle(entry.getValue());
            }

            _expireMap.clear();
        }
    }

    @Override
    public boolean remove(Object item) {
        PreCon.notNull(item);

        synchronized (_sync) {
            ExpireInfo info = _expireMap.remove(item);
            if (info == null)
                return false;

            if (info.isExpired()) {
                _expirePool.recycle(info);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        synchronized (_sync) {
            cleanup();
            return _expireMap.keySet().containsAll(c);
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        boolean isChanged = false;

        synchronized (_sync) {
            for (Object item : collection) {
                //noinspection SuspiciousMethodCalls
                ExpireInfo info = _expireMap.remove(item);
                if (info == null)
                    continue;

                if (!info.isExpired())
                    isChanged = true;

                _expirePool.recycle(info);
            }

            return isChanged;
        }
    }

    private void onLifespanEnd(E item) {

        _agents.update("onLifespanEnd", item);

        if (_expireMap.isEmpty()) {
            _agents.update("onEmpty", this);
        }
    }

    private void cleanup() {

        if (_expireMap.isEmpty())
            return;

        // prevent cleanup from running too often
        if (_nextCleanup > System.currentTimeMillis())
            return;

        _nextCleanup = System.currentTimeMillis() + MIN_CLEANUP_INTERVAL_MS;

        _cleanupList.addAll(_expireMap.entrySet());

        for (Entry<E, ExpireInfo> entry : _cleanupList) {
            if (!entry.getValue().isExpired())
                continue;

            _expireMap.remove(entry.getKey());
            onLifespanEnd(entry.getKey());
            _expirePool.recycle(entry.getValue());
        }

        _cleanupList.clear();
    }

    private void startJanitor() {
        if (_janitor != null)
            return;

        _janitor = Scheduler.runTaskRepeatAsync(Nucleus.getPlugin(),
                JANITOR_INITIAL_DELAY_TICKS, JANITOR_INTERVAL_TICKS, new Runnable() {

                    List<TimedHashSet> sets = new ArrayList<>(15);

                    @Override
                    public void run() {

                        synchronized (_instances) {
                            sets.addAll(_instances.keySet());
                        }

                        for (TimedHashSet set : sets) {

                            // remove instance if owning plugin is not enabled
                            if (!set.getPlugin().isEnabled()) {

                                synchronized (_instances) {
                                    _instances.remove(set);
                                }
                                continue;
                            }

                            // cleanup instance
                            synchronized (set._sync) {
                                set.cleanup();
                            }
                        }

                        sets.clear();
                    }
                });
    }

    private static final class ExpireInfo {

        long expires;

        ExpireInfo set(long lifespan, TimeScale timeScale) {
            expires = System.currentTimeMillis() + (lifespan * timeScale.getTimeFactor());
            return this;
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= expires;
        }
    }

    private final class Itr implements Iterator<E> {

        Iterator<Entry<E, ExpireInfo>> iterator
                = _expireMap.entrySet().iterator();

        Entry<E, ExpireInfo> peek;
        boolean invokedHasNext;
        boolean invokedNext;

        @Override
        public boolean hasNext() {

            IteratorWrapper.assertIteratorLock(_sync);

            invokedHasNext = true;

            if (!iterator.hasNext())
                return false;

            while (iterator.hasNext()) {
                peek = iterator.next();

                if (peek.getValue().isExpired()) {
                    iterator.remove();
                }
                else {
                    return true;
                }
            }

            return false;

        }

        @Override
        public E next() {

            IteratorWrapper.assertIteratorLock(_sync);

            if (!invokedHasNext)
                throw new IllegalStateException("Cannot invoke 'next' before invoking 'hasNext'.");

            invokedHasNext = false;
            invokedNext = true;

            if (peek == null)
                hasNext();

            if (peek == null)
                throw new NoSuchElementException();

            Entry<E, ExpireInfo> n = peek;
            peek = null;
            return n.getKey();
        }

        @Override
        public void remove() {

            IteratorWrapper.assertIteratorLock(_sync);

            if (!invokedNext)
                throw new IllegalStateException("Cannot invoke 'remove' before invoking 'next'");

            iterator.remove();
        }
    }
}
