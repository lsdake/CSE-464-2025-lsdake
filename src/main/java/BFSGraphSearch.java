// BFS implementation using the template pattern
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;          // ← add this

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

    @Override
    protected List<String> planNext(String current, List<String> visitedNames) {
        return super.planNext(current, visitedNames);
    }

    @Override
    protected void onVisit(String pathSoFar) {
        // printing for demo
        System.out.println("visiting " + pathSoFar);
    }
}
