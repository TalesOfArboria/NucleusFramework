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

import com.jcwhatever.nucleus.utils.text.TextFormat;
import com.jcwhatever.nucleus.utils.text.components.IChatClickable;
import com.jcwhatever.nucleus.utils.text.components.IChatComponent;
import com.jcwhatever.nucleus.utils.text.components.IChatHoverable;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.components.IChatModifier;
import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.EnumChatFormat;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Chat handler for v1_8_R3
 */
class v1_8_R3_Chat {

    /**
     * Get NMS Chat component from {@link IChatMessage}.
     */
    ChatComponentText getComponent(IChatMessage message) {

        List<IChatComponent> components = message.getComponents();
        ChatComponentText text = getComponent(components.get(0));

        for (int i=1; i < components.size(); i++) {
            ChatComponentText sibling = getComponent(components.get(i));
            text.addSibling(sibling);
        }

        return text;
    }

    /**
     * Get NMS Chat component from {@link IChatComponent}.
     */
    ChatComponentText getComponent(IChatComponent component) {

        ChatComponentText text = new ChatComponentText(component.getText());

        ChatModifier modifier = getModifier(component.getModifier());
        if (modifier != null) {
            text.setChatModifier(modifier);
        }

        return text;
    }

    /**
     * Get NMS Chat modifier from {@link IChatModifier}.
     */
    @Nullable
    ChatModifier getModifier(@Nullable IChatModifier modifier) {
        if (modifier == null)
            return null;

        ChatModifier nmsModifier = new ChatModifier()
                .setBold(modifier.isBold())
                .setItalic(modifier.isItalic())
                .setRandom(modifier.isMagic())
                .setStrikethrough(modifier.isStrikeThrough())
                .setUnderline(modifier.isUnderlined());

        if (modifier.getColor() != null) {
            nmsModifier.setColor(getFormat(modifier.getColor()));
        }

        if (modifier.getClickable() != null) {

            ChatClickable nmsClickable = new ChatClickable(getClickAction(modifier.getClickable().getAction()),
                    modifier.getClickable().getArgument());
            nmsModifier.setChatClickable(nmsClickable);
        }

        IChatHoverable hoverable = modifier.getHoverable();
        if (hoverable != null) {
            ChatHoverable nmsHoverable = new ChatHoverable(getHoverAction(hoverable.getAction()),
                    getComponent(hoverable.getMessage()));
            nmsModifier.setChatHoverable(nmsHoverable);
        }

        return nmsModifier;
    }

    static EnumChatFormat getFormat(TextFormat format) {
        return EnumChatFormat.b(format.getMinecraftName());
    }

    static EnumClickAction getClickAction(IChatClickable.ClickAction action) {
        switch (action) {
            case OPEN_URL:
                return EnumClickAction.OPEN_URL;
            case RUN_COMMAND:
                return EnumClickAction.RUN_COMMAND;
            case SUGGEST_COMMAND:
                return EnumClickAction.SUGGEST_COMMAND;
            default:
                return null;
        }
    }

    static EnumHoverAction getHoverAction(IChatHoverable.HoverAction action) {
        switch (action) {
            case SHOW_TEXT:
                return EnumHoverAction.SHOW_TEXT;
            case SHOW_ENTITY:
                return EnumHoverAction.SHOW_ENTITY;
            case SHOW_ITEM:
                return EnumHoverAction.SHOW_ITEM;
            default:
                return null;
        }
    }
}
