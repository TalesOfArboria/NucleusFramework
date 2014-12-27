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

package com.jcwhatever.nucleus.commands.arguments;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfoContainer;
import com.jcwhatever.nucleus.commands.parameters.FlagParameter;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * {@code ArgumentParser} results.
 */
public class ArgumentParseResults {

    private final AbstractCommand _command;

    private final Map<String, CommandArgument> _argMap;
    private final Map<String, Boolean> _flagMap;

    private final List<CommandArgument> _staticArgs;
    private final List<CommandArgument> _floatingArgs;

    /**
     * Constructor.
     *
     * @param command  The command the results are for.
     */
    public ArgumentParseResults(AbstractCommand command) {
        PreCon.notNull(command);

        _command = command;

        int staticSize = command.getInfo().getRawStaticParams().length;
        int floatingSize = command.getInfo().getRawFloatingParams().length;
        int flagSize = command.getInfo().getRawFlagParams().length;

        _staticArgs = new ArrayList<>(staticSize);
        _floatingArgs = new ArrayList<>(floatingSize);
        _flagMap = new HashMap<>(flagSize);
        _argMap = new HashMap<String, CommandArgument>(staticSize + floatingSize);

        for (FlagParameter flagParam : command.getInfo().getFlagParams()) {
            _flagMap.put(flagParam.getName(), false);
        }
    }

    /**
     * Get the commands the results are for.
     */
    public AbstractCommand getCommand() {
        return _command;
    }

    /**
     * Get the commands info.
     */
    public CommandInfoContainer getInfo() {
        return _command.getInfo();
    }

    /**
     * Get the parsed static arguments.
     */
    public List<CommandArgument> getStaticArgs() {
        return _staticArgs;
    }

    /**
     * Get the parsed floating arguments.
     */
    public List<CommandArgument> getFloatingArgs() {
        return _floatingArgs;
    }

    /**
     * Get a map containing all parsed static and
     * floating arguments keyed to the parameter name.
     */
    public Map<String, CommandArgument> getArgMap() {
        return _argMap;
    }

    /**
     * Get a flag parameter by flag name.
     *
     * @param name  The name of the flag.
     *
     * @return Null if no such flag defined.
     */
    @Nullable
    public Boolean getFlag(String name) {
        return _flagMap.get(name);
    }

    /**
     * Set a flags value.
     *
     * @param name   The name of the flag.
     * @param value  The value of the flag.
     */
    public void setFlag(String name, boolean value) {
        _flagMap.put(name, value);
    }
}
