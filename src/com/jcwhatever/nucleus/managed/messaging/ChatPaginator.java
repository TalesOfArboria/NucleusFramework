/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.nucleus.managed.messaging;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.components.IChatClickable.ClickAction;
import com.jcwhatever.nucleus.utils.text.components.IChatHoverable.HoverAction;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatMessage;
import com.jcwhatever.nucleus.utils.text.format.args.ClickableArgModifier;
import com.jcwhatever.nucleus.utils.text.format.args.HoverableArgModifier;
import com.jcwhatever.nucleus.utils.text.format.args.TextArg;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Chat list paginator.
 */
public class ChatPaginator implements IPluginOwned {

    @Localizable static final String _HEADER =
            "{GRAY}------------------------------------\n" +
            "{AQUA}{5: title}\n";

    @Localizable static final String _FOOTER =
            "\n{0: prev} {GRAY}Page {1: current page} of {2: total pages} {3: next}\n" +
            "{GRAY}------------------------------------";

    @Localizable static final String _SEARCH_HEADER = _HEADER +
            "{ITALIC}{LIGHT_PURPLE}Search: '{4: search term}'\n";

    @Localizable static final String _NO_ITEMS = "No items to display.";
    @Localizable static final String _PAGE_NOT_FOUND = "Page {0: page} was not found.";
    @Localizable static final String _PREV = "{AQUA}Prev";
    @Localizable static final String _PREV_DISABLED = "{DARK_GRAY}Prev";
    @Localizable static final String _PREV_HOVER = "{YELLOW}Click to go to the previous page.";
    @Localizable static final String _NEXT = "{AQUA}Next";
    @Localizable static final String _NEXT_DISABLED = "{DARK_GRAY}Next";
    @Localizable static final String _NEXT_HOVER = "{YELLOW}Click to go to the next page.";

    private final Plugin _plugin;
    private final IMessenger _msg;
    private final List<Object[]> _printList = new ArrayList<>(50);

    protected int _itemsPerPage = 6;
    protected String _headerFormat;
    protected String _footerFormat;
    protected String _searchHeaderFormat;
    protected IChatMessage _title;
    protected String _searchTerm;
    protected IChatPaginatorCommands _commands;
    protected TextArg _prev;
    protected TextArg _next;

    /**
     * Constructor.
     *
     * <p>6 items per page, no title</p>
     *
     * @param plugin  The owning plugin.
     */
    public ChatPaginator(Plugin plugin) {
        this(plugin, 6, null, null, "");
    }

    /**
     * Constructor.
     *
     * <p>No title.</p>
     *
     * @param plugin        The owning plugin.
     * @param itemsPerPage  Number of items to show per page.
     */
    public ChatPaginator(Plugin plugin, int itemsPerPage) {
        this(plugin, itemsPerPage, null, null, "");
    }

    /**
     * Constructor.
     *
     * <p>6 items per page.</p>
     *
     * @param plugin  The owning plugin.
     * @param title   The title to insert into the header.
     * @param args    Optional title format args.
     */
    public ChatPaginator(Plugin plugin, CharSequence title, Object... args) {
        this(plugin, 6, null, null, title, args);
    }

    /**
     * Constructor.
     *
     * @param plugin        The owning plugin.
     * @param itemsPerPage  Number of items to show per page.
     * @param commands      Command generator for clickable links.
     * @param title         The title to insert into the header.
     * @param args          Optional title format args.
     */
    public ChatPaginator(Plugin plugin, int itemsPerPage,
                         @Nullable IChatPaginatorCommands commands,
                         CharSequence title, Object... args) {
        PreCon.notNull(title);
        PreCon.notNull(args);

        _plugin = plugin;
        _msg = Nucleus.getMessengerFactory().get(plugin);
        _commands = commands;
        _itemsPerPage = itemsPerPage;
        _headerFormat = _HEADER;
        _footerFormat = _FOOTER;
        _searchHeaderFormat = _SEARCH_HEADER;
        _title = TextUtils.format(title, args);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the paginator title.
     */
    @Nullable
    public String getTitle() {
        return _title.toString();
    }

    /**
     * Get the header text/format.
     *
     * @return  The header or null if none.
     */
    @Nullable
    public String getHeader() {
        return _headerFormat;
    }

    /**
     * Set the header text/format.
     *
     * <p>Header format uses numbers in braces to insert title, current page and total pages:</p>
     * <ul>
     *      <li>{0} = title</li>
     *      <li>{1} = current page</li>
     *      <li>{2} = total pages</li>
     * </ul>
     *
     * @param header The header text.
     */
    public void setHeader(@Nullable String header) {
        _headerFormat = header;
    }

    /**
     * Get the footer text/format.
     *
     * @return  The footer or null if none.
     */
    @Nullable
    public String getFooter() {
        return _footerFormat;
    }

    /**
     * Set the footer text/format.
     *
     * <p>Footer format uses numbers in braces to insert title, current page and total pages:</p>
     * <ul>
     *      <li>{0} = title</li>
     *      <li>{1} = current page</li>
     *      <li>{2} = total pages</li>
     * </ul>
     *
     * @param footer The footer text.
     */
    public void setFooter(@Nullable String footer) {
        _footerFormat = footer;
    }


    /**
     * Get the search header text/format.
     *
     * @return  The search header or null if none.
     */
    @Nullable
    public String getSearchHeader() {
        return _searchHeaderFormat;
    }

    /**
     * Set the search header text/format.
     *
     * <p>Footer format uses numbers in braces to insert title, current page and total pages:</p>
     * <ul>
     *      <li>{0} = title</li>
     *      <li>{1} = current page</li>
     *      <li>{2} = total pages</li>
     *      <li>{3} = search term</li>
     * </ul>
     *
     * @param searchHeader  The search header text.
     */
    public void setSearchHeader(@Nullable String searchHeader) {
        _searchHeaderFormat = searchHeader;
    }

    /**
     * Get the search filter term.
     *
     * @return  The search filter term or null if not set.
     */
    @Nullable
    public String getSearchTerm() {
        return _searchTerm;
    }

    /**
     * Set the search filter term.
     *
     * @param searchTerm  The search text or null to remove search term.
     */
    public void setSearchTerm(@Nullable String searchTerm) {
        _searchTerm = searchTerm;
    }

    /**
     * Add an item.
     *
     * <p>The format of the item is specified when the paginator is shown to a
     * {@link org.bukkit.command.CommandSender}. The objects provided as
     * parameters are inserted into the format.</p>
     *
     * @param args  The object arguments inserted into the item format.
     */
    public void add(Object...args) {
        PreCon.notNull(args);
        PreCon.greaterThanZero(args.length);

        _printList.add(args);
    }

    /**
     * Add an item with a format that overrides the item format used when
     * displaying the paginator.
     *
     * @param format  The format that applies to the item. See
     *                {@link com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate}
     *                for ready-made formats.
     * @param args    The object arguments inserted into the item format.
     */
    public void addFormatted(Object format, Object...args) {
        PreCon.notNull(format);
        PreCon.notNull(args);

        _printList.add(new Object[]{new PreFormattedLine(format, args)});
    }

    /**
     * Add all objects from a collection.
     *
     * <p>Each item in the collection is 1 item in the paginator. Multiple objects can
     * be added per item by placing them in an {@link java.lang.Object[]}</p>
     *
     * @param collection  The collection to add.
     */
    public void addAll(Collection<?> collection) {
        PreCon.notNull(collection);

        for (Object object : collection) {
            if (object instanceof Object[]) {
                _printList.add((Object[]) object );
            }
            else {
                _printList.add(new Object[] { object });
            }
        }
    }

    /**
     * Get the total number of pages given the specified format.
     *
     * @param format  The format.
     */
    public int getTotalPages(Object format) {

        int totalItems;

        totalItems = _searchTerm == null ? _printList.size() : getSearchLines(format).size();

        return (int) Math.ceil((double) totalItems / _itemsPerPage);
    }

    /**
     * Show a page of the paginator to a {@link org.bukkit.command.CommandSender}.
     *
     * <p>The format argument only applies to items that are not added with a
     * specific format.</p>
     *
     * @param sender  The command sender to display the page to.
     * @param page    The page to display.
     * @param format  The format that applies to the items on the page. See
     *                {@link TextUtils.FormatTemplate}
     *                for ready-made formats.
     *
     */
    public void show(CommandSender sender, int page, Object format) {
        PreCon.notNull(sender);
        PreCon.notNull(format);

        page = page > 0 ? page : 1;

        int totalPages = getTotalPages(format);

        loadCommands(page, totalPages);

        IChatMessage header = getFormattedHeader(page, totalPages);
        if (header.length() != 0)
            _msg.tell(sender, header);

        if (page < 1 || page > totalPages) {
            if (page == 1) {
                _msg.tell(sender, NucLang.get(getPlugin(), _NO_ITEMS));
            }
            else {
                _msg.tell(sender, NucLang.get(getPlugin(), _PAGE_NOT_FOUND, page));
            }
        }
        else if (_searchTerm == null) {
            showAll(sender, page, format);
        }
        else {
            showSearch(sender, page, format);
        }

        IChatMessage footer = getFormattedFooter(page, totalPages);
        if (footer.length() != 0)
            _msg.tell(sender, footer);
    }

    /**
     * Show all lines from a page.
     */
    protected void showAll(CommandSender sender, int page, Object format) {

        int start = page * _itemsPerPage - _itemsPerPage;
        int end = Math.min(start + _itemsPerPage - 1, _printList.size() - 1);

        for (int i = start; i <= end; ++i) {
            Object[] arguments = _printList.get(i);

            Object localFormat = format;

            if (arguments.length == 1 && arguments[0] instanceof PreFormattedLine) {
                PreFormattedLine preFormatted = (PreFormattedLine) arguments[0];
                localFormat = preFormatted.format;
                arguments = preFormatted.arguments;
            }

            _msg.tell(sender, localFormat, arguments);
        }
    }

    /**
     * Show lines from search filtered results.
     */
    protected void showSearch(CommandSender sender, int page, Object format) {

        List<IChatMessage> lines = getSearchLines(format);

        int start = page * _itemsPerPage - _itemsPerPage;
        int end = Math.min(start + _itemsPerPage - 1, lines.size() - 1);

        for (int i = start; i <= end; ++i) {
            _msg.tell(sender, lines.get(i));
        }
    }

    /*
     * Get all formatted pagin lines filtered by the current search term.
     */
    protected List<IChatMessage> getSearchLines(Object format) {

        List<IChatMessage> lines = new ArrayList<>(_printList.size());

        for (Object[] arguments : _printList) {

            Object localFormat = format;
            Object[] localArgs = arguments;

            if (arguments.length == 1 && arguments[0] instanceof PreFormattedLine) {
                PreFormattedLine preformatted = (PreFormattedLine) arguments[0];
                localFormat = preformatted.format;
                localArgs = preformatted.arguments;
            }

            lines.add(TextUtils.format(localFormat, localArgs));
        }

        return CollectionUtils.textSearch(lines, _searchTerm);
    }

    /**
     * Get the header to use and format it.
     */
    protected IChatMessage getFormattedHeader(int page, int totalPages) {

        String format = _searchTerm != null ? _searchHeaderFormat : _headerFormat;

        if (format == null || format.isEmpty())
            return new SimpleChatMessage();

        return NucLang.get(_plugin, format,
                _prev, Math.max(1, page), Math.max(1, totalPages), _next, _searchTerm, _title);
    }

    /**
     * Get the footer to use and format it.
     */
    protected IChatMessage getFormattedFooter(int page, int totalPages) {

        if (_footerFormat == null || _footerFormat.isEmpty())
            return new SimpleChatMessage();

        return NucLang.get(_plugin, _footerFormat,
                _prev, Math.max(1, page), Math.max(1, totalPages), _next, _searchTerm, _title);
    }

    protected void loadCommands(int page, int totalPages) {
        if (_commands == null) {
            _prev = new TextArg("");
            _next = _prev;
            return;
        }

        String prevCommand = _commands.getPrevCommand(this, page, totalPages);
        String nextCommand = _commands.getNextCommand(this, page, totalPages);

        _prev = prevCommand == null
                ? new TextArg(NucLang.get(_plugin, _PREV_DISABLED))
                : new TextArg(NucLang.get(_plugin, _PREV),
                new ClickableArgModifier(ClickAction.RUN_COMMAND, prevCommand),
                new HoverableArgModifier(HoverAction.SHOW_TEXT, NucLang.get(_plugin, _PREV_HOVER)));

        _next = nextCommand == null
                ? new TextArg(NucLang.get(_plugin, _NEXT_DISABLED))
                : new TextArg(NucLang.get(_plugin, _NEXT),
                new ClickableArgModifier(ClickAction.RUN_COMMAND, nextCommand),
                new HoverableArgModifier(HoverAction.SHOW_TEXT, NucLang.get(_plugin, _NEXT_HOVER)));
    }

    /**
     * Used to store formatting information for a single item.
     */
    protected static class PreFormattedLine {
        public final Object format;
        public final Object[] arguments;

        public PreFormattedLine(Object format, Object[] arguments) {
            this.format = format;
            this.arguments = arguments;
        }
    }
}
