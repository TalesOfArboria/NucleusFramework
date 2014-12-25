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


package com.jcwhatever.generic.signs;

import com.jcwhatever.generic.mixins.IPluginOwned;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.PreCon;
import com.jcwhatever.generic.utils.Scheduler;
import com.jcwhatever.generic.utils.SignUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Container for a sign. Used to pass signs into
 * sign handler event methods.
 */
public class SignContainer implements IPluginOwned {

    private final Plugin _plugin;
    private final Sign _sign;
    private final Location _signLocation;
    private final IDataNode _signNode;
    private SignChangeEvent _changeEvent;
    private boolean _isDataSaveRequired;
    private boolean _isUpdateScheduled;


    /**
     * Constructor.
     *
     * @param plugin        The owning plugin.
     * @param signLocation  The location of the sign.
     */
    public SignContainer(Plugin plugin, Location signLocation) {
        PreCon.notNull(plugin);
        PreCon.notNull(signLocation);

        _plugin = plugin;
        _signLocation = signLocation;
        _sign = SignUtils.getSign(signLocation.getBlock());
        _signNode = null;
    }

    /**
     * Constructor.
     *
     * @param plugin        The owning plugin.
     * @param signLocation  The location of the sign.
     * @param signNode      The data node of the sign.
     */
    public SignContainer(Plugin plugin, Location signLocation, IDataNode signNode) {
        PreCon.notNull(plugin);
        PreCon.notNull(signLocation);
        PreCon.notNull(signNode);

        _plugin = plugin;
        _signLocation = signLocation;
        _sign = SignUtils.getSign(signLocation.getBlock());
        _signNode = signNode;
    }

    /**
     * Constructor.
     *
     * @param plugin        The owning plugin.
     * @param signLocation  The sign to encapsulate.
     * @param signNode      The data node of the sign.
     * @param event         Sign change event.
     */
    public SignContainer(Plugin plugin, Location signLocation, IDataNode signNode, SignChangeEvent event) {
        PreCon.notNull(plugin);
        PreCon.notNull(signLocation);
        PreCon.notNull(signNode);
        PreCon.notNull(event);

        _plugin = plugin;
        _signLocation = signLocation;
        _sign = SignUtils.getSign(signLocation.getBlock());
        _signNode = signNode;
        _changeEvent = event;
    }


    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the encapsulated sign.
     */
    @Nullable
    public Sign getSign() {
        return _sign;
    }

    /**
     * Get the sign data node.
     */
    @Nullable
    public IDataNode getDataNode() {
        return _signNode;
    }

    /**
     * Get the location of the sign.
     */
    public Location getLocation() {
        return _signLocation;
    }

    /**
     * Determine if changes have been made that
     * require a data node save.
     */
    public boolean isDataSaveRequired() {
        return _isDataSaveRequired;
    }

    /**
     * Get a line from the sign as is.
     *
     * @param index  The index of the line. (0-3)
     */
    public String getLine(int index) {
        PreCon.positiveNumber(index);
        PreCon.isValid(index < 4);

        if (_sign == null)
            return "";

        return _changeEvent != null ? _changeEvent.getLine(index) : _sign.getLine(index);
    }

    /**
     * Set the line on a sign.
     *
     * @param index  The index of the line. (0-3)
     * @param line   The new text line.
     */
    public void setLine(int index, String line) {
        PreCon.positiveNumber(index);
        PreCon.isValid(index < 4);
        PreCon.notNull(line);

        if (_changeEvent != null) {
            _changeEvent.setLine(index, line);
        }
        else if (_sign != null) {
            _sign.setLine(index, line);
        }
    }

    /**
     * Get a line from the sign with color formatting removed.
     *
     * @param index  The index of the line. (0-3)
     */
    public String getRawLine(int index) {
        String line = getLine(index);

        return ChatColor.stripColor(line);
    }

    /**
     * Get a saved line from the data node.
     *
     * @param index  The index of the line. (0-3)
     *
     * @return  Null if there is no data node set.
     */
    @Nullable
    public String getSavedLine(int index) {
        if (_signNode == null)
            return null;

        return _signNode.getString("line" + index, "");
    }

    /**
     * Set a saved line from the data node.
     *
     * @param index  The index of the line. (0-3)
     * @param line   The new text line.
     *
     * @return  False if there is no data node set.
     */
    public boolean setSavedLine(int index, String line) {
        PreCon.positiveNumber(index);
        PreCon.isValid(index < 4);
        PreCon.notNull(line);

        if (_signNode == null)
            return false;

        _signNode.set("line" + index, line);
        _isDataSaveRequired = true;

        return true;
    }

    /**
     * Update changes to the sign.
     */
    public boolean update() {
        if (_sign == null)
            return false;

        if (_changeEvent == null) {
            _sign.update(true);
        }
        else if (!_isUpdateScheduled) {

            _isUpdateScheduled = true;
            Scheduler.runTaskLater(_plugin, new Runnable() {

                @Override
                public void run() {
                    _sign.update(true);
                    _isUpdateScheduled = false;
                }
            });
        }
        return true;
    }

    /**
     * Save data node changes.
     *
     * @return  False if there is not data node set.
     */
    public boolean save() {
        if (_signNode == null)
            return false;

        _signNode.saveAsync(null);

        return true;
    }
}
