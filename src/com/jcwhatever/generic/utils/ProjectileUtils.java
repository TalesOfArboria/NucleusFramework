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

package com.jcwhatever.generic.utils;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Utilities for projectiles
 */
public final class ProjectileUtils {

    private ProjectileUtils() {}

    /**
     * Default gravity for ballistics
     */
    public static final double GRAVITY = 20.0D;

    /**
     * Get projectile shooting source location for a living entity.
     *
     * @param from    The living entity.
     * @param target  The target location.
     */
    public static Location getEntitySource(LivingEntity from, Location target){
        PreCon.notNull(from);
        PreCon.notNull(target);

        Location eyeLocation =  from.getEyeLocation();

        Vector vector = target.clone().subtract(eyeLocation).toVector();

        vector = vector.normalize().multiply(0.5);

        return eyeLocation.add(vector);
    }

    /**
     * Get the approx. heart position of a human entity.
     *
     * @param human  The human entity.
     */
    public static Location getHeartLocation(HumanEntity human) {
        PreCon.notNull(human);

        return human.getLocation().add(0, .33, 0);
    }

    /**
     * Get the angle at which a projectile must be launched in order to go a specified
     * distance at the specified velocity.
     *
     * @param velocity  The velocity.
     * @param distance  The distance.
     * @param gravity   The projectile gravity.
     */
    public static double getAngleOfReach(double velocity, double distance, double gravity) {

        return 0.5 * Math.asin((gravity * distance) / (velocity * velocity));
    }

    /**
     * Get the launch angle required to hit a target from a source location using the
     * specified velocity and elevation.
     *
     * @param source     The projectile source location.
     * @param target     The projectile target location.
     * @param elevation  The launch elevation.
     * @param velocity   The launch velocity.
     *
     * @return  Null if an angle could not be found.
     */
    @Nullable
    public static Double getBallisticAngle(Location source, Location target,
                                           double elevation, double velocity) {
        return getBallisticAngle(source, target, elevation, velocity, GRAVITY);
    }

    /**
     * Get the launch angle required to hit a target from a source location using the
     * specified velocity, elevation and gravity.
     *
     * @param source     The projectile source location.
     * @param target     The projectile target location.
     * @param elevation  The launch elevation.
     * @param velocity   The launch velocity.
     * @param gravity    The projectile gravity.
     */
    @Nullable
    public static Double getBallisticAngle(Location source, Location target,
                                           double elevation, double velocity, double gravity) {
        PreCon.notNull(source);
        PreCon.notNull(target);
        PreCon.positiveNumber(velocity);

        if (!Objects.equals(source.getWorld(), target.getWorld())) {
            return null;
        }

        if (source.distanceSquared(target) <= 2.0D) {
            return null;
        }

        double velocitySquared = velocity * velocity;
        double velocityQuad = velocitySquared * velocitySquared;

        double distanceSquared = source.distanceSquared(target);
        double distance = Math.sqrt(distanceSquared);

        double formulaComponent = gravity * ((gravity * distanceSquared) + (2 * elevation * velocitySquared));

        // make sure formula can be correctly used
        if (velocityQuad < formulaComponent)
            return null;

        return Math.atan(
                (velocitySquared - Math.sqrt(velocityQuad - formulaComponent))
                /
                (gravity * distance)
        );
    }

    /**
     * Shoot a ballistic projectile at the specified target.
     *
     * @param source           The projectile source entity.
     * @param target           The projectile target.
     * @param velocity         The projectile velocity.
     * @param projectileClass  The projectile class.
     *
     * @param <T>  The projectile type.
     */
    public static <T extends Projectile> T shootBallistic(LivingEntity source, Location target,
                                                          double velocity, Class<T> projectileClass) {

        T projectile = shootBallistic(getEntitySource(source, target), target, velocity, GRAVITY, projectileClass);
        projectile.setShooter(source);

        return projectile;
    }

    /**
     * Shoot a ballistic projectile at the specified target.
     *
     * @param source           The projectile source location.
     * @param target           The projectile target.
     * @param velocity         The projectile velocity.
     * @param projectileClass  The projectile class.
     *
     * @param <T>  The projectile type.
     */
    public static <T extends Projectile> T shootBallistic(Location source, Location target,
                                                          double velocity, Class<T> projectileClass) {

        return shootBallistic(source, target, velocity, GRAVITY, projectileClass);
    }

    /**
     * Shoot a ballistic projectile at the specified target.
     *
     * @param source           The projectile source entity.
     * @param target           The projectile target.
     * @param velocity         The projectile velocity.
     * @param gravity          The projectile gravity.
     * @param projectileClass  The projectile class.
     *
     * @param <T>  The projectile type.
     */
    public static <T extends Projectile> T shootBallistic(LivingEntity source, Location target,
                                                          double velocity, double gravity,
                                                          Class<T> projectileClass) {
        return shootBallistic(source, target, 1.0D, velocity, gravity, projectileClass);
    }

    /**
     * Shoot a ballistic projectile at the specified target.
     *
     * @param source           The projectile source entity.
     * @param target           The projectile target.
     * @param sourceRadius     The radius of the source entity.
     * @param velocity         The projectile velocity.
     * @param gravity          The projectile gravity.
     * @param projectileClass  The projectile class.
     *
     * @param <T>  The projectile type.
     */
    public static <T extends Projectile> T shootBallistic(LivingEntity source, Location target,
                                                          double sourceRadius, double velocity, double gravity,
                                                          Class<T> projectileClass) {

        T projectile = shootBallistic(getEntitySource(source, target),
                target, sourceRadius, velocity, gravity, projectileClass);

        projectile.setShooter(source);

        return projectile;
    }

    /**
     * Shoot a ballistic projectile at the specified target.
     *
     * @param source           The projectile source location.
     * @param target           The projectile target.
     * @param velocity         The projectile velocity.
     * @param gravity          The projectile gravity.
     * @param projectileClass  The projectile class.
     *
     * @param <T>  The projectile type.
     */
    public static <T extends Projectile> T shootBallistic(Location source, Location target,
                                                          double velocity, double gravity,
                                                          Class<T> projectileClass) {
        return shootBallistic(source, target, 1.0D, velocity, gravity, projectileClass);
    }


        /**
         * Shoot a ballistic projectile at the specified target.
         *
         * @param source           The projectile source location.
         * @param target           The projectile target.
         * @param sourceRadius     The radius of the source.
         * @param velocity         The projectile velocity.
         * @param gravity          The projectile gravity.
         * @param projectileClass  The projectile class.
         *
         * @param <T>  The projectile type.
         */
    public static <T extends Projectile> T shootBallistic(Location source, Location target,
                                                          double sourceRadius, double velocity, double gravity,
                                                          Class<T> projectileClass) {
        PreCon.notNull(source);
        PreCon.notNull(target);
        PreCon.positiveNumber(velocity);
        PreCon.notNull(projectileClass);

        Vector vector = target.clone().subtract(source).toVector();
        Double distance = source.distance(target);

        Vector radiusVector = vector.clone();

        double magnitude = Math.sqrt(
                (radiusVector.getX() * radiusVector.getX()) +
                (radiusVector.getY() * radiusVector.getY()) +
                (radiusVector.getZ() * radiusVector.getZ()));

        magnitude -= sourceRadius;
        magnitude = Math.max(0, magnitude);

        if (magnitude != 0)
            radiusVector = radiusVector.multiply(1 / magnitude);

        source = source.add(radiusVector);

        Double launchAngle = getBallisticAngle(source, target, vector.getY(), velocity, gravity);
        if (launchAngle == null) {
            launchAngle = 0.0D;
        }

        vector.setY(Math.tan(launchAngle) * distance);

        T projectile = source.getWorld().spawn(source, projectileClass);
        projectile.setVelocity(vector);

        return projectile;
    }

    /**
     * Shoot a projectile directly towards a target.
     *
     * @param source           The projectile source entity.
     * @param target           The projectile target location.
     * @param velocity         The projectile velocity.
     * @param projectileClass  The projectile class.
     *
     * @param <T>  The projectile type.
     */
    public static <T extends Projectile> T shoot(LivingEntity source, Location target,
                                                 double velocity, Class<T> projectileClass) {
        T projectile = shoot(getEntitySource(source, target), target, velocity, projectileClass);
        projectile.setShooter(source);

        return projectile;
    }

    /**
     * Shoot a projectile directly towards a target.
     *
     * @param source           The projectile source location.
     * @param target           The projectile target location.
     * @param velocity         The projectile velocity.
     * @param projectileClass  The projectile class.
     *
     * @param <T>  The projectile type.
     */
    public static <T extends Projectile> T shoot(Location source, Location target,
                                                 double velocity, Class<T> projectileClass) {
        PreCon.notNull(source);
        PreCon.notNull(target);
        PreCon.positiveNumber(velocity);
        PreCon.notNull(projectileClass);

        T projectile = source.getWorld().spawn(source, projectileClass);

        Vector vector = target.clone().subtract(source).toVector();
        projectile.setVelocity(vector);

        return projectile;
    }
}
