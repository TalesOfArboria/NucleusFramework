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


package com.jcwhatever.bukkit.generic.player;

import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Saves and restores snapshots of a players state.
 * <p>
 *     The players state consists of inventory items, armor, applied enchantments,
 *     game mode, flight, health, food level, exp, fire ticks, fall distance,
 *     and location.
 * </p>
 */
public class PlayerState {

    // store player states by plugin
    private static Map<Plugin, PlayerMap<PlayerState>> _statesByPlugin;

    static {
        _statesByPlugin = new WeakHashMap<>(50);
    }

    /**
     * Get the currently stored player state.
     *
     * <p>
     *     If no player state is found in memory, an attempt
     *     is made to see if a player state is saved to disk.
     * </p>
     *
     * @param plugin  The owning plugin.
     * @param p       The player.
     *
     * @return  Null if no player state is stored.
     */
    @Nullable
    public static PlayerState get(Plugin plugin, Player p) {
        PlayerMap<PlayerState> states = getStateMap(plugin);
        PlayerState state = states.get(p.getUniqueId());

        // load state from file, if any
        if (state == null) {
            state = new PlayerState(plugin, p);
            if (!loadFromFile(state))
                return null;

            states.put(p.getUniqueId(), state);
        }

        return state;
    }

    /**
     * Saves a player current state and returns the information in
     * a {@code PlayerState} object.
     *
     * <p>
     *     If a player state is already saved for the specified plugin,
     *     the current player state data overwrites the data in the
     *     existing {@code PlayerState} object. Otherwise, a new object
     *     is created.
     * </p>
     *
     * @param plugin  The owning plugin.
     * @param p       The player.
     */
    public static PlayerState store(Plugin plugin, Player p) {
        PlayerMap<PlayerState> states = getStateMap(plugin);

        PlayerState state = states.get(p.getUniqueId());
        if (state == null) {
            state = new PlayerState(plugin, p);
            states.put(p.getUniqueId(), state);
        }

        state.save();

        return state;
    }

    /**
     * Specify if the players location should be restored.
     */
    public enum RestoreLocation {
        TRUE,
        FALSE
    }

    private final Player _player;
    private final UUID _playerId;
    private final Plugin _plugin;

    private boolean _isSaved;
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
     * Private Constructor.
     *
     * @param plugin  The owning plugin.
     * @param p       The player.
     */
    private PlayerState(Plugin plugin, Player p) {
        _player = p;
        _playerId = p.getUniqueId();
        _plugin = plugin;
    }

    /**
     * Determine if the player state is saved.
     */
    public boolean isSaved() {
        return _isSaved;
    }

    /**
     * Get the saved items.
     */
    public ItemStack[] getSavedItems() {
        return _items;
    }

    /**
     * Get the saved armor.
     */
    public ItemStack[] getSavedArmor() {
        return _armor;
    }

    public Location getSavedLocation() {
        return _location;
    }

    /**
     * Records player inventory and saves to disk.
     *
     * @return  True if saving to disk is successful. Save to memory is always successful.
     */
    public boolean save() {

        _isSaved = true;

        _items = _player.getInventory().getContents().clone();
        _armor = _player.getInventory().getArmorContents().clone();
        _location = _player.getLocation();
        _gameMode = _player.getGameMode();
        _potions = new ArrayList<PotionEffect>(_player.getActivePotionEffects());
        _health = _player.getHealth();
        _food = _player.getFoodLevel();
        _level = _player.getLevel();
        _exp = _player.getExp();
        _flight = _player.isFlying();
        _allowFlight = _player.getAllowFlight();
        _fireTicks = _player.getFireTicks();
        _fallDistance = _player.getFallDistance();

        if (_plugin == null)
            return false;

        IDataNode config = DataStorage.getTransientStorage(_plugin, new DataPath("player-states." + _playerId));
        config.set("items", _items);
        config.set("armor", _armor);
        config.set("location", _location);
        config.set("gamemode", _gameMode);
        config.set("health", _health);
        config.set("food", _food);
        config.set("levels", _level);
        config.set("exp", _exp);
        config.set("flight", _flight);
        config.set("allow-flight", _allowFlight);
        config.set("fall-distance", _fallDistance);
        config.set("fire-ticks", _fireTicks);

        IDataNode potionNode = config.getNode("potions");

        for (PotionEffect effect : _potions) {
            IDataNode effectNode = potionNode.getNode(UUID.randomUUID().toString());
            setEffectToNode(effect, effectNode);
        }

        config.saveAsync(null);
        return true;
    }

    /**
     * Restore the players state.
     * <p>
     *     The stored to disk player state is deleted.
     * </p>
     *
     * @param restoreLocation  Specify if the players saved location should be restored.
     *
     * @return  The restore location or null if no location is stored.
     *
     * @throws IOException
     * @throws InvalidConfigurationException
     */
    @Nullable
    public Location restore(RestoreLocation restoreLocation) throws IOException, InvalidConfigurationException {
        PreCon.notNull(restoreLocation);

        _isSaved = false;

        if (restoreLocation == RestoreLocation.TRUE && _location != null)
            LocationUtils.teleportCentered(_player, _location);

        // wait till after the player is teleported to restore
        Scheduler.runTaskLater(_plugin, 2, new Runnable() {
            @Override
            public void run() {

                // remove all potion effects
                for (PotionEffectType potion : PotionEffectType.values()) {
                    if (potion == null || !_player.hasPotionEffect(potion)) continue;
                    _player.removePotionEffect(potion);
                }
                _player.getActivePotionEffects().clear();

                _player.getInventory().setContents(_items);
                _player.getInventory().setArmorContents(_armor);
                _player.setFoodLevel(_food);
                _player.setLevel(_level);
                _player.setExp(_exp);
                _player.setHealth(_health);
                _player.addPotionEffects(_potions);
                _player.setGameMode(_gameMode);
                _player.setAllowFlight(_allowFlight);
                _player.setFlying(_flight);
                _player.setFireTicks(_fireTicks);
                _player.setFallDistance(0);// prevent player respawn deaths
            }
        });

        // remove back up state storage
        DataStorage.removeTransientStorage(_plugin, new DataPath("player-states." + _playerId));

        // remove from state map
        getStateMap(_plugin).remove(_player.getUniqueId());

        // return restore location
        return _location != null ? LocationUtils.getCenteredLocation(_location) : null;
    }

    /*
     * Get a player state map for the specified plugin.
      * Finds the existing one or creates a new one.
     */
    private static PlayerMap<PlayerState> getStateMap(Plugin plugin) {
        PlayerMap<PlayerState> state = _statesByPlugin.get(plugin);
        if (state == null) {
            state = new PlayerMap<PlayerState>();
            _statesByPlugin.put(plugin, state);
        }
        return state;
    }

    /*
     *  Loads player state data from a file a fills the provided
     *  PlayerState object with the data.
     */
    private static boolean loadFromFile(PlayerState state) {

        if (!DataStorage.hasTransientStorage(state._plugin, new DataPath("player-states." + state._playerId)))
            return false;

        IDataNode config = DataStorage.getTransientStorage(state._plugin, new DataPath("player-states." + state._playerId));
        if (!config.load())
            return false;

        state._items = config.getItemStacks("items");
        state._armor = config.getItemStacks("armor");
        state._location = config.getLocation("location");
        state._gameMode = config.getEnum("gamemode", GameMode.SURVIVAL, GameMode.class);
        state._health = config.getDouble("health", 20);
        state._food = config.getInteger("food", 20);
        state._level = config.getInteger("levels", 0);
        state._exp = (float)config.getDouble("exp", 0);
        state._flight = config.getBoolean("flight", false);
        state._allowFlight = config.getBoolean("allow-flight", false);
        state._fireTicks = config.getInteger("fire-ticks", 0);
        state._fallDistance = (float)config.getDouble("fall-distance", 0);

        try {

            Set<String> potionNodeNames = config.getNode("potions").getSubNodeNames();

            state._potions = new ArrayList<>(potionNodeNames.size());

            for (String nodeName : potionNodeNames) {

                IDataNode dataNode = config.getNode(nodeName);

                PotionEffect effect = getEffectFromNode(dataNode);
                if (effect == null)
                    continue;

                state._potions.add(effect);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
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
