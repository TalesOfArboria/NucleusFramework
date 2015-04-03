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

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;

import org.bukkit.command.CommandSender;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * A context for a command response request.
 *
 * <p>The context can be used to send the request as well as receive updates
 * on the status of the request.</p>
 */
public interface IRequestContext extends IPluginOwned, INamedInsensitive {

    /**
     * Get the command sender.
     */
    CommandSender getCommandSender();

    /**
     * Send the request to the {@link CommandSender}.
     *
     * <p>Can only be invoked once.</p>
     *
     * @return  True if the request was sent, false if it has already been sent or otherwise.
     */
    boolean sendRequest();

    /**
     * Get the amount of time in seconds before the request times out.
     */
    int getTimeout();

    /**
     * Determine if a response type is contained in the context.
     *
     * @param responseType  The {@link ResponseType} to check.
     */
    boolean contains(ResponseType responseType);

    /**
     * Get the requested response types.
     */
    Collection<ResponseType> getResponseTypes();

    /**
     * Determine if the request is cancelled.
     */
    boolean isCancelled();

    /**
     * Determine if the request has timed out.
     */
    boolean isTimeout();

    /**
     * Get the {@link CommandSender}'s response.
     *
     * @return  The {@link ResponseType} or null if the {@link CommandSender} did not
     * respond or has not responded yet.
     */
    @Nullable
    ResponseType getResponse();

    /**
     * Cancel the response request.
     */
    void cancel();

    /**
     * Invoked by {@link IResponseRequestor} to set response.
     *
     * @param responseType  The response.
     */
    void respond(ResponseType responseType);

    /**
     * Register a subscriber for updates when the request receives any type of update.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    IRequestContext onResult(IUpdateSubscriber<IRequestContext> subscriber);

    /**
     * Register a subscriber to receive updates when the request is received.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    IRequestContext onRespond(IUpdateSubscriber<IRequestContext> subscriber);

    /**
     * Register a subscriber to receive updates when the request is cancelled.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    IRequestContext onCancel(IUpdateSubscriber<IRequestContext> subscriber);

    /**
     * Register a subscriber to receive updates when the request times out.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    IRequestContext onTimeout(IUpdateSubscriber<IRequestContext> subscriber);
}
