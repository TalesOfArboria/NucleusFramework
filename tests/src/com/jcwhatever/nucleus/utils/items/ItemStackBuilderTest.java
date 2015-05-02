package com.jcwhatever.nucleus.utils.items;

import com.jcwhatever.v1_8_R2.MockServer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class ItemStackBuilderTest {

    @BeforeClass
    public static void testStartup() {
        try {
            Bukkit.setServer(new MockServer());
        }
        catch (UnsupportedOperationException ignore) {}
    }

    @Test
    public void testBuild() throws Exception {

        ItemStack materialTest = new ItemStackBuilder(Material.WOOD).build();
        ItemStack amountTest = new ItemStackBuilder(Material.WOOD).amount(5).build();
        ItemStack durabilityTest = new ItemStackBuilder(Material.WOOD).durability(15).build();
        ItemStack displayNameTest = new ItemStackBuilder(Material.WOOD).display("test").build();
        ItemStack enchantmentTest = new ItemStackBuilder(Material.WOOD).enchant(1, Enchantment.ARROW_DAMAGE).build();
        ItemStack loreTest = new ItemStackBuilder(Material.WOOD).lore("line1", "line2").build();
        ItemStack metaTest = new ItemStackBuilder(Material.WOOD).meta(2).build();


        Assert.assertEquals(Material.WOOD, materialTest.getType());
        Assert.assertEquals(5, amountTest.getAmount());
        Assert.assertEquals(15, durabilityTest.getDurability());
        Assert.assertEquals("test", displayNameTest.getItemMeta().getDisplayName());
        Assert.assertEquals(1, enchantmentTest.getEnchantmentLevel(Enchantment.ARROW_DAMAGE));
        Assert.assertEquals(2, metaTest.getData().getData());

        List<String> lore = loreTest.getItemMeta().getLore();
        Assert.assertEquals(2, lore.size());
        Assert.assertEquals("line1", lore.get(0));
        Assert.assertEquals("line2", lore.get(1));
    }
}