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

package com.jcwhatever.nucleus.commands.response;

import org.bukkit.command.CommandSender;

/**
 * Interface for an {@link IRequestContext} instance builder.
 */
public interface IRequestContextBuilder {

    /**
     * Set the name of the context.
     *
     * @param contextName  The context name.
     *
     * @return The second builder for chaining.
     */
    IRequestContextBuilder2 name(String contextName);


    interface IRequestContextBuilder2 {

        /**
         * Set the request timeout in seconds.
         *
         * @param seconds  The number of seconds before the response request times out.
         *
         * @return  The third builder for chaining.
         */
        IRequestContextBuilder3 timeout(int seconds);
    }

    interface IRequestContextBuilder3 {

        /**
         * Add an acceptable response to the request.
         *
         * @param acceptableResponse  The response request.
         *
         * @return  The fourth builder or self for chaining.
         */
        IRequestContextBuilder4 response(ResponseType acceptableResponse);
    }

    interface IRequestContextBuilder4 extends IRequestContextBuilder3 {

        /**
         * Build the context for the specified {@link CommandSender}.
         *
         * @param sender  The {@link CommandSender}.
         *
         * @return  The new {@link IRequestContext}.
         */
        IRequestContext build(final CommandSender sender);
    }
}
