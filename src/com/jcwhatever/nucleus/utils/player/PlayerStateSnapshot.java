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


package com.jcwhatever.nucleus.utils.player;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A snapshot of a players state.
 *
 * <p>The players state consists of chest items, armor, applied enchantments,
 * game mode, flight, health, food level, exp, fire ticks, fall distance,
 * and location.</p>
 */
public class PlayerStateSnapshot {

    /**
     * Loads a players state from a data node into a {@link PlayerStateSnapshot}.
     *
     * @param dataNode  The data node the player state is stored on.
     *
     * @return  Null if the node does not have valid player state data.
     */
    @Nullable
    public static PlayerStateSnapshot load(IDataNode dataNode) {

        PlayerStateSnapshot snapshot = new PlayerStateSnapshot();

        if ((snapshot._playerId = dataNode.getUUID("player-id")) == null)
            return null;

        if ((snapshot._items = dataNode.getItemStacks("items")) == null)
            return null;

        if ((snapshot._armor = dataNode.getItemStacks("armor")) == null)
            return null;

        if ((snapshot._location = dataNode.getLocation("location")) == null)
            return null;

        if ((snapshot._gameMode = dataNode.getEnum("gamemode", GameMode.class)) == null)
            return null;

        snapshot._health = dataNode.getDouble("health", 20);
        snapshot._food = dataNode.getInteger("food", 20);
        snapshot._level = dataNode.getInteger("levels", 0);
        snapshot._exp = (float)dataNode.getDouble("exp", 0);
        snapshot._flight = dataNode.getBoolean("flight", false);
        snapshot._allowFlight = dataNode.getBoolean("allow-flight", false);
        snapshot._fireTicks = dataNode.getInteger("fire-ticks", 0);
        snapshot._fallDistance = (float)dataNode.getDouble("fall-distance", 0);

        try {

            IDataNode potionsNode = dataNode.getNode("potions");

            snapshot._potions = new ArrayList<>(potionsNode.size());

            for (IDataNode potionNode : potionsNode) {

                PotionEffect effect = getEffectFromNode(potionNode);
                if (effect == null)
                    continue;

                snapshot._potions.add(effect);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return snapshot;
    }

    private UUID _playerId;
    private ItemStack[] _items;
    private ItemStack[] _armor;
    private Location _location;
    private GameMode _gameMode;
    private Collection<PotionEffect> _potions;
    private float _exp;
    private int _food;
    private int _level;
    private double _health;
    private boolean _flight;
    private boolean _allowFlight;
    private int _fireTicks;
    private double _fallDistance;

    /**
     * Constructor.
     *
     * @param p  The player to take a state snapshot from.
     */
    public PlayerStateSnapshot(Player p) {

        _playerId = p.getUniqueId();

        _items = p.getInventory().getContents().clone();
        _armor = p.getInventory().getArmorContents().clone();
        _location = p.getLocation();
        _gameMode = p.getGameMode();
        _potions = new ArrayList<PotionEffect>(p.getActivePotionEffects());
        _health = p.getHealth();
        _food = p.getFoodLevel();
        _level = p.getLevel();
        _exp = p.getExp();
        _flight = p.isFlying();
        _allowFlight = p.getAllowFlight();
        _fireTicks = p.getFireTicks();
        _fallDistance = p.getFallDistance();
    }

    /**
     * Private Constructor.
     */
    private PlayerStateSnapshot() {

    }

    /**
     * Get the ID of the player the snapshot was
     * taken from.
     */
    public UUID getPlayerId() {
        return _playerId;
    }

    /**
     * Get the player chest items.
     */
    public ItemStack[] getItems() {
        return _items.clone();
    }

    /**
     * Get the player armor chest.
     */
    public ItemStack[] getArmor() {
        return _armor.clone();
    }

    /**
     * Get the player location.
     */
    public Location getLocation() {
        return _location;
    }

    /**
     * Get the players {@link org.bukkit.GameMode}.
     */
    public GameMode getGameMode() {
        return _gameMode;
    }

    /**
     * Get the players active potion effects.
     */
    public List<PotionEffect> getPotionEffects() {
        return new ArrayList<>(_potions);
    }

    /**
     * Get the players health.
     */
    public double getHealth() {
        return _health;
    }

    /**
     * Get the players food level.
     */
    public int getFoodLevel() {
        return _food;
    }

    /**
     * Get the players food level.
     */
    public int getLevel() {
        return _level;
    }

    /**
     * Get the players Experience.
     */
    public double getExp() {
        return _exp;
    }

    /**
     * Determine if the player is flying.
     */
    public boolean isFlying() {
        return _flight;
    }

    /**
     * Determine if flight is allowed for the player.
     */
    public boolean isFlightAllowed() {
        return _allowFlight;
    }

    /**
     * Get the players fire ticks.
     */
    public int getFireTicks() {
        return _fireTicks;
    }

    /**
     * Get the fall distance.
     */
    public double getFallDistance() {
        return _fallDistance;
    }

    /**
     * Save the player state to a data node.
     *
     * @param dataNode  The data node to store player state data on.
     */
    public void save(IDataNode dataNode) {

        dataNode.set("player-id", _playerId);
        dataNode.set("items", _items);
        dataNode.set("armor", _armor);
        dataNode.set("location", _location);
        dataNode.set("gamemode", _gameMode);
        dataNode.set("health", _health);
        dataNode.set("food", _food);
        dataNode.set("levels", _level);
        dataNode.set("exp", _exp);
        dataNode.set("flight", _flight);
        dataNode.set("allow-flight", _allowFlight);
        dataNode.set("fall-distance", _fallDistance);
        dataNode.set("fire-ticks", _fireTicks);

        IDataNode potionNode = dataNode.getNode("potions");

        potionNode.remove(); // clear node

        for (PotionEffect effect : _potions) {
            IDataNode effectNode = potionNode.getNode(UUID.randomUUID().toString());
            setEffectToNode(effect, effectNode);
        }

        dataNode.save();
    }

    /*
     * Load a new potion effect from effect data on the specified data node.
     */
    @Nullable
    private static PotionEffect getEffectFromNode(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        int amplifier = dataNode.getInteger("amplifier");
        int duration = dataNode.getInteger("duration");
        boolean isAmbient = dataNode.getBoolean("ambient");

        String typeName = dataNode.getString("effect-name");
        if (typeName == null)
            return null;

        PotionEffectType type = PotionEffectType.getByName(typeName);
        if (type == null)
            return null;

        return new PotionEffect(type, duration, amplifier, isAmbient);
    }

    /*
     * Sets potion effect data to the specified node. Does not save the node.
     */
    private static void setEffectToNode(PotionEffect effect, IDataNode dataNode) {
        PreCon.notNull(effect);
        PreCon.notNull(dataNode);

        dataNode.set("amplifier", effect.getAmplifier());
        dataNode.set("duration", effect.getDuration());
        dataNode.set("ambient", effect.isAmbient());
        dataNode.set("effect-name", effect.getType().getName());
    }


}
