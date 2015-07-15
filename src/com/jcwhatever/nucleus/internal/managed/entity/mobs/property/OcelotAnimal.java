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

package com.jcwhatever.nucleus.internal.managed.entity.mobs.property;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.loremeta.LoreMetaItem;
import com.jcwhatever.nucleus.utils.items.loremeta.LoreMetaMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;

/*
 * 
 */
public class OcelotAnimal implements IMobProperties, INamedMob {

    public static MobType getType() {
        return new MobType("ocelot", Ocelot.class) {

            @Override
            public MobSpecificity getSpecificity() {
                return MobSpecificity.SPECIFIC;
            }

            @Override
            public IMobProperties getProperties(Entity entity) {
                return new OcelotAnimal(entity);
            }

            @Override
            public IMobProperties getProperties(IDataNode dataNode) {

                OcelotAnimal animal = new OcelotAnimal();

                try {
                    animal.deserialize(dataNode);
                } catch (DeserializeException e) {
                    return null;
                }

                return animal;
            }

            @Override
            public IMobProperties getProperties(LoreMetaMap metaMap) {
                OcelotAnimal animal = new OcelotAnimal();
                animal.deserialize(metaMap);
                return animal;
            }
        };
    }

    private Type _type;
    private boolean _isSitting;

    private OcelotAnimal() {}

    public OcelotAnimal(Entity entity) {
        PreCon.isValid(entity instanceof Ocelot, "org.bukkit.entity.Ocelot expected.");

        Ocelot ocelot = (Ocelot)entity;

        _type = ocelot.getCatType();
        _isSitting = ocelot.isSitting();
    }

    @Override
    public String getName() {
        return "ocelot";
    }

    @Override
    public String getAnimalName() {
        switch (_type) {
            case BLACK_CAT:
                return "Black Cat";
            case RED_CAT:
                return "Red Cat";
            case SIAMESE_CAT:
                return "Siamese Cat";
            case WILD_OCELOT:
                return "Wild Ocelot";
            default:
                return "Ocelot";
        }
    }

    @Override
    public boolean apply(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Ocelot, "org.bukkit.entity.Ocelot expected.");

        Ocelot ocelot = (Ocelot)entity;

        ocelot.setCatType(_type);
        ocelot.setSitting(_isSitting);

        return true;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("cat-type", _type);
        dataNode.set("sitting", _isSitting);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _type = dataNode.getEnum("cat-type", Type.WILD_OCELOT, Type.class);
        _isSitting = dataNode.getBoolean("sitting");
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        metaMap.put("cat-type", _type.name());
        metaMap.put("sitting", String.valueOf(_isSitting));
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) {
        LoreMetaItem typeItem = metaMap.get("cat-type");
        if (typeItem != null)
            _type = typeItem.enumValue(Type.class);

        LoreMetaItem sittingItem = metaMap.get("sitting");
        if (sittingItem != null)
            _isSitting = sittingItem.booleanValue();
    }
}
