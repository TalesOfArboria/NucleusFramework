package com.jcwhatever.bukkit.generic.worlds.commands;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.worlds.WorldManager;

/**
 * 
 * @ICommandInfo(
		parameters={"worldName=$current"},
		usage="/... delWorld [worldName]",
		description="Remove the current world or specified world from the list of valid/invalid worlds.")

 * 
 * @author JC The Pants
 *
 */
public abstract class DelWorldCommand extends AbstractCommand {

	@Override
	public void execute(CommandSender sender, CommandArguments args)
	            throws InvalidValueException, InvalidCommandSenderException {
		
		String worldName = args.getString("worldName");
		if (worldName.equals("$current")) {
		    
		    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, "Console has no location.");
			
			Player p = (Player)sender;
			
			World world = p.getWorld();
			
			if (world == null)
				return; // finish
						
			worldName = world.getName();
		}
				
		if (!getWorldManager().removeWorld(worldName)) {
			tellError(sender, "Failed to remove world '{0}'.", worldName);
			return; // finish
		}

		tellSuccess(sender, "World '{0}' removed.", worldName);
	}
	
	
	protected abstract WorldManager getWorldManager();
	
}
