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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.nms.INmsActionBarHandler;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Minecraft Action Bar handler.
 */
class NmsActionBarHandler extends AbstractNMSHandler implements INmsActionBarHandler {

    @Override
    public void send(Collection<? extends Player> players, String rawText) {
        PreCon.notNull(players);
        PreCon.notNull(rawText);

        String jsonText = "{text:\"" + TextUtils.format(rawText).replace("\"", "\\\"") + "\"}";

        sendJson(players, jsonText);
    }

    @Override
    public void sendJson(final Collection<? extends Player> players, final String jsonText) {
        PreCon.notNull(players);
        PreCon.notNull(jsonText);

        if (Bukkit.isPrimaryThread()) {
            syncSend(players, jsonText);
        }
        else {
            Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    syncSend(players, jsonText);
                }
            });
        }
    }

    private void syncSend(Collection<? extends Player> players, String text) {

        try {

            Object packet = nms().getActionBarPacket(text);

            for (Player player : players) {
                nms().sendPacket(player, packet);
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            setAvailable(false);
        }
    }
}
