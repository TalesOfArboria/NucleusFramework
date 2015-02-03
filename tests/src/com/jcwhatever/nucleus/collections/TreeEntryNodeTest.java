package com.jcwhatever.nucleus.collections;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

public class TreeEntryNodeTest {

    @Test
    public void testIsRoot() throws Exception {

        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");

        Assert.assertEquals(true, root.isRoot());
        Assert.assertEquals(false, child.isRoot());

    }

    @Test
    public void testIsLeaf() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");

        Assert.assertEquals(false, root.isLeaf());
        Assert.assertEquals(true, child.isLeaf());
    }

    @Test
    public void testGetDepth() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");
        TreeEntryNode<String, String> child2 = child.putChild("child2", "test");

        Assert.assertEquals(0, root.getDepth());
        Assert.assertEquals(1, child.getDepth());
        Assert.assertEquals(2, child2.getDepth());
    }

    @Test
    public void testGetKey() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");

        Assert.assertEquals("root", root.getKey());
        Assert.assertEquals("child", child.getKey());
    }

    @Test
    public void testGetValue() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test1");
        TreeEntryNode<String, String> child = root.putChild("child", "test2");

        Assert.assertEquals("test1", root.getValue());
        Assert.assertEquals("test2", child.getValue());
    }

    @Test
    public void testGetParent() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");
        TreeEntryNode<String, String> child2 = child.putChild("child2", "test");

        Assert.assertEquals(null, root.getParent());
        Assert.assertEquals(root, child.getParent());
        Assert.assertEquals(child, child2.getParent());
    }

    @Test
    public void testGetChildren() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");

        Collection<TreeEntryNode<String, String>> children = root.getChildren();

        Assert.assertEquals(1, children.size());
        Assert.assertEquals(child, children.iterator().next());
    }

    @Test
    public void testTotalChildren() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");
        TreeEntryNode<String, String> child2 = root.putChild("child2", "test");
        TreeEntryNode<String, String> child_child = child.putChild("child_child", "test");

        Assert.assertEquals(2, root.totalChildren());
        Assert.assertEquals(1, child.totalChildren());
        Assert.assertEquals(0, child2.totalChildren());
        Assert.assertEquals(0, child_child.totalChildren());
    }

    @Test
    public void testHasChild() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");
        TreeEntryNode<String, String> child2 = root.putChild("child2", "test");
        TreeEntryNode<String, String> child_child = child.putChild("child_child", "test");

        Assert.assertEquals(true, root.hasChild("child"));
        Assert.assertEquals(false, root.hasChild("child_child"));
        Assert.assertEquals(false, root.hasChild("root"));
        Assert.assertEquals(false, child2.hasChild("root"));
        Assert.assertEquals(true, child.hasChild("child_child"));
        Assert.assertEquals(false, child_child.hasChild("child2"));
    }

    @Test
    public void testGetChild() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");
        TreeEntryNode<String, String> child2 = root.putChild("child2", "test");
        TreeEntryNode<String, String> child_child = child.putChild("child_child", "test");

        Assert.assertEquals(child, root.getChild("child"));
        Assert.assertEquals(null, root.getChild("child_child"));
        Assert.assertEquals(null, child2.getChild("root"));
        Assert.assertEquals(child_child, child.getChild("child_child"));
        Assert.assertEquals(null, child_child.getChild("child2"));
    }
    @Test
    public void testRemoveChild() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("child", "test");
        TreeEntryNode<String, String> child2 = root.putChild("child2", "test");

        root.removeChild("child");

        Assert.assertEquals(1, root.totalChildren());

        root.removeChild(child2);

        Assert.assertEquals(0, root.totalChildren());
    }

    @Test
    public void testIterator() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("root_child", "test");
        TreeEntryNode<String, String> child2 = root.putChild("root_child2", "test");

        int count = 0;
        for (TreeEntryNode<String, String> node : root) {
            count ++;
        }

        Assert.assertEquals(2, count);


        TreeEntryNode<String, String> child_child = child.putChild("child_child", "test");
        TreeEntryNode<String, String> child_child2 = child.putChild("child_child2", "test");

        count = 0;
        for (TreeEntryNode<String, String> node : root) {
            count ++;
        }

        Assert.assertEquals(4, count);
    }

    @Test
    public void testIteratorRemove() throws Exception {
        TreeEntryNode<String, String> root = new TreeEntryNode<>("root", "test");
        TreeEntryNode<String, String> child = root.putChild("root_child", "test");
        TreeEntryNode<String, String> child2 = root.putChild("root_child2", "test");
        TreeEntryNode<String, String> child_child = child.putChild("child_child", "test");
        TreeEntryNode<String, String> child_child2 = child.putChild("child_child2", "test");

        int count = 0;
        for (TreeEntryNode<String, String> node : root) {
            count ++;
        }

        // make sure all 4 child nodes were iterated
        Assert.assertEquals(4, count);

        count = 0;
        Iterator<TreeEntryNode<String, String>> iterator = root.iterator();
        while(iterator.hasNext()) {
            TreeEntryNode<String, String> node = iterator.next();

            count ++;

            if (node.equals(child_child)) {
                iterator.remove();
            }
        }

        // make sure all 4 child nodes were iterated
        Assert.assertEquals(4, count);

        count = 0;
        for (TreeEntryNode<String, String> node : root) {
            count ++;
        }

        // make sure 1 child node was removed
        Assert.assertEquals(3, count);
    }
}