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
import com.jcwhatever.nucleus.regions.Region;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.script.ScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;

import org.bukkit.entity.Player;

/**
 * A region used by scripts for region related events.
 */
public final class InternalScriptRegion extends Region implements IScriptRegion {

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

    public void clearSubscribers() {
        _agents.disposeAgents();
        setEventListener(false);
    }

    @Override
    public boolean onEnter(IScriptUpdateSubscriber<Player> subscriber) {
        PreCon.notNull(subscriber, "subscriber");

        ScriptUpdateSubscriber<Player> updateSubscriber =
                new ScriptUpdateSubscriber<>(subscriber);

        _agents.getAgent("onEnter").addSubscriber(updateSubscriber);
        setEventListener(true);

        return true;
    }

    @Override
    public boolean onLeave(IScriptUpdateSubscriber<Player> subscriber) {
        PreCon.notNull(subscriber);

        ScriptUpdateSubscriber<Player> updateSubscriber =
                new ScriptUpdateSubscriber<>(subscriber);

        _agents.getAgent("onLeave").addSubscriber(updateSubscriber);
        setEventListener(true);

        return true;
    }

    @Override
    protected void onPlayerEnter(Player player, EnterRegionReason reason) {

        if (!_agents.hasAgent("onEnter"))
            return;

        _agents.getAgent("onEnter").update(player);
    }

    @Override
    protected void onPlayerLeave(Player player, LeaveRegionReason reason) {

        _agents.getAgent("onLeave").update(player);
    }

    @Override
    protected boolean canDoPlayerEnter(Player player, EnterRegionReason reason) {
        return _agents.hasAgent("onEnter");
    }

    @Override
    protected boolean canDoPlayerLeave(Player player, LeaveRegionReason reason) {
        return _agents.hasAgent("onLeave");
    }
}
