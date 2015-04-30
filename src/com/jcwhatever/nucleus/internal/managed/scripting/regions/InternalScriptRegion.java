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


package com.jcwhatever.nucleus.internal.managed.scripting.regions;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.scripting.regions.IScriptRegion;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.ReadOnlyRegion;
import com.jcwhatever.nucleus.regions.Region;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.script.ScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;
import com.jcwhatever.nucleus.utils.observer.update.UpdateAgent;

import org.bukkit.entity.Player;

/**
 * A region used by scripts for region related events.
 */
public final class InternalScriptRegion extends Region {

    private final NamedUpdateAgents _agents = new NamedUpdateAgents();

    /**
     * Constructor.
     *
     * @param name      The name of the region.
     * @param settings  The data node to load and save settings.
     */
    public InternalScriptRegion(String name, IDataNode settings) {
        super(Nucleus.getPlugin(), name, settings);
    }

    public ScriptRegion getScriptRegion() {
        return new ScriptRegion(this);
    }

    public boolean onEnter(ScriptUpdateSubscriber<Player> subscriber) {
        PreCon.notNull(subscriber, "subscriber");

        _agents.getAgent("onEnter").addSubscriber(subscriber);
        setEventListener(true);

        return true;
    }

    public boolean onLeave(ScriptUpdateSubscriber<Player> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onLeave").addSubscriber(subscriber);
        setEventListener(true);

        return true;
    }

    @Override
    protected void onPlayerEnter(Player player, EnterRegionReason reason) {
        _agents.update("onEnter", player);
    }

    @Override
    protected void onPlayerLeave(Player player, LeaveRegionReason reason) {
        _agents.update("onLeave", player);
    }

    @Override
    protected boolean canDoPlayerEnter(Player player, EnterRegionReason reason) {
        return _agents.hasAgent("onEnter");
    }

    @Override
    protected boolean canDoPlayerLeave(Player player, LeaveRegionReason reason) {
        return _agents.hasAgent("onLeave");
    }

    @Override
    protected void onDispose() {
        super.onDispose();

        disposeSubscribers("onEnter", _agents);
        disposeSubscribers("onLeave", _agents);
        _agents.disposeAgents();
    }

    private void disposeSubscribers(String agentName, NamedUpdateAgents agents) {

        if (!agents.hasAgent(agentName))
            return;

        UpdateAgent<?> enterAgent = agents.getAgent(agentName);
        for (ISubscriber subscriber : enterAgent.getSubscribers()) {
            subscriber.dispose();
        }
    }

    private class ScriptRegion extends ReadOnlyRegion implements IScriptRegion {

        final NamedUpdateAgents agents = new NamedUpdateAgents();
        boolean isDisposed;

        /**
         * Constructor.
         *
         * @param region The region to encapsulate.
         */
        ScriptRegion(IRegion region) {
            super(region);
        }

        @Override
        public boolean onEnter(IScriptUpdateSubscriber<Player> subscriber) {
            PreCon.notNull(subscriber, "subscriber");

            ScriptUpdateSubscriber<Player> updateSubscriber =
                    new ScriptUpdateSubscriber<>(subscriber);

            agents.getAgent("onEnter").addSubscriber(updateSubscriber);
            return InternalScriptRegion.this.onEnter(updateSubscriber);
        }

        @Override
        public boolean onLeave(IScriptUpdateSubscriber<Player> subscriber) {
            PreCon.notNull(subscriber);

            ScriptUpdateSubscriber<Player> updateSubscriber =
                    new ScriptUpdateSubscriber<>(subscriber);

            agents.getAgent("onLeave").addSubscriber(updateSubscriber);
            return InternalScriptRegion.this.onLeave(updateSubscriber);
        }

        @Override
        public boolean isDisposed() {
            return isDisposed;
        }

        @Override
        public void dispose() {
            disposeSubscribers("onEnter", agents);
            disposeSubscribers("onLeave", agents);
            agents.disposeAgents();
            isDisposed = true;
        }

        public void clearSubscribers() {
            agents.disposeAgents();
            //setEventListener(false);
        }
    }
}
