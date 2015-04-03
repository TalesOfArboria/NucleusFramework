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

package com.jcwhatever.nucleus.managed.language;

import com.jcwhatever.nucleus.mixins.IPluginOwned;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A language context use for transforming internal text into text specified
 * in language files within the contexts jar file or other external files.
 */
public interface ILanguageContext extends IPluginOwned {

    /**
     * Clear all localizations.
     */
    void clear();

    /**
     * Reload internal localizations.
     */
    void reload();

    /**
     * Merge a language file into the language context.
     *
     * @param file  The language file to include
     *
     * @return  True if the file was merged, otherwise false.
     */
    boolean addFile(File file) throws FileNotFoundException;

    /**
     * Localize a text string.
     *
     * <p>The text must be a key that was parsed from the compiled
     * jar file and is an entry in the lang.key.txt file in the plugins
     * resource file.</p>
     *
     * <p>If an entry is not found, the unlocalized text is formatted and returned.</p>
     *
     * @param text    The text to localize.
     * @param params  Optional format arguments.
     */
    @Localized
    String get(String text, Object... params);
}
