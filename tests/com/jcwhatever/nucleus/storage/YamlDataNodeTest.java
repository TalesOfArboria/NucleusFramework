package com.jcwhatever.nucleus.storage;

import com.jcwhatever.dummy.DummyPlugin;

import org.bukkit.plugin.Plugin;


public class YamlDataNodeTest extends IDataNodeTest{

    public YamlDataNodeTest() {

        Plugin plugin = new DummyPlugin("dummy");

        YamlDataNode initNode = new YamlDataNode(plugin, "");

        initNode(initNode);
        initNode.save();

        _dataNode = new YamlDataNode(plugin, initNode._yamlString);
        _dataNode.load();
    }
}