import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

// graph class! yay :)
public class DotGraph {

    // enum for BFS/DFS search stuff
    public enum Algorithm {
        BFS,
        DFS
    }

    // data structs for nodes/edges
    private Set<String> nodes;
    private Set<Edge> edges;

    // magic strings
    private static final String DOT_HEADER = "digraph {";
    private static final String DOT_FOOTER = "}";


    // graph constructor
    public DotGraph() {
        nodes = new LinkedHashSet<>();
        edges = new LinkedHashSet<>();
    }

    Algorithm getBFS() {
        return Algorithm.BFS;
    }
    Algorithm getDFS() {
        return Algorithm.DFS;
    }

    private static String removeSemicolon(String line) {
        String newLine = "";
        if (line.endsWith(";")) {
            newLine = line.substring(0, line.length() - 1);
        }
        return newLine;
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
            line = removeSemicolon(line);
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
        // should only remove a node if the node already exists
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
        addNode(startLabel);    // if one or both of the nodes DNE yet in the graph then it gets added in
        addNode(endLabel);
        Edge edge = new Edge(startLabel, endLabel); // make the edge
        edges.add(edge);    // add it to the graph
    }

    // remove directed edge from the graph
    public void removeEdge(String startLabel, String endLabel) {
        Edge temp = new Edge(startLabel, endLabel);
        edges.remove(temp);
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

    public String nodesToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("").append(nodes).append("\n");
        return sb.toString();
    }

    public String edgesToString() {
        StringBuilder sb = new StringBuilder();
        for (Edge e : edges) {
            sb.append("  ").append(e).append("\n");
        }
        return sb.toString();
    }

    // output graph to .txt file
    public void outputGraphToText(String filepath) throws IOException {
        try (FileWriter writer = new FileWriter(filepath)) {    // writes it lol
            writer.write(this.toString());
        }
    }

    // output graph to dot file
    public void outputDOTGraph(String filepath) throws IOException {
        try (FileWriter writer = new FileWriter(filepath)) {
            writer.write(DOT_HEADER + "\n");
            for (String node : nodes) {
                writer.write("    " + node + ";\n");
            }
            for (Edge e : edges) {
                writer.write("    " + e.getStart() + " -> " + e.getEnd() + ";\n");
            }
            writer.write(DOT_FOOTER);
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


    public Path GraphSearch(String src, String dst, Algorithm algo) {
        switch(algo) {
            case BFS:
                return GraphSearchBFS(src, dst);
            case DFS:
                return GraphSearchDFS(src, dst);
            default:
                return null;
        }
    }

     // bfs search
    Path GraphSearchBFS(String src, String dst) {
        // Initialize all nodes and a queue
        Map<String, PathNode> nodeMap = new HashMap<>();
        for (String name : nodes) {
            nodeMap.put(name, new PathNode(name));
        }
        Queue<PathNode> q = new LinkedList<>();
        
        // Start from the source
        PathNode root = nodeMap.get(src);
        root.setExplored(true);
        q.add(root);
        
        // BFS loop
        while (!q.isEmpty()) {
            PathNode current = q.poll();
            
            // Check if we reached the destination
            if (current.getName().equals(dst)) {
                // Reconstruct the path from destination back to source
                List<PathNode> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = nodeMap.get(current.getParent()); // getParent returns parent's name or null if root
                }
                Collections.reverse(path);
                return new Path(path);
            }
            
            // Process all adjacent edges of current
            for (Edge edge : edges) {
                // Use equals() to compare strings
                if (edge.getStart().equals(current.getName())) {
                    String adjacentName = edge.getEnd();
                    PathNode adjacent = nodeMap.get(adjacentName);
                    if (!adjacent.isExplored()) {
                        adjacent.setExplored(true);
                        adjacent.setParent(current.getName());
                        q.add(adjacent);
                    }
                }
            }
        }
        return null; // No path found
    }

    // dfs search
    Path GraphSearchDFS(String src, String dst) {
        // Initialize all nodes in a map
        Map<String, PathNode> nodeMap = new HashMap<>();
        for (String name : nodes) {
            nodeMap.put(name, new PathNode(name));
        }
        
        // Use a stack for DFS
        Stack<PathNode> stack = new Stack<>();
        
        // Start from the source node
        PathNode root = nodeMap.get(src);
        root.setExplored(true);
        stack.push(root);
        
        // DFS loop
        while (!stack.isEmpty()) {
            PathNode current = stack.pop();
            
            // Check if we reached the destination
            if (current.getName().equals(dst)) {
                // Reconstruct the path from destination back to source using parent pointers
                List<PathNode> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = nodeMap.get(current.getParent()); // getParent returns parent's name or null if root
                }
                Collections.reverse(path);
                return new Path(path);
            }
            
            // Process all adjacent edges of current
            // (Using a loop over all edges; adjust if you have a direct way to get adjacent nodes)
            for (Edge edge : edges) {
                // Check if the current node is the start of the edge using equals() for string comparison
                if (edge.getStart().equals(current.getName())) {
                    String adjacentName = edge.getEnd();
                    PathNode adjacent = nodeMap.get(adjacentName);
                    if (!adjacent.isExplored()) {
                        adjacent.setExplored(true);
                        adjacent.setParent(current.getName());
                        stack.push(adjacent);
                    }
                }
            }
        }
        return null; // No path found
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
