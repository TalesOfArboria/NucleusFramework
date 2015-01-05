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

import com.jcwhatever.nucleus.collections.players.PlayerElement.PlayerElementMatcher;
import com.jcwhatever.nucleus.collections.wrappers.AbstractConversionIterator;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
public class PlayerSet implements IPlayerCollection, Set<Player> {

    private final Plugin _plugin;
    private final Set<PlayerElement> _players;
    private final PlayerCollectionTracker _tracker;
    private final Object _sync = new Object();

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
        PreCon.notNull(plugin);

        _plugin = plugin;
        _players = new HashSet<>(size);
        _tracker = new PlayerCollectionTracker(this);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public boolean add(Player p) {
        PreCon.notNull(p);

        synchronized (_sync) {

            if (_players.add(new PlayerElement(p))) {
                _tracker.notifyPlayerAdded(p.getUniqueId());
                return true;
            }

            return false;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Player> collection) {
        PreCon.notNull(collection);

        synchronized (_sync) {
            boolean isChanged = false;

            for (Player p : collection) {
                if (_players.add(new PlayerElement(p))) {
                    _tracker.notifyPlayerAdded(p.getUniqueId());
                    isChanged = true;
                }
            }

            return isChanged;
        }
    }

    @Override
    public void clear() {

        synchronized (_sync) {
            for (PlayerElement p : _players) {
                _tracker.notifyPlayerRemoved(p.getUniqueId());
            }

            _players.clear();
        }
    }

    @Override
    public boolean contains(Object o) {
        synchronized (_sync) {
            return _players.contains(new PlayerElementMatcher(o));
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        PreCon.notNull(collection);

        synchronized (_sync) {
            if (collection.isEmpty())
                return false;

            for (Object obj : collection) {
                if (!contains(obj))
                    return false;
            }

            return true;
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            return _players.isEmpty();
        }
    }

    @Override
    public Iterator<Player> iterator() {
        return new PlayerIterator();
    }

    @Override
    public boolean remove(Object o) {
        PreCon.notNull(o);

        synchronized (_sync) {

            if (_players.remove(new PlayerElementMatcher(o))) {
                _tracker.notifyPlayerRemoved(((Player) o).getUniqueId());
                return true;
            }

            return false;
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        synchronized (_sync) {

            boolean isChanged = false;

            for (Object obj : collection) {
                isChanged = isChanged | remove(obj);
            }

            return isChanged;
        }
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        PreCon.notNull(collection);

        boolean isChanged = false;

        synchronized (_sync) {

            Iterator<PlayerElement> iterator = _players.iterator();
            while (iterator.hasNext()) {
                PlayerElement entry = iterator.next();

                boolean keep = false;
                for (Object obj : collection) {
                    if (obj.equals(entry.getPlayer())) {
                        keep = true;
                        break;
                    }
                }

                if (!keep) {
                    iterator.remove();
                    isChanged = true;
                }
            }
        }

        return isChanged;
    }

    @Override
    public int size() {
        synchronized (_sync) {
            return _players.size();
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (_sync) {
            return _players.toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        synchronized (_sync) {
            //noinspection SuspiciousToArrayCall
            return _players.toArray(a);
        }
    }

    @Override
    public void removePlayer(Player p) {
        synchronized (_sync) {
            _players.remove(new PlayerElement(p));
        }
    }

    private final class PlayerIterator extends AbstractConversionIterator<Player, PlayerElement> {

        Iterator<PlayerElement> iterator = _players.iterator();

        @Override
        public void remove() {
            synchronized (_sync) {
                _tracker.notifyPlayerRemoved(_current.getUniqueId());
                iterator.remove();
            }
        }

        @Override
        protected Player getElement(PlayerElement trueElement) {
            return trueElement.getPlayer();
        }

        @Override
        protected Iterator<PlayerElement> getIterator() {
            return iterator;
        }
    }
}
