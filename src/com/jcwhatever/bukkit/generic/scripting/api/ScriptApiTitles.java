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

package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import com.jcwhatever.bukkit.generic.titles.GenericsTitle;
import com.jcwhatever.bukkit.generic.titles.INamedTitle;
import com.jcwhatever.bukkit.generic.titles.ITitle;
import com.jcwhatever.bukkit.generic.utils.PlayerUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.sun.istack.internal.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@ScriptApiInfo(
        variableName = "titles",
        description = "Add GenericsLib titles scripting API support.")
public class ScriptApiTitles extends GenericsScriptApi {

    /**
     * Constructor.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiTitles(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return new ApiObject();
    }

    public static class ApiObject implements IScriptApiObject {

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public void dispose() {
            // do nothing
        }

        /**
         * Show a named title from the GenericsLib title manager.
         *
         * @param player     The player to show the title to.
         * @param titleName  The name of the title.
         */
        public void show(Object player, String titleName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(titleName);

            Player p = PlayerUtils.getPlayer(player);
            PreCon.notNull(p);

            INamedTitle title = GenericsLib.getTitleManager().getTitle(titleName);
            PreCon.notNull(title, "Failed to find a title named " + titleName);

            title.showTo(p);
        }

        /**
         * Create a new transient title object to display to players.
         *
         * @param title         The title text.
         * @param subTitle      Optional sub title text.
         * @param fadeInTicks   The time spent fading in.
         * @param stayTicks     The time spent being displayed.
         * @param fadeOutTicks  The time spent fading out.
         */
        public ITitle create(String title, @Nullable String subTitle,
                             int fadeInTicks, int stayTicks, int fadeOutTicks) {

            PreCon.notNullOrEmpty(title);

            return new GenericsTitle(title, subTitle,
                    fadeInTicks, stayTicks, fadeOutTicks);
        }
    }
}
