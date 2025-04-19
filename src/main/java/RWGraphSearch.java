import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

// Concrete Random Walk implementation using template and strategy patterns.
// At each step, selects one random neighbor to continue.
public class RWGraphSearch extends GraphSearchTemplate {
    private final Random random = new Random();

    @Override
    protected Deque<PathNode> initFrontier() {
        return new ArrayDeque<>();
    }

    @Override
    protected void frontierAdd(PathNode node) {
        frontier.clear();
        frontier.addFirst(node);
    }

    @Override
    protected PathNode frontierRemove() {
        return frontier.removeFirst();
    }

    protected List<String> getNeighbors(String node) {
        List<String> all = graph.getNeighbors(node);
        if (all.isEmpty()) return Collections.emptyList();
        String next = all.get(random.nextInt(all.size()));
        return Collections.singletonList(next);
    }
}