import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

// Random walk using the template pattern with built-in variations

public class RWGraphSearch extends GraphSearchTemplate {
    public enum Mode {
        FULLY_RANDOM,
        RANDOM_UNVISITED,
        HISTORY_BACKTRACK
    }

    private final Mode mode;
    private static final Random rnd = new Random();

    public RWGraphSearch(Mode mode) {
        super();
        this.mode = mode;
    }

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

    // Template calls planNext(...) to get possible neighbors to enqueue.
    @Override
    protected List<String> planNext(String current, List<String> visitedNames) {
        List<String> nbrs = new ArrayList<>(graph.getNeighbors(current));
        switch (mode) {
            case FULLY_RANDOM:
                if (nbrs.isEmpty()) return Collections.emptyList();
                return Collections.singletonList(
                    nbrs.get(rnd.nextInt(nbrs.size()))
                );

            case RANDOM_UNVISITED:
                List<String> unvisited = new ArrayList<>();
                for (String n : nbrs) {
                    if (!visitedNames.contains(n)) {
                        unvisited.add(n);
                    }
                }
                if (unvisited.isEmpty()) return Collections.emptyList();
                return Collections.singletonList(
                    unvisited.get(rnd.nextInt(unvisited.size()))
                );

            case HISTORY_BACKTRACK:
                List<String> unvis = new ArrayList<>();
                for (String n : nbrs) {
                    if (!visitedNames.contains(n)) unvis.add(n);
                }
                if (!unvis.isEmpty()) {
                    return Collections.singletonList(
                        unvis.get(rnd.nextInt(unvis.size()))
                    );
                }
                // backtrack
                return Collections.emptyList();

            default:
                return Collections.emptyList();
        }
    }
}
