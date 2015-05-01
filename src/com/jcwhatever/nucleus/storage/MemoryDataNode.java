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

package com.jcwhatever.nucleus.storage;

import com.jcwhatever.nucleus.collections.TreeEntryNode;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * A transient memory data node.
 */
public class MemoryDataNode extends AbstractDataNode {

    private final Plugin _plugin;
    private final MemoryDataNode _rootStorage;
    private TreeEntryNode<String, Object> _node;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public MemoryDataNode(Plugin plugin) {
        PreCon.notNull(plugin);
        
        _plugin = plugin;
        _node = new TreeEntryNode<>("", null);
        _rootStorage = this;
    }

    /**
     * Constructor for child data nodes.
     *
     * @param root  The root node.
     * @param path  The child node path.
     * @param node  The child node.
     */
    protected MemoryDataNode(MemoryDataNode root, String path,
                             TreeEntryNode<String, Object> node) {
        super(root, path);
        
        PreCon.notNull(root);

        _plugin = root.getPlugin();
        _node = node;
        _rootStorage = root;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public IDataNode getRoot() {
        return _rootStorage;
    }

    @Override
    public boolean isRoot() {
        return _rootStorage == this;
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public IFuture loadAsync() {

        return new FutureAgent().success(null);
    }

    @Override
    public boolean saveSync() {
        cleanAll();
        return true;
    }

    @Override
    public IFuture save() {

        cleanAll();

        return new FutureAgent().success(null);
    }

    @Override
    public boolean saveSync(File destination) {
        return false;
    }

    @Override
    public IFuture save(File destination) {
        return new FutureAgent().cancel(null);
    }

    @Override
    public AutoSaveMode getDefaultAutoSaveMode() {
        return AutoSaveMode.DISABLED;
    }

    @Override
    public int size() {
        return _node.totalChildren();
    }

    @Override
    public boolean hasNode(String nodePath) {
        return getNodeFromPath(nodePath, false) != null;
    }

    @Override
    public IDataNode getNode(String nodePath) {
        TreeEntryNode<String, Object> treeNode = getNodeFromPath(nodePath, true);
        if (treeNode == _node)
            return this;

        return new MemoryDataNode(_rootStorage, getFullPath(nodePath), treeNode);
    }

    @Override
    public Collection<String> getSubNodeNames() {
        return _node.getChildKeySet();
    }

    @Override
    public <T extends Collection<String>> T getSubNodeNames(T output) {
        PreCon.notNull(output);

        output.addAll(_node.getChildKeySet());
        return output;
    }

    @Override
    public Collection<String> getSubNodeNames(String nodePath) {
        return getSubNodeNames(nodePath, new HashSet<String>(0));
    }

    @Override
    public <T extends Collection<String>> T getSubNodeNames(String nodePath, T output) {
        PreCon.notNull(nodePath);
        PreCon.notNull(output);

        TreeEntryNode<String, Object> treeNode = getNodeFromPath(nodePath, false);
        if (treeNode == null)
            return output;

        return treeNode.getChildKeySet(output);
    }

    @Override
    public void clear() {
        _node.clearChildren();

        markDirty();
    }

    @Override
    public void remove() {
        if (_node.getParent() == null)
            throw new UnsupportedOperationException("Cannot remove root node.");

        markDirty();

        _node.getParent().removeChild(_node);
    }

    @Override
    public void remove(String nodePath) {
        TreeEntryNode<String, Object> treeNode = getNodeFromPath(nodePath, false);
        if (treeNode == null)
            return;

        markDirty();

        //noinspection ConstantConditions
        treeNode.getParent().removeChild(treeNode);
    }

    @Override
    public Map<String, Object> getAllValues() {
        Map<String, Object> result = new HashMap<>(20);

        for (TreeEntryNode<String, Object> node : _node) {
            if (node == _node || node.getValue() == null)
                continue;

            result.put(getPath(node), node.getValue());
        }

        return result;
    }

    @Nullable
    @Override
    public Object get(String keyPath) {
        TreeEntryNode<String, Object> entry = getNodeFromPath(keyPath, false);
        if (entry == null)
            return null;

        return entry.getValue();
    }

    @Override
    public boolean set(String keyPath, @Nullable Object value) {

        markDirty();

        if (value instanceof IDataNodeSerializable) {
            removeNode(keyPath);
            IDataNode node = getNode(keyPath);
            IDataNodeSerializable serializable = (IDataNodeSerializable)value;
            serializable.serialize(node);
        }
        else if (value != null) {
            setNodeFromPath(keyPath, value);
        } else {
            removeNode(keyPath);
        }
        return true;
    }

    @Nullable
    protected TreeEntryNode<String, Object> getNodeFromPath(String nodePath, boolean create) {

        if (nodePath.isEmpty())
            return _node;

        TreeEntryNode<String, Object> node = _node;

        String[] pathNames = TextUtils.PATTERN_DOT.split(nodePath);

        for (int i=0; i < pathNames.length; i++) {
            String pathName = pathNames[i];
            TreeEntryNode<String, Object> child = node.getChild(pathName);

            if (child == null) {
                if (create) {
                    child = node.putChild(pathName, null);
                }
                else {
                    return null;
                }
            }
            if (create && i < pathNames.length - 1) {
                child.setValue(null);
            }
            node = child;
        }

        return node;
    }

    @Nullable
    protected TreeEntryNode<String, Object> setNodeFromPath(String nodePath, Object value) {

        TreeEntryNode<String, Object> node = getNodeFromPath(nodePath, true);
        assert node != null;

        node.setValue(value);
        node.clearChildren();

        return node;
    }

    protected boolean removeNode(String keyPath) {
        TreeEntryNode<String, Object> treeNode = getNodeFromPath(keyPath, false);
        if (treeNode == null)
            return false;

        if (treeNode == _node) {
            treeNode.clearChildren();
        }
        else {
            //noinspection ConstantConditions
            treeNode.getParent().removeChild(treeNode);
        }

        return true;
    }

    protected String getPath(TreeEntryNode<String, Object> node) {
        PreCon.notNull(node);
        LinkedList<String> components = new LinkedList<>();

        while (!node.equals(_node)) {
            if (!node.isRoot())
                components.addFirst(node.getKey());

            node = node.getParent();
            if (node == null)
                break;
        }

        return TextUtils.concat(components, ".");
    }
}
