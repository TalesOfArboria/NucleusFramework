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


package com.jcwhatever.bukkit.generic.commands.arguments;

/**
 * Specifies the argument type.
 */
enum ArgumentType {
    /**
     * An argument whose parameter was not referenced at all
     * by the command sender. Only possible with floating parameters.
     */
    UNDEFINED,

    /**
     * An argument whose parameter was referenced by the command sender
     * but no value provided. Only possible with floating parameters.
     *
     * <p>Indicates the use of the parameter flag without providing a value.</p>
     *
     */
    DEFINED_PARAM_UNDEFINED_VALUE,

    /**
     * Indicates the argument value was provided by the command sender.
     */
    DEFINED_VALUE,

    /**
     * Indicates no argument provided but a default value is available.
     * Only possible with static parameters.
     */
    DEFAULT_VALUE
}
