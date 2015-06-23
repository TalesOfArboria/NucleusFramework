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
import com.jcwhatever.nucleus.utils.nms.INmsListHeaderFooterHandler;
import com.jcwhatever.nucleus.utils.text.SimpleJSONBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Minecraft Tab List Header Footer Title handler.
 */
class NmsListHeaderFooterHandler extends AbstractNMSHandler
        implements INmsListHeaderFooterHandler {

    @Override
    public void send(Collection<? extends Player> players,
                     String rawHeaderText, String rawFooterText) {

        PreCon.notNull(players);

        if (rawHeaderText == null && rawFooterText == null)
            return;

        String jsonHeader = rawHeaderText != null
                ? SimpleJSONBuilder.text(rawHeaderText)
                : null;

        String jsonFooter = rawFooterText != null
                ? SimpleJSONBuilder.text(rawFooterText)
                : null;

        sendJson(players, jsonHeader, jsonFooter);
    }

    @Override
    public void sendJson(final Collection<? extends Player> players,
                         final @Nullable String jsonHeaderText, final @Nullable String jsonFooterText) {
        PreCon.notNull(players);

        if (Bukkit.isPrimaryThread()) {
            syncSend(players, jsonHeaderText, jsonFooterText);
        }
        else {
            Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    syncSend(players, jsonHeaderText, jsonFooterText);
                }
            });
        }
    }

    private void syncSend (Collection<? extends Player> players,
                           @Nullable String headerText, @Nullable String footerText) {

        try {

            // create packet instance based on the presence of a header
            Object packet = nms().getHeaderFooterPacket(headerText, footerText);
            for (Player player : players) {
                // send packet
                nms().sendPacket(player, packet);
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            setAvailable(false);
        }
    }
}
