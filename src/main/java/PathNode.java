import java.util.ArrayList;
import java.util.List;

public class PathNode {
    private String name;
    private String parent;
    private final List<PathNode> adjacent = new ArrayList<>();
    private boolean explored;

    public PathNode(String name) {
        this.name = name;
        this.parent = null;
        this.explored = false;
    }

    public String getName() {
        return name;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParent() {
        return parent;
    }

    public List<PathNode> getAdjacent() {
        return adjacent;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public void addAdjacent(PathNode node) {
        adjacent.add(node);
    }
}
