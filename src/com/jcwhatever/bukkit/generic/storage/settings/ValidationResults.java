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


package com.jcwhatever.bukkit.generic.storage.settings;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Object that defines the results of setting a value in a
 * {@code SettingManager}.
 */
public class ValidationResults {

    private boolean _isValid;
    private String _message;
    private Object[] _messageParams;
    private String _formattedMessage;

    public static final ValidationResults TRUE = new ValidationResults(true);
    public static final ValidationResults FALSE = new ValidationResults(false);

    /**
     * Constructor.
     *
     * @param isValid  True to create a valid result.
     */
    public ValidationResults(boolean isValid) {
        _isValid = isValid;
    }

    /**
     * Constructor.
     *
     * @param isValid        True to create a valid result.
     * @param message        A message to return with the result which might be displayed to a user.
     * @param messageParams  Optional message format parameters.
     */
    public ValidationResults(boolean isValid, String message, Object...messageParams) {
        PreCon.notNull(message);
        PreCon.notNull(messageParams);

        _isValid = isValid;
        _message = message;
        _messageParams = messageParams;
    }

    /**
     * Determine if the result is valid.
     */
    public boolean isValid() {
        return _isValid;
    }

    /**
     * Determine if the result contains a message.
     */
    public boolean hasMessage() {
        return _message != null;
    }

    /**
     * Get the result message.
     */
    @Nullable
    public String getMessage() {
        if (!hasMessage())
            return null;

        if (_formattedMessage == null) {
            _formattedMessage = TextUtils.format(_message, _messageParams);
        }
        return _formattedMessage;
    }

    /**
     * Display the result message to a {@code CommandSender}.
     *
     * @param plugin  The owning plugin.
     * @param sender  The command sender.
     *
     * @return  True if the message was displayed.
     */
    public boolean tellMessage(Plugin plugin, CommandSender sender) {
        return hasMessage() && Messenger.tell(plugin, sender, _message, _messageParams);
    }

}
