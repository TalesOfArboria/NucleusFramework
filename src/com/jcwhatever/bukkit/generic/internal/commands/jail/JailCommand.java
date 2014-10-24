package com.jcwhatever.bukkit.generic.internal.commands.jail;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;


@ICommandInfo(
        command="jail", 
        description="Manage default jail.")

public class JailCommand extends AbstractCommand {
    
    public JailCommand() {
        super();

        registerSubCommand(AddTPSubCommand.class);
        registerSubCommand(ClearReleaseTPSubCommand.class);
        registerSubCommand(DelTPSubCommand.class);
        registerSubCommand(ListTPSubCommand.class);
        registerSubCommand(ReleaseSubCommand.class);
        registerSubCommand(SendSubCommand.class);
        registerSubCommand(SetRegionSubCommand.class);
        registerSubCommand(SetReleaseTPSubCommand.class);
        
    }

}