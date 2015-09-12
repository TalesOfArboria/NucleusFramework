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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.nms.INmsEffectHandler;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of {@link INmsEffectHandler}.
 */
class NmsEffectHandler extends AbstractNMSHandler implements INmsEffectHandler {

    @Override
    public Collection<Player> lightningStrike(Location location, double radius, boolean sound) {
        PreCon.notNull(location);
        PreCon.greaterThanZero(radius);

        Collection<Player> nearby = PlayerUtils.getNearbyPlayers(location, radius);
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

        Collection<Player> players = lightningStrike(location, radius, sound);

        if (output instanceof ArrayList) {
            ((ArrayList) output).ensureCapacity(output.size() + players.size());
        }
        output.addAll(players);
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
                10000.0f, (float)Rand.getDouble());
    }

    private Object getThunderSoundPacket(Location location) {
        return nms().getNamedSoundPacket("random.explode",
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                2.0f, (float)Rand.getDouble());
    }
}
