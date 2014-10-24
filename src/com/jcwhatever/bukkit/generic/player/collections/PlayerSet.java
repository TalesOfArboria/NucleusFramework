package com.jcwhatever.bukkit.generic.player.collections;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PlayerSet implements Set<Player>, IPlayerCollection {

    private final Set<Player> _players;
    private final PlayerCollectionListener _listener;

    public PlayerSet() {
        _players = new HashSet<Player>(10);
        _listener = PlayerCollectionListener.get();
    }

    public PlayerSet(int size) {
        _players = new HashSet<Player>(size);
        _listener = PlayerCollectionListener.get();
    }

    @Override
    public synchronized boolean add(Player p) {
        if (_players.add(p)) {
            _listener.addCollection(p, this);
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean addAll(Collection<? extends Player> c) {

        for (Player p : c) {
            _listener.addCollection(p, this);
        }

        return _players.addAll(c);
    }

    @Override
    public synchronized void clear() {

        for (Player p : _players) {
            _listener.removeCollection(p, this);
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
        return new Iter(this);
    }

    @Override
    public synchronized boolean remove(Object o) {
        if (_players.remove(o)) {
            if (o instanceof Player) {
                _listener.removeCollection((Player) o, this);
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {

        for (Object obj : c) {
            if (obj instanceof Player) {
                _listener.removeCollection((Player) obj, this);
            }
        }

        return _players.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {

        Set<Player> temp = new HashSet<>(_players);
        temp.removeAll(c);

        for (Player p : temp) {
            _listener.removeCollection(p, this);
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
        return _players.toArray(a);
    }

    @Override
    public synchronized void removePlayer(Player p) {
        remove(p);
    }



    private final class Iter implements Iterator<Player> {

        private final Iterator<Player> _iterator;
        private final PlayerSet _parent;
        private Player _current;

        public Iter(PlayerSet parent) {
            _iterator = new ArrayList<Player>(_players).iterator();
            _parent = parent;
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
            _listener.removeCollection(_current, _parent);
        }

    }

}
