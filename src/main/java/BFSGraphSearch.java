import java.util.Deque;
import java.util.LinkedList;

// BFS implementation using the template pattern

public class BFSGraphSearch extends GraphSearchTemplate {
    @Override
    protected Deque<PathNode> initFrontier() {
        return new LinkedList<>();
    }

    @Override
    protected void frontierAdd(PathNode node) {
        frontier.addLast(node);
    }

    @Override
    protected PathNode frontierRemove() {
        return frontier.removeFirst();
    }
}