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


package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.sounds.ResourceSound;
import com.jcwhatever.bukkit.generic.sounds.SoundManager;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


@IScriptApiInfo(
        variableName = "sounds",
        description = "Provide scripts with API access to resource sounds.")
public class ScriptApiSounds extends GenericsScriptApi {

    private ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiSounds(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject(getPlugin());

        return _api;
    }

    @Override
    public void reset() {
        if (_api != null)
            _api.reset();
    }

    public static class ApiObject implements IScriptApiObject {

        private final Plugin _plugin;

        ApiObject(Plugin plugin) {
            _plugin = plugin;
        }

        @Override
        public void reset() {
            // do nothing
        }

        /**
         * Play a resource sound to a player.
         *
         * @param player     The player.
         * @param soundName  The name of the resource sound.
         */
        public boolean play(Object player, String soundName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(soundName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            ResourceSound sound = SoundManager.getSound(soundName);
            if (sound == null)
                return false;

            SoundManager.playSound(_plugin, p, sound, p.getLocation(), 500.0f, null);
            return true;
        }

        /**
         * Play a resource sound to a player at the specified coordinates
         * and with the specified volume.
         *
         * @param player     The player.
         * @param soundName  The name of the resource sound.
         * @param x          The X coordinates.
         * @param y          The Y coordinates.
         * @param z          The Z coordinates.
         * @param volume     The volume.
         */
        public boolean playAt(Object player, String soundName, int x, int y, int z, double volume) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(soundName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            Location location = new Location(p.getWorld(), x, y, z);

            ResourceSound sound = SoundManager.getSound(soundName);
            if (sound == null)
                return false;

            SoundManager.playSound(_plugin, p, sound, location, (float)volume, null);
            return false;
        }

        /**
         * Play a resource sound to a player at the specified coordinates
         * and with the specified volume.
         *
         * @param player     The player.
         * @param soundName  The name of the resource sound.
         * @param location   The location to play at.
         * @param volume     The volume.
         */
        public boolean playLocation(Object player, String soundName, Location location, double volume) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(soundName);
            PreCon.notNull(location);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            ResourceSound sound = SoundManager.getSound(soundName);
            if (sound == null)
                return false;

            SoundManager.playSound(_plugin, p, sound, location, (float)volume, null);
            return false;
        }
    }

}
