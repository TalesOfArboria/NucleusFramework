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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.wrap.CollectionWrapper;
import com.jcwhatever.nucleus.collections.wrap.MapWrapper;
import com.jcwhatever.nucleus.collections.wrap.SetWrapper;
import com.jcwhatever.nucleus.collections.wrap.SyncStrategy;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * An encapsulated {@link Multimap} where each key has an individual lifespan that
 * when ended, causes the item to be removed.
 *
 * <p>The lifespan can be reset by re-adding a key.</p>
 *
 * <p>Items can be added using the default lifespan time or a lifespan can be specified per item.</p>
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
 * <p>The maps iterators must be used inside a synchronized block which locks the
 * map instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public abstract class TimedMultimap<K, V> implements Multimap<K, V>, IPluginOwned {

    // The minimum interval the cleanup is allowed to run at.
    // Used to prevent cleanup from being run too often.
    private static final int MIN_CLEANUP_INTERVAL_MS = 50;

    // The interval the janitor runs at
    private static final int JANITOR_INTERVAL_TICKS = 10;

    // random initial delay interval for janitor, random to help spread out
    // task execution in relation to other scheduled tasks
    private static final int JANITOR_INITIAL_DELAY_TICKS = Rand.getInt(1, 9);

    private static final Map<TimedMultimap, Void> _instances = new WeakHashMap<>(10);
    private static IScheduledTask _janitor;

    private final Plugin _plugin;
    private final Multimap<K, V> _map;
    private final Map<K, ExpireInfo> _expireMap;

    private final int _lifespan;
    private final TimeScale _timeScale;

    private final transient KeySetWrapper _keySet;
    private final transient ValuesWrapper _values;
    private final transient EntriesWrapper _entries;
    private final transient AsMapWrapper _asMap;

    private final transient Object _sync;
    private final transient SyncStrategy _strategy;
    private transient long _nextCleanup;

    private final transient NamedUpdateAgents _agents = new NamedUpdateAgents();

    /**
     * Constructor. Default lifespan is 20 ticks.
     */
    public TimedMultimap(Plugin plugin) {
        this(plugin, 20, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param defaultLifespan  The lifespan used when one is not specified.
     * @param timeScale        The lifespan timescale.
     */
    public TimedMultimap(Plugin plugin, int defaultLifespan, TimeScale timeScale) {
        PreCon.notNull(plugin);
        PreCon.positiveNumber(defaultLifespan);
        PreCon.notNull(timeScale);

        _plugin = plugin;
        _sync = this;
        _strategy = new SyncStrategy(this);
        _lifespan = defaultLifespan * timeScale.getTimeFactor();
        _timeScale = timeScale;
        _map = createMultimap();
        _expireMap = new HashMap<>(_map.keySet().size() + 5);

        _keySet = new KeySetWrapper();
        _values = new ValuesWrapper();
        _entries = new EntriesWrapper();
        _asMap = new AsMapWrapper();

        synchronized (_instances) {
            _instances.put(this, null);
        }

        startJanitor();
    }

    /**
     * Put an item into the map using the specified lifespan in
     * the time scale specified in the constructor..
     *
     * @param key        The item key.
     * @param value      The item to add.
     * @param lifespan   The items lifespan.
     */
    public boolean put(K key, V value, long lifespan) {
        return put(key, value, lifespan, _timeScale);
    }

    /**
     * Put an item into the map using the specified lifespan in
     * the time scale specified.
     *
     * @param key        The item key.
     * @param value      The item to add.
     * @param lifespan   The items lifespan.
     * @param timeScale  The time scale of the specified lifespan
     */
    public boolean put(K key, V value, long lifespan, TimeScale timeScale) {
        PreCon.notNull(key);
        PreCon.notNull(value);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        synchronized (_sync) {

            if (_map.put(key, value)) {
                _expireMap.put(key, new ExpireInfo(key, lifespan, timeScale));

                return true;
            }
            return false;
        }
    }

    /**
     * Put a map of items into the map using the specified lifespan
     * in the time scale specified in the constructor..
     *
     * @param entries   The map to add.
     * @param lifespan  The lifespan of the added items.
     */
    public void putAll(Map<? extends K, ? extends V> entries, int lifespan) {
        putAll(entries, lifespan, _timeScale);
    }

    /**
     * Put a map of items into the map using the specified lifespan
     * int the time scale specified.
     *
     * @param entries    The map to add.
     * @param lifespan   The lifespan of the added items.
     * @param timeScale  The time scale of the specified lifespan.
     */
    public void putAll(Map<? extends K, ? extends V> entries, int lifespan, TimeScale timeScale) {
        PreCon.notNull(entries);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        synchronized (_sync) {

            for (Map.Entry<? extends K, ? extends V> entry : entries.entrySet()) {
                put(entry.getKey(), entry.getValue(), lifespan, timeScale);
            }
        }
    }

    /**
     * Register a subscriber to be notified when an entry's lifespan ends.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    public TimedMultimap<K, V> onLifespanEnd(IUpdateSubscriber<Entry<K, Collection<V>>> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onLifespanEnd").addSubscriber(subscriber);

        return this;
    }

    /**
     * Register a subscriber to be notified when the collection becomes
     * empty due to an entry's lifespan ending.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    public TimedMultimap<K, V> onEmpty(IUpdateSubscriber<TimedMultimap<K, V>> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onEmpty").addSubscriber(subscriber);

        return this;
    }

    /**
     * Invoked once during the constructor to create the
     * encapsulated {@link Multimap}.
     */
    protected abstract Multimap<K, V> createMultimap();

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public void clear() {
        _map.clear();
        _expireMap.clear();
    }

    @Override
    public int size() {
        synchronized (_sync) {
            cleanup();
            return _map.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            cleanup();
            return _map.isEmpty();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        PreCon.notNull(key);

        synchronized (_sync) {
            return !isExpiredRemove(key) &&
                    _map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(@Nullable Object value) {

        synchronized (_sync) {
            cleanup();
            return _map.containsValue(value);
        }
    }

    @Override
    public boolean containsEntry(@Nullable Object o, @Nullable Object o1) {

        synchronized (_sync) {
            cleanup();
            return _map.containsEntry(o, o1);
        }
    }

    @Override
    @Nullable
    public Collection<V> get(K key) {
        PreCon.notNull(key);

        synchronized (_sync) {
            if (isExpiredRemove(key)) {
                return CollectionUtils.unmodifiableList();
            }
            return _map.get(key);
        }
    }

    @Override
    public Set<K> keySet() {
        return _keySet;
    }

    @Override
    public Multiset<K> keys() {
        return _map.keys(); // TODO: Wrap
    }

    @Override
    public Collection<V> values() {
        return _values;
    }

    @Override
    public Collection<Entry<K, V>> entries() {
        return _entries;
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return _asMap;
    }

    @Override
    @Nullable
    public boolean put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        return put(key, value, _lifespan, TimeScale.TICKS);
    }

    @Override
    public boolean putAll(Multimap<? extends K, ? extends V> entries) {
        PreCon.notNull(entries);

        boolean isChanged = false;

        for (Map.Entry<? extends K, ? extends V> entry : entries.entries()) {
            isChanged = put(entry.getKey(), entry.getValue()) || isChanged;
        }

        return isChanged;
    }

    @Override
    public Collection<V> replaceValues(@Nullable K k, Iterable<? extends V> iterable) {
        synchronized (_sync) {
            Collection<V> result = _map.replaceValues(k, iterable);
            _expireMap.put(k, new ExpireInfo(k, _lifespan, TimeScale.MILLISECONDS));
            return result;
        }
    }

    @Override
    public Collection<V> removeAll(@Nullable Object o) {
        Collection<V> result = _map.removeAll(o);
        //noinspection SuspiciousMethodCalls
        _expireMap.remove(o);

        return result;
    }

    @Override
    public boolean remove(Object key, Object value) {
        PreCon.notNull(key);

        synchronized (_sync) {

            if (_map.remove(key, value)) {
                //noinspection SuspiciousMethodCalls
                _expireMap.remove(key);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean putAll(@Nullable K k, Iterable<? extends V> iterable) {
        if (iterable == null)
            return false;

        boolean isChanged = false;

        for (V element : iterable) {
            isChanged = put(k, element) || isChanged;
        }

        return isChanged;
    }


    private void onLifespanEnd(final K key, final Collection<V> values) {
        if (_agents.hasAgent("onLifespanEnd")) {
            _agents.getAgent("onLifespanEnd").update(getEntry(key, values));
        }

        if (isEmpty() && _agents.hasAgent("onEmpty")) {
            _agents.getAgent("onEmpty").update(this);
        }
    }

    private boolean isExpiredRemove(Object key) {
        //noinspection SuspiciousMethodCalls
        ExpireInfo info = _expireMap.get(key);
        if (info == null)
            return true;

        if (info.isExpired()) {

            //noinspection SuspiciousMethodCalls
            Collection<V> collection = _map.removeAll(key);

            //noinspection SuspiciousMethodCalls
            _expireMap.remove(key);

            onLifespanEnd(info.key, collection);

            return true;
        }

        return false;
    }

    private void cleanup() {

        if (_expireMap.isEmpty())
            return;

        // prevent cleanup from running too often
        if (_nextCleanup > System.currentTimeMillis())
            return;

        _nextCleanup = System.currentTimeMillis() + MIN_CLEANUP_INTERVAL_MS;

        Set<Map.Entry<K, ExpireInfo>> entries = new HashSet<>(_expireMap.entrySet());

        // iterate over entry set for items to remove
        for (Map.Entry<K, ExpireInfo> entry : entries) {

            // check if entry is expired
            if (entry.getValue().isExpired()) {

                _expireMap.remove(entry.getKey());

                Collection<V> removed = _map.removeAll(entry.getKey());

                // notify subscribers
                onLifespanEnd(entry.getKey(), removed);
            }
        }
    }

    private Entry<K, Collection<V>> getEntry(final K key, final Collection<V> values) {
        return new Entry<K, Collection<V>>() {

            Collection<V> value = values;

            @Override
            public K getKey() {
                return key;
            }

            @Override
            public Collection<V> getValue() {
                return values;
            }

            @Override
            public Collection<V> setValue(Collection<V> value) {
                Collection<V> prev = this.value;
                this.value = value;
                return prev;
            }
        };
    }

    private void startJanitor() {

        if (_janitor != null)
            return;

        synchronized (_instances) {

            if (_janitor != null)
                return;

            _janitor = Scheduler.runTaskRepeatAsync(
                    Nucleus.getPlugin(), JANITOR_INITIAL_DELAY_TICKS, JANITOR_INTERVAL_TICKS, new Runnable() {
                        @Override
                        public void run() {

                            List<TimedMultimap> maps;

                            synchronized (_instances) {
                                maps = new ArrayList<>(_instances.keySet());
                            }

                            for (TimedMultimap map : maps) {

                                if (!map._plugin.isEnabled()) {
                                    synchronized (_instances) {
                                        _instances.remove(map);
                                    }
                                    continue;
                                }

                                synchronized (map._sync) {
                                    map.cleanup();
                                }
                            }
                        }
                    });
        }
    }

    private final class ExpireInfo {
        final K key;
        final long expires;

        ExpireInfo(K key, long lifespan, TimeScale timeScale) {
            this.key = key;
            this.expires = System.currentTimeMillis() + (lifespan * timeScale.getTimeFactor());
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= expires;
        }
    }

    private final class KeySetWrapper extends SetWrapper<K> {

        KeySetWrapper() {
            super(TimedMultimap.this._strategy);
        }

        @Override
        protected boolean onPreAdd(K k) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void onRemoved(Object o) {
            synchronized (TimedMultimap.this._sync) {
                //noinspection SuspiciousMethodCalls
                _expireMap.remove(o);
            }
        }

        @Override
        protected void onClear(Collection<K> values) {
            synchronized (TimedMultimap.this._sync) {
                _expireMap.clear();
            }
        }

        @Override
        protected Set<K> set() {
            return _map.keySet();
        }
    }

    private final class ValuesWrapper extends CollectionWrapper<V> {

        ValuesWrapper() {
            super(TimedMultimap.this._strategy);
        }

        @Override
        protected boolean onPreAdd(V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean onPreRemove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void onClear(Collection<V> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected final Collection<V> collection() {
            return _map.values();
        }
    }

    private final class EntriesWrapper extends CollectionWrapper<Entry<K, V>> {

        EntriesWrapper() {
            super(TimedMultimap.this._strategy);
        }

        @Override
        protected void onAdded(Entry<K, V> entry) {
            synchronized (TimedMultimap.this._sync) {
                _expireMap.put(entry.getKey(), new ExpireInfo(entry.getKey(), _lifespan, TimeScale.MILLISECONDS));
            }
        }

        @Override
        protected boolean onPreRemove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void onClear(Collection<Entry<K, V>> values) {
            synchronized (TimedMultimap.this._sync) {
                _expireMap.clear();
            }
        }

        @Override
        protected Collection<Entry<K, V>> collection() {
            return _map.entries();
        }
    }

    private final class AsMapWrapper extends MapWrapper<K, Collection<V>> {

        AsMapWrapper() {
            super(TimedMultimap.this._strategy);
        }

        @Override
        protected Map<K, Collection<V>> map() {
            return _map.asMap();
        }
    }
}
