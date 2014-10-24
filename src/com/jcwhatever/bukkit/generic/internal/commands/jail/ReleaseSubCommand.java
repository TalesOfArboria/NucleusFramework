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

import java.util.UUID;


@ICommandInfo(
        parent="jail",
        command="release",
        staticParams={"playerName"},
        usage="/jcg jail release <playerName>",
        description="Release prisoner from default jail.")

public class ReleaseSubCommand extends AbstractCommand {

    @Localizable static final String _PLAYER_NOT_FOUND = "Could not find a player with the name '{0}'.";
    @Localizable static final String _PLAYER_NOT_IMPRISONED = "Player '{0}' is not imprisoned in the Default Jail.";
    @Localizable static final String _SUCCESS = "Player '{0}' released from the Default Jail.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String playerName = args.getName("playerName");
        
        UUID playerId = PlayerHelper.getPlayerId(playerName);
        if (playerId == null) {
            tellError(sender, Lang.get(_PLAYER_NOT_FOUND, playerName));
            return; // finish
        }
        
        JailManager jailManager = JailManager.getDefault();
        JailSession jailSession = jailManager.getJailSession(playerId);
        
        if (jailSession == null) {
            tellError(sender, Lang.get(_PLAYER_NOT_IMPRISONED, playerName));
            return; // finish
        }
        
        jailSession.release(true);
        
        tellSuccess(sender, Lang.get(_SUCCESS, playerName));
    }

}

