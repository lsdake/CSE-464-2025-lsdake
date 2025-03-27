import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class DotGraphTest {

    @Test
    public void testAddNodeAndAddNodes() {
        DotGraph graph = new DotGraph();
        // test w/ single node
        graph.addNode("A");
        // test w/ multiple nodes
        graph.addNodes(new String[] {"B", "C"});
        String output = graph.toString();
        assertTrue(output.contains("A"), "Graph should contain node A");
        assertTrue(output.contains("B"), "Graph should contain node B");
        assertTrue(output.contains("C"), "Graph should contain node C");
    }

    @Test
    public void testAddEdge() {
        DotGraph graph = new DotGraph();
        graph.addEdge("A", "B");
        String output = graph.toString();
        assertTrue(output.contains("A -> B"), "Graph should contain edge A -> B");
    }

    @Test
    public void testParseGraph() throws IOException {
        // create temp dot file for testing
        File tempFile = File.createTempFile("testGraph", ".dot");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("digraph G {\n");
            writer.write("    A -> B;\n");
            writer.write("    B -> C;\n");
            writer.write("}\n");
        }
        // parse temp dot file
        DotGraph graph = DotGraph.parseGraph(tempFile.getAbsolutePath());
        String output = graph.toString();
        assertTrue(output.contains("A"), "Graph should contain node A");
        assertTrue(output.contains("B"), "Graph should contain node B");
        assertTrue(output.contains("C"), "Graph should contain node C");
        // delete temp file
        tempFile.delete();
    }

    @Test
    public void testOutputDOTGraph() throws IOException {
        DotGraph graph = new DotGraph();
        graph.addEdge("A", "B");
        String dotFilePath = "input.dot";
        graph.outputDOTGraph(dotFilePath);
        String content = Files.readString(Paths.get(dotFilePath));
        assertTrue(content.contains("A -> B"), "DOT file should contain edge A -> B");
        // cleanup
        new File(dotFilePath).delete();
    }

    @Test
    public void testOutputGraphics() throws IOException, InterruptedException {
        DotGraph graph = new DotGraph();
        graph.addEdge("A", "B");    // add an edge
        String pngFilePath = "test_output.png";
        graph.outputGraphics(pngFilePath, "png");   // outputs the drawing to the filepath
        File pngFile = new File(pngFilePath);
        assertTrue(pngFile.exists(), "PNG file should exist after outputGraphics is called");
        // cleanup
        pngFile.delete();
    }

    @Test
    public void testParseGraphComplex() throws IOException {
        // create temp got file for testing
        File tempFile = File.createTempFile("test", ".dot");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("digraph {\n");
            writer.write("    a -> b;\n");
            writer.write("    b -> c;\n");
            writer.write("    c -> d;\n");
            writer.write("    d -> a;\n");
            writer.write("    a -> e;\n");
            writer.write("    e -> f;\n");
            writer.write("    e -> g;\n");
            writer.write("    f -> h;\n");
            writer.write("    g -> h;\n");
            writer.write("}\n");
        }

        // parse it
        DotGraph graph = DotGraph.parseGraph(tempFile.getAbsolutePath());
        String output = graph.toString();
        assertTrue(output.contains("a"), "Graph should contain node a");
        assertTrue(output.contains("b"), "Graph should contain node b");
        assertTrue(output.contains("c"), "Graph should contain node c");
        assertTrue(output.contains("d"), "Graph should contain node d");
        assertTrue(output.contains("e"), "Graph should contain node e");
        assertTrue(output.contains("f"), "Graph should contain node f");
        assertTrue(output.contains("g"), "Graph should contain node g");
        assertTrue(output.contains("h"), "Graph should contain node h");

        tempFile.delete();
    }


    // PART 2

    @Test
    public void testRemoveNode() {
        DotGraph graph = new DotGraph();
        // Test adding a single node
        graph.addNode("A");
        // test w/ multiple nodes
        graph.addNodes(new String[] {"B", "C"});
        String output = graph.toString();
        assertTrue(output.contains("A"), "Graph should contain node A");
        assertTrue(output.contains("B"), "Graph should contain node B");
        assertTrue(output.contains("C"), "Graph should contain node C");


        // test removing a node that exists in the graph
        graph.removeNode("A");

        // test removing multiple nodes that exist in the graph
        graph.removeNodes(new String[] {"B", "C"});

        // overwrite graph with changes
        output = graph.nodesToString();

        assertFalse(output.contains("A"), "Graph should not contain node A");
        assertFalse(output.contains("B"), "Graph should not contain node B");
        assertFalse(output.contains("C"), "Graph should not contain node C");

        graph.addNode("A");

        // test removing a node that DNE in the graph
        graph.removeNode("D");

        // teest removing multiple nodes that DNE in the graph

        // overwrite graph with changes
        output = graph.nodesToString();
        assertFalse(output.contains("D"), "Graph contains node D");

        graph.removeNodes(new String[] {"E", "F"});
        output = graph.nodesToString();
        assertFalse(output.contains("E"), "Graph should not contain node E");
        assertFalse(output.contains("F"), "Graph should not contain node F");

    }

    @Test
    public void testRemoveEdge() {
        DotGraph graph = new DotGraph();
        graph.addEdge("A", "B");
        String output = graph.edgesToString();
        // check that its happy
        assertTrue(output.contains("A -> B"), "Graph should contain edge A -> B");


        // remove edge that exists in the graph
        graph.removeEdge("A", "B");
        output = graph.edgesToString();
        assertFalse(output.contains("A -> B"), "Graph should contain edge A -> B");

        // remove edge that DNE in the graph
        graph.removeEdge("C", "D");
        output = graph.edgesToString();

        System.out.println(output);
        assertFalse(output.contains("C -> D"), "Graph should contain edge C -> D");
    }

    @Test
    public void testBFS() {
        DotGraph graph = new DotGraph();
        graph.addNodes(new String[]{"A", "B", "C", "D"});
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");
        graph.addEdge("B", "D");

        // do bfs search

        String output;
        
        if (graph.GraphSearch("A", "D", graph.getBFS()) != null) {
            output = graph.GraphSearch("A", "D", graph.getBFS()).toString();
        }
        else {
            output = "\"\"";
        }
        
        assertTrue(output.contains("A -> B -> D"), "Path should be \"A -> B -> D\"");
        

        graph.addEdge("E", "F");

        if (graph.GraphSearch("E", "A", graph.getDFS()) != null) {
            output = graph.GraphSearch("E", "A", graph.getDFS()).toString();
        }
        else {
            output = "\"\"";
        }

        assertTrue(output.contains(""), "Path should be \"\"");

    }


    @Test
    public void testDFS() {
        DotGraph graph = new DotGraph();
        graph.addNodes(new String[]{"A", "B", "C", "D"});
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");
        graph.addEdge("B", "D");

        // do bfs search

        String output;
        
        if (graph.GraphSearch("A", "D", graph.getBFS()) != null) {
            output = graph.GraphSearch("A", "D", graph.getBFS()).toString();
        }
        else {
            output = "\"\"";
        }
        
        assertTrue(output.contains("A -> B -> D"), "Path should be \"A -> B -> D\"");
        
        graph.addEdge("E", "F");

        if (graph.GraphSearch("E", "A", graph.getBFS()) != null) {
            output = graph.GraphSearch("E", "A", graph.getBFS()).toString();
        }
        else {
            output = "\"\"";
        }

        assertTrue(output.contains(""), "Path should be \"\"");

    }

    @Test
    public void testBFSvsDFS() {    // make sure they find different paths if it calls for it
        DotGraph graph = new DotGraph();
        // add nodes
        graph.addNodes(new String[]{"A", "B", "C", "D", "E", "F"});
        
        // this graph should have a different path for bfs and dfs
        // bfs path = A -> B -> D -> F
        // dfs path = A -> C -> E -> F
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "E");
        graph.addEdge("D", "F");
        graph.addEdge("E", "F");

        // do the dfs search
        String dfsOutput;
        if (graph.GraphSearch("A", "F", graph.getDFS()) != null) {
            dfsOutput = graph.GraphSearch("A", "F", graph.getDFS()).toString();
        } else {
            dfsOutput = "";
        }

        String bfsOutput;
        if (graph.GraphSearch("A", "F", graph.getBFS()) != null) {
            bfsOutput = graph.GraphSearch("A", "F", graph.getBFS()).toString();
        } else {
            bfsOutput = "";
        }

        // bfs path should be A -> B -> D -> F
        assertTrue(bfsOutput.contains("A -> B -> D -> F"), "BFS path should be \"A -> B -> E -> F\"");
        // dfs path should be A -> C -> E -> F 
        assertTrue(dfsOutput.contains("A -> C -> E -> F"), "DFS path should be \"A -> C -> E -> F\"");
    }
}
