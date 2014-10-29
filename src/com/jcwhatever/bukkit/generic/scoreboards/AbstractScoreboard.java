/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.scoreboards;

import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Base implementation of a scoreboard container.
 *
 * <p>
 *     Scoreboards applied to a player are tracked. If another scoreboard is
 *     applied then removed, the previous scoreboard is applied.
 * </p>
 */
public abstract class AbstractScoreboard implements IScoreboard {

    // stores scoreboard instances applied to a player, auto removes player entries
    // when the player logs out. Use to reapply previous scoreboards when the most recent
    // scoreboard is removed.
    private static Map<UUID, LinkedList<IScoreboard>> _stackMap = new PlayerMap<>();

    private final Plugin _plugin;
    private final ScoreboardInfo _typeInfo;
    private final Scoreboard _scoreboard;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public AbstractScoreboard(Plugin plugin) {
        PreCon.notNull(plugin);

        ScoreboardInfo typeInfo = this.getClass().getAnnotation(ScoreboardInfo.class);
        if (typeInfo == null)
            throw new IllegalStateException("Scoreboard class is missing its ITypeInfo annotation.");

        _plugin = plugin;
        _typeInfo = typeInfo;
        _scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        onInit();
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the scoreboard type name.
     */
    @Override
    public final String getType() {
        return _typeInfo.name();
    }

    /**
     * Get the encapsulated scoreboard.
     */
    @Override
    public final Scoreboard getScoreboard() {
        return _scoreboard;
    }

    /**
     * Apply the scoreboard to the specified player.
     *
     * @param p  The player.
     */
    @Override
    public final void apply(Player p) {
        PreCon.notNull(p);

        // get the list of scoreboard instances applied to the player
        LinkedList<IScoreboard> stack = _stackMap.get(p.getUniqueId());

        // add a new list if one doesn't exist
        if (stack == null) {
            stack = new LinkedList<IScoreboard>();
            _stackMap.put(p.getUniqueId(), stack);
        }

        // apply scoreboards
        p.setScoreboard(_scoreboard);

        // make sure the scoreboard isn't already the most recent scoreboard
        if (!stack.isEmpty()) {
            IScoreboard current = stack.peek();
            if (current == this) {
                return;
            }
        }

        // push scoreboard
        stack.push(this);

        onApply(p);
    }

    /**
     * Remove the player from the scoreboard.
     *
     * @param p  The player.
     */
    @Override
    public final void remove(Player p) {
        PreCon.notNull(p);

        // get the list of scoreboard instances applied to the player
        LinkedList<IScoreboard> stack = _stackMap.get(p.getUniqueId());
        IScoreboard previous = null;

        if (stack != null && !stack.isEmpty()) {

            // remove the current scoreboard from the list.
            if (stack.peek() == this) {
                stack.pop();
            }
            else {
                stack.remove(this);
            }
        }

        // get the previous scoreboard
        if (stack != null && !stack.isEmpty()) {
            previous = stack.peek();
        }

        if (previous != null) {
            // apply the previous scoreboard
            p.setScoreboard(previous.getScoreboard());
        }
        else {
            // remove the player scoreboard
            p.setScoreboard(null);
        }

        onRemove(p);
    }

    /**
     * Dispose the scoreboard.
     */
    @Override
    public void dispose() {
        Set<Objective> objectives = _scoreboard.getObjectives();
        for (Objective objective : objectives) {
            objective.unregister();
        }
    }


    /**
     * Called to get the display header.
     */
    protected abstract String getDisplayHeader();

    /**
     * Called after the instance is initialized.
     */
    protected abstract void onInit();

    /**
     * Called after the scoreboard is applied to a player.
     *
     * @param p  The player.
     */
    protected abstract void onApply(Player p);

    /**
     * Called after the scoreboard is remove from a player.
     *
     * @param p  The player.
     */
    protected abstract void onRemove(Player p);
}
