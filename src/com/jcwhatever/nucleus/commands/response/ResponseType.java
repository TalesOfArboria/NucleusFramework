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


package com.jcwhatever.nucleus.commands.response;


import com.jcwhatever.nucleus.internal.Lang;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.nucleus.language.Localized;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class ResponseType {

    @Localizable
    static final String _YES = "yes";
    @Localizable static final String _NO = "no";
    @Localizable static final String _ACCEPT = "accept";
    @Localizable static final String _DECLINE = "decline";
    @Localizable static final String _OK = "ok";
    @Localizable static final String _CANCEL = "cancel";
    @Localizable static final String _ALLOW = "allow";
    @Localizable static final String _DENY = "deny";
    @Localizable static final String _CONFIRM = "confirm";

    private static Map<String, ResponseType> _typeMap = new HashMap<>(9);

    public static final ResponseType YES = new ResponseType(_YES);
    public static final ResponseType NO = new ResponseType(_NO);
    public static final ResponseType ACCEPT = new ResponseType(_ACCEPT);
    public static final ResponseType DECLINE = new ResponseType(_DECLINE);
    public static final ResponseType OK = new ResponseType(_OK);
    public static final ResponseType CANCEL = new ResponseType(_CANCEL);
    public static final ResponseType ALLOW = new ResponseType(_ALLOW);
    public static final ResponseType DENY = new ResponseType(_DENY);
    public static final ResponseType CONFIRM = new ResponseType(_CONFIRM);

    public static int totalTypes() {
        return 9;
    }

    private String _commandName;

    ResponseType (String commandName) {
        _commandName = commandName;
        _typeMap.put(commandName, this);
    }

    @Localized
    public String getCommandName() {
        return Lang.get(_commandName);
    }

    @Nullable
    public static ResponseType from(String commandName) {
        PreCon.notNullOrEmpty(commandName);

        ResponseType responseType = _typeMap.get(commandName.toLowerCase());

        if (responseType == null) {
            // make sure its not in another language
            for (ResponseType type : _typeMap.values()) {

                if (type.getCommandName().equalsIgnoreCase(commandName)) {
                    responseType = type;
                    break;
                }
            }
        }

        return responseType;
    }

}
