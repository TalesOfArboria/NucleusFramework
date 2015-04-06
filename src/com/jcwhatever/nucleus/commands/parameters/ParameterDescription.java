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

package com.jcwhatever.nucleus.commands.parameters;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.language.Localized;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextFormatter.ITagFormatter;
import com.jcwhatever.nucleus.utils.text.TextFormatterSettings;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.plugin.Plugin;

/**
 * A command parameter description.
 */
public class ParameterDescription implements IPluginOwned, INamed {

    @Localizable
    public static final String NAME16 =
            "Must be no more than 16 characters in length and " +
                    "include only alphanumeric characters. Underscores are allowed.";

    @Localizable public static final String NAME =
            "Can include only alphanumeric characters. Underscores are allowed.";

    @Localizable public static final String ITEM_STACK =
            "A parsable ItemStack string can be entered or you can use 'inhand' to use the item in your hand, " +
                    "'hotbar' to use the items in your hot bar, or 'inventory' to use all the items " +
                    "in your inventory.";

    @Localizable public static final String LOCATION =
            "Use 'current' to use your current location or 'select' to select a location " +
                    "by clicking on a block.";

    @Localizable public static final String PAGE =
            "Optional page number to specify which page of the results you want to use. " +
                    "If not specified, page 1 is displayed.";

    private static final ITagFormatter NAME_FORMATTER = new ITagFormatter() {
        @Override
        public String getTag() {
            return "NAME";
        }

        @Override
        public void append(StringBuilder sb, String rawTag) {
            sb.append(NucLang.get(NAME));
        }
    };

    private static final ITagFormatter NAME16_FORMATTER = new ITagFormatter() {
        @Override
        public String getTag() {
            return "NAME16";
        }

        @Override
        public void append(StringBuilder sb, String rawTag) {
            sb.append(NucLang.get(NAME16));
        }
    };

    private static final ITagFormatter ITEM_STACK_FORMATTER = new ITagFormatter() {
        @Override
        public String getTag() {
            return "ITEM_STACK";
        }

        @Override
        public void append(StringBuilder sb, String rawTag) {
            sb.append(NucLang.get(ITEM_STACK));
        }
    };

    private static final ITagFormatter LOCATION_FORMATTER = new ITagFormatter() {
        @Override
        public String getTag() {
            return "LOCATION";
        }

        @Override
        public void append(StringBuilder sb, String rawTag) {
            sb.append(NucLang.get(LOCATION));
        }
    };

    private static final ITagFormatter PAGE_FORMATTER = new ITagFormatter() {
        @Override
        public String getTag() {
            return "PAGE";
        }

        @Override
        public void append(StringBuilder sb, String rawTag) {
            sb.append(NucLang.get(PAGE));
        }
    };

    private static final TextFormatterSettings FORMAT_SETTINGS = new TextFormatterSettings(
            TextUtils.TEXT_FORMATTER_SETTINGS,
            NAME_FORMATTER,
            NAME16_FORMATTER,
            ITEM_STACK_FORMATTER,
            LOCATION_FORMATTER,
            PAGE_FORMATTER
    );

    private final AbstractCommand _command;
    private final String _parameterName;
    private final String _description;

    /**
     * Constructor.
     *
     * @param parameterName  The parameter name.
     * @param description    The parameter description.
     */
    public ParameterDescription(AbstractCommand command, String parameterName, String description) {
        PreCon.notNull(command);
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(description);

        _command = command;
        _parameterName = parameterName;
        _description = TextUtils.format(FORMAT_SETTINGS, NucLang.get(command.getPlugin(), description));
    }

    /**
     * Constructor.
     *
     * @param rawDescription  The raw description to parse.
     */
    public ParameterDescription(AbstractCommand command, String rawDescription) {
        PreCon.notNull(command);
        PreCon.notNullOrEmpty(rawDescription);

        _command = command;

        String[] descComp = TextUtils.PATTERN_EQUALS.split(rawDescription, -1);

        _parameterName = descComp[0].trim();

        if (descComp.length < 2) {
            throw new RuntimeException("Invalid description for parameter '"
                    + _parameterName + "' in command: " + command.getClass().getName());
        }

        // re-add equal characters that may have been in the description.
        _description = TextUtils.format(FORMAT_SETTINGS,
                NucLang.get(command.getPlugin(), TextUtils.concat(1, descComp, "=")));
    }

    @Override
    public Plugin getPlugin() {
        return _command.getPlugin();
    }

    @Override
    public String getName() {
        return _parameterName;
    }

    /**
     * Get the parameter description.
     */
    @Localized
    public String getDescription() {
        return _description;
    }

    @Override
    @Localized
    public String toString() {
        return getDescription();
    }
}
