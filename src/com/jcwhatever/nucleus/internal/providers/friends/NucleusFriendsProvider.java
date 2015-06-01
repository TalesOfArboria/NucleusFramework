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

package com.jcwhatever.nucleus.internal.providers.friends;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.friends.FriendLevels;
import com.jcwhatever.nucleus.providers.friends.IFriendLevel;
import com.jcwhatever.nucleus.providers.friends.IFriendsContext;
import com.jcwhatever.nucleus.providers.friends.IFriendsProvider;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Nucleus implementation of {@link IFriendsProvider}.
 */
public final class NucleusFriendsProvider extends Provider implements IFriendsProvider {

    public static final String NAME = "NucleusFriends";

    private final IDataNode _dataNode;
    private final NucleusFriendsContext _defaultContext;
    private final Comparator<Integer> _rawLevelSort = new RawLevelSorter();
    private final Map<String, IFriendsContext> _contexts = new HashMap<>(10);
    private final Map<String, IFriendLevel> _levelMap = new HashMap<>(15);
    private final Map<Class<? extends IFriendLevel>, TreeMap<Integer, IFriendLevel>> _levels =
            new HashMap<>(15);
    private final TreeMap<Integer, Set<IFriendLevel>> _allLevels = new TreeMap<>(_rawLevelSort);
    private final Object _sync = new Object();

    public NucleusFriendsProvider() {
        setInfo(new InternalProviderInfo(this.getClass(),
                "NucleusFriends", "Default friends provider."));

        _dataNode = Bukkit.getOnlineMode()
                ? getDataNode()
                : DataStorage.get(Nucleus.getPlugin(), getDataPath("config-offline"));
        _dataNode.load();

        _defaultContext = new NucleusNamedFriendsContext(this, "_default");

        registerLevel(FriendLevels.CASUAL);
        registerLevel(FriendLevels.CLOSE);
        registerLevel(FriendLevels.BEST);

        loadContexts();
    }

    @Override
    public IFriendsContext getDefaultContext() {
        return _defaultContext;
    }

    @Nullable
    @Override
    public IFriendsContext createTransientContext() {
        return new NucleusFriendsContext(this);
    }

    @Nullable
    @Override
    public IFriendsContext createContext(String name) {
        PreCon.notNullOrEmpty(name);
        PreCon.isValid(!name.equals("_default"), "_default is reserved for the default friend context.");

        synchronized (_sync) {
            if (_contexts.containsKey(name.toLowerCase()))
                return null;
        }

        NucleusNamedFriendsContext context = new NucleusNamedFriendsContext(this, name);

        synchronized (_sync) {
            _contexts.put(context.getSearchName(), context);
        }

        return context;
    }

    @Nullable
    @Override
    public IFriendsContext getContext(String name) {
        PreCon.notNull(name);

        synchronized (_sync) {
            return _contexts.get(name.toLowerCase());
        }
    }

    @Override
    public Collection<IFriendsContext> getContexts() {
        synchronized (_sync) {
            return new ArrayList<>(_contexts.values());
        }
    }

    @Override
    public <T extends Collection<IFriendsContext>> T getContexts(T output) {
        PreCon.notNull(output);

        synchronized (_sync) {
            output.addAll(_contexts.values());
            return output;
        }
    }

    @Override
    public boolean removeContext(String name) {
        PreCon.notNullOrEmpty(name);

        synchronized (_sync) {
            return _contexts.remove(name.toLowerCase()) != null;
        }
    }

    @Override
    public List<IFriendLevel> getLevels() {

        List<IFriendLevel> result = new ArrayList<>(_allLevels.size() * 2);

        synchronized (_sync) {
            for (Set<IFriendLevel> set : _allLevels.values()) {
                result.addAll(set);
            }
        }

        return result;
    }

    @Override
    public IFriendLevel getLevel(String name) {
        PreCon.notNullOrEmpty(name);

        synchronized (_sync) {
            return _levelMap.get(name.toLowerCase());
        }
    }

    @Override
    public boolean registerLevel(IFriendLevel level) {
        PreCon.notNull(level);

        Class<? extends IFriendLevel> clazz = level.getClass();

        synchronized (_sync) {

            if (_levelMap.containsKey(level.getSearchName()))
                return false;

            TreeMap<Integer, IFriendLevel> map = _levels.get(clazz);

            if (map == null) {
                map = new TreeMap<>(_rawLevelSort);
                _levels.put(clazz, map);
            }

            IFriendLevel previous = map.put(level.getRawLevel(), level);

            Set<IFriendLevel> set = _allLevels.get(level.getRawLevel());
            if (set == null) {
                set = new HashSet<>(5);
                _allLevels.put(level.getRawLevel(), set);
            }

            if (previous != null)
                set.remove(previous);

            set.add(level);

            _levelMap.put(level.getSearchName(), level);
        }

        return true;
    }

    @Override
    public boolean unregisterLevel(IFriendLevel level) {
        PreCon.notNull(level);

        Class<? extends IFriendLevel> clazz = level.getClass();

        synchronized (_sync) {

            IFriendLevel current = _levelMap.remove(level.getSearchName());
            if (current == null)
                return false;

            if (!current.equals(level)) {
                _levelMap.put(current.getSearchName(), current);
                return false;
            }

            TreeMap<Integer, IFriendLevel> map = _levels.get(clazz);
            if (map == null)
                return false;

            map.remove(level.getRawLevel());

            if (map.size() == 0)
                _levels.remove(clazz);

            Set<IFriendLevel> set = _allLevels.get(level.getRawLevel());
            if (set != null) {
                set.remove(level);
            }
        }

        return true;
    }

    @Nullable
    public <T extends IFriendLevel> T getClosestBelowLevel(Class<T> clazz, int rawLevel) {
        PreCon.notNull(clazz);

        TreeMap<Integer, IFriendLevel> map = _levels.get(clazz);
        if (map == null)
            return null;

        Entry<Integer, IFriendLevel> entry = map.floorEntry(rawLevel);
        if (entry == null)
            return null;

        @SuppressWarnings("unchecked")
        T result = (T)entry.getValue();

        return result;
    }

    @Nullable
    public <T extends IFriendLevel> T getClosestBelowLevel(int rawLevel) {

        Entry<Integer, Set<IFriendLevel>> entry = _allLevels.floorEntry(rawLevel);
        if (entry == null)
            return null;

        Set<IFriendLevel> set = entry.getValue();
        if (set.isEmpty())
            return null;

        Iterator<IFriendLevel> iterator = set.iterator();
        if (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            T result = (T)iterator.next();

            return result;
        }

        return null;
    }

    private void loadContexts() {

        for (IDataNode dataNode : _dataNode) {
            NucleusNamedFriendsContext context = new NucleusNamedFriendsContext(this, dataNode.getName());
            _contexts.put(context.getSearchName(), context);
        }
    }

    private static class RawLevelSorter implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return Integer.compare(o1, o2);
        }
    }
}
