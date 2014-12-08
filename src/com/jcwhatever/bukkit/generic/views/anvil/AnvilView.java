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

package com.jcwhatever.bukkit.generic.views.anvil;

import com.jcwhatever.bukkit.generic.views.IViewFactory;
import com.jcwhatever.bukkit.generic.views.IViewSession;
import com.jcwhatever.bukkit.generic.views.View;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewCloseReason;
import com.jcwhatever.bukkit.generic.views.data.ViewResults;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

/*
 * 
 */
public class AnvilView extends View {

    protected AnvilView(@Nullable String title, IViewSession session,
                        IViewFactory factory, ViewArguments arguments) {
        super(title, session, factory, arguments);
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        // do nothing
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.ANVIL;
    }

    @Nullable
    @Override
    public InventoryView getInventoryView() {
        return null;
    }

    @Override
    public boolean isInventoryViewable() {
        return false;
    }

    @Nullable
    @Override
    public ViewResults getResults() {
        return null;
    }

    @Nullable
    public boolean show() {

        Block block = getViewSession().getSessionBlock();
        if (block == null || block.getType() != Material.ANVIL) {
            throw new RuntimeException("Anvil Views must have an anvil source block.");
        }

        Location loc = block.getLocation();

        try {

            Player p = getPlayer();

            Method getHandle = p.getClass().getDeclaredMethod("getHandle");

            Object entityHuman = getHandle.invoke(p);

            Method openAnvil = entityHuman.getClass().getDeclaredMethod("openAnvil", int.class, int.class, int.class);

            openAnvil.invoke(entityHuman, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
