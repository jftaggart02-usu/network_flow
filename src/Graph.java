import java.util.LinkedList;

public class Graph {
    private final GraphNode[] vertices;  // Adjacency list for graph.
    private final String name;  //The file from which the graph was created.
    private int[][] flow;  // Keep track of flow for all edges

    public Graph(String name, int vertexCount) {
        this.name = name;

        vertices = new GraphNode[vertexCount];
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertices[vertex] = new GraphNode(vertex);
        }

        // Initialize flow for all edges to 0
        flow = new int[vertexCount][vertexCount];
        for (int i = 0; i < flow.length; i++) {
            for (int j = 0; j < flow[i].length; j++) {
                flow[i][j] = 0;
            }
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
        GraphNode source = getVertex(s);
        GraphNode sink = getVertex(t);

        // Set total flow to zero
        int totalFlow = 0;

        // While there is an augmenting path
        while (hasAugmentingPath(s, t)) {

            // Set available flow to the largest possible integer
            int availableFlow = Integer.MAX_VALUE;

            // Keep track of augmenting path for display purposes
            var augmentingPath = new LinkedList<Integer>();

            // Find min available flow in augmenting path
            GraphNode v = sink;
            augmentingPath.addFirst(v.id);
            while (v != source) {

                // Get parent
                GraphNode p = getVertex(v.parent);

                // Get edge from parent to child
                GraphNode.EdgeInfo e = getEdge(p, v);

                // Update min flow in residual graph
                availableFlow = Math.min(availableFlow, getResidual(e));

                v = p;
                augmentingPath.addFirst(v.id);

            }

            // Update residual using available flow
            v = sink;
            while (v != source) {

                // Get parent
                GraphNode p = getVertex(v.parent);

                // Get edge from parent to child
                GraphNode.EdgeInfo e = getEdge(p, v);

                // Update min flow in residual graph
                updateResidual(e, availableFlow);

                v = p;

            }

            totalFlow += availableFlow;

            if (report) {
                System.out.print("Flow " + availableFlow + ":");
                for (Integer a : augmentingPath) {
                    System.out.print(" " + a);
                }
                System.out.println();
            }

        }

        if (report) {
            System.out.println();
            for (GraphNode v : vertices) {
                for (GraphNode.EdgeInfo e : v.successor) {
                    if (e.capacity > 0) {
                        int transport = flow[e.from][e.to];
                        if (transport > 0) {
                            System.out.printf("Edge(%d, %d) transports %d items\n", e.from, e.to, transport);
                        }
                    }
                }
            }
        }

        return totalFlow;
    }

    /**
     * Algorithm to find an augmenting path in a network
     */
    private boolean hasAugmentingPath(int s, int t) {
        GraphNode source = getVertex(s);
        GraphNode sink = getVertex(t);

        var q = new LinkedList<GraphNode>();

        // Reset parent of all vertices
        for (GraphNode v : vertices) {
            v.parent = -1;
        }

        // Add source vertex to the queue
        q.addLast(source);

        // Perform a breadth-first search and find the shortest path (in terms of number of edges) through the residual
        // graph with non-zero edge weights
        while (!q.isEmpty() && sink.parent < 0) {
            // Remove a vertex (v) from the queue
            GraphNode v = q.removeFirst();

            // Examine all of v's neighbors (w)
            for (GraphNode.EdgeInfo e : v.successor) {

                GraphNode w = getVertex(e.to);

                // If there is residual capacity from v to w, and w has not been visited (i.e. no parent) and w is not s
                // Add to the queue and mark as visited (i.e. keep track of parent)
                if (getResidual(e) > 0 && w.parent < 0 && w.id != source.id) {
                    q.addLast(w);
                    w.parent = v.id;
                }

            }
        }
        // If vertex t has a parent, then there is an augmenting path from s to t
        return sink.parent >= 0;
    }

    /**
     * Algorithm to find the min-cut edges in a network
     */
    public void findMinCut(int s) {

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

    private int getResidual(GraphNode.EdgeInfo e) {
        if (e.capacity == 0) {
            return flow[e.to][e.from];
        }

        if (e.capacity < 0) {
            System.out.println("Warning! Negative edge capacity encountered!");
        }

        return e.capacity - flow[e.from][e.to];
    }

    private void updateResidual(GraphNode.EdgeInfo e, int change) {
        if (e.capacity == 0) {
            flow[e.to][e.from] -= change;
        }

        if (e.capacity < 0) {
            System.out.println("Warning! Negative edge capacity encountered!");
        }

        flow[e.from][e.to] += change;
    }

    // Get edge from v1 to v2
    private GraphNode.EdgeInfo getEdge(GraphNode v1, GraphNode v2) {
        for (GraphNode.EdgeInfo e : v1.successor) {
            if (e.to == v2.id) {
                return e;
            }
        }
        return null;
    }

}
