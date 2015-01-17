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

package com.jcwhatever.nucleus.internal.providers;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.modules.YamlModuleInfo;
import com.jcwhatever.nucleus.storage.IDataNode;

import java.util.jar.JarFile;

/**
 * Module info for a provider.
 */
public class ProviderModuleInfo extends YamlModuleInfo {

    private String _version;
    private int _logicalVersion;
    private String _description;
    private String _moduleClassName;

    /**
     * Constructor.
     *
     * @param jarFile  The jar file that contains the module info.
     */
    public ProviderModuleInfo(JarFile jarFile) {
        super(Nucleus.getPlugin(), "provider.yml", jarFile);
    }

    /**
     * Get the module version as a displayable string..
     */
    public String getVersion() {
        return _version;
    }

    /**
     * Get the logical version of the module.
     */
    public int getLogicalVersion() {
        return _logicalVersion;
    }

    /**
     * Get the modules description.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Get the class name of the module.
     */
    public String getModuleClassName() {
        return _moduleClassName;
    }

    @Override
    protected boolean onLoad(IDataNode dataNode) {

        _version = dataNode.getString("version", "");
        _logicalVersion = dataNode.getInteger("logical-version", 0);
        _description = dataNode.getString("description", "");
        _moduleClassName = dataNode.getString("class");

        return _moduleClassName != null;
    }
}
