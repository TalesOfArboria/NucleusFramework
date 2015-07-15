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
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import java.util.UUID;

/*
 * 
 */
public class TameableAnimal implements IMobProperties {

    public static MobType getType() {
        return new MobType("tameable", Tameable.class) {

            @Override
            public MobSpecificity getSpecificity() {
                return MobSpecificity.MIXIN;
            }

            @Override
            public IMobProperties getProperties(Entity entity) {
                return new TameableAnimal(entity);
            }

            @Override
            public IMobProperties getProperties(IDataNode dataNode) {

                TameableAnimal animal = new TameableAnimal();

                try {
                    animal.deserialize(dataNode);
                } catch (DeserializeException e) {
                    return null;
                }

                return animal;
            }

            @Override
            public IMobProperties getProperties(LoreMetaMap metaMap) {
                TameableAnimal animal = new TameableAnimal();

                animal.deserialize(metaMap);

                return animal;
            }
        };
    }

    private boolean _isTamed;
    private UUID _ownerId;

    private TameableAnimal() {}

    public TameableAnimal (Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Tameable, "entity meust be an instance of Tameable");

        Tameable tameable = (Tameable)entity;

        _isTamed = tameable.isTamed();
        if (tameable.getOwner() != null)
            _ownerId = tameable.getOwner().getUniqueId();
    }

    @Override
    public String getName() {
        return "tameable";
    }

    @Override
    public boolean apply(Entity entity) {
        PreCon.notNull(entity);
        PreCon.isValid(entity instanceof Tameable, "entity meust be an instance of Tameable");

        Tameable tameable = (Tameable)entity;

        tameable.setTamed(_isTamed);

        if (_ownerId != null) {
            Player player = PlayerUtils.getPlayer(_ownerId);
            tameable.setOwner(player);
        }
        else {
            tameable.setOwner(null);
        }

        return true;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("tame", _isTamed);
        dataNode.set("owner", _ownerId);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _isTamed = dataNode.getBoolean("tame");
        _ownerId = dataNode.getUUID("owner");
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        metaMap.put("tame", String.valueOf(_isTamed));

        if (_isTamed)
            metaMap.put("owner", _ownerId.toString());
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) {
        LoreMetaItem tameItem = metaMap.get("tame");
        if (tameItem != null)
            _isTamed = tameItem.booleanValue();

        LoreMetaItem ownerItem = metaMap.get("owner");
        if (ownerItem != null)
            _ownerId = ownerItem.uuidValue();

    }
}
