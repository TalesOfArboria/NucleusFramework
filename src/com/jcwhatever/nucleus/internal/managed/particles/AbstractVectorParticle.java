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

package com.jcwhatever.nucleus.internal.managed.particles;

import com.jcwhatever.nucleus.managed.particles.IVectorParticle;
import com.jcwhatever.nucleus.managed.particles.ParticleType;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ThreadSingletons;
import com.jcwhatever.nucleus.utils.coords.ICoords3D;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract vector particle.
 */
abstract class AbstractVectorParticle extends AbstractParticle implements IVectorParticle {

    private static final ThreadSingletons<Location> LOCATIONS = LocationUtils.createThreadSingleton();

    /**
     * Constructor.
     *
     * @param type  The particle type.
     */
    AbstractVectorParticle(ParticleType type) {
        super(type);
    }

    @Override
    public boolean showTo(Player player, Location location, Vector vector) {
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.notNull(vector);

        return player.getWorld().equals(location.getWorld()) &&
                showTo(ArrayUtils.asList(player),
                    location.getX(), location.getY(), location.getZ(), vector);
    }

    @Override
    public boolean showTo(Player player, ICoords3D coords, Vector vector) {
        PreCon.notNull(player);
        PreCon.notNull(coords);
        PreCon.notNull(vector);

        return showTo(ArrayUtils.asList(player),
                coords.getX(), coords.getY(), coords.getZ(), vector);
    }

    @Override
    public boolean showTo(Player player, ICoords3Di coords, Vector vector) {
        PreCon.notNull(player);
        PreCon.notNull(coords);
        PreCon.notNull(vector);

        return showTo(ArrayUtils.asList(player),
                coords.getX(), coords.getY(), coords.getZ(), vector);
    }

    @Override
    public boolean showTo(Collection<? extends Player> players, Location location, Vector vector) {
        PreCon.notNull(players);
        PreCon.notNull(location);
        PreCon.notNull(vector);

        List<Player> list = new ArrayList<>(players.size());

        for (Player player : players) {
            if (!player.getWorld().equals(location.getWorld()))
                continue;

            list.add(player);
        }

        return showTo(list, location.getX(), location.getY(), location.getZ(), vector);
    }

    @Override
    public boolean showTo(Collection<? extends Player> players, ICoords3D coords, Vector vector) {
        PreCon.notNull(players);
        PreCon.notNull(coords);
        PreCon.notNull(vector);

        return showTo(players, coords.getX(), coords.getY(), coords.getZ(), vector);
    }

    @Override
    public boolean showTo(Collection<? extends Player> players, ICoords3Di coords, Vector vector) {
        PreCon.notNull(players);
        PreCon.notNull(coords);
        PreCon.notNull(vector);

        return showTo(players, coords.getX(), coords.getY(), coords.getZ(), vector);
    }

    @Override
    public boolean showFrom(Location location, Vector vector) {
        PreCon.notNull(location);
        PreCon.notNull(vector);

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        List<Player> visible = new ArrayList<>(30);

        for (Player player : players) {

            if (!player.getWorld().equals(location.getWorld()))
                continue;

            Location playerLocation = player.getLocation(LOCATIONS.get());

            if (playerLocation.distanceSquared(location) > radiusSquared())
                continue;

            visible.add(player);
        }

        return !visible.isEmpty() &&
                showTo(visible, location.getX(), location.getY(), location.getZ(), vector);

    }

    private boolean showTo(Collection<? extends Player> players,
                           double x, double y, double z, Vector vector) {

        INmsParticleEffectHandler handler = NmsUtils.getParticleEffectHandler();
        if (handler == null)
            return false;

        showVectoredTo(handler, players, x, y, z, vector);
        return true;
    }

    protected void showVectoredTo(INmsParticleEffectHandler handler,
                                  Collection<? extends Player> players,
                                  double x, double y, double z, Vector vector) {

        handler.send(players, getType(), true, x, y, z,
                vector.getX(), vector.getY(), vector.getZ(), getNmsSpeed(), 0);
    }
}
