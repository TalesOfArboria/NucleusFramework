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

	ACACIA_STAIRS         (Material.ACACIA_STAIRS,           MP.PLACEABLE, MP.CRAFTABLE),
	ACTIVATOR_RAIL        (Material.ACTIVATOR_RAIL,          MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	AIR			          (Material.AIR),
	ANVIL                 (Material.ANVIL,                   MP.PLACEABLE, MP.GUI, MP.CRAFTABLE),
	APPLE                 (Material.APPLE),
	ARROW                 (Material.ARROW,                   MP.THROWABLE, MP.CRAFTABLE),
	BAKED_POTATOE         (Material.BAKED_POTATO,            MP.CRAFTABLE),
	BEACON                (Material.BEACON,                  MP.PLACEABLE, MP.GUI, MP.CRAFTABLE),
	BED                   (Material.BED,                     MP.PLACEABLE, MP.CRAFTABLE),
	BED_BLOCK             (Material.BED_BLOCK),
	BEDROCK               (Material.BEDROCK,                 MP.PLACEABLE),
	BIRCH_WOOD_STAIRS     (Material.BIRCH_WOOD_STAIRS,       MP.PLACEABLE, MP.CRAFTABLE),
	BLAZE_POWDER          (Material.BLAZE_POWDER,            MP.CRAFTABLE),
	BLAZE_ROD             (Material.BLAZE_POWDER),
	BOAT                  (Material.BOAT,                    MP.CRAFTABLE),
	BONE                  (Material.BONE),
	BOOK                  (Material.BOOK,                    MP.CRAFTABLE),
	BOOK_AND_QUILL        (Material.BOOK_AND_QUILL,          MP.CRAFTABLE),
	BOOKSHELF             (Material.BOOKSHELF,               MP.PLACEABLE, MP.CRAFTABLE),
	BOW                   (Material.BOW,                     MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
	BOWL                  (Material.BOWL,                    MP.CRAFTABLE),
	BREAD                 (Material.BREAD,                   MP.CRAFTABLE),
	BREWING_STAND         (Material.BREWING_STAND,           MP.GUI),
	BREWING_STAND_ITEM    (Material.BREWING_STAND_ITEM,      MP.PLACEABLE),
	BRICK                 (Material.BRICK,                   MP.CRAFTABLE),
	BRICK_STAIRS          (Material.BRICK_STAIRS,            MP.PLACEABLE, MP.CRAFTABLE),
	BROWN_MUSHROOM        (Material.BROWN_MUSHROOM,          MP.PLACEABLE),
	BUCKET                (Material.BUCKET,                  MP.CRAFTABLE),
	BURNING_FURNACE       (Material.BURNING_FURNACE),
	CACTUS                (Material.CACTUS,                  MP.PLACEABLE),
	CAKE                  (Material.CAKE,                    MP.PLACEABLE, MP.CRAFTABLE),
	CAKE_BLOCK            (Material.CAKE_BLOCK),
	CARPET                (Material.CARPET,                  MP.MULTICOLOR, MP.PLACEABLE, MP.CRAFTABLE),
	CARROT                (Material.CARROT),
	CARROT_ITEM           (Material.CARROT_ITEM,             MP.PLACEABLE),
	CARROT_STICK          (Material.CARROT_STICK,            MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	CAULDRON              (Material.CAULDRON),
	CAULDRON_ITEM         (Material.CAULDRON_ITEM,           MP.PLACEABLE, MP.CRAFTABLE),
	CHAINMAIL_BOOTS       (Material.CHAINMAIL_BOOTS,         MP.REPAIRABLE, MP.WEARABLE),
	CHAINMAIL_CHESTPLATE  (Material.CHAINMAIL_CHESTPLATE,    MP.REPAIRABLE, MP.WEARABLE),
	CHAINMAIL_HELMET      (Material.CHAINMAIL_HELMET,        MP.REPAIRABLE, MP.WEARABLE),
	CHAINMAIL_LEGGINGS    (Material.CHAINMAIL_LEGGINGS,      MP.REPAIRABLE, MP.WEARABLE),
	CHEST                 (Material.CHEST,                   MP.PLACEABLE, MP.INVENTORY, MP.GUI, MP.CRAFTABLE),
	CLAY                  (Material.CLAY,                    MP.PLACEABLE),
	CLAY_BALL             (Material.CLAY_BALL,               MP.CRAFTABLE),
	CLAY_BRICK            (Material.CLAY_BRICK,              MP.CRAFTABLE),
	COAL                  (Material.COAL,                    MP.SUB_MATERIALS),
	COAL_BLOCK            (Material.COAL_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE),
	COAL_ORE              (Material.COAL_ORE,                MP.PLACEABLE, MP.ORE),
	COBBLE_WALL           (Material.COBBLE_WALL,             MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE),
	COBBLESTONE           (Material.COBBLESTONE,             MP.PLACEABLE),
	COBBLESTONE_STAIRS    (Material.COBBLESTONE_STAIRS,      MP.PLACEABLE, MP.CRAFTABLE),
	COCOA                 (Material.COCOA),
	COMMAND               (Material.COMMAND,                 MP.PLACEABLE, MP.GUI),
	COMMAND_MINECART      (Material.COMMAND_MINECART),
	COMPASS               (Material.COMPASS,                 MP.CRAFTABLE),
	COOKED_BEEF           (Material.COOKED_BEEF,             MP.CRAFTABLE),
	COOKED_CHICKEN        (Material.COOKED_CHICKEN,          MP.CRAFTABLE),
	COOKED_FISH           (Material.COOKED_FISH,             MP.CRAFTABLE),
	COOKIE                (Material.COOKIE,                  MP.CRAFTABLE),
	CROPS                 (Material.CROPS),
	DAYLIGHT_DETECTOR     (Material.DAYLIGHT_DETECTOR,       MP.REDSTONE, MP.PLACEABLE, MP.CRAFTABLE),
	DEAD_BUSH             (Material.DEAD_BUSH),
	DETECTOR_RAIL         (Material.DETECTOR_RAIL,           MP.REDSTONE, MP.PLACEABLE, MP.CRAFTABLE),
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
	DIODE_BLOCK_OFF       (Material.DIODE_BLOCK_OFF),
	DIODE_BLOCK_ON        (Material.DIODE_BLOCK_ON),
	DIRT                  (Material.DIRT,                    MP.SUB_MATERIALS, MP.PLACEABLE),
	DISPENSER             (Material.DISPENSER,               MP.INVENTORY, MP.GUI, MP.CRAFTABLE, MP.REDSTONE),
	DOUBLE_PLANT          (Material.DOUBLE_PLANT,            MP.SUB_MATERIALS),
	DOUBLE_STEP           (Material.DOUBLE_STEP,             MP.SUB_MATERIALS),
	DRAGON_EGG            (Material.DRAGON_EGG),
	DROPPER               (Material.DROPPER,                 MP.INVENTORY, MP.GUI, MP.CRAFTABLE, MP.REDSTONE),
	EGG                   (Material.EGG),
	EMERALD               (Material.EMERALD),
	EMERALD_BLOCK         (Material.EMERALD_BLOCK,           MP.PLACEABLE, MP.CRAFTABLE),
	EMERALD_ORE           (Material.EMERALD_ORE,             MP.PLACEABLE, MP.ORE),
	EMPTY_MAP             (Material.EMPTY_MAP),
	ENCHANTED_BOOK        (Material.ENCHANTED_BOOK),
	ENCHANTMENT_TABLE     (Material.ENCHANTMENT_TABLE,       MP.PLACEABLE, MP.CRAFTABLE, MP.GUI),
	ENDER_CHEST           (Material.ENDER_CHEST,             MP.PLACEABLE, MP.CRAFTABLE, MP.INVENTORY, MP.GUI),
	ENDER_PEARL           (Material.ENDER_PEARL,             MP.THROWABLE),
	ENDER_PORTAL          (Material.ENDER_PORTAL),
	ENDER_PORTAL_FRAME    (Material.ENDER_PORTAL_FRAME),
	ENDER_STONE           (Material.ENDER_STONE),
	EXP_BOTTLE            (Material.EXP_BOTTLE,              MP.THROWABLE),
	EXPLOSIVE_MINECART    (Material.EXPLOSIVE_MINECART),
	EYE_OF_ENDER          (Material.EYE_OF_ENDER),
	FEATHER               (Material.FEATHER),
	FENCE                 (Material.FENCE,                   MP.PLACEABLE, MP.CRAFTABLE),
	FENCE_GATE            (Material.FENCE_GATE,              MP.PLACEABLE, MP.CRAFTABLE),
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
	FURNACE               (Material.FURNACE,                 MP.PLACEABLE, MP.GUI),
	GHAST_TEAR            (Material.GHAST_TEAR),
	GLASS                 (Material.GLASS,                   MP.PLACEABLE, MP.CRAFTABLE),
	GLASS_BOTTLE          (Material.GLASS_BOTTLE,            MP.CRAFTABLE),
	GLOWING_REDSTONE_ORE  (Material.GLOWING_REDSTONE_ORE),
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
	GOLD_PLATE            (Material.GOLD_PLATE,              MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	GOLD_RECORD           (Material.GOLD_RECORD),
	GOLD_SPADE            (Material.GOLD_SPADE,              MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	GOLD_SWORD            (Material.GOLD_SWORD,              MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
	GOLDEN_APPLE          (Material.GOLDEN_APPLE,            MP.SUB_MATERIALS, MP.CRAFTABLE),
	GOLDEN_CARROT         (Material.GOLDEN_CARROT,           MP.CRAFTABLE),
	GRASS                 (Material.GRASS,                   MP.PLACEABLE),
	GRAVEL                (Material.GRAVEL,                  MP.PLACEABLE),
	GREEN_RECORD          (Material.GREEN_RECORD),
	GRILLED_PORK          (Material.GRILLED_PORK,            MP.CRAFTABLE),
	HARD_CLAY             (Material.HARD_CLAY,               MP.PLACEABLE),
	HAY_BLOCK             (Material.HAY_BLOCK,               MP.PLACEABLE),
	HOPPER                (Material.HOPPER,                  MP.PLACEABLE, MP.REDSTONE, MP.INVENTORY, MP.GUI, MP.CRAFTABLE),
	HOPPER_MINECART       (Material.HOPPER_MINECART),
	HUGE_MUSHROOM_1       (Material.HUGE_MUSHROOM_1),
	HUGE_MUSHROOM_2       (Material.HUGE_MUSHROOM_2),
	ICE                   (Material.ICE,                     MP.PLACEABLE),
	INK_SACK              (Material.INK_SACK,                MP.MULTICOLOR, MP.SUB_MATERIALS),
	IRON_AXE              (Material.IRON_AXE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	IRON_BARDING          (Material.IRON_BARDING,            MP.WEARABLE),
	IRON_BLOCK            (Material.IRON_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE),
	IRON_BOOTS            (Material.IRON_BOOTS,              MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
	IRON_CHESTPLATE       (Material.IRON_CHESTPLATE,         MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
	IRON_DOOR             (Material.IRON_DOOR,               MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	IRON_DOOR_BLOCK       (Material.IRON_DOOR_BLOCK,         MP.REDSTONE),
	IRON_FENCE            (Material.IRON_FENCE,              MP.PLACEABLE, MP.CRAFTABLE),
	IRON_HELMET           (Material.IRON_HELMET,             MP.PLACEABLE, MP.WEARABLE, MP.CRAFTABLE),
	IRON_HOE              (Material.IRON_HOE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	IRON_INGOT            (Material.IRON_INGOT,              MP.CRAFTABLE),
	IRON_LEGGINGS         (Material.IRON_LEGGINGS,           MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
	IRON_ORE              (Material.IRON_ORE,                MP.PLACEABLE, MP.ORE),
	IRON_PICKAXE          (Material.IRON_PICKAXE,            MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	IRON_PLATE            (Material.IRON_PLATE,              MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	IRON_SPADE            (Material.IRON_SPADE,              MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	IRON_SWORD            (Material.IRON_SWORD,              MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
	ITEM_FRAME            (Material.ITEM_FRAME,              MP.PLACEABLE, MP.CRAFTABLE),
	JACK_O_LANTERN        (Material.JACK_O_LANTERN,          MP.PLACEABLE, MP.WEARABLE),
	JUKEBOX               (Material.JUKEBOX,                 MP.PLACEABLE, MP.CRAFTABLE),
	JUNGLE_WOOD_STAIRS    (Material.JUNGLE_WOOD_STAIRS,      MP.PLACEABLE, MP.CRAFTABLE),
	LADDER                (Material.LADDER,                  MP.PLACEABLE, MP.CRAFTABLE),
	LAPIS_BLOCK           (Material.LAPIS_BLOCK,             MP.PLACEABLE, MP.CRAFTABLE),
	LAPIS_ORE             (Material.LAPIS_ORE,               MP.PLACEABLE, MP.ORE),
	LAVA                  (Material.LAVA,                    MP.PLACEABLE),
	LAVA_BUCKET           (Material.LAVA_BUCKET),
	LEASH                 (Material.LEASH),
	LEATHER               (Material.LEATHER),
	LEATHER_BOOTS         (Material.LEATHER_BOOTS,           MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
	LEATHER_CHESTPLATE    (Material.LEATHER_CHESTPLATE,      MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
	LEATHER_HELMET        (Material.LEATHER_HELMET,          MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
	LEATHER_LEGGINGS      (Material.LEATHER_LEGGINGS,        MP.REPAIRABLE, MP.WEARABLE, MP.CRAFTABLE),
	LEAVES                (Material.LEAVES,                  MP.SUB_MATERIALS, MP.PLACEABLE),
	LEAVES_2              (Material.LEAVES_2,                MP.SUB_MATERIALS, MP.PLACEABLE),
	LEVER                 (Material.LEVER,                   MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	LOG                   (Material.LOG,                     MP.SUB_MATERIALS, MP.PLACEABLE),
	LOG_2                 (Material.LOG_2,                   MP.SUB_MATERIALS, MP.PLACEABLE),
	LONG_GRASS            (Material.LONG_GRASS,              MP.SUB_MATERIALS),
	MAGMA_CREAM           (Material.MAGMA_CREAM),
	MAP                   (Material.MAP),
	MELON                 (Material.MELON),
	MELON_BLOCK           (Material.MELON_BLOCK,             MP.PLACEABLE),
	MELON_SEEDS           (Material.MELON_SEEDS),
	MELON_STEM            (Material.MELON_STEM),
	MILK_BUCKET           (Material.MILK_BUCKET),
	MINECART              (Material.MINECART,                MP.CRAFTABLE),
	MOB_SPAWNER           (Material.MOB_SPAWNER),
	MONSTER_EGG           (Material.MONSTER_EGG),
	MONSTER_EGGS          (Material.MONSTER_EGGS),
	MOSSY_COBBLESTONE     (Material.MOSSY_COBBLESTONE,       MP.PLACEABLE),
	MUSHROOM_SOUP         (Material.MUSHROOM_SOUP,           MP.CRAFTABLE),
	MYCEL                 (Material.MYCEL,                   MP.PLACEABLE),
	NAME_TAG              (Material.NAME_TAG),
	NETHER_BRICK          (Material.NETHER_BRICK,            MP.PLACEABLE),
	NETHER_BRICK_ITEM     (Material.NETHER_BRICK_ITEM),
	NETHER_BRICK_STAIRS   (Material.NETHER_BRICK_STAIRS,     MP.PLACEABLE, MP.CRAFTABLE),
	NETHER_FENCE          (Material.NETHER_FENCE,            MP.PLACEABLE, MP.CRAFTABLE),
	NETHER_STALK          (Material.NETHER_STALK), // Nether wart item
	NETHER_STAR           (Material.NETHER_STAR),
	NETHER_WARTS          (Material.NETHER_WARTS,            MP.PLACEABLE),
	NETHERRACK            (Material.NETHERRACK,              MP.PLACEABLE),
	NOTE_BLOCK            (Material.NOTE_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE),
	OBSIDIAN              (Material.OBSIDIAN,                MP.PLACEABLE),
	PACKED_ICE            (Material.PACKED_ICE,              MP.PLACEABLE),
	PAINTING              (Material.PAINTING,                MP.PLACEABLE, MP.CRAFTABLE),
	PAPER                 (Material.PAPER,                   MP.CRAFTABLE),
	PISTON_BASE           (Material.PISTON_BASE),
	PISTON_EXTENSION      (Material.PISTON_EXTENSION),
	PISTON_MOVING_PIECE   (Material.PISTON_MOVING_PIECE),
	PISTON_STICKY_BASE    (Material.PISTON_STICKY_BASE),
	POISONOUS_POTATO      (Material.POISONOUS_POTATO,        MP.CRAFTABLE),
	PORK                  (Material.PORK),
	PORTAL                (Material.PORTAL),
	POTATO                (Material.POTATO),
	POTATO_ITEM           (Material.POTATO_ITEM),
	POTION                (Material.POTION),
	POWERED_MINECART      (Material.POWERED_MINECART),
	POWERED_RAIL          (Material.POWERED_RAIL,            MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	PUMPKIN               (Material.PUMPKIN,                 MP.PLACEABLE),
	PUMPKIN_PIE           (Material.PUMPKIN_PIE,             MP.CRAFTABLE),
	PUMPKIN_SEEDS         (Material.PUMPKIN_SEEDS),
	PUMPKIN_STEM          (Material.PUMPKIN_STEM),
	QUARTZ                (Material.QUARTZ),
	QUARTZ_BLOCK          (Material.QUARTZ_BLOCK,            MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE),
	QUARTZ_ORE            (Material.QUARTZ_ORE,              MP.PLACEABLE, MP.ORE),
	QUARTZ_STAIRS         (Material.QUARTZ_STAIRS,           MP.PLACEABLE, MP.CRAFTABLE),
	RAILS                 (Material.RAILS,                   MP.PLACEABLE, MP.CRAFTABLE),
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
	RED_MUSHROOM          (Material.RED_MUSHROOM),
	RED_ROSE              (Material.RED_ROSE,                MP.SUB_MATERIALS),
	REDSTONE              (Material.REDSTONE,                MP.PLACEABLE, MP.REDSTONE),
	REDSTONE_BLOCK        (Material.REDSTONE_BLOCK,          MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	REDSTONE_COMPARATOR   (Material.REDSTONE_COMPARATOR,     MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	REDSTONE_COMPARATOR_OFF(Material.REDSTONE_COMPARATOR_OFF, MP.REDSTONE),
	REDSTONE_COMPARATOR_ON(Material.REDSTONE_COMPARATOR_ON,  MP.REDSTONE),
	REDSTONE_LAMP_OFF     (Material.REDSTONE_LAMP_OFF,       MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	REDSTONE_LAMP_ON      (Material.REDSTONE_LAMP_ON,        MP.REDSTONE),
	REDSTONE_ORE          (Material.REDSTONE_ORE,            MP.PLACEABLE),
	REDSTONE_TORCH_OFF    (Material.REDSTONE_TORCH_OFF,      MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	REDSTONE_TORCH_ON     (Material.REDSTONE_TORCH_ON,       MP.REDSTONE),
	REDSTONE_WIRE         (Material.REDSTONE_WIRE,           MP.REDSTONE),
	ROTTEN_FLESH          (Material.ROTTEN_FLESH),
	SADDLE                (Material.SADDLE),
	SAND                  (Material.SAND,                    MP.SUB_MATERIALS, MP.PLACEABLE),
	SANDSTONE             (Material.SANDSTONE,               MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE),
	SANDSTONE_STAIRS      (Material.SANDSTONE_STAIRS,        MP.PLACEABLE, MP.CRAFTABLE),
	SAPLING               (Material.SAPLING,                 MP.SUB_MATERIALS, MP.PLACEABLE),
	SEEDS                 (Material.SEEDS,                   MP.SUB_MATERIALS, MP.PLACEABLE),
	SHEARS                (Material.SHEARS,                  MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	SIGN                  (Material.SIGN,                    MP.PLACEABLE, MP.CRAFTABLE),
	SIGN_POST             (Material.SIGN_POST),
	SKULL                 (Material.SKULL),
	SKULL_ITEM            (Material.SKULL_ITEM,              MP.PLACEABLE, MP.WEARABLE),
	SLIME_BALL            (Material.SLIME_BALL),
	SMOOTH_BRICK          (Material.SMOOTH_BRICK,            MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE),
	SMOOTH_STAIRS         (Material.SMOOTH_STAIRS,           MP.PLACEABLE, MP.CRAFTABLE),
	SNOW                  (Material.SNOW),
	SNOW_BALL             (Material.SNOW_BALL,               MP.THROWABLE),
	SNOW_BLOCK            (Material.SNOW_BLOCK,              MP.PLACEABLE, MP.CRAFTABLE),
	SOIL                  (Material.SOIL),
	SOUL_SAND             (Material.SOUL_SAND,               MP.PLACEABLE),
	SPECKLED_MELON        (Material.SPECKLED_MELON), // Glistering Melon
	SPIDER_EYE            (Material.SPIDER_EYE),
	SPONGE                (Material.SPONGE,                  MP.SUB_MATERIALS, MP.PLACEABLE),
	SPRUCE_WOOD_STAIRS    (Material.SPRUCE_WOOD_STAIRS,      MP.PLACEABLE, MP.CRAFTABLE),
	STAINED_CLAY          (Material.STAINED_CLAY,            MP.PLACEABLE, MP.MULTICOLOR, MP.CRAFTABLE),
	STAINED_GLASS_PANE    (Material.STAINED_GLASS_PANE,      MP.PLACEABLE, MP.MULTICOLOR, MP.CRAFTABLE),
	STAINED_GLASS         (Material.STAINED_GLASS,           MP.PLACEABLE, MP.MULTICOLOR, MP.CRAFTABLE),
	STATIONARY_LAVA       (Material.STATIONARY_LAVA),
	STATIONARY_WATER      (Material.STATIONARY_WATER),
	STEP                  (Material.STEP,                    MP.PLACEABLE, MP.SUB_MATERIALS, MP.CRAFTABLE),
	STICK                 (Material.STICK),
	STONE                 (Material.STONE,                   MP.PLACEABLE, MP.SUB_MATERIALS, MP.CRAFTABLE),
	STONE_AXE             (Material.STONE_AXE,               MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	STONE_BUTTON          (Material.STONE_BUTTON,            MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	STONE_HOE             (Material.STONE_HOE,               MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	STONE_PICKAXE         (Material.STONE_PICKAXE,           MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	STONE_PLATE           (Material.STONE_PLATE,             MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	STONE_SPADE           (Material.STONE_SPADE,             MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	STONE_SWORD           (Material.STONE_SWORD,             MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
	STORAGE_MINECART      (Material.STORAGE_MINECART),
	STRING                (Material.STRING),
	SUGAR                 (Material.SUGAR),
	SUGAR_CANE            (Material.SUGAR_CANE,              MP.PLACEABLE),
	SUGAR_CANE_BLOCK      (Material.SUGAR_CANE_BLOCK),
	SULPHUR               (Material.SULPHUR), // gunpowder
	THIN_GLASS            (Material.THIN_GLASS,              MP.PLACEABLE, MP.CRAFTABLE),
	TNT                   (Material.TNT,                     MP.PLACEABLE, MP.CRAFTABLE),
	TORCH                 (Material.TORCH,                   MP.PLACEABLE, MP.CRAFTABLE),
	TRAP_DOOR             (Material.TRAP_DOOR,               MP.PLACEABLE, MP.CRAFTABLE),
	TRAPPED_CHEST         (Material.TRAPPED_CHEST,           MP.PLACEABLE, MP.CRAFTABLE),
	TRIPWIRE              (Material.TRIPWIRE),
	TRIPWIRE_HOOK         (Material.TRIPWIRE_HOOK,           MP.PLACEABLE, MP.CRAFTABLE),
	VINE                  (Material.VINE,                    MP.PLACEABLE),
	WALL_SIGN             (Material.WALL_SIGN),
	WATCH                 (Material.WATCH,                   MP.CRAFTABLE),
	WATER                 (Material.WATER),
	WATER_BUCKET          (Material.WATER_BUCKET),
	WATER_LILY            (Material.WATER_LILY,              MP.PLACEABLE),
	WEB                   (Material.WEB,                     MP.PLACEABLE),
	WHEAT                 (Material.WHEAT,                   MP.PLACEABLE),
	WOOD                  (Material.WOOD,                    MP.SUB_MATERIALS, MP.PLACEABLE, MP.CRAFTABLE),
	WOOD_AXE              (Material.WOOD_AXE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	WOOD_BUTTON           (Material.WOOD_BUTTON,             MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	WOOD_DOOR             (Material.WOOD_DOOR,               MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	WOOD_DOUBLE_STEP      (Material.WOOD_DOUBLE_STEP,        MP.SUB_MATERIALS),
	WOOD_HOE              (Material.WOOD_HOE,                MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	WOOD_PICKAXE          (Material.WOOD_PICKAXE,            MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	WOOD_PLATE            (Material.WOOD_PLATE,              MP.PLACEABLE, MP.REDSTONE, MP.CRAFTABLE),
	WOOD_SPADE            (Material.WOOD_SPADE,              MP.REPAIRABLE, MP.TOOL, MP.CRAFTABLE),
	WOOD_STAIRS           (Material.WOOD_STAIRS,             MP.PLACEABLE, MP.CRAFTABLE),
	WOOD_STEP             (Material.WOOD_STEP,               MP.SUB_MATERIALS),
	WOOD_SWORD            (Material.WOOD_SWORD,              MP.REPAIRABLE, MP.WEAPON, MP.CRAFTABLE),
	WOODEN_DOOR           (Material.WOODEN_DOOR,             MP.REDSTONE),// block
	WOOL                  (Material.WOOL,                    MP.PLACEABLE, MP.MULTICOLOR, MP.CRAFTABLE),
	WORKBENCH             (Material.WORKBENCH,               MP.PLACEABLE, MP.CRAFTABLE, MP.GUI),
	WRITTEN_BOOK          (Material.WRITTEN_BOOK,            MP.GUI),
	YELLOW_FLOWER         (Material.YELLOW_FLOWER)
	
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
		BLOCK(32768);
		
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
        REPAIRABLE(16384);
        // BLOCK 65536;
        
        private final int _bit;
        
        MP(int bit) {
            _bit = bit;
        }
        
        public int getBit() {
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
	private final int _maxStackSize;
	
	MaterialExt(Material material, MP...properties) {

		_material = material;
		
		int flags = 0;
		
		for (MP property : properties) {
			flags |= property.getBit();
		}

        if (material != null) {
            if (material.isBlock())
                flags |= MaterialProperty.BLOCK.getBit();

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
