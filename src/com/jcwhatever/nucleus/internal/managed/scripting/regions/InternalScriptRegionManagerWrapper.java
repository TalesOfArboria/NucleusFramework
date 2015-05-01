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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Used to allow manager contexts to be stored externally (by plugins) without
 * causing a memory leak when scripts are reloaded.
 *
 * <p>Ensures most recent instance of the manager for the plugin context is used.</p>
 */
public class InternalScriptRegionManagerWrapper implements IScriptRegionManager {

    private final Plugin _plugin;
    private final Map<String, IScriptRegion> _regionMap = new HashMap<>(15);

    public InternalScriptRegionManagerWrapper(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
    }

    @Nullable
    @Override
    public IScriptRegion add(String name, IRegionSelection selection) {

        InternalScriptRegion region = manager().add(name, selection);
        if (region == null)
            return null;

        IScriptRegion scriptRegion = region.getScriptRegion();
        _regionMap.put(region.getSearchName(), scriptRegion);

        return addScriptRegion(region);
    }

    @Nullable
    @Override
    public IScriptRegion addFromAnchor(String name, Location anchor, int radius) {

        InternalScriptRegion region = manager().addFromAnchor(name, anchor, radius);
        if (region == null)
            return null;

        return addScriptRegion(region);
    }

    @Override
    public boolean contains(String name) {
        return manager().contains(name);
    }

    @Nullable
    @Override
    public IScriptRegion get(String name) {
        PreCon.notNullOrEmpty(name);

        IScriptRegion scriptRegion = _regionMap.get(name.toLowerCase());
        if (scriptRegion != null)
            return scriptRegion;

        InternalScriptRegion region = manager().get(name);
        if (region == null)
            return null;

        return addScriptRegion(region);
    }

    @Override
    public Collection<IScriptRegion> getAll() {
        return getAll(new ArrayList<IScriptRegion>(_regionMap.size()));
    }

    @Override
    public <T extends Collection<IScriptRegion>> T getAll(T output) {
        PreCon.notNull(output);

        Collection<InternalScriptRegion> regions = manager().getAll();

        for (InternalScriptRegion region : regions) {

            if (_regionMap.containsKey(region.getSearchName()))
                continue;

            addScriptRegion(region);
        }

        output.addAll(_regionMap.values());
        return output;
    }

    @Override
    public boolean remove(String name) {
        if (manager().remove(name)) {
            _regionMap.remove(name.toLowerCase());
            return true;
        }
        return false;
    }

    private IScriptRegion addScriptRegion(InternalScriptRegion region) {
        IScriptRegion scriptRegion = region.getScriptRegion();
        _regionMap.put(region.getSearchName(), scriptRegion);
        return scriptRegion;
    }

    private InternalScriptRegionManager manager() {
        return ((InternalScriptManager)Nucleus.getScriptManager()).getRegionsDirect(_plugin);
    }
}
