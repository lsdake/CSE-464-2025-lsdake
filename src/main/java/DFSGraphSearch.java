import java.util.ArrayDeque;
import java.util.Deque;

// DFS implementation using the template pattern

public class DFSGraphSearch extends GraphSearchTemplate {
    @Override
    protected Deque<PathNode> initFrontier() {
        return new ArrayDeque<>();
    }

    @Override
    protected void frontierAdd(PathNode node) {
        frontier.addFirst(node);
    }

    @Override
    protected PathNode frontierRemove() {
        return frontier.removeFirst();
    }
}
