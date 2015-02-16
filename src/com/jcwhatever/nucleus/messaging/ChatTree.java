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

package com.jcwhatever.nucleus.messaging;

import com.jcwhatever.nucleus.collections.HierarchyNode;
import com.jcwhatever.nucleus.collections.TreeNode;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.mixins.IHierarchyNode;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextColor;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Converts a collection of root nodes into a list of
 * strings that can be used to display the node hierarchy
 * in chat.
 */
public class ChatTree<T extends IHierarchyNode<T>> implements IPluginOwned, Iterable<T> {

    private final Plugin _plugin;
    protected List<TreeNode<T>> _rootNodes;
    protected StringBuilder _buffer = new StringBuilder(20);

    /**
     * Constructor.
     */
    public ChatTree(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
        _rootNodes = new ArrayList<>(10);
    }

    /**
     * Constructor.
     *
     * @param rootNodes  The initial collection of root nodes.
     */
    public ChatTree(Plugin plugin, Collection<? extends T> rootNodes) {
        PreCon.notNull(plugin);
        PreCon.notNull(rootNodes);

        _plugin = plugin;
        _rootNodes = new ArrayList<>(rootNodes.size());

        for (T node : rootNodes) {
            _rootNodes.add(new HierarchyNode<T>(node));
        }
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Add a root node.
     *
     * @param rootNode  The root node to add.
     */
    public void addRoot(T rootNode) {
        PreCon.notNull(rootNode);

        _rootNodes.add(new HierarchyNode<T>(rootNode));
    }

    /**
     * Add a collection of root nodes.
     *
     * @param rootNodes  The root nodes to add.
     */
    public void addAllRoots(Collection<? extends T> rootNodes) {
        for (T node : rootNodes) {
            _rootNodes.add(new HierarchyNode<T>(node));
        }
    }

    /**
     * Add a {@link HierarchyNode}.
     *
     * @param rootNode  The root node to add.
     */
    public void addRootNode(TreeNode<T> rootNode) {
        PreCon.notNull(rootNode);

        _rootNodes.add(rootNode);
    }

    /**
     * Add a collection of root nodes.
     *
     * @param rootNodes  The root nodes to add.
     */
    public void addAllRootNodes(Collection<? extends TreeNode<T>> rootNodes) {
        PreCon.notNull(rootNodes);

        _rootNodes.addAll(rootNodes);
    }

    /**
     * Show the entire node tree to a {@link  org.bukkit.command.CommandSender}.
     *
     * @param sender  The {@link  org.bukkit.command.CommandSender}.
     */
    public void show(CommandSender sender) {
        PreCon.notNull(sender);

        show(sender, new NodeLineWriter<T>() {
            @Override
            public String write(T nodeValue) {
                return String.valueOf(nodeValue);
            }
        });
    }

    /**
     * Show the entire node tree to a {@link  org.bukkit.command.CommandSender}.
     *
     * @param sender      The {@link  org.bukkit.command.CommandSender}.
     * @param lineWriter  The line writer used to convert node objects into a text line.
     */
    public void show(CommandSender sender, NodeLineWriter<T> lineWriter) {
        PreCon.notNull(sender);
        PreCon.notNull(lineWriter);

        List<String> lines =  toChatLines(lineWriter);

        for (String line : lines) {
            NucMsg.tell(_plugin, sender, line);
        }
    }

    /**
     * Convert each node into a string for use in chat.
     */
    public List<String> toChatLines() {
        return toChatLines(new NodeLineWriter<T>() {
            @Override
            public String write(T nodeValue) {
                return String.valueOf(nodeValue);
            }
        });
    }

    /**
     * Convert each node into a string for use in chat.
     *
     * @param lineWriter The line writer used to convert node objects into a text line.
     */
    public List<String> toChatLines(NodeLineWriter<T> lineWriter) {
        PreCon.notNull(lineWriter);

        List<String> result = new ArrayList<>(_rootNodes.size() * 3);

        for (TreeNode<T> rootNode : _rootNodes) {
            for (TreeNode<T> node : rootNode) {
                String line = lineWriter.write(node.getValue());
                if (line != null) {
                    result.add(getDepthPrefix(node) + line);
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        List<T> iterable = new ArrayList<>(_rootNodes.size() * 3);

        for (TreeNode<T> rootNode : _rootNodes) {
            for (TreeNode<T> node : rootNode) {
                iterable.add(node.getValue());
            }
        }

        return iterable.iterator();
    }

    /**
     * Get the string to prefix to a node line which indicates the node depth.
     *
     * @param node  The node to get a depth prefix for.
     */
    protected String getDepthPrefix(TreeNode<T> node) {
        if (node.isRoot())
            return "";

        TreeNode<T> parent = node.getParent();
        assert parent != null;

        boolean isLast = parent.getIndex(node) == parent.size() - 1;

        int depth = node.getDepth();

        _buffer.setLength(0);

        _buffer.append(getLinePrefix());

        for (int i=0; i < depth; i++) {

            boolean isLastIteration = i == depth - 1;
            _buffer.append(' ')
                   .append(getDepthPrefix(i))
                   .append(isLastIteration ? (isLast ? '\u2514' : '\u251c') : '\u2502');
        }

        return _buffer.toString();
    }

    /**
     * Get the prefix used on all lines.
     */
    protected String getLinePrefix() {
        return TextColor.GRAY.getFormatCode();
    }

    /**
     * Get the prefix used for a specific depth.
     *
     * @param depth  The depth.
     */
    protected String getDepthPrefix(@SuppressWarnings("unused") int depth) {
        return "";
    }

    /**
     * Converts a node object into text.
     *
     * @param <T>  The node object type.
     */
    public static interface NodeLineWriter<T extends IHierarchyNode<T>> {

        /**
         * Write a node value to a string.
         *
         * @param nodeValue  The node value.
         *
         * @return  The node's string representation or null to exclude.
         */
        @Nullable
        String write(T nodeValue);
    }
}
