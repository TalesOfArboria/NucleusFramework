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

import net.minecraft.server.v1_9_R1.DataWatcher;
import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.DataWatcherRegistry;
import net.minecraft.server.v1_9_R1.EntityLiving;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * Data watcher wrapper
 */
public class v1_9_R1_DataWatcher extends DataWatcher implements IDataWatcher {

    private static final DataWatcherObject<Byte> ENTITY_FLAGS = DataWatcher.a(net.minecraft.server.v1_9_R1.Entity.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Integer> ARROW_STICK = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);

    private static final byte SNEAK_FLAG = 1 << 1;

    private final DataWatcher _watcher;
    private boolean _canDismount;
    private boolean _isDismountPressed = false;
    private boolean _canArrowsStick = true;

    public v1_9_R1_DataWatcher(Entity entity) {
        super(null);

        _watcher = ((CraftEntity) entity).getHandle().getDataWatcher();
        _isDismountPressed = getFlag(1);
    }

    @Override
    public boolean canDismount() {
        return _canDismount;
    }

    @Override
    public void setCanDismount(boolean canDismount) {

        if (!canDismount) {
            if(getFlag(1)) { // isSneaking
                setDismountPressed(false);
            }
        }
        _canDismount = canDismount;
    }

    @Override
    public boolean isDismountPressed() {
        return _isDismountPressed;
    }

    @Override
    public void setDismountPressed(boolean isPressed) {
        _isDismountPressed = isPressed;

        if (_canDismount) {
            setFlag(1, isPressed);
        }
    }

    public void setFlag(int i, boolean flag) {
        byte b0 = get(ENTITY_FLAGS);
        if (flag) {
            _watcher.set(ENTITY_FLAGS, (byte) (b0 | 1 << i));
        } else {
            _watcher.set(ENTITY_FLAGS, (byte) (b0 & ~(1 << i)));
        }
    }

    public boolean getFlag(int i) {
        return (get(ENTITY_FLAGS) & 1 << i) != 0;
    }

    @Override
    public void removeArrows() {
        _watcher.set(ARROW_STICK, 0);
    }

    @Override
    public boolean canArrowsStick() {
        return _canArrowsStick;
    }

    @Override
    public void setCanArrowsStick(boolean isAllowed) {
        _canArrowsStick = isAllowed;
        if (!isAllowed)
            removeArrows();
    }

    @Override
    public <T> void set(DataWatcherObject<T> i, T t0) {

        if (ENTITY_FLAGS.equals(i)) {

            byte newFlags = ((Number) t0).byteValue();
            if ((newFlags & SNEAK_FLAG) == SNEAK_FLAG) {
                _isDismountPressed = true;

                if (!_canDismount)
                    return;
            } else {
                _isDismountPressed = false;
            }
        }
        else if (ARROW_STICK.equals(i)) {
            if (!_canArrowsStick && ((Number) t0).intValue() != 0)
                return;
        }

        _watcher.set(i, t0);
    }
}
