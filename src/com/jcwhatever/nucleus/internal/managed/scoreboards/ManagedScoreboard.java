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

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.jcwhatever.nucleus.managed.scoreboards.IHudObjective;
import com.jcwhatever.nucleus.managed.scoreboards.IManagedScoreboard;
import com.jcwhatever.nucleus.managed.scoreboards.IObjective;
import com.jcwhatever.nucleus.managed.scoreboards.IScorableObjective;
import com.jcwhatever.nucleus.managed.scoreboards.IScore;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboard;
import com.jcwhatever.nucleus.managed.scoreboards.IScoreboardExtension;
import com.jcwhatever.nucleus.managed.scoreboards.ITeam;
import com.jcwhatever.nucleus.managed.scoreboards.ScoreboardLifespan;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Internal implementation of {@link IManagedScoreboard}
 */
class ManagedScoreboard implements IScoreboard {

    private final InternalScoreboardManager _tracker;
    private final ScoreboardLifespan _lifespan;
    private final IScoreboardExtension _extension;
    private final Scoreboard _scoreboard;
    private final Map<Objective, IObjective> _objectives = new WeakHashMap<>(10);
    private final SetMultimap<String, IObjective> _objectivesByCriteria =
            MultimapBuilder.hashKeys(10).hashSetValues(10).build();
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param tracker     The parent tracker.
     * @param scoreboard  The scoreboard to manage.
     * @param lifespan    The lifespan type
     */
    public ManagedScoreboard(InternalScoreboardManager tracker,
                             Scoreboard scoreboard, ScoreboardLifespan lifespan,
                             @Nullable IScoreboardExtension extension) {
        PreCon.notNull(tracker);
        PreCon.notNull(lifespan);
        PreCon.notNull(scoreboard);

        _tracker = tracker;
        _lifespan = lifespan;
        _scoreboard = scoreboard;
        _extension = extension;

        if (extension != null) {
            extension.onAttach(this);
        }
    }

    public Scoreboard getHandle() {
        return _scoreboard;
    }

    @Override
    public ScoreboardLifespan getLifespan() {
        return _lifespan;
    }

    @Override
    public boolean apply(Player player) {
        PreCon.notNull(player);

        if (_tracker.apply(player, this)) {

            if (_extension != null)
                _extension.onApply(player, this);

            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Player player) {
        PreCon.notNull(player);

        if (_tracker.remove(player, this)) {

            if (_extension != null)
                _extension.onRemove(player, this);

            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public IScoreboardExtension getExtension() {
        return _extension;
    }

    @Override
    public ManagedObjective registerNewObjective(String name, String criteria) {

        Objective objective = _scoreboard.registerNewObjective(name, criteria);

        ManagedObjective managed = new ManagedObjective(this, objective);
        _objectives.put(objective, managed);
        _objectivesByCriteria.put(criteria, managed);

        return managed;
    }

    @Override
    public IHudObjective registerNewHud(String name) {

        Objective objective = _scoreboard.registerNewObjective(name, "dummy");

        ManagedHudObjective managed = new ManagedHudObjective(this, objective);
        _objectives.put(objective, managed);
        _objectivesByCriteria.put("dummy", managed);

        return managed;
    }

    @Override
    public IObjective getObjective(String name) {
        Objective objective = _scoreboard.getObjective(name);
        if (objective == null)
            return null;

        IObjective managed = _objectives.get(objective);
        if (managed == null) {
            managed = new ManagedObjective(this, objective);
            _objectives.put(objective, managed);
        }

        return managed;
    }

    @Override
    public Set<IObjective> getObjectivesByCriteria(String criteria) {
        PreCon.notNullOrEmpty(criteria);

        return new HashSet<>(_objectivesByCriteria.get(criteria));
    }

    @Override
    public <T extends Collection<IObjective>> T getObjectivesByCriteria(String criteria, T output) {
        PreCon.notNullOrEmpty(criteria);
        PreCon.notNull(output);

        output.addAll(_objectivesByCriteria.get(criteria));
        return output;
    }

    @Override
    public Set<IObjective> getObjectives() {
        return new HashSet<>(_objectives.values());
    }

    @Override
    public <T extends Collection<IObjective>> T getObjectives(T output) {
        PreCon.notNull(output);

        output.addAll(_objectives.values());
        return output;
    }

    @Override
    public IObjective getObjective(DisplaySlot slot) {
        Objective objective = _scoreboard.getObjective(slot);
        if (objective == null)
            return null;

        IObjective managed = _objectives.get(objective);
        if (managed == null) {
            managed = new ManagedObjective(this, objective);
            _objectives.put(objective, managed);
        }

        return managed;
    }

    @Override
    public Collection<IScore> getScores(String entry) {
        return getScores(entry, new HashSet<IScore>(_objectives.size()));
    }

    @Override
    public <T extends Collection<IScore>> T getScores(String entry, T output) {
        PreCon.notNullOrEmpty(entry);
        PreCon.notNull(output);

        for (IObjective objective : _objectives.values()) {
            if (!(objective instanceof IScorableObjective))
                continue;

            IScore score = ((IScorableObjective) objective).getScore(entry);
            if (score == null)
                continue;

            output.add(score);
        }

        return output;
    }

    @Override
    public void resetScores(String entry) {
        _scoreboard.resetScores(entry);
    }

    Map<Team, ITeam> _teams = new WeakHashMap<>(10);

    @Override
    public ITeam registerNewTeam(String name) {
        Team team = _scoreboard.registerNewTeam(name);
        ManagedTeam managed = new ManagedTeam(this, team);
        _teams.put(team, managed);
        return managed;
    }

    @Override
    public ITeam getPlayerTeam(OfflinePlayer player) {
        Team team = _scoreboard.getPlayerTeam(player);
        if (team == null)
            return null;

        ITeam managed = _teams.get(team);
        if (managed == null) {
            managed = new ManagedTeam(this, team);
            _teams.put(team, managed);
        }

        return managed;
    }

    @Override
    public ITeam getTeam(String name) {
        Team team = _scoreboard.getTeam(name);
        if (team == null)
            return null;

        ITeam managed = _teams.get(team);
        if (managed == null) {
            managed = new ManagedTeam(this, team);
            _teams.put(team, managed);
        }

        return managed;
    }

    @Override
    public Set<ITeam> getTeams() {
        return new HashSet<>(_teams.values());
    }

    @Override
    public <T extends Collection<ITeam>> T getTeams(T output) {
        PreCon.notNull(output);

        output.addAll(_teams.values());
        return output;
    }

    @Override
    public Set<String> getEntries() {
        return _scoreboard.getEntries();
    }

    @Override
    public <T extends Collection<String>> T getEntries(T output) {
        PreCon.notNull(output);

        output.addAll(_scoreboard.getEntries());
        return output;
    }

    @Override
    public void clearSlot(DisplaySlot slot) {
        _scoreboard.clearSlot(slot);
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        Set<Objective> objectives = _scoreboard.getObjectives();
        for (Objective objective : objectives) {
            objective.unregister();
        }

        if (_extension != null)
            _extension.onScoreboardDispose(this);

        _isDisposed = true;
    }

    @Override
    public int hashCode() {
        return _scoreboard.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ManagedScoreboard &&
                ((ManagedScoreboard) obj)._scoreboard.equals(_scoreboard);
    }
}

