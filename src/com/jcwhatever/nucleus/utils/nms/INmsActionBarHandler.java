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

/**
 * Interface for NucleusFramework's Minecraft Action Bar handler.
 *
 * @see NmsUtils
 * @see com.jcwhatever.nucleus.utils.actionbar.ActionBar
 * @see com.jcwhatever.nucleus.utils.actionbar.PersistentActionBar
 * @see com.jcwhatever.nucleus.utils.actionbar.TimedActionBar
 */
public interface INmsActionBarHandler extends INmsHandler {

    /**
     * Send action bar text to a player.
     *
     * <p>The handler is responsible for converting the raw text
     * to whatever format is required.</p>
     *
     * @param player   The player to send the text to.
     * @param rawText  The raw text.
     *
     * @return  The json converted text.
     */
    void send(Player player, String rawText);

    /**
     * Send action bar text to a player.
     *
     * <p>Bypasses the handlers text conversion.</p>
     *
     * @param player    The player to send the text to.
     * @param jsonText  The Json text.
     */
    void sendJson(Player player, String jsonText);
}
