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

package com.jcwhatever.nucleus.scripting.api;

import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.ScriptApiInfo;
import com.jcwhatever.nucleus.utils.titles.ITitle;
import com.jcwhatever.nucleus.utils.titles.Title;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;


@ScriptApiInfo(
        variableName = "titles",
        description = "Add NucleusFramework titles scripting API support.")
public class ScriptApiTitles extends NucleusScriptApi {

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

            return new Title(title, subTitle,
                    fadeInTicks, stayTicks, fadeOutTicks);
        }
    }
}
