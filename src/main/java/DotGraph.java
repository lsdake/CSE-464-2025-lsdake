import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// graph class! yay :)
public class DotGraph {

    // class representing a graph edge
    public static class Edge {
        String start;
        String end;

        public Edge(String start, String end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return start + " -> " + end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge edge = (Edge) o;
            return start.equals(edge.start) && end.equals(edge.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
    }

    // data structs for nodes/edges
    private Set<String> nodes;
    private Set<Edge> edges;

    // graph constructor
    public DotGraph() {
        nodes = new LinkedHashSet<>();
        edges = new LinkedHashSet<>();
    }

    // parse dot file and creates its graph
    public static DotGraph parseGraph(String filepath) throws IOException {
        DotGraph graph = new DotGraph();
        List<String> lines = Files.readAllLines(Paths.get(filepath));

        for (String line : lines) {
            line = line.trim();
            // skip empty lines
            if (line.isEmpty() || line.startsWith("digraph") || line.startsWith("{") || line.startsWith("}")) {
                continue;
            }
            // remove trailing semicolon
            if (line.endsWith(";")) {
                line = line.substring(0, line.length() - 1);
            }
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

    // add a single node; will NOT add duplicates
    public void addNode(String label) {
        nodes.add(label);
    }

    // remove a single node
    public void removeNode(String label) {
        // should only remove a node if the node already exists (should avoid errors from trying to remove a node that DNE)
        if (nodes.contains(label)) {
            nodes.remove(label);
        }
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
        addNode(startLabel);    // if one or both of the nodes DNE yet in the graph then it gets added in
        addNode(endLabel);
        Edge edge = new Edge(startLabel, endLabel); // make the edge
        edges.add(edge);    // add it to the graph
    }

    // remove directed edge from the graph
    public void removeEdge(String startLabel, String endLabel) {
        Edge temp = new Edge(startLabel, endLabel);
        if (edges.contains(temp)) {
            edges.remove(temp);
        }
    }

    // returns a string holding the data of the graph
    // this is the one that says how many nodes/edges there are and stuff
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

    // output graph to .txt file
    public void outputGraph(String filepath) throws IOException {
        try (FileWriter writer = new FileWriter(filepath)) {    // writes it lol
            writer.write(this.toString());
        }
    }

    // output graph to dot file
    public void outputDOTGraph(String filepath) throws IOException {
        try (FileWriter writer = new FileWriter(filepath)) {
            writer.write("digraph {\n");
            // write nodes
            for (String node : nodes) {
                writer.write("    " + node + ";\n");
            }
            // write edges
            for (Edge e : edges) {
                writer.write("    " + e.start + " -> " + e.end + ";\n");
            }
            writer.write("}");
        }
    }

    // output graph to image file (i will say idk if .jpg works but ik .png does lol so yay)
    public void outputGraphics(String filepath, String format) throws IOException, InterruptedException {
        // write dot file to a temp file
        File tempDot = File.createTempFile("tempGraph", ".dot");
        outputDOTGraph(tempDot.getAbsolutePath());

        // build graphviz command
        // i.e: dot -T png tempGraph.dot -o output.png
        ProcessBuilder pb = new ProcessBuilder("dot", "-T" + format, tempDot.getAbsolutePath(), "-o", filepath);
        Process process = pb.start();

        // write in/out errors so we can see them
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

        // delete temp file
        tempDot.delete();
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
                graph.addNodes(new String[]{"A", "B", "C"});
                graph.addEdge("A", "B");
                graph.addEdge("B", "C");
                graph.addEdge("C", "A");
            }

            // print graph to console
            System.out.println(graph.toString());

            // write text output to console
            graph.outputGraph("graph_output.txt");

            // write dot file
            graph.outputDOTGraph("graph_output.dot");

            // output image
            graph.outputGraphics("graph_output.png", "png");

            System.out.println("Graph outputs generated successfully!  Yay :)");

        // if it explodes :(
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
