package com.jcwhatever.nucleus.utils.items;

import com.jcwhatever.dummy.DummyServer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ItemStackMatcherTest {

    @BeforeClass
    public static void testStartup() {
        try {
            Bukkit.setServer(new DummyServer());
        }
        catch (UnsupportedOperationException ignore) {}
    }

    @Test
    public void testGetCompareOperations() throws Exception {

        ItemStackMatcher comparer =
                new ItemStackMatcher(ItemStackMatcher.MATCH_TYPE);

        Assert.assertEquals(1, comparer.getMatcherOperations());

        comparer = new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_META | ItemStackMatcher.MATCH_AMOUNT));

        Assert.assertEquals(12, comparer.getMatcherOperations());
    }

    @Test
    public void testComparesType() throws Exception {

        ItemStackMatcher comparer =
                new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_TYPE | ItemStackMatcher.MATCH_AMOUNT));

        Assert.assertEquals(true, comparer.isTypeMatcher());

        comparer = new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_META | ItemStackMatcher.MATCH_AMOUNT));

        Assert.assertEquals(false, comparer.isTypeMatcher());
    }

    @Test
    public void testComparesMeta() throws Exception {
        ItemStackMatcher comparer =
                new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_TYPE | ItemStackMatcher.MATCH_META));

        Assert.assertEquals(true, comparer.isMetaMatcher());

        comparer = new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_TYPE | ItemStackMatcher.MATCH_AMOUNT));

        Assert.assertEquals(false, comparer.isMetaMatcher());
    }

    @Test
    public void testComparesDurability() throws Exception {
        ItemStackMatcher comparer =
                new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_DURABILITY | ItemStackMatcher.MATCH_META));

        Assert.assertEquals(true, comparer.isDurabilityMatcher());

        comparer = new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_TYPE | ItemStackMatcher.MATCH_AMOUNT));

        Assert.assertEquals(false, comparer.isDurabilityMatcher());
    }

    @Test
    public void testComparesAmount() throws Exception {
        ItemStackMatcher matcher =
                new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_AMOUNT | ItemStackMatcher.MATCH_META));

        Assert.assertEquals(true, matcher.isAmountMatcher());

        matcher = new ItemStackMatcher((byte)(ItemStackMatcher.MATCH_TYPE | ItemStackMatcher.MATCH_META));

        Assert.assertEquals(false, matcher.isAmountMatcher());
    }

    @Test
    public void testIsSame() throws Exception {

        ItemStack stone1 = new ItemStackBuilder(Material.STONE).amount(5).durability(50).build();
        ItemStack stone2 = new ItemStackBuilder(Material.STONE).amount(10).durability(25).build();

        ItemStack wood1 = new ItemStackBuilder(Material.WOOD).amount(5).durability(50).build();
        ItemStack wood2 = new ItemStackBuilder(Material.WOOD).amount(10).durability(25).build();

        setupMetaTest(stone1);
        setupMetaTest(stone2);

        ItemStackMatcher typeMatcher = new ItemStackMatcher(ItemStackMatcher.MATCH_TYPE);
        ItemStackMatcher durabilityMatcher = new ItemStackMatcher(ItemStackMatcher.MATCH_DURABILITY);
        ItemStackMatcher amountMatcher = new ItemStackMatcher(ItemStackMatcher.MATCH_AMOUNT);
        ItemStackMatcher metaMatcher = new ItemStackMatcher(ItemStackMatcher.MATCH_META);

        // Type comparer tests
        Assert.assertEquals(true, typeMatcher.isMatch(stone1, stone2));
        Assert.assertEquals(true, typeMatcher.isMatch(stone2, stone1));

        Assert.assertEquals(false, typeMatcher.isMatch(stone1, wood1));
        Assert.assertEquals(false, typeMatcher.isMatch(wood1, stone1));

        Assert.assertEquals(false, typeMatcher.isMatch(stone1, null));
        Assert.assertEquals(false, typeMatcher.isMatch(wood1, null));

        Assert.assertEquals(false, typeMatcher.isMatch(null, stone2));
        Assert.assertEquals(false, typeMatcher.isMatch(null, wood2));

        Assert.assertEquals(false, typeMatcher.isMatch(null, null));
        Assert.assertEquals(false, typeMatcher.isMatch(null, null));

        // Durability comparer tests
        Assert.assertEquals(false, durabilityMatcher.isMatch(stone1, stone2));
        Assert.assertEquals(false, durabilityMatcher.isMatch(stone2, stone1));

        Assert.assertEquals(true, durabilityMatcher.isMatch(stone1, wood1));
        Assert.assertEquals(true, durabilityMatcher.isMatch(wood1, stone1));

        Assert.assertEquals(false, durabilityMatcher.isMatch(stone1, null));
        Assert.assertEquals(false, durabilityMatcher.isMatch(wood1, null));

        Assert.assertEquals(false, durabilityMatcher.isMatch(null, stone2));
        Assert.assertEquals(false, durabilityMatcher.isMatch(null, wood2));

        Assert.assertEquals(false, durabilityMatcher.isMatch(null, null));
        Assert.assertEquals(false, durabilityMatcher.isMatch(null, null));

        // Amount comparer tests
        Assert.assertEquals(false, amountMatcher.isMatch(stone1, stone2));
        Assert.assertEquals(false, amountMatcher.isMatch(stone2, stone1));

        Assert.assertEquals(true, amountMatcher.isMatch(stone1, wood1));
        Assert.assertEquals(true, amountMatcher.isMatch(wood1, stone1));

        Assert.assertEquals(false, amountMatcher.isMatch(stone1, null));
        Assert.assertEquals(false, amountMatcher.isMatch(wood1, null));

        Assert.assertEquals(false, amountMatcher.isMatch(null, stone2));
        Assert.assertEquals(false, amountMatcher.isMatch(null, wood2));

        Assert.assertEquals(false, amountMatcher.isMatch(null, null));
        Assert.assertEquals(false, amountMatcher.isMatch(null, null));

        // Meta comparer tests
        Assert.assertEquals(true, metaMatcher.isMatch(stone1, stone2));
        Assert.assertEquals(true, metaMatcher.isMatch(stone2, stone1));

        Assert.assertEquals(false, metaMatcher.isMatch(stone1, wood1));
        Assert.assertEquals(false, metaMatcher.isMatch(wood1, stone1));

        Assert.assertEquals(false, metaMatcher.isMatch(stone1, null));
        Assert.assertEquals(false, metaMatcher.isMatch(wood1, null));

        Assert.assertEquals(false, metaMatcher.isMatch(null, stone2));
        Assert.assertEquals(false, metaMatcher.isMatch(null, wood2));

        Assert.assertEquals(false, metaMatcher.isMatch(null, null));
        Assert.assertEquals(false, metaMatcher.isMatch(null, null));
    }

    private void setupMetaTest(ItemStack itemStack) {

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("meta test");

        itemStack.setItemMeta(meta);
    }
}