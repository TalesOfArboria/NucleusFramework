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

package com.jcwhatever.nucleus.internal.managed.scripting.api;

import com.jcwhatever.nucleus.managed.resourcepacks.IPlayerResourcePacks;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePackStatus;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePacks;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.Result;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource Pack script API
 */
public class SAPI_ResourcePacks  implements IDisposable {

    private final Map<FutureResultSubscriber<IPlayerResourcePacks>, Void> _subcribers = new HashMap<>(25);
    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        List<FutureResultSubscriber<IPlayerResourcePacks>> subscribers =
                new ArrayList<>(_subcribers.keySet());

        for (FutureResultSubscriber<IPlayerResourcePacks> subscriber: subscribers) {
            subscriber.dispose();
        }

        _subcribers.clear();
        _isDisposed = true;
    }

    /**
     * Determine if a player has loaded the server resource pack.
     *
     * @param player    The player to check.
     * @param callback  The callback to invoke when the result is ready.
     */
    public void hasResourcePack(Object player, final IHasResourcePackCallback callback) {
        PreCon.notNull(player);
        PreCon.notNull(callback);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.isValid(p != null, "Invalid player object");

        FutureResultSubscriber<IPlayerResourcePacks> subscriber =
                new FutureResultSubscriber<IPlayerResourcePacks>() {
                    @Override
                    public void on(Result<IPlayerResourcePacks> result) {
                        assert result.getResult() != null;

                        callback.onResult(result.getResult().getStatus() == ResourcePackStatus.SUCCESS);
                    }
                };

        _subcribers.put(subscriber, null);

        ResourcePacks.get(p).getFinalStatus()
                .onSuccess(subscriber);
    }

    public interface IHasResourcePackCallback {
        void onResult(boolean hasResourcePack);
    }
}
