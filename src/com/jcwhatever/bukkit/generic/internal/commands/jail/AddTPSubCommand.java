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
import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommandInfo(
        parent="jail",
        command="addtp",
        staticParams = { "name" },
        usage="/jcg jail addtp <name>",
        description="Add a location where players are teleported within the jail using your current position.")

public class AddTPSubCommand extends AbstractCommand {

    @Localizable static final String _DUPLICATE_NAME = "There is already a location named '{0}'.";
    @Localizable static final String _FAILED = "Failed to add location.";
    @Localizable static final String _SUCCESS = "Your current location has been added to the default jail and is named '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, "Console has no location");

        String name = args.getName("name");

        Player p = (Player)sender;

        Location loc = p.getLocation();

        JailManager jailManager = JailManager.getDefault();

        INamedLocation current = jailManager.getTeleport(name);
        if (current != null) {
            tellError(sender, Lang.get(_DUPLICATE_NAME, name));
            return; // finished
        }

        if (!jailManager.addTeleport(name, loc)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS, name));
    }

}
