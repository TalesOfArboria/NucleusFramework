package com.jcwhatever.nucleus.storage;

import com.jcwhatever.v1_8_R2.MockPlugin;

import org.bukkit.plugin.Plugin;


public class YamlDataNodeTest extends IDataNodeTest{

    public YamlDataNodeTest() {

        final Plugin plugin = new MockPlugin("dummy").enable();

        setNodeGenerator(new IDataNodeGenerator() {
            @Override
            public IDataNode generateRoot() {
                YamlDataNode node = new YamlDataNode(plugin, "");
                node.load();

                return node;
            }
        });
    }
}