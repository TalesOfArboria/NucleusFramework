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

package com.jcwhatever.nucleus.utils.modules;

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.file.FileUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.nio.charset.StandardCharsets;
import java.util.jar.JarFile;

/**
 * A basic implementation of {@link IModuleInfo} designed to
 * read a Yaml file inside the module jar file to retrieve the
 * name of the module.
 *
 * <p>The data node property name for the name is 'name'.</p>
 */
public class YamlModuleInfo implements IModuleInfo, IPluginOwned {

    private final Plugin _plugin;
    private final boolean _isValid;

    private String _name;
    private String _searchName;

    /**
     * Constructor.
     *
     * @param plugin       The owning plugin.
     * @param fileName     The name and path of the resource file in the jar.
     * @param jarFile      The jar file the module info is in.
     */
    public YamlModuleInfo(Plugin plugin, String fileName, JarFile jarFile) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(fileName);
        PreCon.notNull(jarFile);

        _plugin = plugin;
        _isValid = load(fileName, jarFile);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    /*
     * Determine if the module information was
     * successfully loaded in the constructor.
     */
    public boolean isValid() {
        return _isValid;
    }

    /**
     * Called after the YAML file is loaded from the module
     * and the name is parsed. This method can be overridden
     * to read more data from the file.
     *
     * @param dataNode  The YAML module data node.
     *
     * @return  True if all required information was found.
     */
    protected boolean onLoad(@SuppressWarnings("unused") IDataNode dataNode) {
        return true;
    }

    /*
     * Load module info from yaml string.
     */
    protected boolean load(String filename, JarFile jarFile) {

        String yamlString = FileUtils.scanTextFile(jarFile, filename, StandardCharsets.UTF_8);

        // Load yaml string into data node.
        YamlDataNode moduleNode = new YamlDataNode(getPlugin(), yamlString);
        if (!moduleNode.load())
            return false;

        // get the required name of the module
        _name = moduleNode.getString("name");
        if (_name == null)
            return false;

        // convert the name to lower case.
        _searchName = _name.toLowerCase();

        return onLoad(moduleNode);
    }
}
