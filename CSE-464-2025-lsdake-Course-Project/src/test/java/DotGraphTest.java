import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DotGraphTest {

    @Test
    public void testAddNodeAndAddNodes() {
        DotGraph graph = new DotGraph();
        // Test adding a single node
        graph.addNode("A");
        // Test adding multiple nodes
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
        // Check that the edge is added correctly
        assertTrue(output.contains("A -> B"), "Graph should contain edge A -> B");
    }

    @Test
    public void testParseGraph() throws IOException {
        // Create a temporary DOT file for testing
        File tempFile = File.createTempFile("testGraph", ".dot");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("digraph G {\n");
            writer.write("    A -> B;\n");
            writer.write("    B -> C;\n");
            writer.write("}\n");
        }
        // Parse the temporary DOT file
        DotGraph graph = DotGraph.parseGraph(tempFile.getAbsolutePath());
        String output = graph.toString();
        assertTrue(output.contains("A"), "Graph should contain node A");
        assertTrue(output.contains("B"), "Graph should contain node B");
        assertTrue(output.contains("C"), "Graph should contain node C");
        // Delete temporary file after testing
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
        // Clean up test file
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
        // Clean up test file
        pngFile.delete();
    }

    @Test
    public void testParseGraphComplex() throws IOException {
        // Create a temporary DOT file for testing
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


}
