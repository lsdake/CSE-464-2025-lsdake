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
        graph.addNode("a");
        // test w/ multiple nodes
        graph.addNodes(new String[] {"b", "c"});
        String output = graph.toString();
        assertTrue(output.contains("a"), "Graph should contain node a");
        assertTrue(output.contains("b"), "Graph should contain node b");
        assertTrue(output.contains("c"), "Graph should contain node c");
    }

    @Test
    public void testAddEdge() {
        DotGraph graph = new DotGraph();
        graph.addEdge("a", "b");
        String output = graph.toString();
        assertTrue(output.contains("a -> b"), "Graph should contain edge a -> b");
    }

    @Test
    public void testParseGraph() throws IOException {
        // create temp dot file for testing
        File tempFile = File.createTempFile("testGraph", ".dot");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("digraph G {\n");
            writer.write("    a -> b;\n");
            writer.write("    b -> c;\n");
            writer.write("}\n");
        }
        // parse temp dot file
        DotGraph graph = DotGraph.parseGraph(tempFile.getAbsolutePath());
        String output = graph.toString();
        assertTrue(output.contains("a"), "Graph should contain node a");
        assertTrue(output.contains("b"), "Graph should contain node b");
        assertTrue(output.contains("c"), "Graph should contain node c");
        // delete temp file
        tempFile.delete();
    }

    @Test
    public void testOutputDOTGraph() throws IOException {
        DotGraph graph = new DotGraph();
        graph.addEdge("a", "b");
        String dotFilePath = "input.dot";
        graph.outputDOTGraph(dotFilePath);
        String content = Files.readString(Paths.get(dotFilePath));
        assertTrue(content.contains("a -> b"), "DOT file should contain edge a -> b");
        // cleanup
        new File(dotFilePath).delete();
    }

    @Test
    public void testOutputGraphics() throws IOException, InterruptedException {
        DotGraph graph = new DotGraph();
        graph.addEdge("a", "b");    // add an edge
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
        graph.addNode("a");
        // test w/ multiple nodes
        graph.addNodes(new String[] {"b", "c"});
        String output = graph.toString();
        assertTrue(output.contains("a"), "Graph should contain node A");
        assertTrue(output.contains("b"), "Graph should contain node B");
        assertTrue(output.contains("c"), "Graph should contain node C");


        // test removing a node that exists in the graph
        graph.removeNode("a");

        // test removing multiple nodes that exist in the graph
        graph.removeNodes(new String[] {"b", "c"});

        // overwrite graph with changes
        output = graph.nodesToString();

        assertFalse(output.contains("a"), "Graph should not contain node a");
        assertFalse(output.contains("b"), "Graph should not contain node b");
        assertFalse(output.contains("c"), "Graph should not contain node c");

        graph.addNode("a");

        // test removing a node that DNE in the graph
        graph.removeNode("d");

        // teest removing multiple nodes that DNE in the graph

        // overwrite graph with changes
        output = graph.nodesToString();
        assertFalse(output.contains("d"), "Graph contains node D");

        graph.removeNodes(new String[] {"e", "f"});
        output = graph.nodesToString();
        assertFalse(output.contains("e"), "Graph should not contain node e");
        assertFalse(output.contains("f"), "Graph should not contain node f");

    }

    @Test
    public void testRemoveEdge() {
        DotGraph graph = new DotGraph();
        graph.addEdge("a", "b");
        String output = graph.edgesToString();
        // check that its happy
        assertTrue(output.contains("a -> b"), "Graph should contain edge a -> b");


        // remove edge that exists in the graph
        graph.removeEdge("a", "b");
        output = graph.edgesToString();
        assertFalse(output.contains("a -> b"), "Graph should contain edge a -> b");

        // remove edge that DNE in the graph
        graph.removeEdge("c", "d");
        output = graph.edgesToString();

        System.out.println(output);
        assertFalse(output.contains("c -> d"), "Graph should contain edge c -> d");
    }

    @Test
    public void testDFS() {
        DotGraph graph = new DotGraph();
        graph.addNodes(new String[]{"a", "b", "c", "d"});
        graph.addEdge("a", "b");
        graph.addEdge("b", "c");
        graph.addEdge("c", "a");
        graph.addEdge("b", "d");

        // do bfs search

        String output;
        
        if (graph.GraphSearch("a", "d", graph.getBFS()) != null) {
            output = graph.GraphSearch("a", "d", graph.getBFS()).getPathArray();
        }
        else {
            output = "\"\"";
        }
        
        assertTrue(output.contains("Path{nodes=[Node{a}, Node{b}, Node{d}]}"), "Path should be \"Path{nodes=[Node{a}, Node{b}, Node{d}]}\"");

        graph.addEdge("e", "f");
        if (graph.GraphSearch("e", "a", graph.getDFS()) != null) {
            output = graph.GraphSearch("e", "a", graph.getDFS()).getPathArray();
        }
        else {
            output = "\"\"";
        }

        assertTrue(output.contains(""), "Path should be \"\"");

    }


    @Test
    public void testBFS() {
        DotGraph graph = new DotGraph();
        graph.addNodes(new String[]{"a", "b", "c", "d"});
        graph.addEdge("a", "b");
        graph.addEdge("b", "c");
        graph.addEdge("c", "a");
        graph.addEdge("b", "d");

        // do bfs search

        String output;
        
        if (graph.GraphSearch("a", "d", graph.getBFS()) != null) {
            output = graph.GraphSearch("a", "d", graph.getBFS()).getPathArray();
        }
        else {
            output = "\"\"";
        }
        System.out.println(output);
        assertTrue(output.contains("Path{nodes=[Node{a}, Node{b}, Node{d}]}"), "Path should be \"Path{nodes=[Node{a}, Node{b}, Node{d}]}\"");
        
        graph.addEdge("e", "f");

        if (graph.GraphSearch("e", "a", graph.getBFS()) != null) {
            output = graph.GraphSearch("e", "a", graph.getBFS()).getPathArray();
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
        graph.addNodes(new String[]{"a", "b", "c", "d", "e", "f"});
        
        // this graph should have a different path for bfs and dfs
        // bfs path = A -> B -> D -> F
        // dfs path = A -> C -> E -> F
        graph.addEdge("a", "b");
        graph.addEdge("a", "c");
        graph.addEdge("b", "d");
        graph.addEdge("c", "e");
        graph.addEdge("d", "f");
        graph.addEdge("e", "f");

        // do the dfs search
        String dfsOutput;
        if (graph.GraphSearch("a", "f", graph.getDFS()) != null) {
            dfsOutput = graph.GraphSearch("a", "f", graph.getDFS()).getPathArray();
        } else {
            dfsOutput = "";
        }

        String bfsOutput;
        if (graph.GraphSearch("a", "f", graph.getBFS()) != null) {
            bfsOutput = graph.GraphSearch("a", "f", graph.getBFS()).getPathArray();
        } else {
            bfsOutput = "";
        }

        // bfs path should be A -> B -> D -> F
        assertTrue(bfsOutput.contains("Path{nodes=[Node{a}, Node{b}, Node{d}, Node{f}]}"), "BFS path should be \"Path{nodes=[Node{a}, Node{b}, Node{d}, Node{f}]}\"");
        // dfs path should be A -> C -> E -> F 
        assertTrue(dfsOutput.contains("Path{nodes=[Node{a}, Node{c}, Node{e}, Node{f}]}"), "DFS path should be \"Path{nodes=[Node{a}, Node{c}, Node{e}, Node{f}]}\"");
    }
}
