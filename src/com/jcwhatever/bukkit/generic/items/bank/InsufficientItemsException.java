package com.jcwhatever.bukkit.generic.items.bank;


import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Exception to throw when a specific amount of items is accessed from a players item bank account
 * but there are not enough items to meet the request.
 */
public class InsufficientItemsException extends Exception {

    private UUID _playerId;
    private int _balance;
    private ItemStack _item;

    /**
     * Constructor.
     *
     * @param playerId  The id of the player with insufficient items.
     * @param item      The item stack that represents the item the player has an insufficient amount of.
     * @param balance   The number of items the player actually has.
     */
    public InsufficientItemsException(UUID playerId, ItemStack item, int balance) {
        PreCon.notNull(playerId);
        PreCon.notNull(item);
        PreCon.positiveNumber(balance);

        _playerId = playerId;
        _balance = balance;
        _item = item;
    }

    /**
     * Get the id of the player with insufficient items.
     */
    public UUID getPlayerId() {
        return _playerId;
    }

    /**
     * Get the number of items the player has.
     * @return
     */
    public int getBalance() {
        return _balance;
    }

    /**
     * An item stack that represents the item the player has an insufficient amount of.
     */
    public ItemStack getItem() {
        return _item;
    }
}
