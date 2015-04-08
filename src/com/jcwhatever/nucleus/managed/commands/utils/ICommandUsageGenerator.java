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

package com.jcwhatever.nucleus.managed.commands.utils;

import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;

/**
 * Generates command usage text.
 */
public interface ICommandUsageGenerator {

    @Localizable String HELP_USAGE =
            "{GOLD}/{0: root command}{1: command path}{GREEN}{2: command}{DARK_AQUA}{3: parameters}";

    @Localizable String HELP_USAGE_HAS_SUB_COMMANDS =
            "{GOLD}/{0: root command}{1: command path}{GREEN}{2: command}?";

    @Localizable String PARAMETER_HELP =
            "{GRAY}/{0: root command}{1: command path}{2: command}{GOLD}{3: parameters}";

    @Localizable String INLINE_HELP =
            "{GREEN}/{0: root command}{1: command path}{2: command}?";

    /**
     * Generate default command usage.
     *
     * @param command  The command to generate usage text for.
     */
    String generate(IRegisteredCommand command);

    /**
     * Generate command usage using the specified root command name.
     *
     * @param command          The command to generate usage text for.
     * @param rootCommandName  The root command name.
     */
    String generate(IRegisteredCommand command, String rootCommandName);

    /**
     * Generate command usage using the specified root command name and
     * a formatting template that uses the following parameters:
     *
     * <ul>
     *     <li>{0} - root command name</li>
     *     <li>{1} - command path excluding the root command and the command.</li>
     *     <li>{2} - the command name</li>
     *     <li>{3} - command parameters</li>
     * </ul>
     *
     * <p>Note that a space is added after values inserted for the above parameters
     * except when the parameter value is empty.</p>
     *
     * @param command          The command to generate usage text for.
     * @param rootCommandName  The name of the root command.
     * @param template         The format template for the generated text.
     */
    String generate(IRegisteredCommand command, String rootCommandName, String template);
}
