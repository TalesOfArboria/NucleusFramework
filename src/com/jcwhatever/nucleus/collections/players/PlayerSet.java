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

import com.jcwhatever.nucleus.collections.wrap.ConversionSetWrapper;
import com.jcwhatever.nucleus.collections.wrap.SyncStrategy;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A {@link HashSet} of {@link Player} objects.
 *
 * <p>
 *     {@link Player} object is automatically removed when the player logs out.
 * </p>
 *
 * <p>Thread safe.</p>
 *
 * <p>The sets iterators must be used inside a synchronized block which locks the
 * set instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class PlayerSet extends ConversionSetWrapper<Player, PlayerElement> implements IPlayerCollection {

    private final Plugin _plugin;
    private final Set<PlayerElement> _players;
    private final PlayerCollectionTracker _tracker;

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
        super(SyncStrategy.SYNC);

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
    public void removePlayer(UUID playerId) {
        synchronized (_sync) {
            //noinspection SuspiciousMethodCalls
            _players.remove(new PlayerElement.PlayerElementMatcher(playerId));
        }
    }

    @Override
    protected void onRemoved(Object element) {

        UUID id;

        if (element instanceof PlayerElement) {
            id = ((PlayerElement) element).getUniqueId();
        }
        else {
            Player player = PlayerUtils.getPlayer(element);
            id = player == null
                    ? PlayerUtils.getPlayerId(element)
                    : player.getUniqueId();
        }

        if (id == null)
            throw new ClassCastException("Failed to get Player or Player UUID object.");

        _tracker.notifyPlayerRemoved(id);
    }

    @Override
    protected void onAdded(PlayerElement element) {
        _tracker.notifyPlayerAdded(element.getUniqueId());
    }

    @Override
    protected Player convert(PlayerElement internal) {
        return internal.getPlayer();
    }

    @Override
    protected PlayerElement unconvert(Object external) {

        Player player = PlayerUtils.getPlayer(external);
        if (player == null)
            throw new ClassCastException();

        return new PlayerElement(player);
    }

    @Override
    protected Set<PlayerElement> set() {
        return _players;
    }
}
