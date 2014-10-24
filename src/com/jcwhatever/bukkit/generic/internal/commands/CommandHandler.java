package com.jcwhatever.bukkit.generic.internal.commands;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.commands.AbstractCommandHandler;
import com.jcwhatever.bukkit.generic.internal.commands.jail.JailCommand;


public class CommandHandler extends AbstractCommandHandler {

    public CommandHandler() {
        super(GenericsLib.getInstance());
    }

    @Override
    protected void registerCommands () {
        this.registerCommand(JailCommand.class);
    }
}
