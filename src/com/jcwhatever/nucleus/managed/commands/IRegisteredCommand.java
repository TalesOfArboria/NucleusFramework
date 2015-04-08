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

package com.jcwhatever.nucleus.managed.commands;

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.providers.permissions.IPermission;

import javax.annotation.Nullable;

/**
 * Interface for a wrapper around a registered {@link ICommand}.
 */
public interface IRegisteredCommand extends
        Comparable<IRegisteredCommand>, ICommandOwner, IPluginOwned {

    /**
     * Get the encapsulated command handler.
     */
    ICommand getCommand();

    /**
     * Get the commands info as taken from its {@link CommandInfo} annotation.
     */
    IRegisteredCommandInfo getInfo();

    /**
     * Get the command handler
     */
    ICommandDispatcher getDispatcher();

    /**
     * Get the commands parent command, if any.
     */
    @Nullable
    IRegisteredCommand getParent();

    /**
     * Get the commands permission object.
     */
    IPermission getPermission();
}

