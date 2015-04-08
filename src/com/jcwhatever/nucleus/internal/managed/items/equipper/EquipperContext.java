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

package com.jcwhatever.nucleus.internal.managed.items.equipper;

import com.jcwhatever.nucleus.managed.items.equipper.IEquipper;
import com.jcwhatever.nucleus.managed.items.equipper.IEquipperContext;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Internal implementation of {@link IEquipperContext}.
 */
class EquipperContext implements IEquipperContext {

    private final Plugin _plugin;
    private final Map<EntityType, IEquipper> _equippers = new EnumMap<>(EntityType.class);
    private IEquipper _defaultEquipper;

    /**
     * Constructor.
     */
    public EquipperContext(Plugin plugin, IEquipper defaultEquipper) {
        PreCon.notNull(plugin);
        PreCon.notNull(defaultEquipper);

        _plugin = plugin;
        _defaultEquipper = defaultEquipper;

        setEquipper(EntityType.HORSE, new HorseEquipper());
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public void setDefaultEquipper(IEquipper equipper) {
        PreCon.notNull(equipper);

        _defaultEquipper = equipper;
    }

    @Override
    public void setEquipper(EntityType type, @Nullable IEquipper equipper) {
        PreCon.notNull(type);

        if (equipper == null) {
            _equippers.remove(type);
        }
        else {
            _equippers.put(type, equipper);
        }
    }

    @Override
    public void clearEquipper(EntityType type) {
        PreCon.notNull(type);
        _equippers.remove(type);
    }

    @Override
    public IEquipper getEquipper(EntityType type) {
        PreCon.notNull(type);

        IEquipper equipper = _equippers.get(type);
        return equipper == null ? _defaultEquipper : equipper;
    }
}
