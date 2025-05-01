import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import  java.util.stream.Collectors;

// graph class! yay :)
public class DotGraph {

    // enum for BFS/DFS search stuff
    public enum Algorithm { BFS, DFS, RANDOM_FULL, RANDOM_UNVISITED, RANDOM_BACKTRACK }

    private static final String DOT_HEADER = "digraph {";
    private static final String DOT_FOOTER = "}";

    // data structs for nodes/edges
    private final Set<String> nodes;
    private final Set<Edge> edges;

    private final Map<Algorithm, GraphSearchTemplate> searchStrats;


    // graph constructor
    public DotGraph() {
        nodes = new LinkedHashSet<>();
        edges = new LinkedHashSet<>();

        // gotta init searchStrats since its final
        searchStrats = new HashMap<>();
        searchStrats.put(Algorithm.BFS, new BFSGraphSearch());
        searchStrats.put(Algorithm.DFS, new DFSGraphSearch());
        searchStrats.put(Algorithm.RANDOM_FULL, new RWGraphSearch(RWGraphSearch.Mode.FULLY_RANDOM));
        searchStrats.put(Algorithm.RANDOM_UNVISITED, new RWGraphSearch(RWGraphSearch.Mode.RANDOM_UNVISITED));
        searchStrats.put(Algorithm.RANDOM_BACKTRACK, new RWGraphSearch(RWGraphSearch.Mode.HISTORY_BACKTRACK));
    }

    Algorithm getBFS() { return Algorithm.BFS; }
    Algorithm getDFS() { return Algorithm.DFS; }
    Algorithm getRANDOM_FULL() { return Algorithm.RANDOM_FULL; }
    Algorithm getRANDOM_UNVISITED() { return Algorithm.RANDOM_UNVISITED; }
    Algorithm getRANDOM_BACKTRACK() { return Algorithm.RANDOM_BACKTRACK; }

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
        nodes.add(label.toUpperCase());
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
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb1.append(nodes.size()).append("): ").append(nodes).append("\n");
        sb2.append(edges.size()).append("):\n");
        for (Edge e : edges) {
            sb2.append("  ").append(e).append("\n");
        }
        String s1 = sb1.toString();
        String s2 = sb2.toString();
        String returnString = "Nodes (" + s1.toLowerCase() + "Edges (" + s2.toLowerCase();

        return returnString;
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
                    .filter(e -> e.getStart().equalsIgnoreCase(node))
                    .map(Edge::getEnd)
                    .collect(Collectors.toList());
    }

    // delegates BFS/DFS/Random searches to template-based implementations using strategy pattern
    public Path GraphSearch(String src, String dst, Algorithm algo) {
        GraphSearchTemplate strategy = searchStrats.get(algo);
        if (strategy == null) {     // if its not a real strat then it explodes but this shoooould never happen
            throw new IllegalArgumentException("Unknown algorithm: " + algo);
        }
        Path p = strategy.search(this, src, dst);
        return p;
    }


    public void runRandomSearch(DotGraph graph, String s, String src, String dst) {

        if (s == "full") {

            System.out.println("Fully Random Search:");

            int i = 0;
            int numSuccesses = 0;
            int diffPaths = 0;
            while (i < 5) {
                if (numSuccesses < 2) {
                    // ensure that the first 2 go arounds always find the target node
                    Path currPath = graph.GraphSearch(src, dst, Algorithm.RANDOM_FULL);
                    if (currPath.doesPathReachDestination() == true) {
                        i++;
                        numSuccesses++;
                        System.out.println("Attempt " + i + ": " + currPath.toString());
                    }
                }

                else {
                    Path currPath = graph.GraphSearch(src, dst, Algorithm.RANDOM_FULL);
                    i++;
                    System.out.println("Attempt " + i + ": " + currPath.toString());
                    if (currPath.doesPathReachDestination() == true) {
                        numSuccesses++;
                    }
                }
            }
        }
        else if (s == "unvisited") {

            System.out.println("Unvisited Random Search:");

            int i = 0;
            int numSuccesses = 0;
            while (i < 5) {
                if (numSuccesses < 2) {
                    // ensure that the first 2 go arounds always find the target node
                    Path currPath = graph.GraphSearch(src, dst, Algorithm.RANDOM_UNVISITED);
                    if (currPath.doesPathReachDestination() == true) {
                        i++;
                        numSuccesses++;
                        System.out.println("Attempt " + i + ": " + currPath.toString());
                    }
                }

                else {
                    Path currPath = graph.GraphSearch(src, dst, Algorithm.RANDOM_UNVISITED);
                    i++;
                    System.out.println("Attempt " + i + ": " + currPath.toString());
                    if (currPath.doesPathReachDestination() == true) {
                        numSuccesses++;
                    }
                }
            }
        }
        else if (s == "backtrack") {
            System.out.println("Backtrack Random Search:");

            int i = 0;
            int numSuccesses = 0;
            while (i < 5) {
                if (numSuccesses < 2) {
                    // ensure that the first 2 go arounds always find the target node
                    Path currPath = graph.GraphSearch(src, dst, Algorithm.RANDOM_BACKTRACK);
                    if (currPath.doesPathReachDestination() == true) {
                        i++;
                        numSuccesses++;
                        System.out.println("Attempt " + i + ": " + currPath.toString());
                    }
                }

                else {
                    Path currPath = graph.GraphSearch(src, dst, Algorithm.RANDOM_BACKTRACK);
                    i++;
                    System.out.println("Attempt " + i + ": " + currPath.toString());
                    if (currPath.doesPathReachDestination() == true) {
                        
                        numSuccesses++;
                    }
                }
            }
        }
        else if (s == "all") {
            runRandomSearch(graph, "full", src, dst);
            System.out.println();
            runRandomSearch(graph, "unvisited", src, dst);
            System.out.println();
            runRandomSearch(graph, "backtrack", src, dst);
            System.out.println();
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
            }
            else {
                graph = new DotGraph();
            }
            
            /*
            else {
                // or create new graph & add data manually
                graph.addNodes(new String[]{"A", "B", "C", "D"});
                graph.addEdge("A", "B");
                graph.addEdge("B", "C");
                graph.addEdge("C", "A");
                graph.addEdge("B", "D");
            }
            */

            // FOR DEMO: SET A SOURCE AND DESTINATION NODE (for the search algorithms)
            System.out.println();
            String startNode = "";
            String endNode = "";
            Scanner scanner = new Scanner(System.in);
            System.out.print("Name of Start Node: ");
            startNode = scanner.nextLine();
            System.out.print("Name of Destination Node: ");
            endNode = scanner.nextLine();

            System.out.println();

            // print graph to console
            System.out.println(graph.toString());

            // write text output to console
            graph.outputGraphToText("graph_output.txt");

            // write dot file
            graph.outputDOTGraph("graph_output.dot");

            // output image
            graph.outputGraphics("graph_output.png", "png");

            // print BFS path
            System.out.println("BFS Path:");
            System.out.println(graph.GraphSearch(startNode, endNode, Algorithm.BFS).getPathArray());
            System.out.println();

            // print DFS path
            System.out.println("DFS Path:");
            System.out.println(graph.GraphSearch(startNode, endNode, Algorithm.DFS).getPathArray());
            System.out.println();

            graph.runRandomSearch(graph, "all", startNode, endNode);

        // if it explodes :(
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
