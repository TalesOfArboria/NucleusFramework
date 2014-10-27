/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.extended.serializable;

import com.jcwhatever.bukkit.generic.file.GenericsByteReader;
import com.jcwhatever.bukkit.generic.file.GenericsByteWriter;
import com.jcwhatever.bukkit.generic.file.IGenericsSerializable;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;

import java.io.IOException;

/**
 * Used to serialize entities that are considered part of structures, much like blocks.
 *
 * <p>Cannot create a {@code BlockState} instance but instead retains the information
 * necessary to apply the data to the original block location.</p>
 *
 * <p>Supported Entity Interfaces:</p>
 *
 * <p>{@code Directional}</p>
 * <p>{@code Painting}</p>
 * <p>{@code ItemFrame}</p>
 */
public class SerializableFurnitureEntity implements IGenericsSerializable {

    /**
     * Determine if an entity is a furniture entity
     *
     * @param entity  The entity to check.
     */
    public static boolean isFurnitureEntity(Entity entity) {
        return entity instanceof Painting ||
                entity instanceof ItemFrame;
    }

    /**
     * Get classes used to detect a furniture entity.
     */
    public static Class<?>[] getFurnitureClasses() {
        return new Class<?>[] {
                Painting.class,
                ItemFrame.class
        };
    }

    boolean _canSerialize;

    private Location _location;
    private EntityType _type;

    // Directional
    private BlockFace _facing;

    // Painting
    private Art _art;

    // ItemFrame
    private ItemStack _frameItem;
    private Rotation _frameRotation;

    /**
     * Constructor.
     *
     * <p>Required by {@code GenericsByteReader} to deserialize.</p>
     */
    public SerializableFurnitureEntity() {}

    /**
     * Constructor.
     *
     * @param entity  The {@code Entity} that needs to be serialized.
     */
    public SerializableFurnitureEntity(Entity entity) {

        _location = entity.getLocation();
        _type = entity.getType();

        if (entity instanceof Directional) {
            Directional directional = (Directional)entity;

            _facing = directional.getFacing();
            _canSerialize = true;
        }

        if (entity instanceof Painting) {
            Painting painting = (Painting)entity;

            _art = painting.getArt();
            _canSerialize = true;
        }

        if (entity instanceof ItemFrame) {
            ItemFrame frame = (ItemFrame)entity;

            _frameItem = frame.getItem();
            _frameRotation = frame.getRotation();
        }
    }

    /**
     * Get the stored entity location.
     */
    public Location getLocation() {
        return _location;
    }

    /**
     * Get the type of the stored entity.
     */
    public EntityType getType() {
        return _type;
    }

    /**
     * Apply the stored {@code Entity} data to the
     * supplied {@code Entity} object.
     */
    public void apply(Entity entity) {

        entity.teleport(_location);

        if (entity instanceof Directional && _facing != null) {

            Directional directional = (Directional)entity;
            directional.setFacingDirection(_facing);
        }

        if (entity instanceof Painting && _art != null) {
            Painting painting = (Painting)entity;

            painting.setArt(_art);
        }

        if (entity instanceof ItemFrame && _frameItem != null) {
            ItemFrame frame = (ItemFrame)entity;

            frame.setItem(_frameItem);
            frame.setRotation(_frameRotation);
        }
    }

    /**
     * Spawn the entity in its original location.
     */
    public boolean spawn() {

        if (_facing == null)
            return false;

        Block block = getLocation().getBlock().getRelative(_facing.getOppositeFace());

        Entity entity = getLocation().getWorld().spawnEntity(block.getLocation(), getType());

        apply(entity);

        return true;
    }

    /**
     * True if the Entity represented can be serialized.
     */
    public boolean canSerialize() {
        return _canSerialize;
    }

    @Override
    public void serializeToBytes(GenericsByteWriter writer) throws IOException {

        writer.write(_location);
        writer.write(_type);

        boolean hasDirection = _facing != null;
        boolean hasArt = _art != null;
        boolean hasItemFrame = _frameItem != null;

        writer.write(hasDirection);
        writer.write(hasArt);
        writer.write(hasItemFrame);

        if (hasDirection) {
            writer.write(_facing);
        }

        if (hasArt) {
            writer.write(_art);
        }

        if (hasItemFrame) {

            writer.write(_frameItem);
            writer.write(_frameRotation);
        }
    }

    @Override
    public void deserializeFromBytes(GenericsByteReader reader) throws IOException, ClassNotFoundException {

        _location = reader.getLocation();
        _type = reader.getEnum(EntityType.class);

        boolean hasDirection = reader.getBoolean();
        boolean hasArt = reader.getBoolean();
        boolean hasItemFrame = reader.getBoolean();

        if (hasDirection) {
            _facing = reader.getEnum(BlockFace.class);
        }

        if (hasArt) {
            _art = reader.getEnum(Art.class);
        }

        if (hasItemFrame) {
            _frameItem = reader.getItemStack();
            _frameRotation = reader.getEnum(Rotation.class);
        }
    }
}
