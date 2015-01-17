package com.jcwhatever.nucleus.storage;

import com.jcwhatever.dummy.DummyPlugin;

import org.bukkit.plugin.Plugin;


public class YamlDataNodeTest extends IDataNodeTest{

    public YamlDataNodeTest() {

        final Plugin plugin = new DummyPlugin("dummy");

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