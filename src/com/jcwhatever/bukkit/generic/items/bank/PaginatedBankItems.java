/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.generic.items.bank;

import com.jcwhatever.bukkit.generic.items.bank.BankItem.BankItemStack;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility to paginate a players item bank account items for use in an inventory view.
 */
public class PaginatedBankItems  {

    public static final int MAX_PER_PAGE = 6 * 9;
    public static final int MAX_PAGES = 6 * 9;

    private UUID _playerId;

    /**
     * Constructor.
     *
     * @param playerId  The id of the player whose account items will be paginated.
     */
    public PaginatedBankItems(UUID playerId) {
        _playerId = playerId;        
    }

    /**
     * Constructor.
     *
     * @param p  The player whose account items will be paginated.
     */
    public PaginatedBankItems(Player p) {
        _playerId = p.getUniqueId();        
    }

    /**
     * The id of the player
     */
    public UUID getPlayerId() {
        return _playerId;
    }

    /**
     * Get total number of stacks (slots) required by the
     * account.
     */
    public int getTotalItems() {
        
        List<BankItem> bankItems = ItemBankManager.getBankItems(_playerId);
        if (bankItems == null)
            return 0;
        
        int size = 0;
        
        for (BankItem bankItem : bankItems) {
            size += (int)Math.ceil((double)bankItem.getQty() / (double)bankItem.getMaxStackSize());
        }
        
        return size;
    }

    /**
     * Get total number of pages required to display the account items
     * in a 6 x 9 inventory.
     */
    public int getTotalPages () {

        int size = getTotalItems();

        int totalPages = (int) Math.ceil((double) size / MAX_PER_PAGE);

        totalPages = Math.max(totalPages, 1);

        return Math.min(totalPages, MAX_PAGES);
    }

    /**
     * Get a specific page of items represented as {@code BankItemStack}'s.
     *
     * @param page  The page to get.
     */
    public List<BankItemStack> getPage (int page) {
        PreCon.greaterThanZero(page);
        PreCon.lessThanEqual(page, MAX_PAGES);
        
        List<BankItem> bankItems = ItemBankManager.getBankItems(_playerId);
        if (bankItems == null)
            return new ArrayList<>(0);

        List<BankItemStack> itemStacks = getItemStacks(bankItems);
        
        int total = itemStacks.size();
        int firstItem = page * MAX_PER_PAGE - MAX_PER_PAGE;
        int lastItem = Math.min(firstItem + MAX_PER_PAGE - 1, total - 1);

        List<BankItemStack> result = new ArrayList<BankItemStack>(MAX_PER_PAGE);

        if (firstItem < total) {

            for (int i = firstItem; i <= lastItem; i++) {

                BankItemStack item = itemStacks.get(i);

                result.add(item);
            }
        }

        return result;
    }


    private List<BankItemStack> getItemStacks (List<BankItem> bankItems) {

        List<BankItemStack> result = new ArrayList<BankItemStack>(bankItems.size());

        for (BankItem bankItem : bankItems) {
            if (bankItem.getQty() < 1)
                continue;
            
            List<BankItemStack> items = bankItem.getBankItems();
            
            result.addAll(items);
        }
        
        return result;
    }
    
}
