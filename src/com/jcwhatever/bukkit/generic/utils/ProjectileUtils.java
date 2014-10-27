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


package com.jcwhatever.bukkit.generic.utils;

import com.jcwhatever.bukkit.generic.GenericsLib;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class ProjectileUtils {

    private static final double GRAVITY = 20.0D;


    public static Location getFireSource(LivingEntity from, Location to){

        Location fromEye =  from.getEyeLocation();

        Vector norman = to.clone().subtract(fromEye).toVector();

        norman = norman.normalize().multiply(0.5);

        return fromEye.add(norman);
    }

    public static Location getHeartLocation(HumanEntity human) {
        return human.getLocation().add(0, .33, 0);
    }

    public static Vector normalizeVector(Vector vector){
        double magnitude = Math.sqrt((vector.getX() * vector.getX()) + (vector.getY() * vector.getY())  + (vector.getZ() * vector.getZ())) ;
        if (magnitude !=0)
            return vector.multiply(1 / magnitude);

        return vector.multiply(0);
    }


    public static Double getLaunchAngle(Location from, Location to, double elevation, double velocity){

        double v2 = velocity * velocity;
        double v4 = Math.pow(velocity, 4);

        Vector vector = from.clone().subtract(to).toVector();

        double dist =  Math.sqrt((vector.getX() * vector.getX()) + (vector.getZ() * vector.getZ()));
        double dist2 = dist * dist;

        double derp =  GRAVITY * (GRAVITY * dist2 + 2 * elevation * v2);

        //Check unhittable.
        if (v4 < derp) {
            GenericsLib.getPlugin().getLogger().warning("V4: " + v4 + ", DERP: " + derp);
            //target unreachable
            // use this to fire at optimal max angle launchAngle = Math.atan( ( 2*g*elev + v2) / (2*g*elev + 2*v2));
            return null;
        }


        //calc angle
        return Math.atan((v2-  Math.sqrt(v4 - derp)) / (GRAVITY * dist));
    }

    public static <T extends Projectile> void shoot(Player p, Double velocity, Class<T> projectileClass) {

        Location shootLocation = p.getLocation().add(0, 1, 0);
        Vector directionVector = shootLocation.getDirection().normalize();

        double startShift = 2;
        Vector shootShiftVector = new Vector(directionVector.getX() * startShift, directionVector.getY() * startShift, directionVector.getZ() * startShift);
        shootLocation = shootLocation.add(shootShiftVector.getX(), shootShiftVector.getY(), shootShiftVector.getZ());

        T projectile = shootLocation.getWorld().spawn(shootLocation, projectileClass);
        projectile.setVelocity(directionVector.multiply(velocity));
    }

    public static <T extends Projectile> T shoot(LivingEntity shooter, Location target, double velocity, Class<T> projectileClass) {
        Location source = getFireSource(shooter, target);
        T projectile = shoot(source, target, velocity, projectileClass);
        projectile.setShooter(shooter);

        return projectile;
    }

    public static <T extends Projectile> T shoot(Location source, Location target, double velocity, Class<T> projectileClass) {
        // TODO: check los

        Vector vector = target.clone().subtract(source).toVector();
        double distance = source.distance(target);

        // TODO
        boolean isBallistic = true;

        if (isBallistic) {
            Double launchAngle = getLaunchAngle(source, target, vector.getY(), velocity);
            if (launchAngle == null) {
                GenericsLib.getPlugin().getLogger().warning("Failed to get launch angle");
                //return false;
                launchAngle = 0.0D;
            }
            else {
                vector.setY(Math.tan(launchAngle) * distance);
            }
        }

        T projectile = source.getWorld().spawn(source, projectileClass);
        projectile.setVelocity(vector);

        return projectile;
    }


}
