package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.jail.JailManager;
import com.jcwhatever.bukkit.generic.jail.JailSession;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Provide scripts with API access to default jail.
 */
@IScriptApiInfo(
        variableName = "jail",
        description = "Provide scripts with API access to the default jail.")
public class ScriptApiJail extends GenericsScriptApi {

    private static ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiJail(Plugin plugin) {
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

        @Override
        public void reset() {
            // do nothing
        }

        /**
         * Send a player to prison for the specified amount of minutes.
         *
         * @param player   The player to send to jail
         * @param minutes  The time in minutes to spend in jail.
         */
        public boolean imprison(Object player, int minutes) {
            PreCon.notNull(player);
            PreCon.greaterThanZero(minutes);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            return JailManager.getDefault().imprison(p, minutes) != null;
        }

        /**
         * Release a prisoner from jail.
         *
         * @param player  The player to release.
         *
         * @return True if released, false if not in prison.
         */
        public boolean release(Object player) {
            PreCon.notNull(player);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            JailSession session = JailManager.getDefault().getJailSession(p.getUniqueId());
            if (session == null || session.isExpired() || session.isReleased())
                return false;

            session.release(true);

            return true;
        }

        /**
         * Determine if a player is in the default prison.
         * @param player  The player to check.
         */
        public boolean isImprisoned(Object player) {
            PreCon.notNull(player);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            JailSession session = JailManager.getDefault().getJailSession(p.getUniqueId());
            return  session != null && !session.isExpired() && !session.isReleased();
        }
    }

}
