package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.economy.EconomyHelper;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Provides script with Economy API
 */
@IScriptApiInfo(
        variableName = "economy",
        description = "Provide scripts with Economy API.")
public class ScriptApiEconomy extends GenericsScriptApi {

    private static ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiEconomy(Plugin plugin) {
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

        ApiObject(){}

        @Override
        public void reset() {
            // do nothing
        }

        /**
         * Get the balance of a player.
         *
         * @param player  The player.
         */
        public double getBalance(Object player) {
            PreCon.notNull(player);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            return EconomyHelper.getBalance(p);
        }

        /**
         * Give (or take) money from a player.
         * @param player  The player.
         * @param amount  The amount to give (or take if negative)
         *
         * @return  True if successful
         */
        public boolean give(Object player, double amount) {
            PreCon.notNull(player);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            return EconomyHelper.giveMoney(p, amount);
        }

        /**
         * Format an amount into a displayable String.
         *
         * @param amount  The amount to format.
         */
        public String formatAmount(double amount) {

            return EconomyHelper.formatAmount(amount);
        }
    }


}
