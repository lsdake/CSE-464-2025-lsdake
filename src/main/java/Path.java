import java.util.List;
import java.util.stream.Collectors;

public class Path {
    private final List<PathNode> nodes;
    private boolean reachedDestination = false;
    private boolean done = false;

    public Path(List<PathNode> nodes) {
        this.nodes = nodes;
    }

    public List<PathNode> getNodes() {
        return nodes;
    }

    public void setDestinationReached() {
        reachedDestination = true;
    }

    public boolean doesPathReachDestination() {
        return reachedDestination;
    }

    @Override
    public String toString() {
        String base = nodes.stream()
            .map(PathNode::getName)
            .collect(Collectors.joining("->"));

        base = base.toLowerCase();

        if (reachedDestination == true) {
            base = base + " (Target node!)";
        }
        else {
            base = base + " (Dead end)";
        }
        return base;
    }

    public String getPathArray() {
        String body = nodes.stream()
            .map(n -> "Node{" + n.getName().toLowerCase() + "}")
            .collect(Collectors.joining(", "));
        return "Path{nodes=[" + body + "]}";
        
    }
}
