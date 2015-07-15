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
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;

/*
 * 
 */
public class WolfAnimal implements IMobProperties, INamedMob {


    public static MobType getType() {
        return new MobType("wolf", Wolf.class) {

            @Override
            public MobSpecificity getSpecificity() {
                return MobSpecificity.SPECIFIC;
            }

            @Override
            public IMobProperties getProperties(Entity entity) {
                return new WolfAnimal(entity);
            }

            @Override
            public IMobProperties getProperties(IDataNode dataNode) {

                WolfAnimal animal = new WolfAnimal();

                try {
                    animal.deserialize(dataNode);
                } catch (DeserializeException e) {
                    return null;
                }

                return animal;
            }

            @Override
            public IMobProperties getProperties(LoreMetaMap metaMap) {
                WolfAnimal animal = new WolfAnimal();
                animal.deserialize(metaMap);
                return animal;
            }
        };
    }

    private boolean _isAngry;
    private boolean _isSitting;
    private DyeColor _collar;

    private WolfAnimal() {}

    public WolfAnimal(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Wolf, "org.bukkit.entity.Wolf expected.");

        Wolf wolf = (Wolf)entity;

        _isAngry = wolf.isAngry();
        _collar = wolf.getCollarColor();
        _isSitting = wolf.isSitting();
    }

    @Override
    public String getName() {
        return "wolf";
    }

    @Override
    public String getAnimalName() {
        String name = "Wolf";

        if (_collar != null && _collar != DyeColor.WHITE)
            name += " (" + TextUtils.titleCase(_collar.name()) + ')';

        return name;
    }

    @Override
    public boolean apply(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Wolf, "org.bukkit.entity.Wolf expected.");

        Wolf wolf = (Wolf)entity;

        wolf.setSitting(_isSitting);
        wolf.setAngry(_isAngry);
        wolf.setCollarColor(_collar);

        return true;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("sitting", _isSitting);
        dataNode.set("angry", _isAngry);
        dataNode.set("collar", _collar);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _isSitting = dataNode.getBoolean("sitting");
        _isAngry = dataNode.getBoolean("angry");
        _collar = dataNode.getEnum("collar", DyeColor.WHITE, DyeColor.class);
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        metaMap.put("sitting", String.valueOf(_isSitting));
        metaMap.put("angry", String.valueOf(_isAngry));
        metaMap.put("collar", _collar.name());
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) {
        LoreMetaItem sittingItem = metaMap.get("sitting");
        if (sittingItem != null)
            _isSitting = sittingItem.booleanValue();

        LoreMetaItem angryItem = metaMap.get("angry");
        if (angryItem != null)
            _isAngry = angryItem.booleanValue();

        LoreMetaItem collarItem = metaMap.get("collar");
        if (collarItem != null)
            _collar = collarItem.enumValue(DyeColor.class);
    }
}