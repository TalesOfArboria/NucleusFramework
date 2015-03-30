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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

/**
 * Represents a property of a {@link org.bukkit.Material}.
 *
 * @see Materials
 */
public class MaterialProperty implements INamedInsensitive, IPluginOwned {

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
     * The material can be interacted with by a player to create a redstone current (i.e. button or lever)
     */
    public static final MaterialProperty REDSTONE_SWITCH = new MaterialProperty("Redstone_Switch");
    /**
     * The material is a button.
     */
    public static final MaterialProperty BUTTON = new MaterialProperty("Button");
    /**
     * The material is a pressure plate.
     */
    public static final MaterialProperty PRESSURE_PLATE = new MaterialProperty("Pressure_Plate");
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
     * The material is a mining tool. (i.e shovel, pickaxe)
     */
    public static final MaterialProperty MINING_TOOL = new MaterialProperty("Mining_Tool");
    /**
     * The material is a shovel.
     */
    public static final MaterialProperty SHOVEL = new MaterialProperty("Shovel");
    /**
     * The material is a hoe.
     */
    public static final MaterialProperty HOE = new MaterialProperty("Hoe");
    /**
     * The material is an axe.
     */
    public static final MaterialProperty AXE = new MaterialProperty("Axe");
    /**
     * The material is a pickaxe.
     */
    public static final MaterialProperty PICKAXE = new MaterialProperty("Pickaxe");
    /**
     * The material is based on wood. That is that the materials primary component is wood
     * (i.e. Wooden Sword)
     */
    public static final MaterialProperty WOOD_BASED = new MaterialProperty("Wood_Based");
    /**
     * The material is based on stone or cobblestone. That is that the materials primary
     * component is stone or cobblestone. (i.e. Stone pickaxe)
     */
    public static final MaterialProperty STONE_BASED = new MaterialProperty("Stone_Based");
    /**
     * The material is based on iron. That is that the materials primary component is iron
     * (i.e. Iron Shovel)
     */
    public static final MaterialProperty IRON_BASED = new MaterialProperty("Iron_Based");
    /**
     * The material is based on gold. That is that the materials primary component is gold
     * (i.e. Gold Axe)
     */
    public static final MaterialProperty GOLD_BASED = new MaterialProperty("Gold_Based");
    /**
     * The material is based on leather. That is that the materials primary component is leather
     * (i.e. Leather leggings)
     */
    public static final MaterialProperty LEATHER_BASED = new MaterialProperty("Leather_Based");
    /**
     * The material is based on diamond. That is that the materials primary component is diamond
     * (i.e Diamond helmet).
     */
    public static final MaterialProperty DIAMOND_BASED = new MaterialProperty("Diamond_Based");
    /**
     * The material is based on quarts.
     */
    public static final MaterialProperty QUARTZ_BASED = new MaterialProperty("Quartz_Based");
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
     * The material is stairs.
     */
    public static final MaterialProperty STAIRS = new MaterialProperty("Stairs");
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
     * The material is a double block door.
     */
    public static final MaterialProperty DOOR = new MaterialProperty("Door");
    /**
     * The material is a fence gate.
     */
    public static final MaterialProperty FENCE_GATE = new MaterialProperty("Fence_Gate");
    /**
     * The material is a trap door.
     */
    public static final MaterialProperty TRAPDOOR = new MaterialProperty("Trap_Door");
    /**
     * The material is a solid surface that players can walk on.
     */
    public static final MaterialProperty SURFACE = new MaterialProperty("Surface");

    /**
     * Get a temporary {@link MaterialProperty} used to lookup a property from a map.
     *
     * @param name  The full property name.
     */
    static MaterialProperty forLookup(String name) {
        return new MaterialProperty(name);
    }

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;
    private final boolean _isDefault;

    /**
     * Private constructor for default and lookup properties.
     */
    private MaterialProperty(String name) {
        PreCon.notNull(name);

        _plugin = Nucleus.getPlugin();
        _name = name;
        _searchName = name.toLowerCase();
        _isDefault = true;
    }

    /**
     * Constructor.
     *
     * @param plugin  The properties owning plugin.
     * @param name    The name of the property, unique to the plugin.
     */
    public MaterialProperty(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        _plugin = plugin;
        _name = plugin.getName() + ':' + name;
        _searchName = _name.toLowerCase();
        _isDefault = false;
    }

    /**
     * Determine if the property is a default property.
     */
    public final boolean isDefaultProperty() {
        return _isDefault;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
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
