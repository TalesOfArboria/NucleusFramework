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
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

/*
 * 
 */
public class AgeableAnimal implements IMobProperties {

    public static MobType getType() {
        return new MobType("ageable", Ageable.class) {

            @Override
            public MobSpecificity getSpecificity() {
                return MobSpecificity.MIXIN;
            }

            @Override
            public IMobProperties getProperties(Entity entity) {
                return new AgeableAnimal(entity);
            }

            @Override
            public IMobProperties getProperties(IDataNode dataNode) {

                AgeableAnimal animal = new AgeableAnimal();

                try {
                    animal.deserialize(dataNode);
                } catch (DeserializeException e) {
                    return null;
                }

                return animal;
            }

            @Override
            public IMobProperties getProperties(LoreMetaMap metaMap) {
                AgeableAnimal animal = new AgeableAnimal();
                animal.deserialize(metaMap);
                return animal;
            }
        };
    }

    private int _age;
    private boolean _ageLock;

    private AgeableAnimal() {}

    public AgeableAnimal(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Ageable, "org.bukkit.entity.Ageable expected.");

        Ageable ageable = (Ageable)entity;

        _age = ageable.getAge();
        _ageLock = ageable.getAgeLock();
    }

    @Override
    public String getName() {
        return "ageable";
    }

    @Override
    public boolean apply(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Ageable, "org.bukkit.entity.Ageable expected.");

        Ageable ageable = (Ageable)entity;

        ageable.setAge(_age);
        ageable.setAgeLock(_ageLock);

        return true;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("age", _age);
        dataNode.set("age-lock", _ageLock);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _age = dataNode.getInteger("age");
        _ageLock = dataNode.getBoolean("age-lock");
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        metaMap.put("age", String.valueOf(_age));
        metaMap.put("age-lock", String.valueOf(_ageLock));
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) {
        LoreMetaItem ageItem = metaMap.get("age");
        if (ageItem != null)
            _age = ageItem.intValue();

        LoreMetaItem lockItem = metaMap.get("age-lock");
        if (lockItem != null)
            _ageLock = lockItem.booleanValue();
    }
}