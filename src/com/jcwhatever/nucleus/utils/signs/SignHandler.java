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


package com.jcwhatever.nucleus.utils.signs;

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;

/**
 * Abstract sign handler for signs of a specific type.
 */
public abstract class SignHandler implements INamedInsensitive, IPluginOwned {

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;

    private String _displayName;
    private SignHandlerRegistration _registration;
    private IDataNode _dataNode;

    /**
     * Specify the result of handling a sign change event.
     */
    public enum SignChangeResult {
        /**
         * The sign is valid.
         */
        VALID,
        /**
         * The sign is not valid.
         */
        INVALID
    }

    /**
     * Specify the result of handling a sign click event.
     */
    public enum SignClickResult {
        /**
         * The event was handled.
         */
        HANDLED,
        /**
         * The event was ignored.
         */
        IGNORED
    }

    /**
     * Specify the result of handling a sign break event.
     */
    public enum SignBreakResult {
        /**
         * Allow the sign to be broken.
         */
        ALLOW,
        /**
         * Prevent the sign from being broken.
         */
        DENY
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the sign (Used in the sign header). Must be a valid name.
     *                Starts with a letter, alphanumerics only. Underscores allowed.
     */
    public SignHandler(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.validNodeName(name);

        _plugin = plugin;
        _name = name;
        _searchName = name.toLowerCase();
    }

    @Override
    public final Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public final String getName() {
        return _name;
    }

    @Override
    public final String getSearchName() {
        return _searchName;
    }

    /**
     * Get a display name for the sign. Returns the sign handler name
     * with underscores converted to spaces.
     */
    public final String getDisplayName() {
        if (_displayName == null) {
            Matcher headerMatcher = TextUtils.PATTERN_UNDERSCORE.matcher(getName());
            _displayName = headerMatcher.replaceAll(" ");
        }

        return _displayName;
    }

    /**
     * Get a prefix to append to the header of a sign.
     */
    public abstract String getHeaderPrefix();

    /**
     * Get a description of the sign handler.
     */
    public abstract String getDescription();

    /**
     * Get a string array describing sign usage.
     *
     * <p>Each element of the array represents a line on the sign. There must be exactly 4 elements
     * in the array.</p>
     */
    public abstract String[] getUsage();

    /**
     * Invoked by the internal {@link ISignManager} implementation when the handler is registered.
     *
     * @param registration  The internal managers registration object.
     */
    public final void onRegister(SignHandlerRegistration registration) {
        PreCon.notNull(registration);
        PreCon.isValid(_registration == null, "Already registered.");

        _registration = registration;
    }

    /**
     * Invoked when a sign handled by the sign handler is loaded from the
     * {@link ISignManager} data node.
     *
     * @param sign  The loaded sign encapsulated.
     */
    protected abstract void onSignLoad(ISignContainer sign);

    /**
     * Invoked when a sign handled by the sign handler is changed/created.
     *
     * @param player  The {@link Player} who changed/created the sign.
     * @param sign    The encapsulated {@link org.bukkit.block.Sign}.
     *
     * @return  {@link SignChangeResult#VALID} if the change is valid/allowed,
     * otherwise {@link SignChangeResult#INVALID}.
     */
    protected abstract SignChangeResult onSignChange(Player player, ISignContainer sign);

    /**
     * Invoked when a sign handled by the sign handler is clicked on by a
     * {@link Player}.
     *
     * @param player  The {@link Player} who clicked the sign.
     * @param sign    The encapsulated {@link org.bukkit.block.Sign}.
     *
     * @return  {@link SignClickResult#HANDLED} if the click was valid and handled,
     * otherwise {@link SignClickResult#IGNORED}.
     */
    protected abstract SignClickResult onSignClick(Player player, ISignContainer sign);

    /**
     * Invoked when a sign handled by the sign handler is broken by a {@link Player}.
     * Sign break is only invoked when the player is capable of breaking a sign instantly.
     * (i.e creative mode) The sign cannot be broken unless the player is capable of
     * instant break.
     *
     * @param player  The {@link Player} who is breaking the sign.
     * @param sign    The encapsulated {@link org.bukkit.block.Sign}.
     *
     * @return  {@link SignBreakResult#ALLOW} if the break is allowed, otherwise
     * {@link SignBreakResult#DENY}.
     */
    protected abstract SignBreakResult onSignBreak(Player player, ISignContainer sign);

    /**
     * Used internally to access protected methods.
     */
    public static class SignHandlerRegistration {

        public void signLoad(SignHandler handler, ISignContainer sign) {
            PreCon.notNull(handler);
            PreCon.isValid(handler._registration == this, "Invalid registration.");

            handler.onSignLoad(sign);
        }

        public SignChangeResult signChange(SignHandler handler, Player player, ISignContainer sign) {
            PreCon.notNull(handler);
            PreCon.isValid(handler._registration == this, "Invalid registration.");

            return handler.onSignChange(player, sign);
        }

        public SignClickResult signClick(SignHandler handler, Player player, ISignContainer sign) {
            PreCon.notNull(handler);
            PreCon.isValid(handler._registration == this, "Invalid registration.");

            return handler.onSignClick(player, sign);
        }

        public SignBreakResult signBreak(SignHandler handler, Player player, ISignContainer sign) {
            PreCon.notNull(handler);
            PreCon.isValid(handler._registration == this, "Invalid registration.");

            return handler.onSignBreak(player, sign);
        }

        public IDataNode getDataNode(SignHandler handler) {
            PreCon.notNull(handler);
            PreCon.isValid(handler._registration == this, "Invalid registration.");

            if (handler._dataNode == null)
                handler._dataNode = DataStorage.get(handler.getPlugin(), new DataPath("nucleus.signs"));

            return handler._dataNode;
        }
    }
}
