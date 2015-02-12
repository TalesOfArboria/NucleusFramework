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

package com.jcwhatever.nucleus.utils.npc.traits;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.goals.INpcGoal;
import com.jcwhatever.nucleus.providers.npc.goals.NpcGoalResult;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.script.ScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;

import org.bukkit.Location;

import java.util.Collection;
import java.util.LinkedList;

/*
 * 
 */
public class SimpleWaypointsTrait extends NpcTrait {

    private LinkedList<Location> _waypoints = new LinkedList<>();
    private final NamedUpdateAgents _agents = new NamedUpdateAgents();

    private boolean _isDisposed;
    private Location _current;
    private INpcGoal _waypointGoal;

    /**
     * Constructor.
     *
     * @param npc  The NPC the trait is for.
     * @param type The parent type that instantiated the trait.
     */
    public SimpleWaypointsTrait(INpc npc, NpcTraitType type) {
        super(npc, type);
    }

    /**
     * Add a way point location.
     *
     * @param location  The location to add.
     *
     * @return  Self for chaining.
     */
    public SimpleWaypointsTrait addWaypoint(Location location) {
        PreCon.notNull(location);

        _waypoints.add(location);

        return this;
    }

    /**
     * Add a collection of way point locations.
     *
     * @param locations  The locations to add.
     *
     * @return  Self for chaining.
     */
    public SimpleWaypointsTrait addWaypoints(Collection<Location> locations) {
        PreCon.notNull(locations);

        _waypoints.addAll(locations);

        return this;
    }

    /**
     * Add a one time callback that is run when the NPC has finished
     * pathing to all of the way points.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    public SimpleWaypointsTrait onFinish(IScriptUpdateSubscriber<INpc> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onFinish").register(new ScriptUpdateSubscriber<>(subscriber));

        return this;
    }

    /**
     * Start pathing to the added waypoints.
     *
     * @return  Self for chaining.
     */
    public SimpleWaypointsTrait start() {
        if (_waypointGoal == null)
            _waypointGoal = new WaypointGoal();

        getNpc().getGoals().add(1, _waypointGoal);

        return this;
    }

    /**
     * Stop pathing.
     *
     * @return  Self for chaining.
     */
    public SimpleWaypointsTrait stop() {
        if (_waypointGoal == null)
            return this;

        getNpc().getGoals().remove(_waypointGoal);

        return this;
    }

    /**
     * Clear all way points.
     */
    public void clear() {
        _waypoints.clear();
        _current = null;
    }

    /**
     * Determine if the trait is disposed.
     */
    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    /**
     * Dispose the trait.
     */
    @Override
    public void dispose() {
        _isDisposed = true;
        stop();
        clear();
        _agents.disposeAgents();
        super.dispose();
    }

    /**
     * NPC Way point goal
     */
    private class WaypointGoal implements INpcGoal {

        @Override
        public void reset() {
            _waypoints.clear();
        }

        @Override
        public NpcGoalResult run() {

            NpcGoalResult result = NpcGoalResult.CONTINUE;

            if (!getNpc().getNavigator().isRunning()) {
                next();
                if (_waypoints.isEmpty()) {
                    _agents.update("onFinish", getNpc());
                    result = NpcGoalResult.FINISH;
                }
            }

            return result;
        }

        @Override
        public boolean shouldRun() {

            if (_waypoints.isEmpty() && _current == null)
                return false;

            next();

            return true;
        }

        private void next() {

            if (_current == null)
                _current = _waypoints.removeFirst();

            getNpc().getNavigator().setTarget(_current);
            getNpc().lookTowards(_current);
        }
    }
}
