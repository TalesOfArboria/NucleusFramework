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

package com.jcwhatever.generic.titles;

import com.jcwhatever.generic.mixins.IPluginOwned;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * A basic implementation of a title manager. Stores titles
 * by name so they can be stored, retrieved and reused.
 *
 * <p>Optionally can store titles to a data node.</p>
 */
public class TitleManager<T extends INamedTitle> implements IPluginOwned {

    protected final Plugin _plugin;
    protected final INamedTitleFactory<T> _factory;
    protected final IDataNode _dataNode;

    private Map<String, T> _titles = new HashMap<>(15);

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param factory The factory used to generate new titles.
     */
    protected TitleManager(Plugin plugin, INamedTitleFactory<T> factory) {
        this(plugin, null, factory);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  The data node to store titles in.
     * @param factory   The factory used to generate new titles.
     */
    protected TitleManager(Plugin plugin, @Nullable IDataNode dataNode,
                           INamedTitleFactory<T> factory) {
        PreCon.notNull(plugin);
        PreCon.notNull(factory);

        _plugin = plugin;
        _factory = factory;
        _dataNode = dataNode;

        loadSettings();
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Add a title instance.
     *
     * @param title  The title to add.
     *
     * @return  True if added successfully.
     */
    public boolean addTitle(T title) {
        PreCon.notNull(title);

        _titles.put(title.getSearchName(), title);
        return true;
    }

    /**
     * Add a new title.
     *
     * @param name       The name of the title.
     * @param titleText  The title text.
     *
     * @return  True if added successfully.
     */
    public boolean addTitle(String name, String titleText) {
        return addTitle(name, titleText, null, -1, -1, -1);
    }

    /**
     * Add a new title.
     *
     * @param name      The name of the title.
     * @param title     The title text.
     * @param subTitle  The sub title text.
     *
     * @return  True if added successfully.
     */
    public boolean addTitle(String name, String title, @Nullable String subTitle) {
        return addTitle(name, title, subTitle, -1, -1, -1);
    }

    /**
     * Add a new title.
     *
     * @param name         The name of the title.
     * @param title        The title text.
     * @param subTitle     The sub title text.
     * @param fadeInTime   The fade in time.
     * @param stayTime     The stay time.
     * @param fadeOutTime  The fade out time.
     *
     * @return  True if added successfully.
     */
    public boolean addTitle(String name, String title, @Nullable String subTitle,
                            int fadeInTime, int stayTime, int fadeOutTime) {

        PreCon.notNullOrEmpty(name);
        PreCon.notNullOrEmpty(title);

        T instance = _factory.create(name, title, subTitle, fadeInTime, stayTime, fadeOutTime);

        _titles.put(instance.getSearchName(), instance);

        if (saveToDataNode(instance)) {
            assert _dataNode != null;

            _dataNode.saveAsync(null);
        }

        return true;
    }

    /**
     * Get a title by name.
     *
     * @param name  The name of the title.
     *
     * @return  Null if not found.
     */
    @Nullable
    public T getTitle(String name) {
        PreCon.notNullOrEmpty(name);

        return _titles.get(name.toLowerCase());
    }

    /**
     * Get all titles.
     */
    public List<T> getTitles() {
        return new ArrayList<>(_titles.values());
    }

    /**
     * Remove a title by name.
     *
     * @param name  The name of the title.
     */
    @Nullable
    public T removeTitle(String name) {
        PreCon.notNullOrEmpty(name);

        return _titles.remove(name.toLowerCase());
    }

    protected void loadSettings() {
        if (_dataNode == null)
            return;

        Set<String> titleNames = _dataNode.getSubNodeNames();

        for (String titleName : titleNames) {

            IDataNode dataNode = _dataNode.getNode(titleName);

            T title = loadFromDataNode(dataNode);

            _titles.put(title.getSearchName(), title);
        }
    }

    protected boolean saveToDataNode(T title) {
        if (_dataNode == null)
            return false;

        IDataNode node = _dataNode.getNode(title.getName());

        node.set("title", title.getTitle());
        node.set("subtitle", title.getSubTitle());
        node.set("fadein", title.getFadeInTime());
        node.set("stay", title.getStayTime());
        node.set("fadeout", title.getFadeOutTime());
        return true;
    }

    protected T loadFromDataNode(IDataNode dataNode) {

        String name = dataNode.getNodeName();
        String title = dataNode.getString("title");
        String subTitle = dataNode.getString("subtitle");
        int fadein = dataNode.getInteger("fadein", -1);
        int stay = dataNode.getInteger("stay", -1);
        int fadeout = dataNode.getInteger("fadeout", -1);

        if (title == null)
            throw new RuntimeException("Invalid config. title node is missing.");

        assert name != null;

        return _factory.create(name, title, subTitle, fadein, stay, fadeout);
    }
}
