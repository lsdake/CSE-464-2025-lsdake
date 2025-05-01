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

        // handling lowercase inputs Just In Case lol
        src = src.toUpperCase();
        dst = dst.toUpperCase();

        this.graph = graph;
        nodeMap = initNodeMap();
        frontier = initFrontier();

        PathNode start = nodeMap.get(src);
        start.setExplored(true);
        frontierAdd(start);
        PathNode current = start;

        while (!frontier.isEmpty()) {
            current = frontierRemove();

            // for printing for demo
            Path prefix = buildPath(current);
            onVisit(prefix.getPathArray());

            if (current.getName().equals(dst)) {
                Path p = buildPath(current);
                p.setDestinationReached();
                return p;
            }

            // collect visited names for planNext
            List<String> visited = new ArrayList<>();
            for (PathNode pn : frontier) visited.add(pn.getName());
            visited.add(current.getName());

            for (String neighbor : planNext(current.getName(), visited)) {
                PathNode adj = nodeMap.get(neighbor.toUpperCase());
                if (adj != null) {
                    if (!adj.isExplored()) {
                        adj.setExplored(true);
                        adj.setParent(current.getName());
                        frontierAdd(adj);
                    }
                }
                else {
                    return buildPath(current);
                }
            }
        }
        return buildPath(current);
    }

    protected List<String> planNext(String current, List<String> visitedNames) {
        return graph.getNeighbors(current);
    }

    // init the nodeMap from the graph nodes
    private Map<String, PathNode> initNodeMap() {
        Map<String, PathNode> map = new HashMap<>();
        for (String name : graph.getNodes()) {

            name = name.toUpperCase();
            map.put(name, new PathNode(name));
        }
        return map;
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
    
    protected void onVisit(String pathSoFar) {
        // no operation by default
    }
}
