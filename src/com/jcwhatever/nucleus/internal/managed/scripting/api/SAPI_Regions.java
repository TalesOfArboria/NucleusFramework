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

package com.jcwhatever.nucleus.internal.managed.scripting.api;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.IRegionEventHandler;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * General API for {@link IRegion}'s.
 */
public class SAPI_Regions implements IDisposable {

    private final LinkedList<ScriptRegionEvents> _eventHandlers = new LinkedList<>();
    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        while (!_eventHandlers.isEmpty()) {
            _eventHandlers.remove().dispose();
        }

        _isDisposed = true;
    }

    /**
     * Get a region by plugin name and region name.
     *
     * @param pluginName  The name of the regions owning plugin.
     * @param regionName  The name of the region.
     *
     * @return  The region.
     */
    public IRegion get(String pluginName, String regionName) {
        PreCon.notNullOrEmpty(pluginName, "pluginName");
        PreCon.notNullOrEmpty(regionName, "regionName");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        PreCon.isValid(plugin != null, "Plugin named '{0}' not found.", pluginName);

        List<IRegion> regions = Nucleus.getRegionManager().getRegions(plugin, regionName);

        PreCon.isValid(!regions.isEmpty(), "Region named '{0}' from plugin named '{1}' not found.",
                regionName, pluginName);

        PreCon.isValid(regions.size() == 1, "More than 1 region named '{0}' from plugin named '{1}' was found.",
                regionName, pluginName);

        return regions.get(0);
    }

    /**
     * Attach an event handler to a region to be called when a player enters it.
     *
     * @param pluginName    The name of the regions owning plugin.
     * @param regionName    The name of the region.
     * @param enterHandler  The event handler.
     */
    public void onEnter(String pluginName, String regionName, IScriptRegionEnter enterHandler) {

        IRegion region = get(pluginName, regionName);

        ScriptRegionEvents handler = new ScriptRegionEvents(region, enterHandler, null);
        region.addEventHandler(handler);
        _eventHandlers.add(handler);
    }

    /**
     * Attach an event handler to a region to be called when a player leaves it.
     *
     * @param pluginName    The name of the regions owning plugin.
     * @param regionName    The name of the region.
     * @param leaveHandler  The event handler.
     */
    public void onLeave(String pluginName, String regionName, IScriptRegionLeave leaveHandler) {

        IRegion region = get(pluginName, regionName);

        ScriptRegionEvents handler = new ScriptRegionEvents(region, null, leaveHandler);
        region.addEventHandler(handler);
        _eventHandlers.add(handler);
    }

    public interface IScriptRegionEnter {
        void onEnter(Player player, EnterRegionReason reason);
    }

    public interface IScriptRegionLeave {
        void onLeave(Player player, LeaveRegionReason reason);
    }

    private static class ScriptRegionEvents implements IRegionEventHandler, IDisposable {

        private final IRegion region;
        private final IScriptRegionEnter enter;
        private final IScriptRegionLeave leave;
        private boolean isDisposed;

        ScriptRegionEvents(IRegion region,
                           @Nullable IScriptRegionEnter enter,
                           @Nullable IScriptRegionLeave leave) {

            this.region = region;
            this.enter = enter;
            this.leave = leave;
        }

        public IRegion getRegion() {
            return region;
        }

        @Override
        public boolean canDoPlayerEnter(Player player, EnterRegionReason reason) {
            return enter != null;
        }

        @Override
        public boolean canDoPlayerLeave(Player player, LeaveRegionReason reason) {
            return leave != null;
        }

        @Override
        public void onPlayerEnter(Player player, EnterRegionReason reason) {
            assert enter != null;
            enter.onEnter(player, reason);
        }

        @Override
        public void onPlayerLeave(Player player, LeaveRegionReason reason) {
            assert leave != null;
            leave.onLeave(player, reason);
        }

        @Override
        public boolean isDisposed() {
            return this.isDisposed;
        }

        @Override
        public void dispose() {
            region.removeEventHandler(this);
            this.isDisposed = true;
        }
    }
}
