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


package com.jcwhatever.nucleus.messaging;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Chat list paginator.
 */
public class ChatPaginator implements IPluginOwned {

    @Localizable
    static final String _HEADER =
            "----------------------------------------\n" +
            "{AQUA}{0: title} {GRAY}(Page {1: current page} of {2: total pages})";

    @Localizable
    static final String _FOOTER =
            "----------------------------------------";

    private final Plugin _plugin;
    private final IMessenger _msg;
    private final List<Object[]> _printList = new ArrayList<>(50);

    protected int _itemsPerPage = 6;
    protected String _headerFormat;
    protected String _footerFormat;
    protected String _title;

    /**
     * Constructor.
     *
     * <p>6 items per page, no title</p>
     *
     * @param plugin  The owning plugin.
     */
    public ChatPaginator(Plugin plugin) {
        this(plugin, 6, "");
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
        this(plugin, itemsPerPage, "");
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
    public ChatPaginator(Plugin plugin, String title, Object... args) {
        this(plugin, 6, title, args);
    }

    /**
     * Constructor.
     *
     * @param plugin        The owning plugin.
     * @param itemsPerPage  Number of items to show per page.
     * @param title         The title to insert into the header.
     * @param args          Optional title format args.
     */
    public ChatPaginator(Plugin plugin, int itemsPerPage, String title, Object... args) {
        PreCon.notNull(title);
        PreCon.notNull(args);

        _plugin = plugin;
        _msg = MessengerFactory.get(plugin);
        _itemsPerPage = itemsPerPage;
        _headerFormat = _HEADER;
        _footerFormat = _FOOTER;
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
        return _title;
    }

    /**
     * Set the header text/format.
     * <p/>
     * <p>Header format uses numbers in braces to insert title, current page and total pages:</p>
     * <ul>
     * <li>{0} = title</li>
     * <li>{1} = current page</li>
     * <li>{2} = total pages</li>
     * </ul>
     *
     * @param header The header text.
     */
    public void setHeader(@Nullable String header) {
        _headerFormat = header;
    }

    /**
     * Set the footer text/format.
     * <p/>
     * <p>Footer format uses numbers in braces to insert title, current page and total pages:</p>
     * <ul>
     * <li>{0} = title</li>
     * <li>{1} = current page</li>
     * <li>{2} = total pages</li>
     * </ul>
     *
     * @param footer The footer text.
     */
    public void setFooter(@Nullable String footer) {
        _footerFormat = footer;
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

        _printList.add(new Object[]{new PreFormattedLine(format.toString(), args)});
    }

    /**
     * Add all objects from a collection.
     *
     * <p>
     *     Each item in the collection is 1 item in the paginator.
     *     Multiple objects can be added per item by placing them in
     *     an {@link java.lang.Object[]}
     * </p>
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

        int totalPages = (int)Math.ceil((double)_printList.size() / _itemsPerPage);

        String header = _headerFormat != null
                ? NucLang.get(_plugin, _headerFormat, _title, Math.max(1, page), Math.max(1, totalPages))
                : "";

        if (!header.isEmpty())
            _msg.tell(sender, header);

        if (page < 1 || page > totalPages) {
            if (page == 1) {
                _msg.tell(sender, "No items to display.");
            }
            else {
                _msg.tell(sender, "Page " + page + " was not found.");
            }
        }
        else {

            int start = page * _itemsPerPage - _itemsPerPage;
            int end = Math.min(start + _itemsPerPage - 1, _printList.size() - 1);

            for (int i = start; i <= end; ++i) {
                Object[] line = _printList.get(i);
                if (line.length == 1 && line[0] instanceof PreFormattedLine) {
                    PreFormattedLine preformatted = (PreFormattedLine) line[0];
                    _msg.tell(sender, preformatted.format, preformatted.arguments);
                } else {
                    _msg.tell(sender, format, line);
                }
            }
        }

        String footer = _footerFormat != null
                ? NucLang.get(_plugin, _footerFormat, _title, Math.max(1, page), Math.max(1, totalPages))
                : "";

        if (!footer.isEmpty())
            _msg.tell(sender, footer);
    }

    /*
     * Used to store formatting information for a single item.
     */
    protected static class PreFormattedLine {
        public final String format;
        public final Object[] arguments;

        public PreFormattedLine(String format, Object[] arguments) {
            this.format = format;
            this.arguments = arguments;
        }
    }

}
