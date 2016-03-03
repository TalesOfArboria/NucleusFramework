package com.jcwhatever.nucleus.managed.astar.nodes;

import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.managed.astar.AStar;
import com.jcwhatever.nucleus.managed.astar.IAStarContext;
import com.jcwhatever.nucleus.managed.astar.IAStarResult;
import com.jcwhatever.nucleus.managed.astar.IAStarSettings;
import com.jcwhatever.nucleus.managed.astar.examiners.AStarGraphExaminer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * 
 */
public class AStarGraphNodeTest {

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    @Test
    public void test() throws Exception {

        AStarGraphNode nodeA = new AStarGraphNode("a", 0, 0, 0);
        AStarGraphNode nodeB = new AStarGraphNode("b", 0, 0, 0);
        AStarGraphNode nodeC = new AStarGraphNode("c", 0, 0, 0);
        AStarGraphNode nodeD = new AStarGraphNode("a", 0, 0, 0);

        nodeA.addAdjacent(nodeB);
        nodeA.addAdjacent(nodeC);
        nodeB.addAdjacent(nodeA);
        nodeA.addAdjacent(nodeC);
        nodeC.addAdjacent(nodeD);
        nodeD.addAdjacent(nodeC);

        IAStarSettings settings = AStar.createSettings();
        IAStarContext<AStarGraphNode> context = AStar.createContext(nodeA, nodeD, settings,
                new AStarGraphExaminer<AStarGraphNode>());

        IAStarResult<AStarGraphNode> result = AStar.search(context);

        assertEquals(IAStarResult.ResultStatus.RESOLVED, result.getStatus());
    }
}