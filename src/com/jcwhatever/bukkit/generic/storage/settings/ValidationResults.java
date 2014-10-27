/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.TextUtils;

public class ValidationResults {

    private boolean _isValid;
    private String _message;
    private Object[] _messageParams;
    private String _formattedMessage;

    public static final ValidationResults TRUE = new ValidationResults(true);
    public static final ValidationResults FALSE = new ValidationResults(false);

    public ValidationResults(boolean isValid) {
        _isValid = isValid;
    }

    public ValidationResults(boolean isValid, String message, Object...messageParams) {
        _isValid = isValid;
    }

    public boolean isValid() {
        return _isValid;
    }

    public boolean hasMessage() {
        return _message != null;
    }

    public String getMessage() {
        if (!hasMessage())
            return null;

        if (_formattedMessage == null) {
            _formattedMessage = TextUtils.format(_message, _messageParams);
        }
        return _formattedMessage;
    }

    public boolean tellMessage(Plugin plugin, CommandSender sender) {
        if (!hasMessage())
            return false;

        return Messenger.tell(plugin, sender, _message, _messageParams);
    }

}
