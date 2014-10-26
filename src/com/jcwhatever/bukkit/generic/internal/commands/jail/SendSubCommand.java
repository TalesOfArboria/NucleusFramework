package com.jcwhatever.bukkit.generic.internal.commands.jail;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.jail.JailSession;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@ICommandInfo(
        parent="jail",
        command="send",
        staticParams={"playerName", "minutes"},
        usage="/{plugin-command} jail send <playerName> <minutes>",
        description="Imprison player at the default jail.")

public class SendSubCommand extends AbstractCommand {

    @Localizable static final String _PLAYER_NOT_FOUND = "Could not find player '{0}'.";
    @Localizable static final String _FAILED = "Failed to send player to Default Jail. Make sure it is setup.";
    @Localizable static final String _SUCCESS = "Player '{0}' sent to Default Jail for {0} minutes.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {
        
        String playerName = args.getName("playerName");
        int minutes = args.getInt("minutes");
        
        Player player = PlayerHelper.getPlayer(playerName);
        if (player == null) {
            tellError(sender, Lang.get(_PLAYER_NOT_FOUND, playerName));
            return; // finish
        }
        
        JailManager jailManager = JailManager.getDefault();
        JailSession jailSession = jailManager.imprison(player, minutes);
        
        if (jailSession == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }
        
        tellSuccess(sender, Lang.get(_SUCCESS, playerName, minutes));
    }

}