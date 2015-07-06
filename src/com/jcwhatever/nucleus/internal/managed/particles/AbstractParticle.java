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

import com.jcwhatever.nucleus.managed.particles.IAreaParticle;
import com.jcwhatever.nucleus.managed.particles.IParticleEffect;
import com.jcwhatever.nucleus.managed.particles.IRGBColorParticle;
import com.jcwhatever.nucleus.managed.particles.ISizeableParticle;
import com.jcwhatever.nucleus.managed.particles.ISpeedParticle;
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
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract implementation of {@link IParticleEffect}.
 */
abstract class AbstractParticle implements IParticleEffect {

    private static final ThreadSingletons<Location> LOCATIONS = LocationUtils.createThreadSingleton();

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
    public boolean showTo(Player player, Location location, int count) {
        PreCon.notNull(player);
        PreCon.notNull(location);
        PreCon.isValid(player.getWorld().equals(location.getWorld()),
                "Location must be in same world as player.");

        return showTo(ArrayUtils.asList(player),
                location.getX(), location.getY(), location.getZ(), count);
    }

    @Override
    public boolean showTo(Player player, ICoords3D coords, int count) {
        PreCon.notNull(player);
        PreCon.notNull(coords);

        return showTo(ArrayUtils.asList(player),
                coords.getX(), coords.getY(), coords.getZ(), count);
    }

    @Override
    public boolean showTo(Player player, ICoords3Di coords, int count) {
        PreCon.notNull(player);
        PreCon.notNull(coords);
        PreCon.positiveNumber(count);

        return showTo(ArrayUtils.asList(player),
                coords.getX(), coords.getY(), coords.getZ(), count);
    }

    @Override
    public boolean showTo(Collection<? extends Player> players,
                          Location location, int count) {
        PreCon.notNull(players);
        PreCon.notNull(location);

        return showTo(players,
                location.getX(), location.getY(), location.getZ(), count);
    }

    @Override
    public boolean showTo(Collection<? extends Player> players,
                          ICoords3D coords, int count) {
        PreCon.notNull(players);
        PreCon.notNull(coords);

        return showTo(players,
                coords.getX(), coords.getY(), coords.getZ(), count);
    }

    @Override
    public boolean showTo(Collection<? extends Player> players,
                          ICoords3Di coords, int count) {
        PreCon.notNull(players);
        PreCon.notNull(coords);

        return showTo(players,
                coords.getX(), coords.getY(), coords.getZ(), count);
    }

    @Override
    public boolean showFrom(Location location, int count) {
        PreCon.notNull(location);

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
                showTo(visible, location.getX(), location.getY(), location.getZ(), count);

    }

    protected float getNmsSpeed() {
        if (this instanceof ISpeedParticle)
            return ((ISpeedParticle)this).getSpeed();

        if (this instanceof ISizeableParticle)
            return ((ISizeableParticle)this).getSize();

        return 1.0f;
    }

    protected double radiusSquared() {
        return _radiusSquared;
    }

    protected double getOffsetX() {
        if (this instanceof IAreaParticle)
            return ((IAreaParticle)this).getXArea();

        return 0.0f;
    }

    protected double getOffsetY() {
        if (this instanceof IAreaParticle)
            return ((IAreaParticle)this).getYArea();

        return 0.0f;
    }

    protected double getOffsetZ() {
        if (this instanceof IAreaParticle)
            return ((IAreaParticle)this).getZArea();

        return 0.0f;
    }

    protected void showParticleTo(INmsParticleEffectHandler handler,
                                  Collection<? extends Player> players,
                                  double x, double y, double z, int count) {
        PreCon.greaterThanZero(count, "count");

        handler.send(players, getType(), true, x, y, z,
                getOffsetX(), getOffsetY(), getOffsetZ(), getNmsSpeed(), count - 1);
    }

    private boolean showTo(Collection<? extends Player> players,
                           double x, double y, double z, int count) {

        INmsParticleEffectHandler handler = NmsUtils.getParticleEffectHandler();
        if (handler == null)
            return false;

        if (this instanceof AbstractRGBColorParticle) {
            ((AbstractRGBColorParticle)this).showColoredTo(handler, players, x, y, z, count);
            return true;
        }
        else {
            showParticleTo(handler, players, x, y, z, count);
        }

        return true;
    }

    static class ColorHelper implements IRGBColorParticle {

        private Color color;
        private double red;
        private double green;
        private double blue;

        ColorHelper() {
            setColor(Color.LIME);
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public void setColor(Color color) {
            PreCon.notNull(color);

            this.color = color;

            red = color.getRed() / 255D;
            green = color.getGreen() / 255D;
            blue = color.getBlue() / 255D;
        }

        @Override
        public double getRed() {
            return red;
        }

        @Override
        public double getGreen() {
            return green;
        }

        @Override
        public double getBlue() {
            return blue;
        }
    }

    static class AreaHelper implements IAreaParticle {

        private double x;
        private double y;
        private double z;

        @Override
        public double getXArea() {
            return x;
        }

        @Override
        public double getYArea() {
            return y;
        }

        @Override
        public double getZArea() {
            return z;
        }

        @Override
        public void setXArea(double areaSize) {
            PreCon.positiveNumber(areaSize);

            x = areaSize;
        }

        @Override
        public void setYArea(double areaSize) {
            PreCon.positiveNumber(areaSize);

            y = areaSize;
        }

        @Override
        public void setZArea(double areaSize) {
            PreCon.positiveNumber(areaSize);

            z = areaSize;
        }

        @Override
        public void setArea(double areaSize) {
            PreCon.positiveNumber(areaSize);

            x = areaSize;
            y = areaSize;
            z = areaSize;
        }
    }
}
