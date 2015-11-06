package graph;

import java.util.ArrayList;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may add bodies to abstract methods, modify
 * existing bodies, or override inherited methods.  */

/** A directed graph with vertices labeled with VLABEL and edges
 *  labeled with ELABEL.
 *  @author Austin Gandy
 */
public class DirectedGraph<VLabel, ELabel> extends Graph<VLabel, ELabel> {

    /** An empty graph. */
    public DirectedGraph() {
        super();
    }

    /** Returns iterator over all outgoing edges from V. */
    @Override
    public Iteration<Edge> outEdges(Vertex v) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Edge edge : super.outEdges(v)) {
            if (edge.getV0().equals(v)) {
                edges.add(edge);
            }
        }
        return Iteration.iteration(edges.iterator());
    }

    @Override
    public boolean isDirected() {
        return true;
    }

}
