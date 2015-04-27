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
import com.jcwhatever.nucleus.internal.managed.scripting.InternalScriptManager;
import com.jcwhatever.nucleus.managed.scripting.regions.IScriptRegion;
import com.jcwhatever.nucleus.managed.scripting.regions.IScriptRegionManager;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Used to allow manager contexts to be stored externally (by plugins) without
 * causing a memory leak when scripts are reloaded.
 *
 * <p>Ensures most recent instance of the manager for the plugin context is used.</p>
 */
public class InternalScriptRegionManagerWrapper implements IScriptRegionManager {

    private final Plugin _plugin;

    public InternalScriptRegionManagerWrapper(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
    }

    @Nullable
    @Override
    public IScriptRegion add(String name, IRegionSelection selection) {
        return manager().add(name, selection);
    }

    @Nullable
    @Override
    public IScriptRegion addFromAnchor(String name, Location anchor, int radius) {
        return manager().addFromAnchor(name, anchor, radius);
    }

    @Override
    public boolean contains(String name) {
        return manager().contains(name);
    }

    @Nullable
    @Override
    public IScriptRegion get(String name) {
        return manager().get(name);
    }

    @Override
    public Collection<IScriptRegion> getAll() {
        return manager().getAll();
    }

    @Override
    public boolean remove(String name) {
        return manager().remove(name);
    }

    private IScriptRegionManager manager() {
        return ((InternalScriptManager)Nucleus.getScriptManager()).getRegionsDirect(_plugin);
    }
}
