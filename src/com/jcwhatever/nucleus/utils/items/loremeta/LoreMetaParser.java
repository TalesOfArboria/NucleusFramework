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

package com.jcwhatever.nucleus.utils.items.loremeta;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ThreadSingletons;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import net.md_5.bungee.api.ChatColor;

import javax.annotation.Nullable;

/**
 * Default implementation of {@link ILoreMetaParser}.
 */
public class LoreMetaParser implements ILoreMetaParser {

    private static final String META_INDICATOR = TextColor.BLACK.toString()
            + TextColor.DARK_GRAY.toString() + TextColor.DARK_PURPLE.toString();

    private static ThreadSingletons<StringBuilder> STRING_BUILDERS =
            new ThreadSingletons<>(new ThreadSingletons.ISingletonFactory<StringBuilder>() {
                @Override
                public StringBuilder create(Thread thread) {
                    return new StringBuilder(16);
                }
            });

    private static LoreMetaParser _default;

    /**
     * Get the default instance.
     */
    public static LoreMetaParser get() {
        if (_default == null)
            _default = new LoreMetaParser();

        return _default;
    }

    private final TextColor _nameColor;
    private final TextColor _valueColor;

    /**
     * Constructor.
     */
    public LoreMetaParser() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param nameColor   Optional output color for the meta display name.
     * @param valueColor  Optional output color for the meta display value.
     */
    public LoreMetaParser(@Nullable TextColor nameColor, @Nullable TextColor valueColor) {
        _nameColor = nameColor;
        _valueColor = valueColor;
    }

    @Override
    public boolean isLoreMeta(String loreLine) {
        return loreLine.startsWith(META_INDICATOR);
    }

    @Override
    @Nullable
    public LoreMetaItem parseLoreMeta(String loreLine) {

        if (!loreLine.startsWith(META_INDICATOR))
            return null;

        loreLine = ChatColor.stripColor(loreLine);

        String[] components = TextUtils.PATTERN_DASH.split(loreLine);

        if (components.length == 0)
            return null;

        String name = components[0].trim();
        StringBuilder value = STRING_BUILDERS.get();
        value.setLength(0);

        if (components.length == 2) {
            value.append(components[1].trim());
        }
        else {
            for (int i=1; i < components.length; i++) {
                value.append(components[i]);
                if (i < components.length - 1)
                    value.append('-');
            }
        }

        return new LoreMetaItem(name, value.toString().trim());
    }

    @Override
    public String getMetaDisplay(LoreMetaItem item) {
        PreCon.notNull(item);

        return getMetaDisplay(item.getName(), item.getValue());
    }

    @Override
    public String getMetaDisplay(String name, String value) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(value);

        StringBuilder result = STRING_BUILDERS.get();
        result.setLength(0);

        result.append(META_INDICATOR);

        if (_nameColor != null)
            result.append(_nameColor);

        result.append(name);

        if (value.isEmpty())
            return result.toString();

        result.append(TextColor.GRAY);
        result.append(" - ");

        if (_valueColor != null)
            result.append(_valueColor);

        result.append(value);

        return result.toString();
    }
}
