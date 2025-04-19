import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// graph class! yay :)
public class DotGraph {

    // enum for BFS/DFS search stuff
    public enum Algorithm { BFS, DFS }

    private static final String DOT_HEADER = "digraph {";  // Replace magic string
    private static final String DOT_FOOTER = "}";

    // Encapsulated data structs for nodes/edges
    private final Set<String> nodes;
    private final Set<Edge> edges;

    // graph constructor
    public DotGraph() {
        nodes = new LinkedHashSet<>();
        edges = new LinkedHashSet<>();
    }

    Algorithm getBFS() { return Algorithm.BFS; }
    Algorithm getDFS() { return Algorithm.DFS; }

    // parse dot file and creates its graph
     
    public static DotGraph parseGraph(String filepath) throws IOException {
        DotGraph graph = new DotGraph();
        List<String> lines = Files.readAllLines(Paths.get(filepath));

        for (String line : lines) {
            line = line.trim();
            // skip empty or brace/digraph lines
            if (line.isEmpty() || line.startsWith("digraph") || line.startsWith("{") || line.startsWith("}")) {
                continue;
            }
            // remove trailing semicolon
            line = stripSemicolon(line);
            // in format "x -> y"
            String[] parts = line.split("->");
            if (parts.length == 2) {
                String start = parts[0].trim();
                String end = parts[1].trim();
                // add nodes + edge
                graph.addNode(start);
                graph.addNode(end);
                graph.addEdge(start, end);
            }
        }
        return graph;
    }

    /** Helper to strip semicolon (extracted method) */
    private static String stripSemicolon(String line) {
        return line.endsWith(";") ? line.substring(0, line.length() - 1) : line;
    }

    // add a single node; will NOT add duplicates
    public void addNode(String label) {
        nodes.add(label);
    }

    // remove a single node
    public void removeNode(String label) {
        nodes.remove(label);
    }

    // add multiple nodes
    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
    }

    // remove multiple nodes
    public void removeNodes(String[] labels) {
        for (String label : labels) {
            removeNode(label);
        }
    }

    // add directed edge to the graph, checking for duplicates
    public void addEdge(String startLabel, String endLabel) {
        addNode(startLabel);
        addNode(endLabel);
        edges.add(new Edge(startLabel, endLabel));
    }

    // remove directed edge from the graph
    public void removeEdge(String startLabel, String endLabel) {
        edges.remove(new Edge(startLabel, endLabel));
    }

    public Set<String> getNodes() {
        return nodes;
    }

    // output all nodes to a string
    public String nodesToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("").append(nodes).append("\n");
        return sb.toString();
    }

    // output all edges to a string
    public String edgesToString() {
        StringBuilder sb = new StringBuilder();
        for (Edge e : edges) {
            sb.append("  ").append(e).append("\n");
        }
        return sb.toString();
    }


    // returns a string holding the data of the graph
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nodes (").append(nodes.size()).append("): ").append(nodes).append("\n");
        sb.append("Edges (").append(edges.size()).append("):\n");
        for (Edge e : edges) {
            sb.append("  ").append(e).append("\n");
        }
        return sb.toString();
    }

    // outputs the graph in text format
    public void outputGraphToText(String filepath) throws IOException {
        try (FileWriter writer = new FileWriter(filepath)) {
            writer.write(toString());
        }
    }

    // output graph to dot file
    public void outputDOTGraph(String filepath) throws IOException {
        try (FileWriter writer = new FileWriter(filepath)) {
            writer.write(DOT_HEADER + "\n");
            // write nodes
            for (String node : nodes) {
                writer.write("    " + node + ";\n");
            }
            // write edges
            for (Edge e : edges) {
                writer.write("    " + e.getStart() + " -> " + e.getEnd() + ";\n");
            }
            writer.write(DOT_FOOTER);
        }
    }

    // output graph to image file (i will say idk if .jpg works but ik .png does lol so yay)
    public void outputGraphics(String filepath, String format) throws IOException, InterruptedException {
        File tempDot = File.createTempFile("tempGraph", ".dot");
        outputDOTGraph(tempDot.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder("dot", "-T" + format, tempDot.getAbsolutePath(), "-o", filepath);
        Process process = pb.start();

        // capture errors
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String s;
        StringBuilder errorOutput = new StringBuilder();
        while ((s = stdError.readLine()) != null) {
            errorOutput.append(s).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Graphviz process failed: " + errorOutput.toString());
        }

        tempDot.delete();
    }

    // return all neighbors of a given node
    public List<String> getNeighbors(String node) {
        return edges.stream()
                .filter(e -> e.getStart().equals(node))
                .map(Edge::getEnd)
                .collect(Collectors.toList());
    }

    // delegates BFS/DFS searches to template-based implementations
    public Path GraphSearch(String src, String dst, Algorithm algo) {
        switch (algo) {
            case BFS: return new BFSGraphSearch().search(this, src, dst);
            case DFS: return new DFSGraphSearch().search(this, src, dst);
            default:   return null;
        }
    }

    // main
    public static void main(String[] args) {
        try {
            // either parse provided dot file from arg
            DotGraph graph;
            if (args.length > 0 && args[0].endsWith(".dot")) {
                System.out.println("Parsing DOT file: " + args[0]);
                graph = parseGraph(args[0]);
            } else {
                // or create new graph & add data manually
                graph = new DotGraph();
                graph.addNodes(new String[]{"A", "B", "C", "D"});
                graph.addEdge("A", "B");
                graph.addEdge("B", "C");
                graph.addEdge("C", "A");
                graph.addEdge("B", "D");
            }

            // print graph to console
            System.out.println(graph.toString());

            // write text output to console
            graph.outputGraphToText("graph_output.txt");

            // write dot file
            graph.outputDOTGraph("graph_output.dot");

            // output image
            graph.outputGraphics("graph_output.png", "png");

            System.out.println(graph.GraphSearch("A", "C", Algorithm.BFS).toString());

            System.out.println("Graph outputs generated successfully!  Yay :)");

            System.out.println(graph.GraphSearch("A", "C", Algorithm.DFS).toString());

            System.out.println("Graph outputs generated successfully!  Yay :)");

        // if it explodes :(
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
