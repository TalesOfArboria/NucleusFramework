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


package com.jcwhatever.generic.internal.providers.permissions;

import com.jcwhatever.generic.permissions.IPermission;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Map;

/**
 * Container for Bukkits {@code Permission} permission.
 */
public final class SuperPermission implements IPermission {

    private Permission _permission;

    public SuperPermission(Permission permission) {
        _permission = permission;
    }

    @Override
    public String getName() {
        return _permission.getName();
    }

    @Override
    public void addParent(IPermission permission, boolean isAllowed) {
        _permission.addParent((Permission)permission.getHandle(), isAllowed);
    }

    @Override
    public void addParent(String name, boolean isAllowed) {
        _permission.addParent(name, isAllowed);
    }

    @Override
    public Map<String, Boolean> getChildren() {
        return _permission.getChildren();
    }

    @Override
    public PermissionDefault getDefault() {
        return _permission.getDefault();
    }

    @Override
    public void setDefault(PermissionDefault value) {
        _permission.setDefault(value);
    }

    @Override
    public String getDescription() {
        return _permission.getDescription();
    }

    @Override
    public void setDescription(String description) {
        _permission.setDescription(description);
    }

    @Override
    public Permission getHandle() {
        return _permission;
    }

    @Override
    public int hashCode() {
        return _permission.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Permission) {
            return _permission.equals(o);
        }
        else if (o instanceof SuperPermission) {
            return _permission.equals(((SuperPermission)o)._permission);
        }

        return false;
    }

}
