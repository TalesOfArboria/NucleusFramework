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

package com.jcwhatever.nucleus.utils.items.equipper;

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Manages Equipper implementations.
 */
public class EntityEquipperManager {

    private final IEntityEquipper DEFAULT_EQUIPPER = new DefaultEquipper();
    private final Map<EntityType, IEntityEquipper> _equippers = new EnumMap<>(EntityType.class);

    /**
     * Constructor.
     */
    public EntityEquipperManager() {
        registerEquipper(EntityType.HORSE, new HorseEquipper());
    }

    /**
     * Register an equipper instance.
     *
     * @param type      The entity type the equipper is for.
     * @param equipper  The equipper or null to remove.
     */
    public void registerEquipper(EntityType type, @Nullable IEntityEquipper equipper) {
        PreCon.notNull(type);

        if (equipper == null) {
            _equippers.remove(type);
        }
        else {
            _equippers.put(type, equipper);
        }
    }

    /**
     * Get an equipper for the specified entity type.
     *
     * @param type  The entity type.
     */
    public IEntityEquipper getEquipper(EntityType type) {
        PreCon.notNull(type);

        IEntityEquipper equipper = _equippers.get(type);
        return equipper == null ? DEFAULT_EQUIPPER : equipper;
    }
}
