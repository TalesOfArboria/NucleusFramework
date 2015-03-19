package com.jcwhatever.nucleus.utils.signs;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/*
 * 
 */
public class MockSignHandler extends SignHandler {

    public SignChangeResult signChangeResponse = SignChangeResult.VALID;
    public int signChangeCount = 0;
    public Player lastChangePlayer;
    public SignContainer lastChangeContainer;

    public SignClickResult signClickResponse = SignClickResult.HANDLED;
    public int signClickCount = 0;
    public Player lastClickPlayer;
    public SignContainer lastClickContainer;

    public SignBreakResult signBreakResponse = SignBreakResult.ALLOW;
    public int signBreakCount = 0;
    public Player lastBreakPlayer;
    public SignContainer lastBreakContainer;

    public int signLoadCount = 0;
    public SignContainer lastSignLoadContainer;

    public MockSignHandler(Plugin plugin) {
        super(plugin, "Sign_Name");
    }

    @Override
    public String getHeaderPrefix() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "",
                "",
                "",
                ""
        };
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        signLoadCount++;
        lastSignLoadContainer = sign;
    }

    @Override
    protected SignChangeResult onSignChange(Player p, SignContainer sign) {
        lastChangePlayer = p;
        lastChangeContainer = sign;
        signChangeCount++;
        return signChangeResponse;
    }

    @Override
    protected SignClickResult onSignClick(Player p, SignContainer sign) {
        lastClickPlayer = p;
        lastClickContainer = sign;
        signClickCount++;
        return signClickResponse;
    }

    @Override
    protected SignBreakResult onSignBreak(Player p, SignContainer sign) {
        lastBreakPlayer = p;
        lastBreakContainer = sign;
        signBreakCount++;
        return signBreakResponse;
    }

    public void reset() {
        signChangeResponse = SignChangeResult.VALID;
        signChangeCount = 0;
        lastChangePlayer = null;
        lastChangeContainer = null;

        signClickResponse = SignClickResult.HANDLED;
        signClickCount = 0;
        lastClickPlayer = null;
        lastClickContainer = null;

        signBreakResponse = SignBreakResult.ALLOW;
        signBreakCount = 0;
        lastBreakPlayer = null;
        lastBreakContainer = null;

        signLoadCount = 0;
        lastSignLoadContainer = null;
    }
}
