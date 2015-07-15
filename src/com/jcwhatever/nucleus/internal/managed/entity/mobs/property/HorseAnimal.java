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
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;

/*
 * 
 */
public class HorseAnimal implements IMobProperties, INamedMob {


    public static MobType getType() {
        return new MobType("horse", Horse.class) {

            @Override
            public MobSpecificity getSpecificity() {
                return MobSpecificity.SPECIFIC;
            }

            @Override
            public IMobProperties getProperties(Entity entity) {
                return new HorseAnimal(entity);
            }

            @Override
            public IMobProperties getProperties(IDataNode dataNode) {

                HorseAnimal animal = new HorseAnimal();

                try {
                    animal.deserialize(dataNode);
                } catch (DeserializeException e) {
                    return null;
                }

                return animal;
            }

            @Override
            public IMobProperties getProperties(LoreMetaMap metaMap) {
                HorseAnimal animal = new HorseAnimal();
                animal.deserialize(metaMap);
                return animal;
            }
        };
    }

    private Color _color;
    private Variant _variant;
    private Style _style;
    private int _domestication;
    private int _maxDomestication;
    private boolean _hasChest;
    private double _jumpStrength;

    private HorseAnimal() {
    }

    public HorseAnimal(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Horse, "org.bukkit.entity.Horse expected.");

        Horse horse = (Horse) entity;

        _color = horse.getColor();
        _variant = horse.getVariant();
        _style = horse.getStyle();
        _domestication = horse.getDomestication();
        _maxDomestication = horse.getMaxDomestication();
        _hasChest = horse.isCarryingChest();
        _jumpStrength = horse.getJumpStrength();
    }

    @Override
    public String getName() {
        return "horse";
    }

    @Override
    public String getAnimalName() {
        switch (_variant) {
            case MULE:
                return "Mule";
            case DONKEY:
                return "Donkey";
            case SKELETON_HORSE:
                return "Skeleton Horse";
            case UNDEAD_HORSE:
                return "Undead Horse";
            default:
                return "Horse";
        }
    }

    @Override
    public boolean apply(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Horse, "org.bukkit.entity.Horse expected.");

        Horse horse = (Horse) entity;

        horse.setColor(_color);
        horse.setVariant(_variant);
        horse.setStyle(_style);
        horse.setMaxDomestication(_maxDomestication);
        horse.setDomestication(_domestication);
        horse.setCarryingChest(_hasChest);
        horse.setJumpStrength(_jumpStrength);
        return true;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("color", _color);
        dataNode.set("variant", _variant);
        dataNode.set("style", _style);
        dataNode.set("dom", _domestication);
        dataNode.set("max-dom", _maxDomestication);
        dataNode.set("chest", _hasChest);
        dataNode.set("jump", _jumpStrength);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _color = dataNode.getEnum("color", Color.WHITE, Color.class);
        _variant = dataNode.getEnum("variant", Variant.HORSE, Variant.class);
        _style = dataNode.getEnum("style", Style.NONE, Style.class);
        _domestication = dataNode.getInteger("dom");
        _maxDomestication = dataNode.getInteger("max-dom");
        _hasChest = dataNode.getBoolean("chest");
        _jumpStrength = dataNode.getDouble("jump", 1.0D);
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        metaMap.put("color", _color.name());
        metaMap.put("variant", _variant.name());
        metaMap.put("style", _style.name());
        metaMap.put("dom", String.valueOf(_domestication));
        metaMap.put("max-dom", String.valueOf(_maxDomestication));
        metaMap.put("chest", String.valueOf(_hasChest));
        metaMap.put("jump", String.valueOf(_jumpStrength));
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) {

        LoreMetaItem colorItem = metaMap.get("color");
        if (colorItem != null)
            _color = colorItem.enumValue(Color.class);

        LoreMetaItem variantItem = metaMap.get("variant");
        if (variantItem != null)
            _variant = variantItem.enumValue(Variant.class);

        LoreMetaItem styleItem = metaMap.get("style");
        if (styleItem != null)
            _style = styleItem.enumValue(Style.class);

        LoreMetaItem domItem = metaMap.get("dom");
        if (domItem != null)
            _domestication = domItem.intValue();

        LoreMetaItem maxDomItem = metaMap.get("max-dom");
        if (maxDomItem != null)
            _maxDomestication = maxDomItem.intValue();

        LoreMetaItem chestItem = metaMap.get("chest");
        if (chestItem != null)
            _hasChest = chestItem.booleanValue();

        LoreMetaItem jumpItem = metaMap.get("jump");
        if (jumpItem != null)
            _jumpStrength = jumpItem.doubleValue();
    }
}
