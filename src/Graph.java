import java.util.LinkedList;

public class Graph {
    private final GraphNode[] vertices;  // Adjacency list for graph.
    private final String name;  //The file from which the graph was created.

    public Graph(String name, int vertexCount) {
        this.name = name;

        vertices = new GraphNode[vertexCount];
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertices[vertex] = new GraphNode(vertex);
        }
    }

    public boolean addEdge(int source, int destination, int capacity) {
        // A little bit of validation
        if (source < 0 || source >= vertices.length) return false;
        if (destination < 0 || destination >= vertices.length) return false;

        // This adds the actual requested edge, along with its capacity
        vertices[source].addEdge(source, destination, capacity);

        // TODO: This is what you have to describe in the required README.TXT file
        //       that you submit as part of this assignment.
        vertices[destination].addEdge(destination, source, 0);

        return true;
    }

    /**
     * Algorithm to find max-flow in a network
     */
    public int findMaxFlow(int s, int t, boolean report) {
        // Set total flow to zero
        int totalFlow = 0;

        // While there is an augmenting path
        while (hasAugmentingPath(s, t)) {

            // Set the available flow to the largest possible integer that Java can represent
            int availableFlow = Integer.MAX_VALUE;

            // Follow the augmenting path from t to s; using vertex v as the current vertex
            // Set available flow to the minimum capacity of an edge along the path from s to t
            int v = t;
            while (v != s) {

                // Get the edge from the parent of v to v
                // Set available flow to min of available flow and edge capacity
                int p = getVertex(v).parent;
                for (GraphNode.EdgeInfo e : getVertex(p).successor) {
                    if (e.to == v) {
                        availableFlow = Math.min(availableFlow, e.capacity);
                    }
                }

                // Update v
                v = p;

            }

            // Follow the augmenting path from t to s; using vertex v as the current vertex
            // Update the residual graph
            // Subtract available flow in direction of s to t
            // Add available flow in direction of t to s
            v = t;
            while (v != s) {

                // Get parent
                int p = getVertex(v).parent;

                // Get the edge in direction of s to t and subtract available flow
                for (GraphNode.EdgeInfo e: getVertex(p).successor) {
                    if (e.to == v) {
                        e.capacity -= availableFlow;
                    }
                }

                // Get the edge in direction of t to s and add available flow
                for (GraphNode.EdgeInfo e : getVertex(v).successor) {
                    if (e.to == p) {
                        e.capacity += availableFlow;
                    }
                }

                // Update v
                v = p;

            }

            // Add available flow to total flow
            totalFlow += availableFlow;

        }

        return totalFlow;
    }

    /**
     * Algorithm to find an augmenting path in a network
     */
    private boolean hasAugmentingPath(int s, int t) {
        var q = new LinkedList<Integer>();

        // Reset parent of all vertices
        for (GraphNode v : vertices) {
            v.parent = -1;
        }

        // Add s to the queue
        q.addFirst(s);

        // While queue is not empty and vertex t does not have a parent
        while (!q.isEmpty() && getVertex(t).parent < 0) {
            // Remove from queue as vertex v
            int v = q.removeFirst();

            // For all successor edges from v
            for (GraphNode.EdgeInfo e : getVertex(v).successor) {
                // For the edge, call the other vertex w
                int w = e.to;
                // If there is residual capacity from v to w and not already part of the augmenting path, and it isn't vertex s, then it can be used
                if (e.capacity > 0 && getVertex(w).parent < 0 && w != s) {
                    // Remember the path; set parent of w to v
                    getVertex(w).parent = v;
                    // Add w to the queue
                    q.addFirst(w);
                }
            }
        }
        // If vertex t has a parent, then there is an augmenting path from s to t
        return getVertex(t).parent >= 0;
    }

    /**
     * Algorithm to find the min-cut edges in a network
     */
    public void findMinCut(int s) {
        // TODO:
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("The Graph " + name + " \n");
        for (var vertex : vertices) {
            sb.append((vertex.toString()));
        }
        return sb.toString();
    }

    private GraphNode getVertex(int id) {
        for (GraphNode v : vertices) {
            if (v.id == id) {
                return v;
            }
        }
        return null;
    }
}
