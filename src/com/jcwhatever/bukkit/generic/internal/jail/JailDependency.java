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

package com.jcwhatever.bukkit.generic.internal.jail;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.jail.Jail;
import com.jcwhatever.bukkit.generic.utils.DependencyRunner.IDependantRunnable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Date;
import java.util.UUID;

/**
 * Loads jail sessions created by plugins and jails that may not be loaded yet.
 */
public class JailDependency implements IDependantRunnable {

    private final String _pluginName;
    private final String _jailName;
    private final UUID _playerId;
    private final Date _expireTime;

    private Plugin _plugin;
    private Jail _jail;

    public JailDependency(String pluginName, String jailName, UUID playerId, Date expireTime) {
        _pluginName = pluginName;
        _jailName = jailName;
        _playerId = playerId;
        _expireTime = expireTime;
    }

    @Override
    public boolean isDependencyReady() {

        if (_plugin == null) {
            _plugin = Bukkit.getPluginManager().getPlugin(_pluginName);
            if (_plugin == null) {
                return false;
            }
        }

        _jail = GenericsLib.getJailManager().getJail(_plugin, _jailName);

        return _jail != null;
    }

    @Override
    public void run() {
        GenericsLib.getJailManager().registerJailSession(_jail, _playerId, _expireTime);
    }
}

