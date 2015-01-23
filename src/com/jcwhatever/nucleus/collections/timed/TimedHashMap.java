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
import com.jcwhatever.nucleus.collections.wrap.ConversionEntryWrapper;
import com.jcwhatever.nucleus.collections.wrap.ConversionIteratorWrapper;
import com.jcwhatever.nucleus.collections.wrap.IteratorWrapper;
import com.jcwhatever.nucleus.collections.wrap.SetWrapper;
import com.jcwhatever.nucleus.collections.wrap.SyncStrategy;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;
import com.jcwhatever.nucleus.utils.scheduler.ScheduledTask;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * An encapsulated {@link HashMap} where each key value has an individual lifespan that
 * when ended, causes the item to be removed.
 *
 * <p>The lifespan can be reset by re-adding an item.</p>
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
 * <p>Thread Safe.</p>
 *
 * <p>The maps iterators must be used inside a synchronized block which locks the
 * map instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class TimedHashMap<K, V> implements Map<K, V>, IPluginOwned {

    // The minimum interval the cleanup is allowed to run at.
    // Used to prevent cleanup from being run too often.
    private static final int MIN_CLEANUP_INTERVAL_MS = 50;

    // The interval the janitor runs at
    private static final int JANITOR_INTERVAL_TICKS = 10;

    // random initial delay interval for janitor, random to help spread out
    // task execution in relation to other scheduled tasks
    private static final int JANITOR_INITIAL_DELAY_TICKS = Rand.getInt(1, 5);

    private final static Map<TimedHashMap, Void> _instances = new WeakHashMap<>(10);
    private static ScheduledTask _janitor;

    private final Plugin _plugin;
    private final Map<K, DateEntry<K, V>> _map;

    private final int _lifespan; // milliseconds
    private final TimeScale _timeScale;

    private final transient ValuesWrapper _valuesWrapper;
    private final transient KeySetWrapper _keySetWrapper;
    private final transient EntrySetWrapper _entrySetWrapper;

    private final transient Object _sync;
    private final transient SyncStrategy _strategy;
    private transient long _nextCleanup;

    private final transient NamedUpdateAgents _agents = new NamedUpdateAgents();

    /**
     * Constructor. Default lifespan is 20 ticks.
     */
    public TimedHashMap(Plugin plugin) {
        this(plugin, 10, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Default lifespan is 20 ticks.
     *
     * @param size  The initial capacity of the map.
     */
    public TimedHashMap(Plugin plugin, int size) {
        this(plugin, size, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Time scale is in ticks.
     *
     * @param size             The initial capacity of the map.
     * @param defaultLifespan  The default lifespan of items in ticks.
     */
    public TimedHashMap(Plugin plugin, int size, int defaultLifespan) {
        this(plugin, size, defaultLifespan, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param size             The initial capacity of the map.
     * @param defaultLifespan  The default lifespan of items in ticks.
     * @param timeScale        The lifespan time scale.
     */
    public TimedHashMap(Plugin plugin, int size, int defaultLifespan, TimeScale timeScale) {
        PreCon.notNull(plugin);
        PreCon.positiveNumber(defaultLifespan);
        PreCon.notNull(timeScale);

        _plugin = plugin;
        _sync = this;
        _strategy = new SyncStrategy(this);
        _lifespan = defaultLifespan * timeScale.getTimeFactor();
        _timeScale = timeScale;
        _map = new HashMap<>(size);
        _valuesWrapper = new ValuesWrapper();
        _keySetWrapper = new KeySetWrapper();
        _entrySetWrapper = new EntrySetWrapper();

        synchronized (_instances) {
            _instances.put(this, null);
        }

        startJanitor();
    }

    /**
     * Put an item into the map using the specified lifespan in
     * the time scale specified in the constructor.
     *
     * @param key        The item key.
     * @param value      The item to add.
     * @param lifespan   The items lifespan.
     */
    public V put(final K key, final V value, int lifespan) {
        return put(key, value, lifespan, _timeScale);
    }

    /**
     * Put an item into the map using the specified lifespan in
     * the time scale specified.
     *
     * @param key        The item key.
     * @param value      The item to add.
     * @param lifespan   The items lifespan.
     * @param timeScale  The time scale of the specified lifespan.
     */
    public V put(final K key, final V value, int lifespan, TimeScale timeScale) {
        PreCon.notNull(key);
        PreCon.notNull(value);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        DateEntry<K, V> previous;

        synchronized (_sync) {
            previous = _map.put(key, new DateEntry<K, V>(key, value, lifespan, timeScale));
        }

        if (previous == null)
            return null;

        return previous.value;
    }

    /**
     * Put a map of items into the map using the specified lifespan in
     * the time scale specified in the constructor.
     *
     * @param entries    The map to add.
     * @param lifespan   The lifespan of the added items.
     */
    public void putAll(Map<? extends K, ? extends V> entries, int lifespan) {
        putAll(entries, lifespan, _timeScale);
    }

    /**
     * Put a map of items into the map using the specified lifespan in
     * the time scale specified..
     *
     * @param entries    The map to add.
     * @param lifespan   The lifespan of the added items.
     * @param timeScale  The timeScale of the specified lifespan.
     */
    public void putAll(Map<? extends K, ? extends V> entries, int lifespan, TimeScale timeScale) {
        PreCon.notNull(entries);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        synchronized (_sync) {

            for (Map.Entry<? extends K, ? extends V> entry : entries.entrySet()) {
                _map.put(entry.getKey(), new DateEntry<K, V>(
                        entry.getKey(), entry.getValue(), lifespan, timeScale));
            }
        }
    }

    /**
     * Register a subscriber to be notified whenever an entry is removed
     * due to its lifespan ending.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    public TimedHashMap<K, V> onLifespanEnd(IUpdateSubscriber<Entry<K, V>> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onLifespanEnd").register(subscriber);

        return this;
    }

    /**
     * Register a subscriber to be notified whenever the map becomes
     * empty due to an entries lifespan ending.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    public TimedHashMap<K, V> onEmpty(IUpdateSubscriber<TimedHashMap<K, V>> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onEmpty").register(subscriber);

        return this;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public void clear() {
        synchronized (_sync) {
            _map.clear();
        }
    }

    @Override
    public Set<K> keySet() {
        return _keySetWrapper;
    }

    @Override
    public Collection<V> values() {
        return _valuesWrapper;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return _entrySetWrapper;
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

            //noinspection SuspiciousMethodCalls
            DateEntry<K, V> entry = _map.get(key);
            if (entry == null)
                return false;

            if (entry.isExpired()) {
                //noinspection SuspiciousMethodCalls
                _map.remove(key);
                onLifespanEnd(getEntry(entry.key, entry.value));
                return false;
            }

            return true;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        PreCon.notNull(value);

        synchronized (_sync) {
            cleanup();
            return _map.containsValue(value);
        }
    }

    @Override
    @Nullable
    public V get(Object key) {
        PreCon.notNull(key);

        synchronized (_sync) {
            DateEntry<K, V> entry = _map.get(key);
            if (entry == null)
                return null;

            if (entry.isExpired()) {
                //noinspection SuspiciousMethodCalls
                _map.remove(key);
                onLifespanEnd(getEntry(entry.key, entry.value));
            }
            else {
                return entry.value;
            }
        }

        return null;
    }

    @Override
    @Nullable
    public V put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        return put(key, value, _lifespan, TimeScale.MILLISECONDS);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> entries) {
        PreCon.notNull(entries);

        putAll(entries, _lifespan, TimeScale.MILLISECONDS);
    }

    @Override
    @Nullable
    public V remove(Object key) {
        PreCon.notNull(key);

        synchronized (_sync) {

            DateEntry<K, V> value = _map.remove(key);
            if (value == null)
                return null;

            return value.value;
        }
    }

    private void onLifespanEnd(Entry<K, V> value) {
        if (!_agents.hasAgent("onLifespanEnd"))
            return;

        _agents.getAgent("onLifespanEnd").update(value);

        if (!_map.isEmpty() || !_agents.hasAgent("onEmpty"))
            return;

        _agents.getAgent("onEmpty").update(this);
    }

    private void cleanup() {

        if (_map.isEmpty())
            return;

        // prevent cleanup from running too often
        if (_nextCleanup > System.currentTimeMillis())
            return;

        _nextCleanup = System.currentTimeMillis() + MIN_CLEANUP_INTERVAL_MS;

        Iterator<Entry<K, DateEntry<K, V>>> iterator = _map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<K, DateEntry<K, V>> entry = iterator.next();

            if (entry.getValue().isExpired()) {
                iterator.remove();
                onLifespanEnd(getEntry(entry.getKey(), entry.getValue().value));
            }
        }
    }

    private void startJanitor() {
        if (_janitor != null)
            return;

        _janitor = Scheduler.runTaskRepeatAsync(
                Nucleus.getPlugin(), JANITOR_INITIAL_DELAY_TICKS, JANITOR_INTERVAL_TICKS, new Runnable() {
                    @Override
                    public void run() {

                        List<TimedHashMap> maps;

                        synchronized (_instances) {
                            maps = new ArrayList<TimedHashMap>(_instances.keySet());
                        }

                        for (TimedHashMap map : maps) {

                            // remove from instances if owning plugin is disabled
                            if (!map._plugin.isEnabled()) {
                                synchronized (_instances) {
                                    _instances.remove(map);
                                }
                                continue;
                            }

                            // run cleanup
                            synchronized (map._sync) {
                                map.cleanup();
                            }
                        }
                    }
                });
    }

    private Entry<K, V> getEntry(final K k, final V v) {
        return new Entry<K, V>() {

            V value = v;

            @Override
            public K getKey() {
                return k;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                V prev = this.value;
                this.value = prev;
                return prev;
            }
        };
    }

    private Entry<K, V> getEntry(final Entry<K, DateEntry<K, V>> entry) {

        return new ConversionEntryWrapper<K, V, DateEntry<K, V>>(_strategy) {

            @Override
            protected Entry<K, DateEntry<K, V>> entry() {
                return entry;
            }

            @Override
            protected V convert(K key, DateEntry<K, V> internal) {
                return internal.value;
            }

            @Override
            protected DateEntry<K, V> unconvert(K key, V external) {
                return new DateEntry<K, V>(key, external, _lifespan, TimeScale.MILLISECONDS);
            }
        };
    }

    private static final class DateEntry<K, V> {

        final K key;
        final V value;
        final long expires;
        final Object match;

        DateEntry(K key,V value, long lifespan, TimeScale timeScale) {
            this.key = key;
            this.value = value;
            this.expires = System.currentTimeMillis() + (lifespan * timeScale.getTimeFactor());
            this.match = value;
        }

        DateEntry(Object match) {
            this.key = null;
            this.match = match;
            this.expires = 0;
            this.value = null;
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= expires;
        }

        @Override
        public int hashCode() {
            return match != null ? match.hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == null
                    ? match == null
                    : match != null && (obj instanceof DateEntry
                    ? ((DateEntry) obj).match.equals(match)
                    : obj.equals(match));
        }
    }

    private final class KeySetWrapper extends SetWrapper<K> {

        KeySetWrapper() {
            super(TimedHashMap.this._strategy);
        }

        @Override
        protected boolean onPreAdd(K e) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Set<K> set() {
            return _map.keySet();
        }
    }

    private final class ValuesWrapper implements Collection<V> {

        @Override
        public int size() {
            return TimedHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return TimedHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            synchronized (_sync) {
                return _map.values().contains(new DateEntry(o));
            }
        }

        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {

                Iterator<DateEntry<K, V>> iterator = _map.values().iterator();

                @Override
                public boolean hasNext() {

                    IteratorWrapper.assertIteratorLock(_sync);
                    return iterator.hasNext();
                }

                @Override
                public V next() {

                    IteratorWrapper.assertIteratorLock(_sync);
                    return iterator.next().value;
                }

                @Override
                public void remove() {

                    IteratorWrapper.assertIteratorLock(_sync);
                    iterator.remove();

                }
            };
        }

        @Override
        public Object[] toArray() {
            Object[] array;

            synchronized (_sync) {
                array = _map.values().toArray();
            }

            Object[] output = new Object[array.length];

            for (int i=0; i < array.length; i++) {

                @SuppressWarnings("unchecked")
                DateEntry entry = ((DateEntry<K, V>)array[i]);

                output[i] = entry.value;
            }

            return output;
        }

        @Override
        public <T> T[] toArray(T[] a) {

            Object[] array;

            synchronized (_sync) {
                array = _map.values().toArray();
            }

            for (int i=0; i < array.length; i++) {

                @SuppressWarnings("unchecked")
                DateEntry entry = ((DateEntry<K, V>)array[i]);

                @SuppressWarnings("unchecked")
                T value = (T)entry.value;

                a[i] = value;
            }

            return a;
        }

        @Override
        public boolean add(V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {

            synchronized (_sync) {
                for (Object obj : c) {
                    if (!_map.values().contains(new DateEntry(obj)))
                        return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {

            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    private final class EntrySetWrapper implements Set<Entry<K, V>> {

        @Override
        public int size() {
            return TimedHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return TimedHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return TimedHashMap.this.containsKey(o);
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new ConversionIteratorWrapper<Entry<K, V>, Entry<K, DateEntry<K, V>>>(_strategy) {

                Iterator<Entry<K, DateEntry<K, V>>> iterator = _map.entrySet().iterator();

                @Override
                protected Entry<K, V> convert(Entry<K, DateEntry<K, V>> internal) {
                    return getEntry(internal);
                }

                @Override
                protected Iterator<Entry<K, DateEntry<K, V>>> iterator() {
                    return iterator;
                }
            };
        }

        @Override
        public Object[] toArray() {

            Object[] entries;

            synchronized (_sync) {
                entries =_map.entrySet().toArray();
            }

            Object[] array = new Object[entries.length];

            for (int i=0; i < entries.length; i++) {

                @SuppressWarnings("unchecked")
                Entry<K, DateEntry<K, V>> entry = (Entry<K, DateEntry<K, V>>)entries[i];

                array[i] = getEntry(entry);
            }

            return array;
        }

        @Override
        public <T> T[] toArray(T[] a) {

            Object[] entries;

            synchronized (_sync) {
                entries =_map.entrySet().toArray();
            }

            for (int i=0; i < entries.length; i++) {

                @SuppressWarnings("unchecked")
                Entry<K, DateEntry<K, V>> entry = (Entry<K, DateEntry<K, V>>)entries[i];

                @SuppressWarnings("unchecked")
                T result = (T)getEntry(entry);

                a[i] = result;
            }

            return a;
        }

        @Override
        public boolean add(Entry<K, V> kvEntry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {

            synchronized (_sync) {
                for (Object obj : c) {
                    if (obj instanceof Entry) {

                        Entry entry = (Entry) obj;

                        //noinspection SuspiciousMethodCalls
                        DateEntry<K, V> value = _map.get(entry.getKey());
                        if (value == null)
                            return false;

                        if (!value.equals(entry.getValue()))
                            return false;

                        if (value.isExpired()) {
                            //noinspection SuspiciousMethodCalls
                            _map.remove(entry.getKey());
                            onLifespanEnd(getEntry(value.key, value.value));
                            return false;
                        }

                    } else {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }
}
