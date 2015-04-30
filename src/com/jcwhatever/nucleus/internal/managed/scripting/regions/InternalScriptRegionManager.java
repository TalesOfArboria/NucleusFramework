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
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.regions.RegionManager;
import com.jcwhatever.nucleus.regions.SimpleRegionSelection;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Manages quest scripting regions.
 */
public final class InternalScriptRegionManager extends RegionManager<InternalScriptRegion> {

    /**
     * Constructor.
     *
     * @param dataNode  The data node to load and store settings.
     */
    public InternalScriptRegionManager(IDataNode dataNode) {
        super(Nucleus.getPlugin(), dataNode, true);
    }

    @Nullable
    public InternalScriptRegion addFromAnchor(String name, Location anchor, int radius) {
        PreCon.notNull(name);
        PreCon.notNull(anchor);
        PreCon.greaterThanZero(radius);

        Location p1 = new Location(anchor.getWorld(),
                anchor.getBlockX() + radius,
                anchor.getBlockY() + radius,
                anchor.getBlockZ() + radius);

        Location p2 = new Location(anchor.getWorld(),
                anchor.getBlockX() - radius,
                anchor.getBlockY() - radius,
                anchor.getBlockZ() - radius);

        return add(name, new SimpleRegionSelection(p1, p2));
    }

    @Nullable
    @Override
    protected InternalScriptRegion load(String name, IDataNode itemNode) {
        return new InternalScriptRegion(name, itemNode);
    }

    @Nullable
    @Override
    protected void save(InternalScriptRegion item, IDataNode itemNode) {
        // do nothing
    }

    @Override
    protected InternalScriptRegion create(String name,
                                          @Nullable IDataNode dataNode, IRegionSelection selection) {
        InternalScriptRegion region = new InternalScriptRegion(name, dataNode);
        region.setCoords(selection.getP1(), selection.getP2());

        return region;
    }
}
