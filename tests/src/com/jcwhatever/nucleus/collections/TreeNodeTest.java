package com.jcwhatever.nucleus.collections;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

public class TreeNodeTest {


    @Test
    public void testIsRoot() throws Exception {

        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("child");

        Assert.assertEquals(true, root.isRoot());
        Assert.assertEquals(false, child.isRoot());

    }

    @Test
    public void testIsLeaf() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("child");

        Assert.assertEquals(false, root.isLeaf());
        Assert.assertEquals(true, child.isLeaf());
    }

    @Test
    public void testGetDepth() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("child");
        TreeNode<String> child2 = child.addChild("child2");

        Assert.assertEquals(0, root.getDepth());
        Assert.assertEquals(1, child.getDepth());
        Assert.assertEquals(2, child2.getDepth());
    }

    @Test
    public void testGetValue() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("child");

        Assert.assertEquals("root", root.getValue());
        Assert.assertEquals("child", child.getValue());
    }

    @Test
    public void testGetParent() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("child");
        TreeNode<String> child2 = child.addChild("child2");

        Assert.assertEquals(null, root.getParent());
        Assert.assertEquals(root, child.getParent());
        Assert.assertEquals(child, child2.getParent());
    }

    @Test
    public void testGetChildren() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("child");

        Collection<TreeNode<String>> children = root.getChildren();

        Assert.assertEquals(1, children.size());
        Assert.assertEquals(child, children.iterator().next());
    }

    @Test
    public void testTotalChildren() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("child");
        TreeNode<String> child2 = root.addChild("child2");
        TreeNode<String> child_child = child.addChild("child_child");

        Assert.assertEquals(2, root.totalChildren());
        Assert.assertEquals(1, child.totalChildren());
        Assert.assertEquals(0, child2.totalChildren());
        Assert.assertEquals(0, child_child.totalChildren());
    }

    @Test
    public void testRemoveChild() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("child");
        TreeNode<String> child2 = root.addChild("child2");

        root.removeChild("child");

        Assert.assertEquals(1, root.totalChildren());

        root.removeChild(child2);

        Assert.assertEquals(0, root.totalChildren());
    }

    @Test
    public void testIterator() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("root_child");
        TreeNode<String> child2 = root.addChild("root_child2");

        int count = 0;
        for (TreeNode<String> node : root) {
            count ++;
        }

        Assert.assertEquals(3, count);

        TreeNode<String> child_child = child.addChild("child_child");
        TreeNode<String> child_child2 = child.addChild("child_child2");

        count = 0;
        for (TreeNode<String> node : root) {
            count ++;
        }

        Assert.assertEquals(5, count);
    }

    @Test
    public void testIteratorRemove() throws Exception {
        TreeNode<String> root = new TreeNode<>("root");
        TreeNode<String> child = root.addChild("root_child");
        root.addChild("root_child2");
        root.addChild("root_child3");
        root.addChild("root_child4");


        int count = 0;
        for (TreeNode<String> node : root) {
            count ++;
        }

        // make sure all 5 nodes were iterated.
        Assert.assertEquals(5, count);

        count = 0;
        Iterator<TreeNode<String>> iterator = root.iterator();
        while(iterator.hasNext()) {
            TreeNode<String> node = iterator.next();

            count++;

            if (node.getValue().equals("root_child2") ||
                    node.getValue().equals("root_child3") ||
                    node.getValue().equals("root_child4")) {
                iterator.remove();
            }
        }

        // make sure all 5 nodes were iterated.
        Assert.assertEquals(5, count);

        count = 0;
        for (TreeNode<String> node : root) {
            count ++;
        }

        // make sure 3 nodes were removed.
        Assert.assertEquals(2, count);

    }

}