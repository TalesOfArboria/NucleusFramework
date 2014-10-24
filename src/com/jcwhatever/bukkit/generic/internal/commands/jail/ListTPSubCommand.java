package com.jcwhatever.bukkit.generic.internal.commands.jail;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator.PaginatorTemplate;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="jail",
        command="listtp",
        staticParams = { "page=1" },
        usage="/jcg jail listtp [page]",
        description="List locations where players are teleported within the jail.")

public class ListTPSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR = "Jail Teleport Locations";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        int page = args.getInt("page");

        JailManager jailManager = JailManager.getDefault();

        List<INamedLocation> locations = jailManager.getTeleports();

        ChatPaginator pagin = new ChatPaginator(GenericsLib.getInstance(), 6,
                PaginatorTemplate.HEADER, PaginatorTemplate.FOOTER, _PAGINATOR);

        for (INamedLocation loc : locations) {
            pagin.add(loc.getName());
        }

        pagin.show(sender, page, FormatTemplate.ITEM);
    }

}

