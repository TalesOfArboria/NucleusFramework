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

package com.jcwhatever.nucleus.utils.npc.traits;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

/*
 * 
 */
public class LookingTrait extends NpcTrait implements Runnable {

    private WeakReference<Entity> _lookEntity;
    private Location _lookLocation;
    private double _lookCloseRange = 5;

    private boolean _isEnabled;
    private Location _currentLook;
    private Location _adjustedLocation;
    private LookMode _lookMode = LookMode.LOOK_CLOSE;

    private long _runCount = 0;

    public enum LookMode {
        TALK_ENTITY,
        LOOK_ENTITY,
        LOOK_LOCATION,
        LOOK_CLOSE
    }


    /**
     * Constructor.
     *
     * @param npc      The NPC the trait is for.
     * @param type     The parent type that instantiated the trait.
     */
    public LookingTrait(INpc npc, NpcTraitType type) {
        super(npc, type);
    }

    public boolean isEnabled() {
        return _isEnabled;
    }

    public LookingTrait setEnabled(boolean isEnabled) {
        if (_isEnabled == isEnabled)
            return this;

        _isEnabled = isEnabled;
        _runCount = 0;

        return this;
    }

    public LookMode getLookMode() {
        return _lookMode;
    }

    public LookingTrait setLookMode(Object lookModeObject) {
        PreCon.notNull(lookModeObject);

        LookMode lookMode;
        if (lookModeObject instanceof LookMode) {
            lookMode = (LookMode)lookModeObject;
        }
        else if (lookModeObject instanceof String) {

            String name = ((String)lookModeObject).toUpperCase();

            lookMode = LookMode.valueOf(name);
        }
        else {
            throw new IllegalArgumentException("LookMode constant or constant name is expected.");
        }

        _lookMode = lookMode;

        return this;
    }

    @Nullable
    public Entity getLookEntity() {
        if (_lookEntity == null)
            return null;

        return _lookEntity.get();
    }

    public LookingTrait setLookEntity(Entity entity) {
        PreCon.notNull(entity);

        _lookEntity = new WeakReference<Entity>(entity);

        return this;
    }

    @Nullable
    public Location getLookLocation() {
        return _lookLocation;
    }

    public LookingTrait setLookLocation(Location location) {
        _lookLocation = location;

        return this;
    }

    public double getLookCloseRange() {
        return _lookCloseRange;
    }

    public LookingTrait setLookCloseRange(double range) {
        _lookCloseRange = range;

        return this;
    }

    @Override
    public void run() {
        if (!_isEnabled || !getNpc().isSpawned())
            return;

        // don't perform look if npc is currently navigating
        if (getNpc().getNavigator().isRunning())
            return;

        switch (_lookMode) {
            case TALK_ENTITY:
                talkEntity();
                break;
            case LOOK_ENTITY:
                lookEntity();
                break;
            case LOOK_LOCATION:
                lookLocation();
                break;
            case LOOK_CLOSE:
                lookClose();
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void save(IDataNode dataNode) {

    }

    private void talkEntity() {
        Entity lookEntity = getLookEntity();
        if (lookEntity == null)
            return;

        // slow down head nods
        if ((_runCount++ & 0x3L) != 0)
            return;

        Location npcLocation = getNpc().getLocation();
        assert npcLocation != null;

        // get the target look location
        Location lookLocation = lookEntity instanceof LivingEntity
                ? lookEntity.getLocation().clone().add(0, ((LivingEntity) lookEntity).getEyeHeight() - 1.5, 0)
                : lookEntity.getLocation();

        if (!lookLocation.getWorld().equals(npcLocation.getWorld()))
            return;

        // make sure calculations are needed, check cached look location
        if (_currentLook == null || !_currentLook.equals(lookLocation)) {

            // normalize location for consistent head movement regardless
            // of player distance.

            double x = lookLocation.getX() - npcLocation.getX();
            double y = lookLocation.getY() - npcLocation.getY();
            double z = lookLocation.getZ() - npcLocation.getZ();

            double magnitude = Math.sqrt((x * x) + (y * y) + (z * z));

            if (magnitude > 1) {

                x = lookEntity.getLocation().getX() + (x / magnitude);
                y = lookEntity.getLocation().getY() + (y / magnitude);
                z = lookEntity.getLocation().getZ() + (z / magnitude);

                _adjustedLocation = new Location(npcLocation.getWorld(), x, y, z);
            }
        }

        _currentLook = lookLocation;

        if (_adjustedLocation != null) {
            getNpc().lookTowards(LocationUtils.addNoise(_adjustedLocation, 0.2D, 0.4D, 0.2D));
        }
    }

    private void lookEntity() {

        Entity entity = getLookEntity();
        Location location = getNpc().getLocation();
        assert location != null;

        if (entity == null ||
                !entity.getWorld().equals(location.getWorld())) {
            return;
        }

        if (entity.getLocation().distanceSquared(location) > 1) {

            Location finalLook = entity instanceof LivingEntity
                    ? entity.getLocation().clone().add(0, ((LivingEntity) entity).getEyeHeight() - 1.5, 0)
                    : entity.getLocation();

            _currentLook = getNextLook(finalLook, 5);

            getNpc().lookTowards(_currentLook);
        }
    }

    private void lookLocation() {

        Location location = getNpc().getLocation();
        assert location != null;

        if (_lookLocation == null ||
                !_lookLocation.getWorld().equals(location.getWorld())) {
            return;
        }

        _currentLook = getNextLook(_lookLocation, 5);

        getNpc().lookTowards(_currentLook);
    }

    private void lookClose() {

        Entity npcEntity = getNpc().getEntity();
        assert npcEntity != null;

        LivingEntity close = EntityUtils.getClosestLivingEntity(
                npcEntity, _lookCloseRange, new IValidator<LivingEntity>() {
                    @Override
                    public boolean isValid(LivingEntity element) {
                        return element instanceof Player && !element.hasMetadata("NPC");
                    }
                });

        if (close != null && close.getLocation().distanceSquared(npcEntity.getLocation()) > 1) {

            Location target = close.getLocation().clone().add(0, close.getEyeHeight() - 1.5, 0);

            _currentLook = getNextLook(target, 5);
            getNpc().lookTowards(_currentLook);
        }
    }

    private Location getNextLook(Location target, int steps) {

        Location current = _currentLook != null ? _currentLook : target.clone();

        if (!current.equals(target)) {

            double deltaX = (target.getX() - current.getX()) / steps;
            double deltaY = (target.getY() - current.getY()) / steps;
            double deltaZ = (target.getZ() - current.getZ()) / steps;

            return current.clone().add(deltaX, deltaY, deltaZ);
        }

        return target;
    }
}
