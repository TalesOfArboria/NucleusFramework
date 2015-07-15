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

package com.jcwhatever.nucleus.internal.managed.entity.mobs;

import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.AgeableAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.ColorableAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.HorseAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.IMobProperties;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.InventoryHolderAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.MobType;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.OcelotAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.PigAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.RabbitAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.SheepAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.TameableAnimal;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.WolfAnimal;
import com.jcwhatever.nucleus.managed.entity.mob.IMobSerializer;
import com.jcwhatever.nucleus.managed.entity.mob.ISerializableMob;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.loremeta.LoreMetaMap;
import com.jcwhatever.nucleus.utils.managers.NamedManager;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IMobSerializer}.
 */
public class InternalMobSerializer extends NamedManager<MobType> implements IMobSerializer {

    private Map<Class<? extends Entity>, List<MobType>> _cachedTypes
            = new HashMap<>(20);

    public InternalMobSerializer() {
        add(HorseAnimal.getType());
        add(OcelotAnimal.getType());
        add(PigAnimal.getType());
        add(SheepAnimal.getType());
        add(TameableAnimal.getType());
        add(WolfAnimal.getType());
        add(RabbitAnimal.getType());

        add(AgeableAnimal.getType());
        add(ColorableAnimal.getType());
        add(TameableAnimal.getType());
        add(InventoryHolderAnimal.getType());
    }

    @Nullable
    @Override
    public ISerializableMob getSerializable(Entity entity) {
        PreCon.notNull(entity);

        return new Mob(entity, getProperties(entity));
    }

    @Nullable
    @Override
    public ISerializableMob deserialize(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        LoreMetaMap metaMap = new LoreMetaMap(itemStack);

        Mob mob = new Mob();

        try {
            mob.deserialize(metaMap);
        }
        catch (DeserializeException e) {
            return null;
        }

        return mob;
    }

    @Nullable
    @Override
    public ISerializableMob deserialize(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        Mob mob = new Mob();

        try {
            mob.deserialize(dataNode);
        } catch (DeserializeException e) {
            return null;
        }
        return mob;
    }

    public List<IMobProperties> getProperties(Entity entity) {
        PreCon.notNull(entity);

        List<MobType> types = _cachedTypes.get(entity.getClass());

        if (types == null) {
            types = new ArrayList<>(5);
            Collection<MobType> allTypes = getAll();

            for (MobType type : allTypes) {
                if (type.getEntityClass().isAssignableFrom(entity.getClass())) {
                    types.add(type);
                }
            }

            Collections.sort(types);
            _cachedTypes.put(entity.getClass(), types);
        }

        List<IMobProperties> results = new ArrayList<>(types.size());
        for (MobType type : types) {
            results.add(type.getProperties(entity));
        }

        return results;
    }

    public List<IMobProperties> deserializeProperties(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        List<String> types = dataNode.getStringList("types", null);
        List<IMobProperties> results;

        if (types != null) {
            results = new ArrayList<>(types.size());

            List<MobType> animalTypes = new ArrayList<>(types.size());

            for (String typeName : types) {

                MobType type = get(typeName);
                if (type == null)
                    continue;

                animalTypes.add(type);
            }

            Collections.sort(animalTypes);

            for (MobType type : animalTypes) {

                IDataNode typeNode = dataNode.getNode(type.getName());
                IMobProperties properties = type.getProperties(typeNode);

                results.add(properties);
            }
        }
        else {
            results = new ArrayList<>(0);
        }

        return results;
    }
}
