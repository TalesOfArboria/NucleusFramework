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

package com.jcwhatever.nucleus.utils.nms;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Interface for NucleusFramework's Minecraft Titles handler.
 *
 * @see NmsUtils
 * @see com.jcwhatever.nucleus.utils.titles.Title
 */
public interface INmsTitleHandler extends INmsHandler {

    /**
     * Send a title to a player.
     *
     * <p>The handler is responsible for converting the raw
     * text into the appropriate format.</p>
     *
     * @param player        The player to send the title to.
     * @param rawTitle      The title text.
     * @param rawSubtitle   Optional subtitle text.
     * @param fadeIn        The fade-in time.
     * @param stay          The stay time.
     * @param fadeOut       The fade-out time.
     */
    void send(Player player, String rawTitle, @Nullable String rawSubtitle,
              int fadeIn, int stay, int fadeOut);

    /**
     * Send a title to a player.
     *
     * <p>Bypasses the handlers text conversion.</p>
     *
     * @param player        The player to send the title to.
     * @param jsonTitle     The title text.
     * @param jsonSubtitle  Optional subtitle text.
     * @param fadeIn        The fade-in time.
     * @param stay          The stay time.
     * @param fadeOut       The fade-out time.
     */
    void sendJson(Player player, String jsonTitle, @Nullable String jsonSubtitle,
              int fadeIn, int stay, int fadeOut);
}
