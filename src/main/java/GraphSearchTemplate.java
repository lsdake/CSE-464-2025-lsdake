import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// template for graph search algorithms

public abstract class GraphSearchTemplate {
    protected DotGraph graph;
    protected Map<String, PathNode> nodeMap;
    protected Deque<PathNode> frontier;

    
    // template method, defines the skeleton of the graph search
    
    public Path search(DotGraph graph, String src, String dst) {
        this.graph = graph;
        initNodeMap();
        frontier = initFrontier();

        PathNode start = nodeMap.get(src);
        start.setExplored(true);
        frontierAdd(start);

        while (!frontier.isEmpty()) {
            PathNode current = frontierRemove();
            if (current.getName().equals(dst)) {
                return buildPath(current);
            }
            for (String neighbor : graph.getNeighbors(current.getName())) {
                PathNode adj = nodeMap.get(neighbor);
                if (!adj.isExplored()) {
                    adj.setExplored(true);
                    adj.setParent(current.getName());
                    frontierAdd(adj);
                }
            }
        }
        return null;
    }

    // init the nodeMap from the graph nodes
    private void initNodeMap() {
        nodeMap = new HashMap<>();
        for (String name : graph.getNodes()) {
            nodeMap.put(name, new PathNode(name));
        }
    }

    // rebuilds the path by going from child to parent till it reaches the end
    private Path buildPath(PathNode dstNode) {
        List<PathNode> path = new ArrayList<>();
        for (PathNode at = dstNode; at != null; at = nodeMap.get(at.getParent())) {
            path.add(at);
        }
        Collections.reverse(path);
        return new Path(path);
    }

    // operations that subclasses implement:
    protected abstract Deque<PathNode> initFrontier();
    protected abstract void frontierAdd(PathNode node);
    protected abstract PathNode frontierRemove();
}