package com.jcwhatever.bukkit.generic.internal.commands.jail;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.regions.RegionSelection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@ICommandInfo(
        parent="jail",
        command="setregion", 
        usage="/jcg jail setregion",
        description="Set jail region using your current world edit selection.")

public class SetRegionSubCommand extends AbstractCommand {

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {
        
        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, "Console cannot select a region.");
        
        if (!isWorldEditInstalled(sender))
            return; // finish
        
        RegionSelection sel = getWorldEditSelection((Player)sender);
        if (sel == null)
            return; // finish
        
        JailManager jailManager = JailManager.getDefault();

        jailManager.getJailBounds().setCoords(sel.getP1(), sel.getP2());

        tellSuccess(sender, "Default Jail region set to your current world edit coords.");
    }

}