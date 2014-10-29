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


package com.jcwhatever.bukkit.generic.permissions;

import org.bukkit.permissions.PermissionDefault;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Interface for a Permission container.
 */
public interface IPermission {

    /**
     * Get the permission name.
     */
    String getName();

    /**
     * Add a parent permission.
     *
     * @param permission  The parent permission.
     * @param isAllowed   True for permission allowed.
     */
    void addParent(IPermission permission, boolean isAllowed);

    /**
     * Add a parent permission.
     *
     * @param name       The name of the parent permission.
     * @param isAllowed  True for permission allowed.
     */
    void addParent(String name, boolean isAllowed);

    /**
     * Get the permission children names  and
     * permission value map.
     */
    Map<String, Boolean> getChildren();

    /**
     * Get the permission default.
     */
    PermissionDefault getDefault();

    /**
     * Set the default permission value.
     */
    void setDefault(PermissionDefault value);

    /**
     * Get the permission description.
     */
    @Nullable
    String getDescription();

    /**
     * Set the permission description.
     *
     * @param description  The description.
     */
    void setDescription(String description);

    /**
     * Get the encapsulated permission handle.
     */
    Object getHandle();

}
