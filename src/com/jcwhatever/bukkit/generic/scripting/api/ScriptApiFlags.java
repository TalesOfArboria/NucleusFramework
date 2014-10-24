package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Provide scripts with API for setting flags on players.
 */
@IScriptApiInfo(
        variableName = "flags",
        description = "Provide scripts with API for setting data node stored flags on players.")
public class ScriptApiFlags extends GenericsScriptApi {

    private final IDataNode _dataNode;
    private ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiFlags(Plugin plugin, IDataNode dataNode) {
        super(plugin);

        _dataNode = dataNode;
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject(_dataNode);

        return _api;
    }

    @Override
    public void reset() {
        if (_api != null)
            _api.reset();
    }

    public static class ApiObject implements IScriptApiObject {

        private final IDataNode _dataNode;

        ApiObject(IDataNode dataNode) {
            _dataNode = dataNode;
        }

        @Override
        public void reset() {
            // do nothing
        }

        /**
         * Determine if a player has a flag set.
         *
         * @param player    The player to check
         * @param flagName  The name of the flag
         *
         * @return  True if the flag is set.
         */
        public boolean has(Object player, String flagName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(flagName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            return _dataNode.getBoolean(p.getUniqueId().toString() + '.' + flagName, false);
        }

        /**
         * Set a flag on a player.
         *
         * @param player    The player.
         * @param flagName  The name of the flag.
         */
        public void set(Object player, String flagName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(flagName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            _dataNode.set(p.getUniqueId().toString() + '.' + flagName, true);
            _dataNode.saveAsync(null);
        }

        /**
         * Clear a flag on a player.
         *
         * @param player    The player.
         * @param flagName  The name of the flag.
         */
        public void clear(Object player, String flagName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(flagName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            _dataNode.remove(p.getUniqueId().toString() + '.' + flagName);
            _dataNode.saveAsync(null);
        }

    }
}
