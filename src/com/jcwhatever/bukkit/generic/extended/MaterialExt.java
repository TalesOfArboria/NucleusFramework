/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.extended;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps extra properties to Bukkits Material enum and
 * provides a way to get a list of materials based
 * on those properties.
 */
public enum MaterialExt {

    UNKNOWN               (null),

    ACACIA_DOOR           (Material.ACACIA_DOOR,             MP.OPENABLE_BOUNDARY, MP.MULTI_BLOCK, MP.REDSTONE),
    ACACIA_DOOR_ITEM      (Material.ACACIA_DOOR_ITEM,        MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    ACACIA_FENCE          (Material.ACACIA_FENCE,            MP.PLACEABLE, MP.CRAFTABLE),
    ACACIA_FENCE_GATE     (Material.ACACIA_FENCE_GATE,       MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    ACACIA_STAIRS         (Material.ACACIA_STAIRS,           MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    ACTIVATOR_RAIL        (Material.ACTIVATOR_RAIL,          MP.TRANSPARENT, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    AIR			          (Material.AIR,                     MP.TRANSPARENT),
    ANVIL                 (Material.ANVIL,                   MP.PLACEABLE, MP.GUI, MP.CRAFTABLE, MP.SURFACE),
    APPLE                 (Material.APPLE),
    ARMOR_STAND           (Material.ARMOR_STAND,             MP.CRAFTABLE, MP.PLACEABLE), // item
    ARROW                 (Material.ARROW,                   MP.THROWABLE, MP.CRAFTABLE),
    BAKED_POTATOE         (Material.BAKED_POTATO,            MP.CRAFTABLE),
    BANNER                (Material.BANNER,                  MP.CRAFTABLE, MP.PLACEABLE), // item
    BEACON                (Material.BEACON,                  MP.PLACEABLE, MP.GUI, MP.CRAFTABLE, MP.SURFACE),
    BED                   (Material.BED,                     MP.PLACEABLE, MP.CRAFTABLE),
    BED_BLOCK             (Material.BED_BLOCK,               MP.SURFACE),
    BEDROCK               (Material.BEDROCK,                 MP.PLACEABLE, MP.SURFACE),
    BIRCH_DOOR            (Material.BIRCH_DOOR,              MP.OPENABLE_BOUNDARY, MP.MULTI_BLOCK, MP.REDSTONE),
    BIRCH_DOOR_ITEM       (Material.BIRCH_DOOR_ITEM,         MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    BIRCH_FENCE           (Material.BIRCH_FENCE,             MP.PLACEABLE, MP.CRAFTABLE),
    BIRCH_FENCE_GATE      (Material.BIRCH_FENCE_GATE,        MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    BIRCH_WOOD_STAIRS     (Material.BIRCH_WOOD_STAIRS,       MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    BLAZE_POWDER          (Material.BLAZE_POWDER,            MP.CRAFTABLE),
    BLAZE_ROD             (Material.BLAZE_POWDER),
    BOAT                  (Material.BOAT,                    MP.CRAFTABLE),
    BONE                  (Material.BONE),
    BOOK                  (Material.BOOK,                    MP.CRAFTABLE),
    BOOK_AND_QUILL        (Material.BOOK_AND_QUILL,          MP.CRAFTABLE),
    BOOKSHELF             (Material.BOOKSHELF,               MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    BOW                   (Material.BOW,                     MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
    BOWL                  (Material.BOWL,                    MP.CRAFTABLE),
    BREAD                 (Material.BREAD,                   MP.CRAFTABLE),
    BREWING_STAND         (Material.BREWING_STAND,           MP.GUI),
    BREWING_STAND_ITEM    (Material.BREWING_STAND_ITEM,      MP.PLACEABLE),
    BRICK                 (Material.BRICK,                   MP.CRAFTABLE),
    BRICK_STAIRS          (Material.BRICK_STAIRS,            MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    BROWN_MUSHROOM        (Material.BROWN_MUSHROOM,          MP.TRANSPARENT, MP.PLACEABLE),
    BUCKET                (Material.BUCKET,                  MP.CRAFTABLE),
    BURNING_FURNACE       (Material.BURNING_FURNACE,         MP.SURFACE),
    CACTUS                (Material.CACTUS,                  MP.PLACEABLE),
    CAKE                  (Material.CAKE,                    MP.PLACEABLE, MP.CRAFTABLE),
    CAKE_BLOCK            (Material.CAKE_BLOCK),
    CARPET                (Material.CARPET,                  MP.TRANSPARENT, MP.MULTICOLOR, MP.PLACEABLE, MP.CRAFTABLE),
    CARROT                (Material.CARROT),
    CARROT_ITEM           (Material.CARROT_ITEM,             MP.PLACEABLE),
    CARROT_STICK          (Material.CARROT_STICK,            MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    CAULDRON              (Material.CAULDRON),
    CAULDRON_ITEM         (Material.CAULDRON_ITEM,           MP.PLACEABLE, MP.CRAFTABLE),
    CHAINMAIL_BOOTS       (Material.CHAINMAIL_BOOTS,         MP.REPAIRABLE, MP.WEARABLE),
    CHAINMAIL_CHESTPLATE  (Material.CHAINMAIL_CHESTPLATE,    MP.REPAIRABLE, MP.WEARABLE),
    CHAINMAIL_HELMET      (Material.CHAINMAIL_HELMET,        MP.REPAIRABLE, MP.WEARABLE),
    CHAINMAIL_LEGGINGS    (Material.CHAINMAIL_LEGGINGS,      MP.REPAIRABLE, MP.WEARABLE),
    CHEST                 (Material.CHEST,                   MP.PLACEABLE, MP.INVENTORY, MP.GUI, MP.CRAFTABLE, MP.SURFACE),
    CLAY                  (Material.CLAY,                    MP.PLACEABLE),
    CLAY_BALL             (Material.CLAY_BALL,               MP.CRAFTABLE),
    CLAY_BRICK            (Material.CLAY_BRICK,              MP.CRAFTABLE),
    COAL                  (Material.COAL,                    MP.SUB_MATERIALS),
    COAL_BLOCK            (Material.COAL_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    COAL_ORE              (Material.COAL_ORE,                MP.PLACEABLE, MP.ORE, MP.SURFACE),
    COBBLE_WALL           (Material.COBBLE_WALL,             MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE),
    COBBLESTONE           (Material.COBBLESTONE,             MP.PLACEABLE, MP.SURFACE),
    COBBLESTONE_STAIRS    (Material.COBBLESTONE_STAIRS,      MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    COCOA                 (Material.COCOA),
    COMMAND               (Material.COMMAND,                 MP.PLACEABLE, MP.GUI, MP.SURFACE),
    COMMAND_MINECART      (Material.COMMAND_MINECART),
    COMPASS               (Material.COMPASS,                 MP.CRAFTABLE),
    COOKED_BEEF           (Material.COOKED_BEEF,             MP.CRAFTABLE),
    COOKED_CHICKEN        (Material.COOKED_CHICKEN,          MP.CRAFTABLE),
    COOKED_FISH           (Material.COOKED_FISH,             MP.CRAFTABLE),
    COOKED_MUTTON         (Material.COOKED_MUTTON,           MP.CRAFTABLE),
    COOKED_RABBIT         (Material.COOKED_RABBIT,           MP.CRAFTABLE),
    COOKIE                (Material.COOKIE,                  MP.CRAFTABLE),
    CROPS                 (Material.CROPS,                   MP.SURFACE),
    DARK_OAK_DOOR         (Material.DARK_OAK_DOOR,           MP.OPENABLE_BOUNDARY, MP.MULTI_BLOCK, MP.REDSTONE),
    DARK_OAK_DOOR_ITEM    (Material.DARK_OAK_DOOR_ITEM,      MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    DARK_OAK_FENCE        (Material.DARK_OAK_FENCE,          MP.PLACEABLE, MP.CRAFTABLE),
    DARK_OAK_FENCE_GATE   (Material.DARK_OAK_FENCE_GATE,     MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    DARK_OAK_STAIRS       (Material.DARK_OAK_STAIRS,         MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    DAYLIGHT_DETECTOR     (Material.DAYLIGHT_DETECTOR,       MP.REDSTONE, MP.PLACEABLE, MP.CRAFTABLE),
    DAYLIGHT_DETECTOR_INVERTED (Material.DAYLIGHT_DETECTOR_INVERTED, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    DEAD_BUSH             (Material.DEAD_BUSH,               MP.TRANSPARENT),
    DETECTOR_RAIL         (Material.DETECTOR_RAIL,           MP.TRANSPARENT, MP.REDSTONE, MP.PLACEABLE, MP.CRAFTABLE),
    DIAMOND               (Material.DIAMOND),
    DIAMOND_AXE           (Material.DIAMOND_AXE,             MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    DIAMOND_BARDING       (Material.DIAMOND_BARDING,         MP.WEARABLE),
    DIAMOND_BLOCK         (Material.DIAMOND_BLOCK,           MP.PLACEABLE, MP.CRAFTABLE),
    DIAMOND_BOOTS         (Material.DIAMOND_BOOTS,           MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    DIAMOND_CHESTPLATE    (Material.DIAMOND_CHESTPLATE,      MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    DIAMOND_HELMET        (Material.DIAMOND_HELMET,          MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    DIAMOND_HOE           (Material.DIAMOND_HOE,             MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    DIAMOND_LEGGINGS      (Material.DIAMOND_LEGGINGS,        MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    DIAMOND_ORE           (Material.DIAMOND_ORE,             MP.PLACEABLE, MP.ORE),
    DIAMOND_PICKAXE       (Material.DIAMOND_PICKAXE,         MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    DIAMOND_SPADE         (Material.DIAMOND_SPADE,           MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    DIAMOND_SWORD         (Material.DIAMOND_SWORD,           MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
    DIODE                 (Material.DIODE,                   MP.REDSTONE, MP.PLACEABLE, MP.CRAFTABLE),
    DIODE_BLOCK_OFF       (Material.DIODE_BLOCK_OFF,         MP.TRANSPARENT, MP.REDSTONE),
    DIODE_BLOCK_ON        (Material.DIODE_BLOCK_ON,          MP.TRANSPARENT, MP.REDSTONE),
    DIRT                  (Material.DIRT,                    MP.SUB_MATERIALS, MP.PLACEABLE, MP.SURFACE),
    DISPENSER             (Material.DISPENSER,               MP.INVENTORY, MP.GUI, MP.CRAFTABLE, MP.REDSTONE, MP.SURFACE),
    DOUBLE_PLANT          (Material.DOUBLE_PLANT,            MP.TRANSPARENT, MP.MULTI_BLOCK, MP.SUB_MATERIALS),
    DOUBLE_STEP           (Material.DOUBLE_STEP,             MP.SUB_MATERIALS, MP.SURFACE),
    DRAGON_EGG            (Material.DRAGON_EGG),
    DROPPER               (Material.DROPPER,                 MP.INVENTORY, MP.GUI, MP.CRAFTABLE, MP.REDSTONE, MP.SURFACE),
    EGG                   (Material.EGG),
    EMERALD               (Material.EMERALD),
    EMERALD_BLOCK         (Material.EMERALD_BLOCK,           MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    EMERALD_ORE           (Material.EMERALD_ORE,             MP.PLACEABLE, MP.ORE, MP.SURFACE),
    EMPTY_MAP             (Material.EMPTY_MAP),
    ENCHANTED_BOOK        (Material.ENCHANTED_BOOK),
    ENCHANTMENT_TABLE     (Material.ENCHANTMENT_TABLE,       MP.PLACEABLE, MP.CRAFTABLE, MP.GUI),
    ENDER_CHEST           (Material.ENDER_CHEST,             MP.PLACEABLE, MP.CRAFTABLE, MP.INVENTORY, MP.GUI, MP.SURFACE),
    ENDER_PEARL           (Material.ENDER_PEARL,             MP.THROWABLE),
    ENDER_PORTAL          (Material.ENDER_PORTAL),
    ENDER_PORTAL_FRAME    (Material.ENDER_PORTAL_FRAME,      MP.SURFACE),
    ENDER_STONE           (Material.ENDER_STONE),
    EXP_BOTTLE            (Material.EXP_BOTTLE,              MP.THROWABLE),
    EXPLOSIVE_MINECART    (Material.EXPLOSIVE_MINECART),
    EYE_OF_ENDER          (Material.EYE_OF_ENDER),
    FEATHER               (Material.FEATHER),
    FENCE                 (Material.FENCE,                   MP.PLACEABLE, MP.CRAFTABLE),
    FENCE_GATE            (Material.FENCE_GATE,              MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE),
    FERMENTED_SPIDER_EYE  (Material.FERMENTED_SPIDER_EYE),
    FIRE                  (Material.FIRE),
    FIREBALL              (Material.FIREBALL,                MP.THROWABLE),
    FIREWORK              (Material.FIREWORK,                MP.CRAFTABLE),
    FIREWORK_CHARGE       (Material.FIREWORK_CHARGE,         MP.CRAFTABLE),
    FISHING_ROD           (Material.FISHING_ROD,             MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    FLINT                 (Material.FLINT),
    FLINT_AND_STEEL       (Material.FLINT_AND_STEEL,         MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    FLOWER_POT            (Material.FLOWER_POT),
    FLOWER_POT_ITEM       (Material.FLOWER_POT_ITEM),
    FURNACE               (Material.FURNACE,                 MP.PLACEABLE, MP.GUI, MP.SURFACE),
    GHAST_TEAR            (Material.GHAST_TEAR),
    GLASS                 (Material.GLASS,                   MP.PLACEABLE, MP.CRAFTABLE),
    GLASS_BOTTLE          (Material.GLASS_BOTTLE,            MP.CRAFTABLE),
    GLOWING_REDSTONE_ORE  (Material.GLOWING_REDSTONE_ORE,    MP.SURFACE),
    GLOWSTONE             (Material.GLOWSTONE,               MP.PLACEABLE),
    GLOWSTONE_DUST        (Material.GLOWSTONE_DUST),
    GOLD_AXE              (Material.GOLD_AXE,                MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
    GOLD_BARDING          (Material.GOLD_BARDING,            MP.WEARABLE),
    GOLD_BLOCK            (Material.GOLD_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE),
    GOLD_BOOTS            (Material.GOLD_BOOTS,              MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    GOLD_CHESTPLATE       (Material.GOLD_CHESTPLATE,         MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    GOLD_HELMET           (Material.GOLD_HELMET,             MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    GOLD_HOE              (Material.GOLD_HOE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    GOLD_INGOT            (Material.GOLD_INGOT,              MP.CRAFTABLE),
    GOLD_LEGGINGS         (Material.GOLD_LEGGINGS,           MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    GOLD_NUGGET           (Material.GOLD_NUGGET,             MP.CRAFTABLE),
    GOLD_ORE              (Material.GOLD_ORE,                MP.PLACEABLE, MP.ORE),
    GOLD_PICKAXE          (Material.GOLD_PICKAXE,            MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    GOLD_PLATE            (Material.GOLD_PLATE,              MP.TRANSPARENT, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    GOLD_RECORD           (Material.GOLD_RECORD),
    GOLD_SPADE            (Material.GOLD_SPADE,              MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    GOLD_SWORD            (Material.GOLD_SWORD,              MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
    GOLDEN_APPLE          (Material.GOLDEN_APPLE,            MP.SUB_MATERIALS, MP.CRAFTABLE),
    GOLDEN_CARROT         (Material.GOLDEN_CARROT,           MP.CRAFTABLE),
    GRASS                 (Material.GRASS,                   MP.PLACEABLE, MP.SURFACE),
    GRAVEL                (Material.GRAVEL,                  MP.PLACEABLE, MP.SURFACE),
    GREEN_RECORD          (Material.GREEN_RECORD),
    GRILLED_PORK          (Material.GRILLED_PORK,            MP.CRAFTABLE),
    HARD_CLAY             (Material.HARD_CLAY,               MP.PLACEABLE),
    HAY_BLOCK             (Material.HAY_BLOCK,               MP.PLACEABLE, MP.SURFACE),
    HOPPER                (Material.HOPPER,                  MP.PLACEABLE, MP.REDSTONE, MP.INVENTORY, MP.GUI, MP.CRAFTABLE),
    HOPPER_MINECART       (Material.HOPPER_MINECART),
    HUGE_MUSHROOM_1       (Material.HUGE_MUSHROOM_1,         MP.SURFACE),
    HUGE_MUSHROOM_2       (Material.HUGE_MUSHROOM_2,         MP.SURFACE),
    ICE                   (Material.ICE,                     MP.PLACEABLE, MP.SURFACE),
    INK_SACK              (Material.INK_SACK,                MP.MULTICOLOR, MP.SUB_MATERIALS),
    IRON_AXE              (Material.IRON_AXE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    IRON_BARDING          (Material.IRON_BARDING,            MP.WEARABLE),
    IRON_BLOCK            (Material.IRON_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE),
    IRON_BOOTS            (Material.IRON_BOOTS,              MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    IRON_CHESTPLATE       (Material.IRON_CHESTPLATE,         MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    IRON_DOOR             (Material.IRON_DOOR,               MP.OPENABLE_BOUNDARY, MP.MULTI_BLOCK, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    IRON_DOOR_BLOCK       (Material.IRON_DOOR_BLOCK,         MP.OPENABLE_BOUNDARY, MP.REDSTONE),
    IRON_FENCE            (Material.IRON_FENCE,              MP.PLACEABLE, MP.CRAFTABLE),
    IRON_HELMET           (Material.IRON_HELMET,             MP.PLACEABLE, MP.WEARABLE, MP.CRAFTABLE),
    IRON_HOE              (Material.IRON_HOE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    IRON_INGOT            (Material.IRON_INGOT,              MP.CRAFTABLE),
    IRON_LEGGINGS         (Material.IRON_LEGGINGS,           MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    IRON_ORE              (Material.IRON_ORE,                MP.PLACEABLE, MP.ORE),
    IRON_PICKAXE          (Material.IRON_PICKAXE,            MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    IRON_PLATE            (Material.IRON_PLATE,              MP.TRANSPARENT, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    IRON_SPADE            (Material.IRON_SPADE,              MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    IRON_SWORD            (Material.IRON_SWORD,              MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
    IRON_TRAPDOOR         (Material.IRON_TRAPDOOR,           MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    ITEM_FRAME            (Material.ITEM_FRAME,              MP.PLACEABLE, MP.CRAFTABLE),
    JACK_O_LANTERN        (Material.JACK_O_LANTERN,          MP.PLACEABLE, MP.WEARABLE, MP.SURFACE),
    JUKEBOX               (Material.JUKEBOX,                 MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    JUNGLE_DOOR           (Material.JUNGLE_DOOR,             MP.OPENABLE_BOUNDARY, MP.MULTI_BLOCK, MP.REDSTONE),
    JUNGLE_DOOR_ITEM      (Material.JUNGLE_DOOR_ITEM,        MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    JUNGLE_FENCE          (Material.JUNGLE_FENCE,            MP.PLACEABLE, MP.CRAFTABLE),
    JUNGLE_FENCE_GATE     (Material.JUNGLE_FENCE_GATE,       MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    JUNGLE_WOOD_STAIRS    (Material.JUNGLE_WOOD_STAIRS,      MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    LADDER                (Material.LADDER,                  MP.TRANSPARENT, MP.PLACEABLE, MP.CRAFTABLE),
    LAPIS_BLOCK           (Material.LAPIS_BLOCK,             MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    LAPIS_ORE             (Material.LAPIS_ORE,               MP.PLACEABLE, MP.ORE, MP.SURFACE),
    LAVA                  (Material.LAVA,                    MP.PLACEABLE),
    LAVA_BUCKET           (Material.LAVA_BUCKET),
    LEASH                 (Material.LEASH),
    LEATHER               (Material.LEATHER),
    LEATHER_BOOTS         (Material.LEATHER_BOOTS,           MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    LEATHER_CHESTPLATE    (Material.LEATHER_CHESTPLATE,      MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    LEATHER_HELMET        (Material.LEATHER_HELMET,          MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    LEATHER_LEGGINGS      (Material.LEATHER_LEGGINGS,        MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
    LEAVES                (Material.LEAVES,                  MP.SUB_MATERIALS, MP.PLACEABLE, MP.SURFACE),
    LEAVES_2              (Material.LEAVES_2,                MP.SUB_MATERIALS, MP.PLACEABLE, MP.SURFACE),
    LEVER                 (Material.LEVER,                   MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    LOG                   (Material.LOG,                     MP.SUB_MATERIALS, MP.PLACEABLE, MP.SURFACE),
    LOG_2                 (Material.LOG_2,                   MP.SUB_MATERIALS, MP.PLACEABLE, MP.SURFACE),
    LONG_GRASS            (Material.LONG_GRASS,              MP.TRANSPARENT, MP.SUB_MATERIALS),
    MAGMA_CREAM           (Material.MAGMA_CREAM),
    MAP                   (Material.MAP),
    MELON                 (Material.MELON),
    MELON_BLOCK           (Material.MELON_BLOCK,             MP.PLACEABLE, MP.SURFACE),
    MELON_SEEDS           (Material.MELON_SEEDS),
    MELON_STEM            (Material.MELON_STEM),
    MILK_BUCKET           (Material.MILK_BUCKET),
    MINECART              (Material.MINECART,                MP.CRAFTABLE),
    MOB_SPAWNER           (Material.MOB_SPAWNER),
    MONSTER_EGG           (Material.MONSTER_EGG),
    MONSTER_EGGS          (Material.MONSTER_EGGS),
    MOSSY_COBBLESTONE     (Material.MOSSY_COBBLESTONE,       MP.PLACEABLE, MP.SURFACE),
    MUSHROOM_SOUP         (Material.MUSHROOM_SOUP,           MP.CRAFTABLE),
    MUTTON                (Material.MUTTON),
    MYCEL                 (Material.MYCEL,                   MP.PLACEABLE, MP.SURFACE),
    NAME_TAG              (Material.NAME_TAG),
    NETHER_BRICK          (Material.NETHER_BRICK,            MP.PLACEABLE, MP.SURFACE),
    NETHER_BRICK_ITEM     (Material.NETHER_BRICK_ITEM),
    NETHER_BRICK_STAIRS   (Material.NETHER_BRICK_STAIRS,     MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    NETHER_FENCE          (Material.NETHER_FENCE,            MP.PLACEABLE, MP.CRAFTABLE),
    NETHER_STALK          (Material.NETHER_STALK), // Nether wart item
    NETHER_STAR           (Material.NETHER_STAR),
    NETHER_WARTS          (Material.NETHER_WARTS,            MP.PLACEABLE),
    NETHERRACK            (Material.NETHERRACK,              MP.PLACEABLE, MP.SURFACE),
    NOTE_BLOCK            (Material.NOTE_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    OBSIDIAN              (Material.OBSIDIAN,                MP.PLACEABLE, MP.SURFACE),
    PACKED_ICE            (Material.PACKED_ICE,              MP.PLACEABLE, MP.SURFACE),
    PAINTING              (Material.PAINTING,                MP.PLACEABLE, MP.CRAFTABLE),
    PAPER                 (Material.PAPER,                   MP.CRAFTABLE),
    PISTON_BASE           (Material.PISTON_BASE,             MP.SURFACE),
    PISTON_EXTENSION      (Material.PISTON_EXTENSION),
    PISTON_MOVING_PIECE   (Material.PISTON_MOVING_PIECE),
    PISTON_STICKY_BASE    (Material.PISTON_STICKY_BASE,      MP.SURFACE),
    POISONOUS_POTATO      (Material.POISONOUS_POTATO,        MP.CRAFTABLE),
    PORK                  (Material.PORK),
    PORTAL                (Material.PORTAL),
    POTATO                (Material.POTATO),
    POTATO_ITEM           (Material.POTATO_ITEM),
    POTION                (Material.POTION),
    POWERED_MINECART      (Material.POWERED_MINECART),
    POWERED_RAIL          (Material.POWERED_RAIL,            MP.TRANSPARENT, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    PRISMARINE            (Material.PRISMARINE),
    PRISMARINE_CRYSTALS   (Material.PRISMARINE_CRYSTALS),
    PRISMARINE_SHARD      (Material.PRISMARINE_SHARD),
    PUMPKIN               (Material.PUMPKIN,                 MP.PLACEABLE),
    PUMPKIN_PIE           (Material.PUMPKIN_PIE,             MP.CRAFTABLE),
    PUMPKIN_SEEDS         (Material.PUMPKIN_SEEDS),
    PUMPKIN_STEM          (Material.PUMPKIN_STEM),
    QUARTZ                (Material.QUARTZ),
    QUARTZ_BLOCK          (Material.QUARTZ_BLOCK,            MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    QUARTZ_ORE            (Material.QUARTZ_ORE,              MP.PLACEABLE, MP.ORE, MP.SURFACE),
    QUARTZ_STAIRS         (Material.QUARTZ_STAIRS,           MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    RABBIT                (Material.RABBIT),
    RABBIT_FOOT           (Material.RABBIT_FOOT),
    RABBIT_HIDE           (Material.RABBIT_HIDE),
    RABBIT_STEW           (Material.RABBIT_STEW),
    RAILS                 (Material.RAILS,                   MP.TRANSPARENT, MP.PLACEABLE, MP.CRAFTABLE),
    RAW_BEEF              (Material.RAW_BEEF),
    RAW_CHICKEN           (Material.RAW_CHICKEN),
    RAW_FISH              (Material.RAW_FISH,                MP.SUB_MATERIALS),
    RECORD_10             (Material.RECORD_10),
    RECORD_11             (Material.RECORD_11),
    RECORD_12             (Material.RECORD_12),
    RECORD_3              (Material.RECORD_3),
    RECORD_4              (Material.RECORD_4),
    RECORD_5              (Material.RECORD_5),
    RECORD_6              (Material.RECORD_6),
    RECORD_7              (Material.RECORD_7),
    RECORD_8              (Material.RECORD_8),
    RECORD_9              (Material.RECORD_9),
    RED_MUSHROOM          (Material.RED_MUSHROOM,            MP.TRANSPARENT),
    RED_ROSE              (Material.RED_ROSE,                MP.TRANSPARENT, MP.SUB_MATERIALS),
    RED_SANDSTONE         (Material.RED_SANDSTONE,           MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    RED_SANDSTONE_STAIRS  (Material.RED_SANDSTONE_STAIRS,    MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    REDSTONE              (Material.REDSTONE,                MP.PLACEABLE, MP.REDSTONE),
    REDSTONE_BLOCK        (Material.REDSTONE_BLOCK,          MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE, MP.SURFACE),
    REDSTONE_COMPARATOR   (Material.REDSTONE_COMPARATOR,     MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    REDSTONE_COMPARATOR_OFF(Material.REDSTONE_COMPARATOR_OFF,MP.TRANSPARENT, MP.REDSTONE),
    REDSTONE_COMPARATOR_ON(Material.REDSTONE_COMPARATOR_ON,  MP.TRANSPARENT, MP.REDSTONE),
    REDSTONE_LAMP_OFF     (Material.REDSTONE_LAMP_OFF,       MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE, MP.SURFACE),
    REDSTONE_LAMP_ON      (Material.REDSTONE_LAMP_ON,        MP.REDSTONE, MP.SURFACE),
    REDSTONE_ORE          (Material.REDSTONE_ORE,            MP.PLACEABLE),
    REDSTONE_TORCH_OFF    (Material.REDSTONE_TORCH_OFF,      MP.TRANSPARENT, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    REDSTONE_TORCH_ON     (Material.REDSTONE_TORCH_ON,       MP.TRANSPARENT, MP.REDSTONE),
    REDSTONE_WIRE         (Material.REDSTONE_WIRE,           MP.TRANSPARENT, MP.REDSTONE),
    ROTTEN_FLESH          (Material.ROTTEN_FLESH),
    SADDLE                (Material.SADDLE),
    SAND                  (Material.SAND,                    MP.SUB_MATERIALS, MP.PLACEABLE, MP.SURFACE),
    SANDSTONE             (Material.SANDSTONE,               MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    SANDSTONE_STAIRS      (Material.SANDSTONE_STAIRS,        MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    SAPLING               (Material.SAPLING,                 MP.TRANSPARENT, MP.SUB_MATERIALS, MP.PLACEABLE),
    SEA_LANTERN           (Material.SEA_LANTERN,             MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    SEEDS                 (Material.SEEDS,                   MP.SUB_MATERIALS, MP.PLACEABLE),
    SHEARS                (Material.SHEARS,                  MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    SIGN                  (Material.SIGN,                    MP.PLACEABLE, MP.CRAFTABLE),
    SIGN_POST             (Material.SIGN_POST,               MP.TRANSPARENT),
    SKULL                 (Material.SKULL),
    SKULL_ITEM            (Material.SKULL_ITEM,              MP.PLACEABLE, MP.WEARABLE),
    SLIME_BALL            (Material.SLIME_BALL),
    SMOOTH_BRICK          (Material.SMOOTH_BRICK,            MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    SMOOTH_STAIRS         (Material.SMOOTH_STAIRS,           MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    SNOW                  (Material.SNOW,                    MP.TRANSPARENT, MP.PLACEABLE),
    SNOW_BALL             (Material.SNOW_BALL,               MP.THROWABLE),
    SNOW_BLOCK            (Material.SNOW_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    SOIL                  (Material.SOIL,                    MP.SURFACE),
    SOUL_SAND             (Material.SOUL_SAND,               MP.PLACEABLE),
    SPECKLED_MELON        (Material.SPECKLED_MELON), // Glistering Melon
    SPIDER_EYE            (Material.SPIDER_EYE),
    SPONGE                (Material.SPONGE,                  MP.SUB_MATERIALS, MP.PLACEABLE, MP.SURFACE),
    SPRUCE_DOOR           (Material.SPRUCE_DOOR,             MP.OPENABLE_BOUNDARY, MP.MULTI_BLOCK, MP.REDSTONE),
    SPRUCE_DOOR_ITEM      (Material.SPRUCE_DOOR_ITEM,        MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    SPRUCE_FENCE          (Material.SPRUCE_FENCE,            MP.PLACEABLE, MP.CRAFTABLE),
    SPRUCE_FENCE_GATE     (Material.SPRUCE_FENCE_GATE,       MP.PLACEABLE, MP.CRAFTABLE, MP.REDSTONE),
    SPRUCE_WOOD_STAIRS    (Material.SPRUCE_WOOD_STAIRS,      MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    STAINED_CLAY          (Material.STAINED_CLAY,            MP.PLACEABLE, MP.MULTICOLOR, MP.CRAFTABLE, MP.SURFACE),
    STAINED_GLASS         (Material.STAINED_GLASS,           MP.PLACEABLE, MP.MULTICOLOR, MP.CRAFTABLE, MP.SURFACE),
    STAINED_GLASS_PANE    (Material.STAINED_GLASS_PANE,      MP.PLACEABLE, MP.MULTICOLOR, MP.CRAFTABLE, MP.SURFACE),
    STANDING_BANNER       (Material.STANDING_BANNER),
    STATIONARY_LAVA       (Material.STATIONARY_LAVA),
    STATIONARY_WATER      (Material.STATIONARY_WATER,        MP.TRANSPARENT),
    STEP                  (Material.STEP,                    MP.PLACEABLE, MP.SUB_MATERIALS, MP.CRAFTABLE, MP.SURFACE),
    STICK                 (Material.STICK),
    STONE                 (Material.STONE,                   MP.PLACEABLE, MP.SUB_MATERIALS, MP.CRAFTABLE, MP.SURFACE),
    STONE_AXE             (Material.STONE_AXE,               MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    STONE_BUTTON          (Material.STONE_BUTTON,            MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    STONE_HOE             (Material.STONE_HOE,               MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    STONE_PICKAXE         (Material.STONE_PICKAXE,           MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    STONE_PLATE           (Material.STONE_PLATE,             MP.TRANSPARENT, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    STONE_SLAB2           (Material.STONE_SLAB2,             MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    STONE_SPADE           (Material.STONE_SPADE,             MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    STONE_SWORD           (Material.STONE_SWORD,             MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
    STORAGE_MINECART      (Material.STORAGE_MINECART),
    STRING                (Material.STRING),
    SUGAR                 (Material.SUGAR),
    SUGAR_CANE            (Material.SUGAR_CANE,              MP.PLACEABLE),
    SUGAR_CANE_BLOCK      (Material.SUGAR_CANE_BLOCK),
    SULPHUR               (Material.SULPHUR), // gunpowder
    THIN_GLASS            (Material.THIN_GLASS,              MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    TNT                   (Material.TNT,                     MP.PLACEABLE, MP.CRAFTABLE),
    TORCH                 (Material.TORCH,                   MP.TRANSPARENT, MP.PLACEABLE, MP.CRAFTABLE),
    TRAP_DOOR             (Material.TRAP_DOOR,               MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.CRAFTABLE),
    TRAPPED_CHEST         (Material.TRAPPED_CHEST,           MP.PLACEABLE, MP.CRAFTABLE),
    TRIPWIRE              (Material.TRIPWIRE),
    TRIPWIRE_HOOK         (Material.TRIPWIRE_HOOK,           MP.PLACEABLE, MP.CRAFTABLE),
    VINE                  (Material.VINE,                    MP.PLACEABLE),
    WALL_BANNER           (Material.WALL_BANNER),
    WALL_SIGN             (Material.WALL_SIGN,               MP.TRANSPARENT),
    WATCH                 (Material.WATCH,                   MP.CRAFTABLE),
    WATER                 (Material.WATER,                   MP.TRANSPARENT),
    WATER_BUCKET          (Material.WATER_BUCKET),
    WATER_LILY            (Material.WATER_LILY,              MP.PLACEABLE),
    WEB                   (Material.WEB,                     MP.TRANSPARENT, MP.PLACEABLE),
    WHEAT                 (Material.WHEAT,                   MP.TRANSPARENT, MP.PLACEABLE),
    WOOD                  (Material.WOOD,                    MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    WOOD_AXE              (Material.WOOD_AXE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    WOOD_BUTTON           (Material.WOOD_BUTTON,             MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    WOOD_DOOR             (Material.WOOD_DOOR,               MP.OPENABLE_BOUNDARY, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    WOOD_DOUBLE_STEP      (Material.WOOD_DOUBLE_STEP,        MP.SUB_MATERIALS, MP.SURFACE),
    WOOD_HOE              (Material.WOOD_HOE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    WOOD_PICKAXE          (Material.WOOD_PICKAXE,            MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    WOOD_PLATE            (Material.WOOD_PLATE,              MP.TRANSPARENT, MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
    WOOD_SPADE            (Material.WOOD_SPADE,              MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
    WOOD_STAIRS           (Material.WOOD_STAIRS,             MP.PLACEABLE, MP.CRAFTABLE, MP.SURFACE),
    WOOD_STEP             (Material.WOOD_STEP,               MP.SUB_MATERIALS, MP.SURFACE),
    WOOD_SWORD            (Material.WOOD_SWORD,              MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
    WOODEN_DOOR           (Material.WOODEN_DOOR,             MP.OPENABLE_BOUNDARY, MP.MULTI_BLOCK, MP.REDSTONE),// block
    WOOL                  (Material.WOOL,                    MP.PLACEABLE, MP.MULTICOLOR, MP.CRAFTABLE),
    WORKBENCH             (Material.WORKBENCH,               MP.PLACEABLE, MP.CRAFTABLE, MP.GUI, MP.SURFACE),
    WRITTEN_BOOK          (Material.WRITTEN_BOOK,            MP.GUI),
    YELLOW_FLOWER         (Material.YELLOW_FLOWER,           MP.TRANSPARENT)

    ;


    /**
     * Represents properties of a Bukkit material.
     */
    public enum MaterialProperty {
        SUB_MATERIALS(1),  // data byte represents sub material
        MULTICOLOR(2), // data byte represents color
        REDSTONE(4), // redstone component
        WEARABLE(8),
        WEAPON(16),
        TOOL(32),
        PLACEABLE(64),
        FOOD(128),
        THROWABLE(256),
        INVENTORY(512),
        GUI(1024),
        CRAFTABLE(2048),
        GRAVITY(4096),
        ORE(8192),
        REPAIRABLE(16384),
        BLOCK(32768),
        MULTI_BLOCK(131072),
        TRANSPARENT(262144),
        OPENABLE_BOUNDARY(524288),
        SURFACE(1048576);

        private final int _bit;

        MaterialProperty(int bit) {
            _bit = bit;
        }

        public int getBit() {
            return _bit;
        }
    }

    // Private copy of MaterialProperty
    // to reduce amount of code and create readability
    // in enum constructors
    private enum MP {
        SUB_MATERIALS(1),  // data byte represents sub material
        MULTICOLOR(2), // data byte represents color
        REDSTONE(4), // redstone component
        WEARABLE(8),
        WEAPON(16),
        TOOL(32),
        PLACEABLE(64),
        // FOOD 128
        THROWABLE(256),
        INVENTORY(512),
        GUI(1024),
        CRAFTABLE(2048),
        // GRAVITY 4096
        ORE(8192),
        REPAIRABLE(16384),
        // BLOCK 65536
        MULTI_BLOCK(131072),
        TRANSPARENT(262144),
        OPENABLE_BOUNDARY(524288),
        SURFACE(1048576);

        private final long _bit;

        MP(long bit) {
            _bit = bit;
        }

        public long getBits() {
            return _bit;
        }
    }

    // cache results of property searches
    private static Map<Integer, List<MaterialExt>> _matchCache = new HashMap<Integer, List<MaterialExt>>(100);
    // map Bukkit Materials to extended materials
    private static Map<Material, MaterialExt> _materialMap = null;

    private final Material _material;
    private final int _flags;
    private final boolean _hasSubMaterials;
    private final boolean _hasMultiColor;
    private final boolean _isRepairable;
    private final boolean _isRedstoneCompatible;
    private final boolean _isWearable;
    private final boolean _isWeapon;
    private final boolean _isTool;
    private final boolean _isPlaceable;
    private final boolean _isFood;
    private final boolean _isThrowable;
    private final boolean _hasInventory;
    private final boolean _hasGUI;
    private final boolean _isCraftable;
    private final boolean _hasGravity;
    private final boolean _isOre;
    private final boolean _isBlock;
    private final boolean _isMultiBlock;
    private final boolean _isSurface;
    private final boolean _isOpenable;
    private final boolean _isTransparent;
    private final int _maxStackSize;


    MaterialExt(Material material, MP...properties) {

        _material = material;

        int flags = 0;

        for (MP property : properties) {
            flags |= property.getBits();
        }

        if (material != null) {
            if (material.isBlock()) {
                flags |= MaterialProperty.BLOCK.getBit();

                if (isSurface(material)) {
                    flags |= MaterialProperty.SURFACE.getBit();
                }
            }

            if (material.isEdible())
                flags |= MaterialProperty.FOOD.getBit();

            if (material.hasGravity())
                flags |= MaterialProperty.GRAVITY.getBit();

            _maxStackSize = material.getMaxStackSize();
        }
        else {
            _maxStackSize = 0;
        }

        _flags = flags;

        _hasSubMaterials = hasProp(MaterialProperty.SUB_MATERIALS);
        _hasMultiColor = hasProp(MaterialProperty.MULTICOLOR);
        _isRepairable = hasProp(MaterialProperty.REPAIRABLE);
        _isRedstoneCompatible = hasProp(MaterialProperty.REDSTONE);
        _isWearable = hasProp(MaterialProperty.WEARABLE);
        _isWeapon = hasProp(MaterialProperty.WEAPON);
        _isTool = hasProp(MaterialProperty.TOOL);
        _isPlaceable = hasProp(MaterialProperty.PLACEABLE);
        _isFood = hasProp(MaterialProperty.FOOD);
        _isThrowable = hasProp(MaterialProperty.THROWABLE);
        _hasInventory = hasProp(MaterialProperty.INVENTORY);
        _hasGUI = hasProp(MaterialProperty.GUI);
        _isCraftable = hasProp(MaterialProperty.CRAFTABLE);
        _hasGravity = hasProp(MaterialProperty.GRAVITY);
        _isOre = hasProp(MaterialProperty.ORE);
        _isBlock = hasProp(MaterialProperty.BLOCK);
        _isMultiBlock = hasProp(MaterialProperty.MULTI_BLOCK);
        _isSurface = hasProp(MaterialProperty.SURFACE);
        _isOpenable = hasProp(MaterialProperty.OPENABLE_BOUNDARY);
        _isTransparent = hasProp(MaterialProperty.TRANSPARENT);
    }

    /**
     * Get the maximum stack size of a material.
     * @return
     */
    public int getMaxStackSize() {
        return _maxStackSize;
    }

    /**
     * Determine if the byte data of a material
     * is used to define a sub material.
     */
    public boolean usesSubMaterialData() {
        return _hasSubMaterials;
    }

    /**
     * Determine if the byte data of a material
     * is used to define the materials color.
     */
    public boolean usesColorData() {
        return _hasMultiColor;
    }

    /**
     * Determine if the byte data of a material
     * is used to define the direction a material
     * faces when it is placed.
     */
    public boolean usesDirectionData() {
        return !_hasSubMaterials && !_hasMultiColor;
    }

    /**
     * Determine if the material has durability
     * and can be repaired.
     */
    public boolean isRepairable() {
        return _isRepairable;
    }

    /**
     * Determine if the material outputs a redstone
     * signal or performs an action when a redstone
     * current is applied.
     * @return
     */
    public boolean isRedstoneCompatible() {
        return _isRedstoneCompatible;
    }

    /**
     * Determine if the material can be worn.
     */
    public boolean isWearable() {
        return _isWearable;
    }

    /**
     * Determine if the material is a weapon.
     */
    public boolean isWeapon() {
        return _isWeapon;
    }

    /**
     * Determine if the material is a tool.
     * i.e. shovel, shears, etc.
     */
    public boolean isTool() {
        return _isTool;
    }

    /**
     * Determine if the material can be placed
     * as a block by the player. Includes materials
     * that can only be retrieved using game mode.
     * Does not include materials that only exists after
     * a material is placed (i.e a bed is placeable but a
     * bed block is not)
     */
    public boolean isPlaceable() {
        return _isPlaceable;
    }

    /**
     * Determine if the material is a block.
     */
    public boolean isBlock() {
        return _isBlock;
    }

    /**
     * Determine if the material requires multiple
     * blocks. (ie Doors)
     */
    public boolean isMultiBlock() {
        return _isMultiBlock;
    }

    /**
     * Determine if the material is edible by players.
     */
    public boolean isFood() {
        return _isFood;
    }

    /**
     * Determine if the material can be thrown.
     */
    public boolean isThrowable() {
        return _isThrowable;
    }

    /**
     * Determine if the material has an inventory.
     * (i.e. chest, trapped chests, etc)
     * @return
     */
    public boolean hasInventory() {
        return _hasInventory;
    }

    /**
     * Determine if the material can open a GUI interface
     */
    public boolean hasGUI() {
        return _hasGUI;
    }

    /**
     * Determine if the material is craftable by the players
     * as part of normal minecraft.
     */
    public boolean isCraftable() {
        return _isCraftable;
    }

    /**
     * Determine if the material falls if there are
     * no blocks below it.
     */
    public boolean hasGravity() {
        return _hasGravity;
    }

    /**
     * Determine if the material is an ore.
     */
    public boolean isOre() {
        return _isOre;
    }

    /**
     * Determine if the material is a surface that
     * can be walked on. Used in the context of mob pathing, therefore
     * (for example) even though a player can technically walk on a door,
     * a door is still not a surface.
     */
    public boolean isSurface() {
        return _isSurface;
    }

    /**
     * Determine if the material is an openable boundary.
     * (i.e a door or gate but not a chest)
     */
    public boolean isOpenableBoundary() {
        return _isOpenable;
    }

    /**
     * Determine if the material is a block that a player can
     * walk through.
     *
     * <p>Webs are considered transparent because they
     * still allow the player through, even if it is slowly.</p>
     */
    public boolean isTransparent() {
        return _isTransparent;
    }

    /**
     * Get the Bukkit material.
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * Determine if the material has the specified property.
     *
     * @param property  The property to check.
     */
    public boolean hasProp(MaterialProperty property) {
        return (_flags & property.getBit()) == property.getBit();
    }

    /**
     * Get a {@code MaterialExt} enum constant that represents
     * the specified Bukkit {@code Material} enum constant.
     *
     * @param material  The Bukkit {@code Material}
     */
    public static MaterialExt from(Material material) {
        buildMaterialMap();

        MaterialExt result = _materialMap.get(material);
        return result != null ? result : MaterialExt.UNKNOWN;
    }

    /**
     * Get a list of {@code MaterialExt} that have the specified properties.
     *
     * @param properties  The properties to check for.
     */
    public static List<MaterialExt> getMatching(MaterialProperty...properties) {
        int flags = 0;

        for (MaterialProperty property : properties) {
            flags |= property.getBit();
        }

        List<MaterialExt> matches = _matchCache.get(flags);

        if (matches != null)
            return new ArrayList<MaterialExt>(matches);

        matches = new ArrayList<MaterialExt>(100);

        for (MaterialExt ext : MaterialExt.values()) {

            boolean add = true;

            for (MaterialProperty property : properties) {

                boolean hasProperty = (ext._flags & property.getBit()) == property.getBit();

                if (!hasProperty) {
                    add = false;
                    break;
                }
            }

            if (add) {
                matches.add(ext);
            }
        }

        _matchCache.put(flags, matches);

        return new ArrayList<MaterialExt>(matches);
    }

    public static boolean isOpenable(Material material) {
        MaterialExt ext = from(material);
        return ext.isOpenableBoundary();
    }

    /**
     * Determine if a material is a surface.
     *
     * @param material  The material to check.
     */
    public static boolean isSurface(Material material) {
        MaterialExt ext = from(material);
        return ext.isSurface();
    }

    /**
     * Determine if a material is transparent.
     *
     * @param material  The material to check.
     */
    public static boolean isTransparent(Material material) {
        MaterialExt ext = from(material);
        return ext.isTransparent();
    }

    // build the material to extended material map.
    private static void buildMaterialMap() {
        if (_materialMap != null)
            return;

        _materialMap = new EnumMap<Material, MaterialExt>(Material.class);

        for (MaterialExt ext : MaterialExt.values()) {
            if (ext.getMaterial() != null)
                _materialMap.put(ext.getMaterial(), ext);
        }
    }
}
