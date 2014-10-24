package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.permissions.Permissions;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Provide scripts with api access to resource sounds.
 */
@IScriptApiInfo(
        variableName = "permissions",
        description = "Provide scripts with API access to player permissions.")
public class ScriptApiPermissions extends GenericsScriptApi {

    private static ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiPermissions(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject();

        return _api;
    }

    @Override
    public void reset() {
        if (_api != null)
            _api.reset();
    }

    public static class ApiObject implements IScriptApiObject {

        ApiObject() {}

        @Override
        public void reset() {
            // do nothing
        }

        /**
         * Determine if a player has the specified permission.
         *
         * @param player          The player.
         * @param permissionName  The name of the permission.
         *
         * @return  True if the player has the permission.
         */
        public boolean has(Object player, String permissionName) {
            PreCon.notNull(player);
            PreCon.notNullOrEmpty(permissionName);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            return Permissions.has(p, permissionName);
        }
    }
}
