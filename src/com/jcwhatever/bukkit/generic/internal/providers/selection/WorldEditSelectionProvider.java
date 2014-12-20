/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.internal.providers.selection;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.providers.IRegionSelectProvider;
import com.jcwhatever.bukkit.generic.regions.selection.IRegionSelection;
import com.jcwhatever.bukkit.generic.regions.selection.RegionSelection;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.RegionSelector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * WorldEdit region selection provider
 */
public final class WorldEditSelectionProvider implements IRegionSelectProvider {

    private static Boolean _isWorldEditInstalled = null;
    private static Plugin _wePlugin;

    public static boolean isWorldEditInstalled() {
        if (_isWorldEditInstalled == null) {

            // Check that World Edit is installed
            _wePlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
            _isWorldEditInstalled = _wePlugin instanceof WorldEditPlugin;
        }

        return _isWorldEditInstalled;
    }

    @Override
    public String getName() {
        return "WorldEdit";
    }

    @Override
    public String getVersion() {
        return GenericsLib.getPlugin().getDescription().getVersion();
    }

    @Override
    public int getLogicalVersion() {
        return 0;
    }

    @Override
    public IRegionSelection getSelection(Player player) {
        PreCon.notNull(player);

        // Check that World Edit is installed
        if (!isWorldEditInstalled()) {
            return null;
        }

        WorldEditPlugin plugin = (WorldEditPlugin)_wePlugin;
        Selection sel;

        // Check for World Edit selection
        if ((sel = plugin.getSelection(player)) == null) {
            return null;
        }

        // Make sure both points are selected
        if (sel.getMaximumPoint() == null || sel.getMinimumPoint() == null) {
            return null;
        }

        if (!sel.getMaximumPoint().getWorld().equals(sel.getMinimumPoint().getWorld())) {
            return null;
        }

        return new RegionSelection(sel.getMinimumPoint(), sel.getMinimumPoint());
    }

    @Override
    public boolean setSelection(Player player, IRegionSelection selection) {
        PreCon.notNull(player);
        PreCon.notNull(selection);

        if (!selection.isDefined())
            return false;

        // Check that World Edit is installed
        if (!isWorldEditInstalled())
            return false;

        Location p1 = selection.getP1();
        Location p2 = selection.getP2();

        if (!p1.getWorld().equals(p2.getWorld())) {
            return false;
        }

        WorldEditPlugin plugin = (WorldEditPlugin)_wePlugin;
        RegionSelector selector = plugin.getSession(player).getRegionSelector(plugin.wrapPlayer(player).getWorld());

        Vector p1Vector = new Vector(p1.toVector().getX(), p1.toVector().getY(), p1.toVector().getZ());
        Vector p2Vector = new Vector(p2.toVector().getX(), p2.toVector().getY(), p2.toVector().getZ());

        selector.selectPrimary(p1Vector);
        selector.selectSecondary(p2Vector);

        return true;
    }

    @Nullable
    private static WorldEditPlugin getWorldEdit() {
        if (!isWorldEditInstalled())
            return null;

        return (WorldEditPlugin)_wePlugin;
    }
}
