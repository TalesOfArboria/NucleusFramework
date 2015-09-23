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

package com.jcwhatever.nucleus.internal.managed.nms;

import com.jcwhatever.nucleus.utils.nms.INmsChatHandler;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Internal implementation of {@link INmsChatHandler}.
 */
public class NmsChatHandler extends AbstractNMSHandler implements INmsChatHandler {

    @Override
    public IChatMessage getMessage(String text) {
        return nms().getMessage(text);
    }

    @Override
    public void send(Player player, IChatMessage message) {
        nms().send(player, message);
    }

    @Override
    public void send(Collection<? extends Player> players, IChatMessage message) {
        nms().send(players, message);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
