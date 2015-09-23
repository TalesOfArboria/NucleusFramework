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

package com.jcwhatever.nucleus.internal.managed.signs;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.nucleus.utils.SignUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

import javax.annotation.Nullable;

/**
 * Internal implementation of {@link ISignContainer}.
 */
class SignContainer implements ISignContainer {

    private final Sign _sign;
    private final Location _signLocation;
    private final IDataNode _signNode;
    private final IDataNode _metaNode;
    private final SignChangeEvent _changeEvent;

    private boolean _isDirty;
    private boolean _isUpdateScheduled;

    /**
     * Constructor.
     *
     * @param signLocation  The location of the sign.
     */
    public SignContainer(Location signLocation) {
        this(signLocation, null, null);
    }

    /**
     * Constructor.
     *
     * @param signLocation  The location of the sign.
     * @param signNode      The data node of the sign.
     */
    public SignContainer(Location signLocation, IDataNode signNode) {
        this(signLocation, signNode, null);
    }

    /**
     * Constructor.
     *
     * @param signLocation  The sign to encapsulate.
     * @param signNode      The data node of the sign.
     * @param event         Sign change event.
     */
    public SignContainer(Location signLocation,
                         IDataNode signNode, @Nullable SignChangeEvent event) {
        PreCon.notNull(signLocation);
        PreCon.notNull(signNode);

        _signLocation = signLocation;
        _sign = SignUtils.getSign(signLocation.getBlock());
        _signNode = signNode;
        _metaNode = signNode.getNode("meta");
        _changeEvent = event;
    }

    @Override
    @Nullable
    public Sign getSign() {
        return _sign;
    }

    @Override
    public IDataNode getMetaNode() {
        return _metaNode;
    }

    /**
     * Get the sign {@link IDataNode}.
     */
    public IDataNode getDataNode() {
        return _signNode;
    }

    @Override
    public Location getLocation() {
        return getLocation(new Location(null, 0, 0, 0));
    }

    @Override
    public Location getLocation(Location output) {
        return LocationUtils.copy(_signLocation, output);
    }

    @Override
    public boolean isDirty() {
        return _isDirty;
    }

    @Override
    public String getLine(int index) {
        PreCon.positiveNumber(index);
        PreCon.isValid(index < 4);

        if (_sign == null)
            return "";

        return _changeEvent != null ? _changeEvent.getLine(index) : _sign.getLine(index);
    }

    @Override
    public void setLine(int index, CharSequence line) {
        PreCon.positiveNumber(index);
        PreCon.isValid(index < 4);
        PreCon.notNull(line);

        if (_changeEvent != null) {
            _changeEvent.setLine(index, line.toString());
        }
        else if (_sign != null) {
            _sign.setLine(index, line.toString());
        }
    }

    @Override
    public String getRawLine(int index) {
        String line = getLine(index);

        return ChatColor.stripColor(line);
    }

    @Override
    @Nullable
    public String getSavedLine(int index) {
        if (_signNode == null)
            return null;

        return _signNode.getString("line" + index, "");
    }

    @Override
    public boolean setSavedLine(int index, CharSequence line) {
        PreCon.positiveNumber(index);
        PreCon.isValid(index < 4);
        PreCon.notNull(line);

        if (_signNode == null)
            return false;

        _signNode.set("line" + index, line);
        _isDirty = true;

        return true;
    }

    @Override
    public boolean update() {
        if (_sign == null)
            return false;

        if (_changeEvent == null) {
            _sign.update(true);
        }
        else if (!_isUpdateScheduled) {

            _isUpdateScheduled = true;
            Scheduler.runTaskLater(Nucleus.getPlugin(), new Runnable() {

                @Override
                public void run() {
                    _sign.update(true);
                    _isUpdateScheduled = false;
                }
            });
        }
        return true;
    }

    @Override
    public void remove() {

        _signLocation.getBlock().setType(Material.AIR);
        _metaNode.remove();
        _signNode.remove();
        _signNode.save();
    }

    @Override
    public boolean save() {
        _signNode.save();

        return true;
    }
}
