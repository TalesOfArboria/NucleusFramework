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


package com.jcwhatever.bukkit.generic.player.collections;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@code HashSet} of {@code Player} objects.
 *
 * <p>
 *     {@code Player} object is automatically removed when the player logs out.
 * </p>
 */
public class PlayerSet extends AbstractPlayerCollection implements Set<Player> {

    private final Set<Player> _players;

    /**
     * Constructor.
     */
    public PlayerSet(Plugin plugin) {
        this(plugin, 10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public PlayerSet(Plugin plugin, int size) {
        super(plugin);
        _players = new HashSet<Player>(size);
    }

    @Override
    public synchronized boolean add(Player p) {
        if (_players.add(p)) {
            notifyPlayerAdded(p.getUniqueId());
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean addAll(Collection<? extends Player> c) {

        for (Player p : c) {
            notifyPlayerAdded(p.getUniqueId());
        }

        return _players.addAll(c);
    }

    @Override
    public synchronized void clear() {

        for (Player p : _players) {
            notifyPlayerRemoved(p.getUniqueId());
        }

        _players.clear();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return _players.contains(o);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        return _players.containsAll(c);
    }

    @Override
    public synchronized boolean isEmpty() {
        return _players.isEmpty();
    }

    @Override
    public synchronized Iterator<Player> iterator() {
        return new PlayerIterator();
    }

    @Override
    public synchronized boolean remove(Object o) {
        if (_players.remove(o)) {
            if (o instanceof Player) {
                notifyPlayerRemoved(((Player)o).getUniqueId());
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {

        for (Object obj : c) {
            if (obj instanceof Player) {
                notifyPlayerRemoved(((Player)obj).getUniqueId());
            }
        }

        return _players.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {

        Set<Player> temp = new HashSet<>(_players);
        //noinspection SuspiciousMethodCalls
        temp.removeAll(c);

        for (Player p : temp) {
            notifyPlayerRemoved(p.getUniqueId());
        }

        return _players.retainAll(c);
    }

    @Override
    public synchronized int size() {
        return _players.size();
    }

    @Override
    public synchronized Object[] toArray() {
        return _players.toArray();
    }

    @Override
    public synchronized <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return _players.toArray(a);
    }

    @Override
    public synchronized void removePlayer(Player p) {
        remove(p);
    }

    /**
     * Call to remove references that prevent
     * the garbage collector from collecting
     * the instance after it is no longer needed.
     */
    @Override
    public void dispose() {
        clear();
    }

    private final class PlayerIterator implements Iterator<Player> {

        private final Iterator<Player> _iterator;
        private Player _current;

        public PlayerIterator() {
            _iterator = new ArrayList<Player>(_players).iterator();
        }

        @Override
        public boolean hasNext() {
            return _iterator.hasNext();
        }

        @Override
        public Player next() {
            _current = _iterator.next();
            return _current;
        }

        @Override
        public void remove() {
            _iterator.remove();
            notifyPlayerRemoved(_current.getUniqueId());
        }
    }
}
