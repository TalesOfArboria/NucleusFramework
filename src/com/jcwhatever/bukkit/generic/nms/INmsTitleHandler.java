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

package com.jcwhatever.bukkit.generic.nms;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Interface for GenericsLib's Minecraft Titles handler which
 * can be retrieved from GenericsLibs NmsManager under the
 * name "TITLES".
 */
public interface INmsTitleHandler extends INmsHandler {

    /**
     * Send a title to a player.
     *
     * @param player        The player to send the title to.
     * @param jsonTitle     The Json title text.
     * @param jsonSubtitle  Optional Json subtitle text.
     * @param fadeIn        The fade-in time.
     * @param stay          The stay time.
     * @param fadeOut       The fade-out time.
     */
    void send(Player player, String jsonTitle, @Nullable String jsonSubtitle,
              int fadeIn, int stay, int fadeOut);

    /**
     * Send a tab list header and/or footer to a player.
     *
     * @param player      The player to send the header/footer to.
     * @param jsonHeader  The Json header text.
     * @param jsonFooter  The Json footer text.
     */
    void sendTab(Player player, String jsonHeader, String jsonFooter);
}
