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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.IMobProperties;
import com.jcwhatever.nucleus.internal.managed.entity.mobs.property.MobType;
import com.jcwhatever.nucleus.managed.entity.mob.ISerializableMob;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.loremeta.LoreMetaItem;
import com.jcwhatever.nucleus.utils.items.loremeta.LoreMetaMap;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of {@link ISerializableMob}.
 */
class Mob implements ISerializableMob {

    private EntityType _type;
    private List<IMobProperties> _properties;
    private String _customName;

    /**
     * Private constructor required by {@link IDataNodeSerializable}.
     */
    Mob() {}

    /**
     * Constructor.
     *
     * @param entity  The entity to store information from.
     */
    public Mob(Entity entity, List<IMobProperties> properties) {
        PreCon.notNull(entity);

        _type = entity.getType();
        _customName = entity.getCustomName();
        _properties = properties;
    }


    @Nullable
    @Override
    public Entity spawn(Location location) {
        PreCon.notNull(location);

        Entity entity = location.getWorld().spawn(location, _type.getEntityClass());

        for (IMobProperties ap : _properties) {
            ap.apply(entity);
        }

        return entity;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("type", _type);
        dataNode.set("name", _customName);

        List<String> typeNames = new ArrayList<>(_properties.size());

        for (IMobProperties type : _properties) {
            typeNames.add(type.getName());
            IDataNode typeNode = dataNode.getNode(type.getName());
            type.serialize(typeNode);
        }

        dataNode.set("types", typeNames);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {

        _type = dataNode.getEnum("type", EntityType.class);
        if (_type == null)
            throw new DeserializeException("type value not found");

        _customName = dataNode.getString("name");
        if (_customName == null)
            throw new DeserializeException("name value not found");

        _properties = serializer().deserializeProperties(dataNode);
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        metaMap.set("type", _type.name());
        if (_customName != null)
            metaMap.set("name", _customName);


        for (IMobProperties type : _properties) {
            type.serialize(metaMap);
        }
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) throws DeserializeException {

        _properties = new ArrayList<>(5);

        LoreMetaItem typeItem = metaMap.get("type");
        if (typeItem == null)
            throw new DeserializeException("Unable to deserialize from meta. Missing mob type.");

        _type = typeItem.enumValue(EntityType.class);

        LoreMetaItem customName = metaMap.get("name");
        if (customName != null)
            _customName = customName.getValue();

        Collection<MobType> mobType = serializer().getAll();

        for (MobType type : mobType) {
            if (type.getEntityClass().isAssignableFrom(_type.getEntityClass())) {
                _properties.add(type.getProperties(metaMap));
            }
        }
    }

    private InternalMobSerializer serializer() {
        return (InternalMobSerializer)Nucleus.getMobSerializer();
    }
}
