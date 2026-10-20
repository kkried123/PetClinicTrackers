import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CareGraph {
    private final Map<String, Set<String>> adjacency =
            new LinkedHashMap<>();

    public void addVertex(String id) {
        id = Animal.required(id);

        if (adjacency.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate vertex.");
        }

        adjacency.put(id, new LinkedHashSet<>());
    }

    public void connect(String a, String b) {
        check(a);
        check(b);

        if (a.equals(b)) {
            throw new IllegalArgumentException(
                    "A vertex cannot connect to itself."
            );
        }

        adjacency.get(a).add(b);
        adjacency.get(b).add(a);
    }

    public void disconnect(String a, String b) {
        check(a);
        check(b);
        adjacency.get(a).remove(b);
        adjacency.get(b).remove(a);
    }

    // Remove a vertex and every connection pointing to it.
    public void removeVertex(String id) {
        check(id);

        for (String neighbor : adjacency.get(id)) {
            adjacency.get(neighbor).remove(id);
        }

        adjacency.remove(id);
    }

    public List<String> neighbors(String id) {
        check(id);
        return new ArrayList<>(adjacency.get(id));
    }

    public int edgeCount() {
        int total = 0;

        for (Set<String> neighbors : adjacency.values()) {
            total += neighbors.size();
        }

        return total / 2;
    }

    private void check(String id) {
        if (!adjacency.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Unknown graph vertex."
            );
        }
    }
}