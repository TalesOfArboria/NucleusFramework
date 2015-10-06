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

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.Vector3f;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.io.IOException;
import java.util.List;

/**
 * Data watcher wrapper
 */
public class v1_8_R3_DataWatcher extends DataWatcher implements IDataWatcher {

    private static final byte SNEAK_FLAG = 1 << 1;

    private final DataWatcher _watcher;
    private boolean _canDismount;
    private boolean _isDismountPressed = false;

    public v1_8_R3_DataWatcher(Entity entity) {
        super(null);
        _watcher = ((CraftEntity)entity).getHandle().getDataWatcher();

        byte flags = getByte(0);
        _isDismountPressed = (flags & SNEAK_FLAG) == SNEAK_FLAG;
    }

    @Override
    public boolean canDismount() {
        return _canDismount;
    }

    @Override
    public void setCanDismount(boolean canDismount) {

        if (!canDismount) {
            byte current = getByte(0);
            current &= ~SNEAK_FLAG;
            watch(0, current);
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
            byte flags = getByte(0);
            if (isPressed) {
                flags |= SNEAK_FLAG;
            }
            else {
                flags &= ~SNEAK_FLAG;
            }
            watch(0, flags);
        }
    }

    @Override
    public <T> void a(int i, T t0) {
        _watcher.a(i, t0);
    }

    @Override
    public void add(int i, int j) {
        _watcher.add(i, j);
    }

    @Override
    public byte getByte(int i) {
        return _watcher.getByte(i);
    }

    @Override
    public short getShort(int i) {
        return _watcher.getShort(i);
    }

    @Override
    public int getInt(int i) {
        return _watcher.getInt(i);
    }

    @Override
    public float getFloat(int i) {
        return _watcher.getFloat(i);
    }

    @Override
    public String getString(int i) {
        return _watcher.getString(i);
    }

    @Override
    public ItemStack getItemStack(int i) {
        return _watcher.getItemStack(i);
    }

    @Override
    public Vector3f h(int i) {
        return _watcher.h(i);
    }

    @Override
    public <T> void watch(int i, T t0) {

        if (i == 0) {
            byte newFlags = ((Number)t0).byteValue();
            if ((newFlags & SNEAK_FLAG) == SNEAK_FLAG) {
                _isDismountPressed = true;

                if (!_canDismount)
                    return;
            }
            else {
                _isDismountPressed = false;
            }
        }
        _watcher.watch(i, t0);
    }

    @Override
    public void update(int i) {
        _watcher.update(i);
    }

    @Override
    public boolean a() {
        return _watcher.a();
    }

    @Override
    public List<DataWatcher.WatchableObject> b() {
        return _watcher.b();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        _watcher.a(packetdataserializer);
    }

    @Override
    public List<DataWatcher.WatchableObject> c() {
        return _watcher.c();
    }

    @Override
    public boolean d() {
        return _watcher.d();
    }

    @Override
    public void e() {
        _watcher.e();
    }
}
