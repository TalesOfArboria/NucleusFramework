package com.jcwhatever.bukkit.generic.commands.response;


import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.language.Localized;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ResponseType {

    @Localizable static final String _YES = "yes";
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
