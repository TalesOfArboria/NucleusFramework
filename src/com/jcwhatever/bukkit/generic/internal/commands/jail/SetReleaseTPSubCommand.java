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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@ICommandInfo(
        parent="jail",
        command="setreleasetp",
        usage="/{plugin-command} jail setreleasetp",
        description="Set location where players are teleported when they are released from the default jail.")

public class SetReleaseTPSubCommand extends AbstractCommand {

    @Localizable static final String _SUCCESS = "Default Jail release location set to your current location.";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {
        
        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, "Console does not have a locaation.");
        
        Player p = (Player)sender;

        Location loc = p.getLocation();

        JailManager jailManager = JailManager.getDefault();
        jailManager.setReleaseLocation(loc);

        tellSuccess(sender, Lang.get(_SUCCESS));
    }

}
