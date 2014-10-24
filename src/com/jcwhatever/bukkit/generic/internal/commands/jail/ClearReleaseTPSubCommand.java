package com.jcwhatever.bukkit.generic.internal.commands.jail;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import org.bukkit.command.CommandSender;


@ICommandInfo(
        parent="jail",
        command="clearreleasetp",
        usage="/jcg jail clearreleasetp",
        description="Clear location where players are teleported when they are released from the default jail.")

public class ClearReleaseTPSubCommand extends AbstractCommand {

    @Localizable static final String _SUCCESS = "Default Jail release location cleared.";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {
        
        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, "Console has no location.");
        
        JailManager jailManager = JailManager.getDefault();
        jailManager.setReleaseLocation(null);

        tellSuccess(sender, Lang.get(_SUCCESS));
    }

}

