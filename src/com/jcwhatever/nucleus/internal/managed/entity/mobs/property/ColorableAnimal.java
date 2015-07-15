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
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.material.Colorable;

/*
 * 
 */
public class ColorableAnimal implements IMobProperties {

    public static MobType getType() {
        return new MobType("colorable", Colorable.class) {

            @Override
            public MobSpecificity getSpecificity() {
                return MobSpecificity.MIXIN;
            }

            @Override
            public IMobProperties getProperties(Entity entity) {
                return new ColorableAnimal(entity);
            }

            @Override
            public IMobProperties getProperties(IDataNode dataNode) {

                ColorableAnimal animal = new ColorableAnimal();

                try {
                    animal.deserialize(dataNode);
                } catch (DeserializeException e) {
                    return null;
                }

                return animal;
            }

            @Override
            public IMobProperties getProperties(LoreMetaMap metaMap) {
                ColorableAnimal animal = new ColorableAnimal();
                animal.deserialize(metaMap);
                return animal;
            }
        };
    }


    private DyeColor _color;

    private ColorableAnimal() {}

    public ColorableAnimal(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Colorable, "org.bukkit.material.Colorable expected.");

        Colorable colorable = (Colorable)entity;

        _color = colorable.getColor();
    }

    @Override
    public String getName() {
        return "colorable";
    }

    @Override
    public boolean apply(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Colorable, "org.bukkit.material.Colorable expected.");

        Colorable colorable = (Colorable)entity;

        colorable.setColor(_color);

        return true;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("color", _color);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _color = dataNode.getEnum("color", DyeColor.WHITE, DyeColor.class);
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        metaMap.put("color", _color.name());
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) {
        LoreMetaItem colorItem = metaMap.get("color");
        if (colorItem != null)
            _color = colorItem.enumValue(DyeColor.class);
    }
}
