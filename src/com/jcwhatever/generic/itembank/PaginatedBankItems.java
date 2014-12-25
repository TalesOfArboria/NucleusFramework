/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.generic.itembank;

import com.jcwhatever.generic.itembank.BankItem.BankItemStack;
import com.jcwhatever.generic.mixins.IPaginator;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

/**
 * Utility to paginate a players item bank account items for use in a chest view.
 */
public class PaginatedBankItems implements IPaginator<BankItemStack> {

    public static final int MAX_PER_PAGE = 6 * 9;
    public static final int MAX_PAGES = 6 * 9;

    private final UUID _playerId;
    private final PageStartIndex _start;
    private int _itemsPerPage = MAX_PER_PAGE;

    /**
     * Constructor.
     *
     * @param p  The player whose account items will be paginated.
     */
    public PaginatedBankItems(Player p) {
        this(p.getUniqueId());
    }

    /**
     * Constructor.
     *
     * @param playerId  The id of the player whose account items will be paginated.
     */
    public PaginatedBankItems(UUID playerId) {
        _playerId = playerId;
        _start = PageStartIndex.ONE;
    }

    /**
     * The id of the player
     */
    public UUID getPlayerId() {
        return _playerId;
    }

    /**
     * Get the {@code PageStartIndex} constant that defines
     * the index of page 1.
     */
    @Override
    public PageStartIndex getPageStartIndex() {
        return _start;
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
     * in a 6 x 9 chest.
     */
    @Override
    public int getTotalPages() {
        int size = getTotalItems();

        int totalPages = (int) Math.ceil((double) size / MAX_PER_PAGE);

        totalPages = Math.max(totalPages, 1);

        return Math.min(totalPages, MAX_PAGES);
    }

    /**
     * Get the max number of items per page.
     */
    @Override
    public int getItemsPerPage() {
        return _itemsPerPage;
    }

    /**
     * Set the max number of items per page.
     *
     * @param itemsPerPage  The number of items per page.
     */
    @Override
    public void setItemsPerPage(int itemsPerPage) {
        _itemsPerPage = itemsPerPage;
    }

    /**
     * Get a sub list of items from the specified page.
     *
     * @param page  The page index.
     */
    @Override
    public List<BankItemStack> getPage(int page) {
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

    /**
     * Get an iterator to iterate over the items of
     * the specified page.
     *
     * @param page  The page index.
     */
    @Override
    public ListIterator<BankItemStack> iterator(int page) {
        checkPage(page);

        return getPage(page).listIterator();
    }

    private void checkPage(int page) {
        switch (_start) {
            case ZERO:
                PreCon.positiveNumber(page);
                break;
            case ONE:
                PreCon.greaterThanZero(page);
                break;
            default:
                throw new AssertionError();
        }
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
