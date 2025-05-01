import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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
    
    @Override
    protected List<String> planNext(String current, List<String> visitedNames) {
        return super.planNext(current, visitedNames);
    }

    @Override
    protected void onVisit(String pathSoFar) {
        System.out.println("visiting " + pathSoFar);
    }
}
