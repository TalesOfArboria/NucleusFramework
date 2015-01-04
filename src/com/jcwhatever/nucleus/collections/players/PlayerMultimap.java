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

package com.jcwhatever.nucleus.collections.players;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.jcwhatever.nucleus.collections.wrappers.AbstractCollectionWrapper;
import com.jcwhatever.nucleus.collections.wrappers.AbstractIteratorWrapper;
import com.jcwhatever.nucleus.collections.wrappers.AbstractMapWrapper;
import com.jcwhatever.nucleus.collections.wrappers.AbstractSetWrapper;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

/**
 * A {@code Multimap} wrapper that stores elements using the player ID as key.
 *
 * <p> When the player logs out, the entry is automatically removed.</p>
 *
 *
 * @param <V>  The value type
 */
public abstract class PlayerMultimap<V> implements IPlayerCollection, Multimap<UUID, V> {

    private final Plugin _plugin;
    private final Multimap<UUID, V> _map;
    private final transient MapWrapper _mapWrapper = new MapWrapper();
    private final transient EntrySetWrapper _entrySet = new EntrySetWrapper();
    private final transient KeySetWrapper _keySet = new KeySetWrapper();
    private final transient PlayerCollectionTracker _tracker;

    private PlayerMultimap() {
        _plugin = null;
        _map = null;
        _tracker = new PlayerCollectionTracker(this);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param multimap  The player Multimap.
     */
    public PlayerMultimap(Plugin plugin, Multimap<UUID, V> multimap) {
        PreCon.notNull(plugin);
        PreCon.notNull(multimap);

        _plugin = plugin;
        _map = multimap;
        _tracker = new PlayerCollectionTracker(this);
    }

    @Override
    public Plugin getPlugin() {
        synchronized (getSync()) {
            return _plugin;
        }
    }

    @Override
    public int size() {
        synchronized (getSync()) {
            return _map.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (getSync()) {
            return _map.isEmpty();
        }
    }

    @Override
    public boolean containsKey(Object o) {
        synchronized (getSync()) {
            return _map.containsKey(o);
        }
    }

    @Override
    public boolean containsValue(Object o) {
        synchronized (getSync()) {
            return _map.containsValue(o);
        }
    }

    @Override
    public boolean containsEntry(Object o, Object o1) {
        synchronized (getSync()) {
            return _map.containsEntry(o, o1);
        }
    }

    @Override
    public boolean put(UUID playerId, V v) {
        synchronized (getSync()) {
            if (!_map.containsKey(playerId))
                _tracker.notifyPlayerAdded(playerId);

            return _map.put(playerId, v);
        }
    }

    @Override
    public boolean remove(Object o, Object o1) {
        synchronized (getSync()) {
            return _map.remove(o, o1);
        }
    }

    @Override
    public boolean putAll(UUID playerId, Iterable<? extends V> iterable) {
        synchronized (getSync()) {
            if (!_map.containsKey(playerId)) {
                _tracker.notifyPlayerAdded(playerId);
            }

            return _map.putAll(playerId, iterable);
        }
    }

    @Override
    public boolean putAll(Multimap<? extends UUID, ? extends V> multimap) {

        synchronized (getSync()) {
            Set<? extends UUID> keys = multimap.keySet();

            for (UUID id : keys) {
                if (!_map.containsKey(id)) {
                    _tracker.notifyPlayerAdded(id);
                }
            }

            return _map.putAll(multimap);
        }
    }

    @Override
    public Collection<V> replaceValues(UUID k, Iterable<? extends V> iterable) {
        PreCon.notNull(k);

        synchronized (getSync()) {
            return _map.replaceValues(k, iterable);
        }
    }

    @Override
    public Collection<V> removeAll(Object o) {
        PreCon.notNull(o);

        synchronized (getSync()) {

            Set<? extends UUID> keys = _map.keySet();

            for (UUID id : keys) {
                if (o.equals(id)) {
                    _tracker.notifyPlayerRemoved(id);
                    break;
                }
            }
            return _map.removeAll(o);
        }
    }

    @Override
    public void clear() {

        synchronized (getSync()) {

            Set<? extends UUID> keys = _map.keySet();

            for (UUID id : keys) {
                _tracker.notifyPlayerRemoved(id);
            }

            _map.clear();
        }
    }

    @Override
    public Collection<V> get(UUID k) {
        synchronized (getSync()) {
            return _map.get(k);
        }
    }

    @Override
    public Set<UUID> keySet() {
        return _keySet;

    }

    @Override
    public Multiset<UUID> keys() {
        return _map.keys(); // TODO wrap
    }

    @Override
    public Collection<V> values() {
        synchronized (getSync()) {
            return _map.values();
        }
    }

    @Override
    public Collection<Entry<UUID, V>> entries() {
        return _entrySet;
    }

    @Override
    public Map<UUID, Collection<V>> asMap() {
        return _mapWrapper;
    }

    @Override
    public void removePlayer(Player p) {
        synchronized (getSync()) {
            _map.removeAll(p.getUniqueId());
        }
    }

    private class KeySetWrapper extends AbstractSetWrapper<UUID> {

        @Override
        public Iterator<UUID> iterator() {
            return new KeySetIteratorWrapper();
        }

        @Override
        public boolean add(UUID key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key) {
            return !PlayerMultimap.this.removeAll(key).isEmpty();
        }

        @Override
        public boolean addAll(Collection<? extends UUID> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean isChanged = false;
            for (Object obj : c) {
                isChanged = remove(obj) || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Set<UUID> removed = new HashSet<>(_map.keySet());

            for (Object obj : c) {
                //noinspection SuspiciousMethodCalls
                removed.remove(obj);
            }

            for (UUID key : removed) {
                remove(key);
            }

            return removed.size() != _map.keySet().size();
        }

        @Override
        protected Set<UUID> getSet() {
            return _map.keySet();
        }
    }

    private class KeySetIteratorWrapper extends AbstractIteratorWrapper<UUID> {

        Iterator<UUID> iterator = _map.keySet().iterator();

        @Override
        public void remove() {
            PlayerMultimap.this.removeAll(_current);
        }

        @Override
        protected Iterator<UUID> getIterator() {
            return iterator;
        }
    }

    private class EntrySetWrapper extends AbstractCollectionWrapper<Entry<UUID, V>> {

        @Override
        public Iterator<Entry<UUID, V>> iterator() {
            return new EntrySetIteratorWrapper();
        }

        @Override
        public boolean add(Entry<UUID, V> e) {
            return PlayerMultimap.this.put(e.getKey(), e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> entry = (Entry<?, ?>)o;
                return PlayerMultimap.this.remove(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Entry<UUID, V>> c) {
            boolean isChanged = false;
            for (Entry<UUID, V> entry : c) {
                isChanged = PlayerMultimap.this.put(entry.getKey(), entry.getValue()) || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean isChanged = false;
            for (Object obj : c) {
                isChanged = remove(obj) || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Set<Entry<UUID, V>> entries = new HashSet<>(_map.entries());

            for (Object obj : c) {
                //noinspection SuspiciousMethodCalls
                entries.remove(obj);
            }

            for (Entry<UUID, V> entry : entries) {
                PlayerMultimap.this.remove(entry.getKey(), entry.getValue());
            }

            return entries.size() != _map.entries().size();
        }

        @Override
        public void clear() {
            PlayerMultimap.this.clear();
        }

        @Override
        protected Collection<Entry<UUID, V>> getCollection() {
            return _map.entries();
        }
    }

    private class EntrySetIteratorWrapper extends AbstractIteratorWrapper<Entry<UUID, V>> {

        Iterator<Entry<UUID, V>> iterator = _map.entries().iterator();

        @Override
        public void remove() {
            PlayerMultimap.this.remove(_current.getKey(), _current.getValue());
        }

        @Override
        protected Iterator<Entry<UUID, V>> getIterator() {
            return iterator;
        }
    }

    private class MapWrapper extends AbstractMapWrapper<UUID, Collection<V>> {

        final MapKeySetWrapper keySet = new MapKeySetWrapper();
        final MapValuesWrapper values = new MapValuesWrapper();
        final MapEntrySetWrapper entrySet = new MapEntrySetWrapper();

        @Override
        public Collection<V> put(UUID key, Collection<V> values) {

            if (!_map.containsKey(key))
                _tracker.notifyPlayerAdded(key);

            Collection<V> current = _map.get(key);
            List<V> result = new ArrayList<V>(values.size());
            for (V value : values) {
                if (current.contains(value))
                    result.add(value);

                PlayerMultimap.this.put(key, value);
            }

            return result;
        }

        @Override
        public Collection<V> remove(Object key) {
            return PlayerMultimap.this.removeAll(key);
        }

        @Override
        public void putAll(Map<? extends UUID, ? extends Collection<V>> m) {
            for (Entry<? extends UUID, ? extends Collection<V>> entry : m.entrySet()) {
                PlayerMultimap.this.putAll(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            PlayerMultimap.this.clear();
        }

        @Override
        public Set<UUID> keySet() {
            return keySet;
        }

        @Override
        public Collection<Collection<V>> values() {
            return values;
        }

        @Override
        public Set<Entry<UUID, Collection<V>>> entrySet() {
            return entrySet;
        }

        @Override
        protected Map<UUID, Collection<V>> getMap() {
            return _map.asMap();
        }
    }

    private class MapKeySetWrapper extends AbstractSetWrapper<UUID> {

        @Override
        public Iterator<UUID> iterator() {
            return new MapKeySetIteratorWrapper();
        }

        @Override
        public boolean add(UUID key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key) {
            return !PlayerMultimap.this.removeAll(key).isEmpty();
        }

        @Override
        public boolean addAll(Collection<? extends UUID> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean isChanged = false;
            for (Object key : c) {
                isChanged = !PlayerMultimap.this.removeAll(key).isEmpty() || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {

            Set<UUID> removed = new HashSet<>(_map.keySet());

            for (Object key : c) {
                //noinspection SuspiciousMethodCalls
                removed.remove(key);
            }

            for (UUID key : removed) {
                PlayerMultimap.this.removeAll(key);
            }

            return removed.size() != _map.keySet().size();
        }

        @Override
        public void clear() {
            PlayerMultimap.this.clear();
        }

        @Override
        protected Set<UUID> getSet() {
            return _map.asMap().keySet();
        }
    }

    private class MapKeySetIteratorWrapper extends AbstractIteratorWrapper<UUID> {

        Iterator<UUID> iterator = _map.keySet().iterator();

        @Override
        public void remove() {
            PlayerMultimap.this.removeAll(_current);
        }

        @Override
        protected Iterator<UUID> getIterator() {
            return iterator;
        }
    }

    private class MapValuesWrapper extends AbstractCollectionWrapper<Collection<V>> {

        @Override
        public Iterator<Collection<V>> iterator() {
            return new MapValuesIteratorWrapper();
        }

        @Override
        public boolean add(Collection<V> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Collection<V>> c) {
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
            PlayerMultimap.this.clear();
        }

        @Override
        protected Collection<Collection<V>> getCollection() {
            return _map.asMap().values();
        }
    }

    private class MapValuesIteratorWrapper extends AbstractIteratorWrapper<Collection<V>> {

        Iterator<Collection<V>> iterator = _map.asMap().values().iterator();

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Iterator<Collection<V>> getIterator() {
            return iterator;
        }
    }

    private class MapEntrySetWrapper extends AbstractSetWrapper<Entry<UUID, Collection<V>>> {

        @Override
        public Iterator<Entry<UUID, Collection<V>>> iterator() {
            return getCollection().iterator();
        }

        @Override
        public boolean add(Entry<UUID, Collection<V>> e) {
            return PlayerMultimap.this.putAll(e.getKey(), e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            Set<Entry<UUID, Collection<V>>> entrySet = _map.asMap().entrySet();
            //noinspection SuspiciousMethodCalls
            if (!entrySet.contains(o))
                return false;

            for (Entry<UUID, Collection<V>> entry : entrySet) {
                if (entry.equals(o)) {
                    return !PlayerMultimap.this.removeAll(entry.getKey()).isEmpty();
                }
            }

            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Entry<UUID, Collection<V>>> c) {
            boolean isChanged = false;
            for (Entry<UUID, Collection<V>> entry : c) {
                isChanged = PlayerMultimap.this.putAll(entry.getKey(), entry.getValue()) || isChanged;
            }
            return isChanged;
        }

        @Override
        public boolean removeAll(Collection<?> c) {

            Set<Entry<UUID, Collection<V>>> entrySet = _map.asMap().entrySet();
            //noinspection SuspiciousMethodCalls

            boolean isChanged = false;

            for (Object obj : c) {
                //noinspection SuspiciousMethodCalls
                if (!entrySet.contains(obj))
                    continue;

                Iterator<Entry<UUID, Collection<V>>> iterator = _map.asMap().entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<UUID, Collection<V>> entry = iterator.next();

                    if (entry.equals(obj)) {
                        isChanged = !PlayerMultimap.this.removeAll(entry.getKey()).isEmpty() || isChanged;
                        break;
                    }

                }
            }
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {

            Set<Entry<UUID, Collection<V>>> removed = new HashSet<>(_map.asMap().entrySet());

            for (Object obj : c) {
                //noinspection SuspiciousMethodCalls
                removed.remove(obj);
            }

            removeAll(removed);

            return removed.size() != _map.asMap().entrySet().size();
        }

        @Override
        public void clear() {
            PlayerMultimap.this.clear();
        }

        @Override
        protected Set<Entry<UUID, Collection<V>>> getSet() {
            return _map.asMap().entrySet();
        }
    }
}
