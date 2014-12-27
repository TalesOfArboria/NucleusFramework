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

import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Represents a command flag parameter.
 */
public class FlagParameter implements INamed {

    private final String _flagName;
    private final int _definitionIndex;

    /**
     * Constructor.
     *
     * @param flagName         The parameter name.
     * @param definitionIndex  The index position of the flag in the annotation flags definition.
     */
    public FlagParameter(String flagName, int definitionIndex) {
        PreCon.notNullOrEmpty(flagName);

        _flagName = flagName;
        _definitionIndex = definitionIndex;
    }

    /**
     * Get the parameter name.
     */
    @Override
    public String getName() {
        return _flagName;
    }

    /**
     * Get the index order of the flag in the
     * annotation flags definition.
     */
    public int getDefinitionIndex() {
        return _definitionIndex;
    }

    @Override
    public final int hashCode() {
        return _flagName.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof FlagParameter &&
                ((FlagParameter) obj)._flagName.equals(_flagName);
    }
}
