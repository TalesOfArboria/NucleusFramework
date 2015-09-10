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

package com.jcwhatever.nucleus.internal.managed.nms;

import com.jcwhatever.nucleus.providers.npc.Npcs;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.nms.INmsEffectHandler;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of {@link INmsEffectHandler}.
 */
class NmsEffectHandler extends AbstractNMSHandler implements INmsEffectHandler {

    private static final PlayerDistanceValidator PLAYER_VALIDATAOR = new PlayerDistanceValidator();

    @Override
    public Collection<Player> lightningStrike(Location location, double radius, boolean sound) {
        PreCon.notNull(location);
        PreCon.greaterThanZero(radius);

        PLAYER_VALIDATAOR.set(location, radius);
        List<Player> nearby = PlayerUtils.getNearbyPlayers(
                location, Math.max(1, (int) radius >> 4), PLAYER_VALIDATAOR);

        if (nearby.isEmpty())
            return nearby;

        Object packet = nms().getLightningPacket(location);
        Object ambientPacket = sound ? getAmbientSoundPacket(location) : null;
        Object soundPacket = sound ? getThunderSoundPacket(location) : null;

        for (Player player : nearby) {
            nms().sendPacket(player, packet);

            if (ambientPacket != null) {
                nms().sendPacket(player, ambientPacket);
            }

            if (soundPacket != null) {
                nms().sendPacket(player, soundPacket);
            }
        }

        return nearby;
    }

    @Override
    public <T extends Collection<Player>> T lightningStrike(Location location, double radius, boolean sound, T output) {
        PreCon.notNull(location);
        PreCon.greaterThanZero(radius);

        PLAYER_VALIDATAOR.set(location, radius);
        List<Player> nearby = PlayerUtils.getNearbyPlayers(
                location, (int) Math.ceil(radius / 16), PLAYER_VALIDATAOR);

        if (nearby.isEmpty())
            return output;

        if (output instanceof ArrayList) {
            ((ArrayList) output).ensureCapacity(output.size() + nearby.size());
        }

        Object packet = nms().getLightningPacket(location);
        Object ambientPacket = sound ? getAmbientSoundPacket(location) : null;
        Object soundPacket = sound ? getThunderSoundPacket(location) : null;

        for (Player player : nearby) {
            nms().sendPacket(player, packet);

            if (ambientPacket != null) {
                nms().sendPacket(player, ambientPacket);
            }

            if (soundPacket != null) {
                nms().sendPacket(player, soundPacket);
            }

            output.add(player);
        }

        return output;
    }

    @Override
    public void lightningStrike(Player player, Location location, boolean sound) {
        PreCon.notNull(player, "player");
        PreCon.notNull(location, "location");
        PreCon.notNull(location.getWorld(), "location world");

        Object packet = nms().getLightningPacket(location);
        nms().sendPacket(player, packet);

        if (sound) {
            nms().sendPacket(player, getAmbientSoundPacket(location));
            nms().sendPacket(player, getThunderSoundPacket(location));
        }
    }

    @Override
    public void lightningStrike(Collection<Player> players, Location location, boolean sound) {
        PreCon.notNull(players, "players");
        PreCon.notNull(location, "location");
        PreCon.notNull(location.getWorld(), "location world");

        Object packet = nms().getLightningPacket(location);
        Object ambientPacket = sound ? getAmbientSoundPacket(location) : null;
        Object soundPacket = sound ? getThunderSoundPacket(location) : null;

        for (Player player : players) {
            nms().sendPacket(player, packet);

            if (ambientPacket != null) {
                nms().sendPacket(player, ambientPacket);
            }

            if (soundPacket != null) {
                nms().sendPacket(player, soundPacket);
            }
        }
    }

    private Object getAmbientSoundPacket(Location location) {
        return nms().getNamedSoundPacket("ambient.weather.thunder",
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                10000.0f, 0.8F + (float)Math.round(Rand.getInt() * 0.2D));
    }

    private Object getThunderSoundPacket(Location location) {
        return nms().getNamedSoundPacket("random.explode",
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                2.0f, 0.5F + (float)Math.round(Rand.getInt() * 0.2D));
    }

    private static class PlayerDistanceValidator implements IValidator<Player> {

        static final Location PLAYER_LOCATION = new Location(null, 0, 0, 0);
        final Location strikeLocation = new Location(null, 0, 0, 0);
        double radiusSquared = 0;

        void set(Location strikeLocation, double radius) {
            LocationUtils.copy(strikeLocation, this.strikeLocation);
            radiusSquared = radius * radius;
        }

        @Override
        public boolean isValid(Player player) {
            return !Npcs.isNpc(player) &&
                    player.getLocation(PLAYER_LOCATION).distanceSquared(strikeLocation) <= radiusSquared;
        }
    }
}
