package com.jcwhatever.bukkit.generic.internal.commands.jail;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommandInfo(
        parent="jail",
        command="deltp",
        staticParams = { "name" },
        usage="/jcg jail deltp <name>",
        description="Remove a location where players are teleported within the jail.")

public class DelTPSubCommand extends AbstractCommand {

    @Localizable static final String _NOT_FOUND = "A location named '{0}' was not found.";
    @Localizable static final String _FAILED = "Failed to remove location.";
    @Localizable static final String _SUCCESS = "Location '{0}' removed.";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        String name = args.getName("name");

        Player p = (Player)sender;

        JailManager jailManager = JailManager.getDefault();

        INamedLocation current = jailManager.getTeleport(name);
        if (current == null) {
            tellError(sender, Lang.get(_NOT_FOUND, name));
            return; // finished
        }

        if (!jailManager.removeTeleport(name)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, Lang.get(_SUCCESS, name));
    }

}

