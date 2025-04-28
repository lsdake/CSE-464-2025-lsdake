
import java.util.List;
import java.util.stream.Collectors;

public class Path {
    private final List<PathNode> nodes;

    public Path(List<PathNode> nodes) {
        this.nodes = nodes;
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