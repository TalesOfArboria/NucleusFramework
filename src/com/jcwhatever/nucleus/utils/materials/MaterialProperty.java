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

package com.jcwhatever.nucleus.utils.materials;

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Represents a property of a {@link org.bukkit.Material}.
 *
 * @see Materials
 */
public class MaterialProperty implements INamedInsensitive {

    /**
     * The materials byte meta data represents its color.
     */
    public static final MaterialProperty MULTI_COLOR_DATA = new MaterialProperty("Multi_Color_Data");
    /**
     * The materials byte meta data represents sub materials.
     */
    public static final MaterialProperty SUB_MATERIAL_DATA = new MaterialProperty("Sub_Material_Data");
    /**
     * The materials byte meta data determines which direction it is facing.
     */
    public static final MaterialProperty DIRECTIONAL_DATA = new MaterialProperty("Directional_Data");
    /**
     * The material is a redstone component.
     */
    public static final MaterialProperty REDSTONE = new MaterialProperty("Redstone");
    /**
     * The material is wearable.
     */
    public static final MaterialProperty WEARABLE = new MaterialProperty("Wearable");
    /**
     * The material provides damage protection.
     */
    public static final MaterialProperty ARMOR = new MaterialProperty("Armor");
    /**
     * The material is armor for a horse.
     */
    public static final MaterialProperty HORSE_ARMOR = new MaterialProperty("Horse_Armor");
    /**
     * The material is a helmet.
     */
    public static final MaterialProperty HELMET = new MaterialProperty("Helmet");
    /**
     * The material is a chest plate.
     */
    public static final MaterialProperty CHESTPLATE = new MaterialProperty("Chestplate");
    /**
     * The material is leggings.
     */
    public static final MaterialProperty LEGGINGS = new MaterialProperty("Leggings");
    /**
     * The material is boots.
     */
    public static final MaterialProperty BOOTS = new MaterialProperty("Boots");
    /**
     * The material is a weapon.
     */
    public static final MaterialProperty WEAPON = new MaterialProperty("Weapon");
    /**
     * The material is a tool.
     */
    public static final MaterialProperty TOOL = new MaterialProperty("Tool");
    /**
     * The material can be placed by a player in at least 1 game mode type.
     */
    public static final MaterialProperty PLACEABLE = new MaterialProperty("Placeable");
    /**
     * The material is a food that can be consumed.
     */
    public static final MaterialProperty FOOD = new MaterialProperty("Food");
    /**
     * The material can be thrown by a player.
     */
    public static final MaterialProperty THROWABLE = new MaterialProperty("Throwable");
    /**
     * The material has its own inventory (i.e chest)
     */
    public static final MaterialProperty INVENTORY = new MaterialProperty("Inventory");
    /**
     * The material has its own Gui.
     */
    public static final MaterialProperty GUI = new MaterialProperty("GUI");
    /**
     * The material can be crafted/cooked/etc by a player.
     */
    public static final MaterialProperty CRAFTABLE = new MaterialProperty("Craftable");
    /**
     * The material is affected by gravity and will fall if not held up by a block below.
     */
    public static final MaterialProperty GRAVITY = new MaterialProperty("Gravity");
    /**
     * The material is an ore.
     */
    public static final MaterialProperty ORE = new MaterialProperty("Ore");
    /**
     * The material is repairable/damageable.
     */
    public static final MaterialProperty REPAIRABLE = new MaterialProperty("Repairable");
    /**
     * The material is a block.
     */
    public static final MaterialProperty BLOCK = new MaterialProperty("Block");
    /**
     * The material consists of more than 1 block (i.e. door)
     */
    public static final MaterialProperty MULTI_BLOCK = new MaterialProperty("MultiBlock");
    /**
     * The material can be walked through by players. (i.e. grass)
     *
     * <p>Does not indicate that the material is see-through (i.e. glass).</p>
     */
    public static final MaterialProperty TRANSPARENT = new MaterialProperty("Transparent");
    /**
     * The material is a boundary that can be opened (i.e. door)
     */
    public static final MaterialProperty OPENABLE_BOUNDARY = new MaterialProperty("Openable_Boundary");
    /**
     * The material is a solid surface that players can walk on.
     */
    public static final MaterialProperty SURFACE = new MaterialProperty("Surface");

    private final String _name;
    private final String _searchName;

    public MaterialProperty(String name) {
        PreCon.notNullOrEmpty(name);

        _name = name;
        _searchName = name.toLowerCase();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    public int hashCode() {
        return _searchName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MaterialProperty &&
                ((MaterialProperty) obj)._searchName.equals(_searchName);
    }

    @Override
    public String toString() {
        return _name;
    }
}
