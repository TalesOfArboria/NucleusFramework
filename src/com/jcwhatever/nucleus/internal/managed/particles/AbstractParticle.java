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

import com.jcwhatever.nucleus.managed.particles.IColoredParticle;
import com.jcwhatever.nucleus.managed.particles.IDirectionalParticle;
import com.jcwhatever.nucleus.managed.particles.IParticleEffect;
import com.jcwhatever.nucleus.managed.particles.ParticleType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ThreadSingletons;
import com.jcwhatever.nucleus.utils.ThreadSingletons.ISingletonFactory;
import com.jcwhatever.nucleus.utils.coords.ICoords3D;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

/**
 * Abstract implementation of {@link IParticleEffect}.
 */
abstract class AbstractParticle implements IParticleEffect {

    private static ThreadSingletons<Vector> VECTORS =
            new ThreadSingletons<>(new ISingletonFactory<Vector>() {
                @Override
                public Vector create() {
                    return new Vector(0, 0, 0);
                }
            });

    private static ThreadSingletons<Location> LOCATIONS = LocationUtils.createThreadSingleton();

    private final ParticleType _type;
    private double _radius = 20;
    private double _radiusSquared = 20 * 20;

    /**
     * Constructor.
     *
     * @param type  The particle type.
     */
    AbstractParticle(ParticleType type) {
        _type = type;
    }

    @Override
    public ParticleType getType() {
        return _type;
    }

    @Override
    public double getRadius() {
        return _radius;
    }

    @Override
    public void setRadius(double radius) {
        _radius = radius;
        _radiusSquared = radius * radius;
    }

    @Override
    public float getData() {
        return 0;
    }

    @Override
    public boolean showTo(Player player, Location location, int count) {
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.greaterThanZero(count);
        PreCon.isValid(player.getWorld().equals(location.getWorld()), "Location must be in same world as player.");

        return showTo(player, location.getX(), location.getY(), location.getZ(), count);
    }

    @Override
    public boolean showTo(Player player, ICoords3D coords, int count) {
        PreCon.notNull(player);
        PreCon.notNull(coords);
        PreCon.greaterThanZero(count);

        return showTo(player, coords.getX(), coords.getY(), coords.getZ(), count);
    }

    @Override
    public boolean showTo(Player player, ICoords3Di coords, int count) {
        PreCon.notNull(player);
        PreCon.notNull(coords);
        PreCon.greaterThanZero(count);

        return showTo(player, coords.getX(), coords.getY(), coords.getZ(), count);
    }

    @Override
    public boolean showTo(Collection<? extends Player> players, Location location, int count) {
        PreCon.notNull(players);
        PreCon.notNull(location);
        PreCon.greaterThanZero(count);

        boolean isShown = false;

        for (Player player : players) {
            isShown = showTo(player, location, count) || isShown;
        }

        return isShown;
    }

    @Override
    public boolean showTo(Collection<? extends Player> players, ICoords3D coords, int count) {
        PreCon.notNull(players);
        PreCon.notNull(coords);
        PreCon.greaterThanZero(count);

        boolean isShown = false;

        for (Player player : players) {
            isShown = showTo(player, coords, count) || isShown;
        }

        return isShown;
    }

    @Override
    public boolean showTo(Collection<? extends Player> players, ICoords3Di coords, int count) {
        PreCon.notNull(players);
        PreCon.notNull(coords);
        PreCon.greaterThanZero(count);

        boolean isShown = false;

        for (Player player : players) {
            isShown = showTo(player, coords, count) || isShown;
        }
        return isShown;
    }

    @Override
    public boolean showFrom(Location location, int count) {
        PreCon.notNull(location);

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        boolean isShown = false;

        for (Player player : players) {

            if (!player.getWorld().equals(location.getWorld()))
                continue;

            isShown = showTo(player, location, count) || isShown;
        }

        return isShown;
    }

    private boolean showTo(Player player, double x, double y, double z, int count) {

        INmsParticleEffectHandler handler = NmsUtils.getParticleEffectHandler();
        if (handler == null)
            return false;

        Location playerLocation = player.getLocation(LOCATIONS.get());

        double deltaX = playerLocation.getX() - x;
        double deltaY = playerLocation.getY() - y;
        double deltaZ = playerLocation.getZ() - z;

        double distance = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        if (distance > _radiusSquared)
            return false;

        if (this instanceof IDirectionalParticle) {

            IDirectionalParticle directional = (IDirectionalParticle)this;
            Vector vector = directional.getVector(VECTORS.get());

            handler.send(player, getType(), true, x, y, z,
                    vector.getX(), vector.getY(), vector.getZ(), getData(), count);
        }
        else if (this instanceof IColoredParticle) {

            IColoredParticle colored = (IColoredParticle)this;

            handler.send(player, getType(), true, x, y, z,
                    colored.getRed(), colored.getGreen(), colored.getBlue(), getData(), count);
        }
        else {
            handler.send(player, getType(), true, x, y, z, 0.0f, 0.0f, 0.0f, getData(), count);
        }
        return true;
    }

    static class DirectionalHelper implements IDirectionalParticle {

        private Vector _vector = new Vector(0, 1, 0);

        @Override
        public Vector getVector() {
            return getVector(new Vector(0, 0, 0));
        }

        @Override
        public Vector getVector(Vector output) {
            PreCon.notNull(output);

            output.setX(_vector.getX());
            output.setY(_vector.getY());
            output.setZ(_vector.getZ());
            return output;
        }

        @Override
        public void setVector(Vector vector) {
            PreCon.notNull(vector);

            _vector.setX(vector.getX());
            _vector.setY(vector.getY());
            _vector.setZ(vector.getZ());
        }
    }

    static class ColorHelper implements IColoredParticle {

        private Color _color;
        private double _red;
        private double _green;
        private double _blue;

        ColorHelper() {
            setColor(Color.LIME);
        }

        @Override
        public Color getColor() {
            return _color;
        }

        @Override
        public void setColor(Color color) {
            PreCon.notNull(color);

            _color = color;

            _red = color.getRed() / 255D;
            _green = color.getGreen() / 255D;
            _blue = color.getBlue() / 255D;
        }

        @Override
        public double getRed() {
            return _red;
        }

        @Override
        public double getGreen() {
            return _green;
        }

        @Override
        public double getBlue() {
            return _blue;
        }
    }
}
