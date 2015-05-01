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

package com.jcwhatever.nucleus.internal.managed.scoreboards;

import com.jcwhatever.nucleus.managed.scoreboards.IScoreboard;
import com.jcwhatever.nucleus.managed.scoreboards.ITeam;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Set;

/**
 * Managed scoreboard team.
 */
class ManagedTeam implements ITeam {

    private final IScoreboard _scoreboard;
    private final Team _team;

    ManagedTeam(IScoreboard scoreboard, Team team) {
        _scoreboard = scoreboard;
        _team = team;
    }

    @Override
    public String getName() {
        return _team.getName();
    }

    @Override
    public String getDisplayName() {
        return _team.getDisplayName();
    }

    @Override
    public void setDisplayName(String displayName, Object... args) {
        _team.setDisplayName(TextUtils.format(displayName, args));
    }

    @Override
    public String getPrefix() {
        return _team.getPrefix();
    }

    @Override
    public void setPrefix(String prefix, Object... args) {
        _team.setPrefix(TextUtils.format(prefix, args));
    }

    @Override
    public String getSuffix() {
        return _team.getSuffix();
    }

    @Override
    public void setSuffix(String suffix, Object... args) {
        _team.setSuffix(TextUtils.format(suffix, args));
    }

    @Override
    public boolean allowFriendlyFire() {
        return _team.allowFriendlyFire();
    }

    @Override
    public void setAllowFriendlyFire(boolean isAllowed) {
        _team.setAllowFriendlyFire(isAllowed);
    }

    @Override
    public boolean canSeeFriendlyInvisibles() {
        return _team.canSeeFriendlyInvisibles();
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean canSee) {
        _team.setCanSeeFriendlyInvisibles(canSee);
    }

    @Override
    public NameTagVisibility getNameTagVisibility() {
        return _team.getNameTagVisibility();
    }

    @Override
    public void setNameTagVisibility(NameTagVisibility nameTagVisibility) {
        _team.setNameTagVisibility(nameTagVisibility);
    }

    @Override
    public Set<OfflinePlayer> getPlayers() {
        return _team.getPlayers();
    }

    @Override
    public <T extends Collection<OfflinePlayer>> T getPlayers(T output) {
        PreCon.notNull(output);

        output.addAll(_team.getPlayers());
        return output;
    }

    @Override
    public Set<String> getEntries() {
        return _team.getEntries();
    }

    @Override
    public <T extends Collection<String>> T getEntries(T output) {
        PreCon.notNull(output);

        output.addAll(_team.getEntries());
        return output;
    }

    @Override
    public int getSize() {
        return _team.getSize();
    }

    @Override
    public IScoreboard getScoreboard() {
        return _scoreboard;
    }

    @Override
    public void addPlayer(OfflinePlayer player) {
        _team.addPlayer(player);
    }

    @Override
    public void addEntry(String entry) {
        _team.addEntry(entry);
    }

    @Override
    public boolean removePlayer(OfflinePlayer player) {
        return _team.removePlayer(player);
    }

    @Override
    public boolean removeEntry(String entry) {
        return _team.removeEntry(entry);
    }

    @Override
    public void unregister() {
        _team.unregister();
    }

    @Override
    public boolean hasPlayer(OfflinePlayer player) {
        return _team.hasPlayer(player);
    }

    @Override
    public boolean hasEntry(String entry) {
        return _team.hasEntry(entry);
    }

    @Override
    public int hashCode() {
        return _team.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ManagedTeam &&
                ((ManagedTeam) obj)._team.equals(_team);
    }
}
