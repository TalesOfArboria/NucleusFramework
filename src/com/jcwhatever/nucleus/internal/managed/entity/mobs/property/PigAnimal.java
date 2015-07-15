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
import org.bukkit.entity.Pig;

/*
 * 
 */
public class PigAnimal implements IMobProperties, INamedMob {

    public static MobType getType() {
        return new MobType("pig", Pig.class) {

            @Override
            public MobSpecificity getSpecificity() {
                return MobSpecificity.SPECIFIC;
            }

            @Override
            public IMobProperties getProperties(Entity entity) {
                return new PigAnimal(entity);
            }

            @Override
            public IMobProperties getProperties(IDataNode dataNode) {

                PigAnimal animal = new PigAnimal();

                try {
                    animal.deserialize(dataNode);
                } catch (DeserializeException e) {
                    return null;
                }

                return animal;
            }

            @Override
            public IMobProperties getProperties(LoreMetaMap metaMap) {
                PigAnimal animal = new PigAnimal();
                animal.deserialize(metaMap);
                return animal;
            }
        };
    }

    private boolean _hasSaddle;

    private PigAnimal() {}

    public PigAnimal(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Pig, "org.bukkit.entity.Pig expected.");

        Pig pig = (Pig)entity;

        _hasSaddle = pig.hasSaddle();
    }

    @Override
    public String getName() {
        return "pig";
    }

    @Override
    public String getAnimalName() {
        return _hasSaddle ? "Saddled Pig" : "Pig";
    }

    @Override
    public boolean apply(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Pig, "org.bukkit.entity.Pig expected.");

        Pig pig = (Pig)entity;

        pig.setSaddle(_hasSaddle);

        return true;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("saddle", _hasSaddle);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _hasSaddle = dataNode.getBoolean("saddle");
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        metaMap.put("saddle", String.valueOf(_hasSaddle));
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) {

        LoreMetaItem saddleItem = metaMap.get("saddle");
        if (saddleItem != null)
            _hasSaddle = saddleItem.booleanValue();
    }
}
