import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
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

    public class PathNode {
        private String name;
        private String parent;
        private Boolean explored = false;

        public PathNode() {
            name = "unnamed";
            parent = "parent";
            explored = false;
        }

        public PathNode(String name) {
            this.name = name;
            explored = false;
        }

        public String getName() {
            return this.name;
        }

        public String getParent() {
            return this.parent;
        }

        public Boolean getExplored() {
            return this.explored;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public void setExplored(Boolean explored) {
            this.explored = explored;
        }



    }

    // class representing a graph path
    public class Path {
        private List<PathNode> nodes;
        public Path() {
            
        }

        public Path(List<PathNode> nodes) {
            this.nodes = nodes;
        }
    
        public void addNode(PathNode newNode) {
            nodes.add(newNode);
        }

        public List<PathNode> getNodes() {
            return nodes;
        }
    
        @Override
        public String toString() {
            return nodes.stream()
                    .map(PathNode::getName)
                    .collect(Collectors.joining(" -> "));
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

    // bfs

    /*      BFS ALGORITHM (wikipedia pseudocode)
    
            1  procedure BFS(G, root) is
            2      let Q be a queue
            3      label root as explored
            4      Q.enqueue(root)
            5      while Q is not empty do
            6          v := Q.dequeue()
            7          if v is the goal then
            8              return v
            9          for all edges from v to w in G.adjacentEdges(v) do
            10              if w is not labeled as explored then
            11                  label w as explored
            12                  w.parent := v
            13                  Q.enqueue(w)


     */

    Path GraphSearch(String src, String dst) {
        Path returnPath = new Path();

        Queue <PathNode> q = new LinkedList<PathNode>();
        List<PathNode> allNodes = new ArrayList<PathNode>();

        List<String> allNodeNames = new ArrayList<>(nodes);

        for (int i = 0; i < allNodeNames.size(); i++) {
            allNodes.add(new PathNode(allNodeNames.get(i)));
        }

        PathNode root = allNodes.get(allNodeNames.indexOf(src));

        // label root as explored
        allNodes.get(allNodeNames.indexOf(src)).setExplored(    true);

        // label root as enqueued
        q.add(root);

        while (q.isEmpty() == false) {
            //  v := Q.dequeue()
            PathNode v = (PathNode) q.peek();
            returnPath.addNode(v);
            q.remove();

            //if v is the goal then { return v }
            if (v.name.equals(dst)) {
                return returnPath;
            }

            // for all edges from v to w in G.adjacentEdges(v) do
            for (int i = 0; i < edges.size(); i++) {

                // convert to array of edges for convenience
                Object[] tempEdges = edges.toArray();

                Edge w = (Edge) tempEdges[i];
                String s = w.getEnd();
                PathNode p = allNodes.get(allNodeNames.indexOf(s));

                // if w is not labeled as explored then
                if (p.getExplored() == false) {

                    // label w as explored
                    p.setExplored(true);
                    
                    // w.parent := v
                    p.setParent(v.name);
                    
                    // Q.enqueue(w)
                    q.add(p);
                }
            }
        }

        if (returnPath.nodes.size() <= 0) {
            return null;
        }
        else {
            return returnPath;
        }
    }

    // dfs search
    Path GraphSearch2(String src, String dst) {
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
            if (current.name.equals(dst)) {
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
                if (edge.getStart().equals(current.name)) {
                    String adjacentName = edge.getEnd();
                    PathNode adjacent = nodeMap.get(adjacentName);
                    if (!adjacent.getExplored()) {
                        adjacent.setExplored(true);
                        adjacent.setParent(current.name);
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
            graph.outputGraph("graph_output.txt");

            // write dot file
            graph.outputDOTGraph("graph_output.dot");

            // output image
            graph.outputGraphics("graph_output.png", "png");

            System.out.println(graph.GraphSearch("A", "C").toString());

            System.out.println("Graph outputs generated successfully!  Yay :)");

        // if it explodes :(
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
