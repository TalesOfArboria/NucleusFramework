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
import com.jcwhatever.nucleus.managed.reflection.IReflectedInstance;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.nms.INmsTitleHandler;
import com.jcwhatever.nucleus.utils.text.SimpleJSONBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Minecraft Title packet handler.
 */
class NmsTitleHandler extends AbstractNMSHandler implements INmsTitleHandler {

    /**
     * Send the packet to a player.
     *
     * @param player        The player to send the title to.
     * @param rawTitle     The Json title text.
     * @param rawSubtitle  Optional Json subtitle text.
     * @param fadeIn        The fade-in time.
     * @param stay          The stay time.
     * @param fadeOut       The fade-out time.
     */
    @Override
    public void send(Player player,
                     String rawTitle,
                     @Nullable String rawSubtitle,
                     int fadeIn, int stay, int fadeOut) {
        PreCon.notNull(player);

        send(ArrayUtils.asList(player), rawTitle, rawSubtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void send(Collection<? extends Player> players,
                     String rawTitle, @Nullable String rawSubtitle, int fadeIn, int stay, int fadeOut) {
        PreCon.notNull(players);
        PreCon.notNullOrEmpty(rawTitle);

        String jsonTitle = SimpleJSONBuilder.text(rawTitle);
        String jsonSubtitle = rawSubtitle != null
                ? SimpleJSONBuilder.text(rawSubtitle)
                : null;

        sendJson(players, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
    }

    /**
     * Send the packet to a player.
     *
     * @param player        The player to send the title to.
     * @param jsonTitle     The title text.
     * @param jsonSubtitle  Optional subtitle text.
     * @param fadeIn        The fade-in time.
     * @param stay          The stay time.
     * @param fadeOut       The fade-out time.
     */
    @Override
    public void sendJson(Player player,
                         String jsonTitle,
                         @Nullable String jsonSubtitle,
                         int fadeIn, int stay, int fadeOut) {
        sendJson(ArrayUtils.asList(player), jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void sendJson(final Collection<? extends Player> players,
                         final String jsonTitle,
                         final @Nullable String jsonSubtitle,
                         final int fadeIn, final int stay, final int fadeOut) {

        if (Bukkit.isPrimaryThread()) {
            syncSend(players, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
        }
        else {
            Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    syncSend(players, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
                }
            });
        }
    }

    private void syncSend(Collection<? extends Player> players, String title, @Nullable String subTitle,
                          int fadeIn, int stay, int fadeOut) {

        try {

            // times packet
            Object timesPacket = nms().getTitlePacketTimes(fadeIn, stay, fadeOut);
            Object subTitlePacket = null;

            // sub title packet
            if (subTitle != null) {
                subTitlePacket = nms().getTitlePacketSub(subTitle);
            }

            // title packet
            Object titlePacket = nms().getTitlePacket(title);

            for (Player player : players) {
                IReflectedInstance connection = nms().getConnection(player);
                nms().sendPacket(connection, timesPacket);

                if (subTitlePacket != null)
                    nms().sendPacket(connection, subTitlePacket);

                nms().sendPacket(connection, titlePacket);
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            setAvailable(false);
        }
    }
}
