/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import com.jcwhatever.bukkit.generic.mixins.IMeta;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/*
 * 
 */
public interface IViewSession extends IMeta, Iterable<IView>, IDisposable {

    /**
     * Get the view sessions owning plugin.
     */
    Plugin getPlugin();

    /**
     * Get the player the view session is for.
     */
    Player getPlayer();

    /**
     * Get the sessions event registration.
     */
    SessionRegistration getRegistration();

    /**
     * Get the block that is the source of the
     * view session. This is normally the block that
     * a player clicks in order to open the view.
     *
     * @return  Null if a block did not start the session.
     */
    @Nullable
    Block getSessionBlock();

    /**
     * Get the view instance the player is currently looking at.
     *
     * @return  Null if the player is not looking at any views in the session.
     */
    @Nullable
    IView getCurrentView();

    /**
     * Get the previous view, if any.
     *
     * @return  Null if the current view is the first view.
     */
    @Nullable
    IView getPrevView();

    /**
     * Get the next view, if any.
     *
     * @return  Null if the current view is the last view.
     */
    @Nullable
    IView getNextView();

    /**
     * Get the first view, if any.
     *
     * @return Null if there are no views.
     */
    @Nullable
    IView getFirstView();

    /**
     * Get the last view, if any.
     *
     * @return Null if there are no views.
     */
    @Nullable
    IView getLastView();

    /**
     * Close the current view and go to the previous view.
     *
     * <p>If there is no previous view, the session is ended.</p>
     *
     * @return Null if there is no previous view.
     */
    @Nullable
    IView back();

    /**
     * Show the next view.
     *
     * @param factory    The factory that will create the next view.
     * @param arguments  Arguments the previous view can pass to the next view.
     *
     * @return The newly created and displayed view.
     */
    IView next(IViewFactory factory, ViewArguments arguments);

    /**
     * Show the next view.
     *
     * @param title      Optional view title. Not all views have customizable titles.
     * @param factory    The factory that will create the next view.
     * @param arguments  Arguments the previous view can pass to the next view.
     *
     * @return The newly created and displayed view.
     */
    IView next(@Nullable String title, IViewFactory factory, ViewArguments arguments);
}
