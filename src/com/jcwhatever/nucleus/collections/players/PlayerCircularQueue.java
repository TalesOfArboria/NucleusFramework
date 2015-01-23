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

import com.jcwhatever.nucleus.collections.CircularQueue;
import com.jcwhatever.nucleus.collections.players.PlayerElement.PlayerElementMatcher;
import com.jcwhatever.nucleus.collections.wrap.ConversionIteratorWrapper;
import com.jcwhatever.nucleus.collections.wrap.SyncStrategy;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A {@code CircularQueue} for {@code Player} objects that automatically removes
 * players when they log out.
 *
 * <p>Thread safe.</p>
 *
 * <p>The queues iterators must be used inside a synchronized block which locks the
 * queue instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class PlayerCircularQueue implements IPlayerCollection, Deque<Player> {

    private final Plugin _plugin;
    private final CircularQueue<PlayerElement> _queue = new CircularQueue<>();
    private final PlayerCollectionTracker _tracker;
    private final Object _sync;
    private final SyncStrategy _strategy;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public PlayerCircularQueue(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
        _sync = this;
        _strategy = new SyncStrategy(this);
        _tracker = new PlayerCollectionTracker(this);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public void addFirst(Player player) {
        PreCon.notNull(player);

        synchronized (_sync) {
            PlayerElement element = new PlayerElement(player);

            if (!_queue.contains(element)) {
                _tracker.notifyPlayerAdded(player.getUniqueId());
            }

            _queue.addFirst(element);
        }
    }

    @Override
    public void addLast(Player player) {
        PreCon.notNull(player);

        PlayerElement element = new PlayerElement(player);

        synchronized (_sync) {

            if (!_queue.contains(element)) {
                _tracker.notifyPlayerAdded(player.getUniqueId());
            }

            _queue.addLast(element);
        }
    }

    @Override
    public boolean offerFirst(Player player) {
        PreCon.notNull(player);

        PlayerElement element = new PlayerElement(player);

        synchronized (_sync) {

            boolean has = _queue.contains(element);

            if (_queue.offerFirst(element)) {

                if (!has)
                    _tracker.notifyPlayerAdded(player.getUniqueId());

                return true;
            }

            return false;
        }
    }

    @Override
    public boolean offerLast(Player player) {
        PreCon.notNull(player);

        PlayerElement element = new PlayerElement(player);

        synchronized (_sync) {

            boolean has = _queue.contains(element);

            if (_queue.offerLast(element)) {

                if (!has)
                    _tracker.notifyPlayerAdded(player.getUniqueId());

                return true;
            }

            return false;
        }
    }

    @Override
    public Player removeFirst() {
        synchronized (_sync) {
            PlayerElement element = _queue.removeFirst();
            Player result = element != null ? element.getPlayer() : null;

            if (result != null && !_queue.contains(element)) {
                _tracker.notifyPlayerRemoved(result.getUniqueId());
            }

            return result;
        }
    }

    @Override
    public Player removeLast() {
        synchronized (_sync) {
            PlayerElement element = _queue.removeLast();
            Player result = element != null ? element.getPlayer() : null;

            if (result != null && !_queue.contains(element)) {
                _tracker.notifyPlayerRemoved(result.getUniqueId());
            }

            return result;
        }
    }

    @Override
    public Player pollFirst() {
        synchronized (_sync) {
            PlayerElement element = _queue.pollFirst();
            Player result = element != null ? element.getPlayer() : null;

            if (result != null && !_queue.contains(element)) {
                _tracker.notifyPlayerRemoved(result.getUniqueId());
            }

            return result;
        }
    }

    @Override
    public Player pollLast() {
        synchronized (_sync) {
            PlayerElement element = _queue.pollLast();
            Player result = element != null ? element.getPlayer() : null;

            if (result != null && !_queue.contains(element)) {
                _tracker.notifyPlayerRemoved(result.getUniqueId());
            }

            return result;
        }
    }

    @Override
    public Player getFirst() {
        synchronized (_sync) {
            PlayerElement entry = _queue.getFirst();
            return entry != null ? entry.getPlayer() : null;
        }
    }

    @Override
    public Player getLast() {
        synchronized (_sync) {
            PlayerElement entry = _queue.getLast();
            return entry != null ? entry.getPlayer() : null;
        }
    }

    @Override
    public Player peekFirst() {
        synchronized (_sync) {
            PlayerElement entry = _queue.peekFirst();
            return entry != null ? entry.getPlayer() : null;
        }
    }

    @Override
    public Player peekLast() {
        synchronized (_sync) {
            PlayerElement entry = _queue.peekLast();
            return entry != null ? entry.getPlayer() : null;
        }
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        synchronized (_sync) {
            PlayerElementMatcher matcher = new PlayerElementMatcher(o);
            if (_queue.removeFirstOccurrence(matcher)) {
                //noinspection SuspiciousMethodCalls
                if (matcher.getUniqueId() != null && !_queue.contains(matcher)) {
                    _tracker.notifyPlayerRemoved(matcher.getUniqueId());
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        synchronized (_sync) {
            PlayerElementMatcher matcher = new PlayerElementMatcher(o);
            if (_queue.removeLastOccurrence(matcher)) {
                //noinspection SuspiciousMethodCalls
                if (matcher.getUniqueId() != null && !_queue.contains(matcher)) {
                    _tracker.notifyPlayerRemoved(matcher.getUniqueId());
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean add(Player player) {
        PreCon.notNull(player);

        synchronized (_sync) {

            PlayerElement element = new PlayerElement(player);

            boolean has = _queue.contains(element);

            if (_queue.add(element)) {

                if (!has)
                    _tracker.notifyPlayerAdded(player.getUniqueId());

                return true;
            }
            return false;
        }
    }

    @Override
    public boolean offer(Player player) {
        PreCon.notNull(player);

        synchronized (_sync) {

            PlayerElement element = new PlayerElement(player);

            boolean has = _queue.contains(element);

            if (_queue.offer(element)) {

                if (!has)
                    _tracker.notifyPlayerAdded(player.getUniqueId());

                return true;
            }
            return false;
        }
    }

    @Override
    public Player remove() {
        synchronized (_sync) {
            PlayerElement entry = _queue.remove();
            Player result = entry != null ? entry.getPlayer() : null;

            if (result != null && !_queue.contains(entry))
                _tracker.notifyPlayerRemoved(entry.getUniqueId());

            return result;
        }
    }

    @Override
    public Player poll() {
        synchronized (_sync) {
            PlayerElement entry = _queue.poll();
            Player result = entry != null ? entry.getPlayer() : null;

            if (result != null && !_queue.contains(entry))
                _tracker.notifyPlayerRemoved(entry.getUniqueId());

            return result;
        }
    }

    @Override
    public Player element() {
        synchronized (_sync) {
            PlayerElement entry = _queue.element();
            return entry != null ? entry.getPlayer() : null;
        }
    }

    @Override
    public Player peek() {
        synchronized (_sync) {
            PlayerElement entry = _queue.peek();
            return entry != null ? entry.getPlayer() : null;
        }
    }

    @Override
    public void push(Player player) {
        PreCon.notNull(player);

        synchronized (_sync) {

            PlayerElement element = new PlayerElement(player);
            boolean has = _queue.contains(element);

            _queue.push(element);

            if (!has)
                _tracker.notifyPlayerAdded(player.getUniqueId());
        }
    }

    @Override
    public Player pop() {
        synchronized (_sync) {
            PlayerElement entry = _queue.pop();
            Player result = entry != null ? entry.getPlayer() : null;

            if (result != null && !_queue.contains(entry))
                _tracker.notifyPlayerRemoved(entry.getUniqueId());

            return result;
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized (_sync) {
            PlayerElementMatcher matcher = new PlayerElementMatcher(o);
            if (_queue.remove(matcher)) {

                //noinspection SuspiciousMethodCalls
                if (matcher.getUniqueId() != null && !_queue.contains(matcher))
                    _tracker.notifyPlayerRemoved(matcher.getUniqueId());

                return true;
            }
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        for (Object obj : c) {
            if (!contains(obj))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Player> c) {
        PreCon.notNull(c);

        boolean isChanged = false;
        synchronized (_sync) {
            for (Player player : c) {
                isChanged = add(player) || isChanged;
            }
        }
        return isChanged;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        PreCon.notNull(c);

        boolean isChanged = false;
        synchronized (_sync) {
            for (Object obj : c) {
                isChanged = remove(obj) || isChanged;
            }
        }
        return isChanged;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        PreCon.notNull(c);

        List<PlayerElement> removed = new ArrayList<>(_queue);
        List<PlayerElementMatcher> entries = new ArrayList<>(c.size());

        for (Object obj : c) {
            PlayerElementMatcher matcher = new PlayerElementMatcher(obj);
            entries.add(matcher);
            //noinspection SuspiciousMethodCalls
            removed.remove(matcher);
        }

        boolean result;

        synchronized (_sync) {
            result = _queue.retainAll(entries);

            for (PlayerElement rem : removed) {
                if (!_queue.contains(rem))
                    _tracker.notifyPlayerRemoved(rem.getUniqueId());
            }
        }

        return result;
    }

    @Override
    public void removePlayer(Player p) {
        synchronized (_sync) {
            //noinspection StatementWithEmptyBody
            while (_queue.remove(new PlayerElement(p)));
        }
    }

    @Override
    public void clear() {
        Set<PlayerElement> removed;
        synchronized (_sync) {
            removed = new HashSet<>(_queue);
            _queue.clear();
        }

        for (PlayerElement element : removed) {
            _tracker.notifyPlayerRemoved(element.getUniqueId());
        }
    }

    @Override
    public boolean contains(Object o) {
        synchronized (_sync) {
            return _queue.contains(new PlayerElementMatcher(o));
        }
    }

    @Override
    public int size() {
        return _queue.size();
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            return _queue.isEmpty();
        }
    }

    @Override
    public Iterator<Player> iterator() {
        return new ItrRight();
    }

    @Override
    public Object[] toArray() {
        Object[] elements;
        synchronized (_sync) {
            elements = _queue.toArray();
        }
        Object[] results = new Object[elements.length];
        for (int i=0; i < elements.length; i++) {
            results[i] = ((PlayerElement)elements[i]).getPlayer();
        }
        return results;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        PreCon.notNull(a);

        Object[] elements;
        synchronized (_sync) {
            elements = _queue.toArray();
        }
        for (int i=0; i < elements.length; i++) {

            @SuppressWarnings("unchecked")
            T player = (T)((PlayerElement)elements[i]).getPlayer();

            a[i] = player;
        }
        return a;
    }

    @Override
    public Iterator<Player> descendingIterator() {
        return new ItrLeft();
    }

    private final class ItrRight extends ConversionIteratorWrapper<Player, PlayerElement> {

        ItrRight() {
            super(PlayerCircularQueue.this._strategy);
        }

        Iterator<PlayerElement> iterator = _queue.iterator();

        @Override
        protected Player convert(PlayerElement internal) {
            return internal.getPlayer();
        }

        @Override
        protected Iterator<PlayerElement> iterator() {
            return iterator;
        }

        @Override
        public void remove() {
            synchronized (_sync) {
                PlayerElement removed = getCurrent();
                iterator.remove();
                if (!contains(removed)) {
                    _tracker.notifyPlayerRemoved(removed.getUniqueId());
                }
            }
        }
    }

    private final class ItrLeft extends ConversionIteratorWrapper<Player, PlayerElement> {

        ItrLeft() {
            super(PlayerCircularQueue.this._strategy);
        }

        Iterator<PlayerElement> iterator = _queue.descendingIterator();

        @Override
        protected Player convert(PlayerElement internal) {
            return internal.getPlayer();
        }

        @Override
        protected Iterator<PlayerElement> iterator() {
            return iterator;
        }

        @Override
        public void remove() {
            synchronized (_sync) {
                PlayerElement removed = getCurrent();
                iterator.remove();
                if (!contains(removed)) {
                    _tracker.notifyPlayerRemoved(removed.getUniqueId());
                }
            }
        }
    }
}
