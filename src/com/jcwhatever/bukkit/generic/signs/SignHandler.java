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


package com.jcwhatever.bukkit.generic.signs;

import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;

/**
 * Handles actions for a specific sign type.
 */
public abstract class SignHandler {

    private String _searchName;
    private String _displayName;

    /**
     * The owning plugin.
     */
    public abstract Plugin getPlugin();

    /**
     * The name of the sign handler, must be a valid name.
     * Starts with a letter, alphanumerics only. Underscores allowed.
     */
    public abstract String getName();

    /**
     * Get a prefix to append to the header of a sign.
     */
    public abstract String getHeaderPrefix();

    /**
     * Get a description of the sign handler.
     */
    public abstract String getDescription();

    /**
     * Get a string array describing sign usage. Each element of
     * the array represents a line on the sign. There must be 4 elements
     * in the array.
     */
    public abstract String[] getUsage();

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
     * Get the sign handler name in all lower case.
     */
    public final String getSearchName() {
        if (_searchName == null)
            _searchName = getName().toLowerCase();

        return _searchName;
    }


    /**
     * Called when a sign handled by the sign handler is
     * loaded from the sign manager data node.
     *
     * @param sign  The loaded sign encapsulated.
     */
    protected abstract void onSignLoad(SignContainer sign);

    /**
     * Called when a sign handled by the sign handler is
     * changed/created.
     *
     * @param p     The player who changed/created the sign.
     * @param sign  The encapsulated sign.
     *
     * @return  True if the change is valid/allowed.
     */
    protected abstract boolean onSignChange(Player p, SignContainer sign);

    /**
     * Called when a sign handled by the sign handler is
     * clicked on by a player.
     *
     * @param p     The player who clicked the sign.
     * @param sign  The encapsulated sign.
     *
     * @return  True if the click is handled.
     */
    protected abstract boolean onSignClick(Player p, SignContainer sign);

    /**
     * Called when a sign handled by the sign handler is
     * broken by a player.
     *
     * @param p     The player who is breaking the sign.
     * @param sign  The encapsulated sign.
     *
     * @return  True if the break is allowed.
     */
    protected abstract boolean onSignBreak(Player p, SignContainer sign);


    boolean signClick(Player p, SignContainer sign) {
        return onSignClick(p, sign);
    }

    boolean signChange(Player p, SignContainer sign) {
        return onSignChange(p, sign);
    }

    boolean signBreak(Player p, SignContainer sign) {
        return onSignBreak(p, sign);
    }

    void signLoad(SignContainer sign) {
        onSignLoad(sign);
    }



}
