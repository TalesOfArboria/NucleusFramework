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

package com.jcwhatever.nucleus.managed.scripting.regions;

import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.utils.managers.INamedManager;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Interface for the global script region manager.
 */
public interface IScriptRegionManager extends INamedManager<IScriptRegion> {

    /**
     * Add a new region.
     *
     * @param name       The node name of the region.
     * @param selection  The regions coordinates.
     *
     * @return  Null if the manager already has a region by the specified node name.
     */
    @Nullable
    IScriptRegion add(String name, IRegionSelection selection);

    /**
     * Add a scripting region using an anchor location and radius.
     *
     * <p>The script region is created with the anchor as its center location.
     * Although the term 'radius' is used, the region is not a sphere, it will be
     * a perfect cube.</p>
     *
     * @param name    The name of the region.
     * @param anchor  The anchor location.
     * @param radius  The radius.
     *
     * @return  The newly created {@link IScriptRegion} or null if failed.
     */
    @Nullable
    IScriptRegion addFromAnchor(String name, Location anchor, int radius);
}
