package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.items.bank.BankItem;
import com.jcwhatever.bukkit.generic.items.bank.InsufficientItemsException;
import com.jcwhatever.bukkit.generic.items.bank.ItemBankManager;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Provide scripts with API access to Item Bank.
 */
@IScriptApiInfo(
        variableName = "itemBank",
        description = "Provide scripts with API access to Item Bank.")
public class ScriptApiItemBank extends GenericsScriptApi {

    private static ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiItemBank(Plugin plugin) {
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
         * Deposit an {@code ItemStack} into the players account.
         *
         * @param player  The player.
         * @param item    The item to deposit.
         * @param qty     The quantity to deposit.
         *
         * @return  True if successful.
         */
        public boolean deposit(Object player, ItemStack item, int qty) {
            PreCon.notNull(player);
            PreCon.notNull(item);
            PreCon.greaterThanZero(qty);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            return ItemBankManager.deposit(p, item, qty) != null;
        }

        /**
         * Withdraw items from the players account.
         *
         * @param player  The player.
         * @param item    The item to withdraw.
         * @param qty     The quantity to withdraw.
         * @return
         */
        public boolean withdraw(Object player, ItemStack item, int qty) {
            PreCon.notNull(player);
            PreCon.notNull(item);
            PreCon.greaterThanZero(qty);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            try {
                ItemBankManager.withdraw(p, item, qty);
            } catch (InsufficientItemsException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        /**
         * Get all {@code ItemStack}'s in the players item bank account.
         *
         * @param player  The player.
         */
        public BankItem[] getBankItems(Object player) {
            PreCon.notNull(player);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);

            List<BankItem> items = ItemBankManager.getBankItems(p);
            if (items == null)
                return new BankItem[0];

            BankItem[] itemsArray = new BankItem[items.size()];

            for (int i=0; i < items.size(); i++) {
                itemsArray[i] = items.get(i);
            }

            return itemsArray;
        }

        /**
         * Determine if the player has the specified {@code ItemStack} in the
         * specified quantity available in account.
         *
         * @param player  The player.
         * @param item    The item to check.
         * @param qty     The quantity to check for.
         *
         * @return  True if available.
         */
        public boolean has(Object player, ItemStack item, int qty) {
            PreCon.notNull(player);
            PreCon.notNull(item);
            PreCon.greaterThanZero(qty);

            Player p = PlayerHelper.getPlayer(qty);
            PreCon.notNull(p);

            BankItem bankItem = ItemBankManager.getBankItem(p, item);
            return bankItem != null && bankItem.getQty() >= qty;
        }

        /**
         * Get the total quantity available of the specified item
         * from the players item bank account.
         *
         * @param player  The player.
         * @param item    The item to check.
         */
        public int getBalance(Object player, ItemStack item) {
            PreCon.notNull(player);
            PreCon.notNull(item);

            Player p = PlayerHelper.getPlayer(player);
            PreCon.notNull(p);
            
            BankItem bankItem = ItemBankManager.getBankItem(p, item);
            if (bankItem == null)
                return 0;

            return bankItem.getQty();
        }
    }

}
