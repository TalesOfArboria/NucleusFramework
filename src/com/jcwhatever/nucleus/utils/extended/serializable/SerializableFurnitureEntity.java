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


package com.jcwhatever.nucleus.utils.extended.serializable;

import com.jcwhatever.nucleus.utils.file.NucleusByteReader;
import com.jcwhatever.nucleus.utils.file.NucleusByteWriter;
import com.jcwhatever.nucleus.utils.file.IBinarySerializable;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.util.EulerAngle;

import java.io.IOException;

/**
 * Used to serialize entities that are considered part of structures, much like blocks.
 *
 * <p>Cannot create a {@link Entity} instance but instead retains the information
 * necessary to apply the data to the original block location.</p>
 *
 * <p>Supported Entity Interfaces:</p>
 *
 * <ul>
 *    <li>{@link Directional}</li>
 *    <li>{@link Painting}</li>
 *    <li>{@link ItemFrame}</li>
 *    <li>{@link ArmorStand}</li>
 * </ul>
 */
public class SerializableFurnitureEntity implements IBinarySerializable {

    /**
     * Determine if an entity is a furniture entity
     *
     * @param entity  The entity to check.
     */
    public static boolean isFurnitureEntity(Entity entity) {
        return entity instanceof Painting ||
                entity instanceof ItemFrame ||
                entity instanceof ArmorStand;
    }

    /**
     * Get classes used to detect a furniture entity.
     */
    public static Class<?>[] getFurnitureClasses() {
        return new Class<?>[] {
                Painting.class,
                ItemFrame.class,
                ArmorStand.class
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

    // ArmorStand
    private boolean _hasArmorStand;
    private ItemStack _itemInHand;
    private ItemStack _helmet;
    private ItemStack _chestplate;
    private ItemStack _leggings;
    private ItemStack _boots;
    private EulerAngle _headPose;
    private EulerAngle _bodyPose;
    private EulerAngle _leftArmPose;
    private EulerAngle _rightArmPose;
    private EulerAngle _leftLegPose;
    private EulerAngle _rightLegPose;
    private boolean _hasArmorBasePlate;
    private boolean _hasArmorGravity;
    private boolean _isArmorVisible;
    private boolean _hasArmorArms;
    private boolean _isArmorSmall;




    /**
     * Constructor.
     *
     * <p>Required by {@code NucleusByteReader} to deserialize.</p>
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

        if (entity instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand)entity;

            _hasArmorStand = true;
            _itemInHand = stand.getItemInHand();
            _helmet = stand.getHelmet();
            _chestplate = stand.getChestplate();
            _leggings = stand.getLeggings();
            _boots = stand.getBoots();
            _headPose = stand.getHeadPose();
            _bodyPose = stand.getBodyPose();
            _leftArmPose = stand.getLeftArmPose();
            _rightArmPose = stand.getRightArmPose();
            _leftLegPose = stand.getLeftLegPose();
            _rightLegPose = stand.getRightLegPose();
            _hasArmorBasePlate = stand.hasBasePlate();
            _hasArmorGravity = stand.hasGravity();
            _isArmorVisible = stand.isVisible();
            _hasArmorArms = stand.hasArms();
            _isArmorSmall = stand.isSmall();
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

        if (entity instanceof ArmorStand && _hasArmorStand) {
            ArmorStand stand = (ArmorStand)entity;

             stand.setItemInHand(_itemInHand);
             stand.setHelmet(_helmet);
             stand.setChestplate(_chestplate);
             stand.setLeggings(_leggings);
             stand.setBoots(_boots);
             stand.setHeadPose(_headPose);
             stand.setBodyPose(_bodyPose);
             stand.setLeftArmPose(_leftArmPose);
             stand.setRightArmPose(_rightArmPose);
             stand.setLeftLegPose(_leftLegPose);
             stand.setRightLegPose(_rightLegPose);
             stand.setBasePlate(_hasArmorBasePlate);
             stand.setGravity(_hasArmorGravity);
             stand.setVisible(_isArmorVisible);
             stand.setArms(_hasArmorArms);
             stand.setSmall(_isArmorSmall);
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
    public void serializeToBytes(NucleusByteWriter writer) throws IOException {

        writer.write(_location);
        writer.write(_type);

        boolean hasDirection = _facing != null;
        boolean hasArt = _art != null;
        boolean hasItemFrame = _frameItem != null;

        writer.write(hasDirection);
        writer.write(hasArt);
        writer.write(hasItemFrame);
        writer.write(_hasArmorStand);

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

        if (_hasArmorStand) {
            writer.write(_hasArmorBasePlate);
            writer.write(_hasArmorGravity);
            writer.write(_isArmorVisible);
            writer.write(_hasArmorArms);
            writer.write(_isArmorSmall);
            writer.write(_itemInHand);
            writer.write(_helmet);
            writer.write(_chestplate);
            writer.write(_leggings);
            writer.write(_boots);
            writer.write(_headPose);
            writer.write(_bodyPose);
            writer.write(_leftArmPose);
            writer.write(_rightArmPose);
            writer.write(_leftLegPose);
            writer.write(_rightLegPose);
        }
    }

    @Override
    public void deserializeFromBytes(NucleusByteReader reader) throws IOException, ClassNotFoundException {

        _location = reader.getLocation();
        _type = reader.getEnum(EntityType.class);

        boolean hasDirection = reader.getBoolean();
        boolean hasArt = reader.getBoolean();
        boolean hasItemFrame = reader.getBoolean();
        _hasArmorStand = reader.getBoolean();

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

        if (_hasArmorStand) {
            _hasArmorBasePlate = reader.getBoolean();
            _hasArmorGravity = reader.getBoolean();
            _isArmorVisible = reader.getBoolean();
            _hasArmorArms = reader.getBoolean();
            _isArmorSmall = reader.getBoolean();
            _itemInHand = reader.getItemStack();
            _helmet = reader.getItemStack();
            _chestplate = reader.getItemStack();
            _leggings = reader.getItemStack();
            _boots = reader.getItemStack();
            _headPose = reader.getEulerAngle();
            _bodyPose = reader.getEulerAngle();
            _leftArmPose = reader.getEulerAngle();
            _rightArmPose = reader.getEulerAngle();
            _leftLegPose = reader.getEulerAngle();
            _rightLegPose = reader.getEulerAngle();
        }
    }
}
