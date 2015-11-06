package graph;

import java.util.HashSet;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may add bodies to abstract methods, modify
 * existing bodies, or override inherited methods.  */

/** An undirected graph with vertices labeled with VLABEL and edges
 *  labeled with ELABEL.
 *  @author Austin Gandy
 */
public class UndirectedGraph<VLabel, ELabel> extends Graph<VLabel, ELabel> {

    /** An empty graph. */
    public UndirectedGraph() {
        super();
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public Iteration<Edge> inEdges(Vertex v) {
        return super.outEdges(v);
    }

    /*@Override
    public Iteration<Edge> outEdges(Vertex v) {
        HashSet<Edge> edges = new HashSet<Edge>();
        for (Edge edge : super.outEdges(v)) {
            edges.add(edge);
        }
        for (Edge edge : super.inEdges(v)) {
            edges.add(edge);
        }
        return Iteration.iteration(edges);
    }*/

    @Override
    /** Returns an iterator over all successors of V. */
    public Iteration<Vertex> successors(Vertex v) {
        HashSet<Vertex> successors = new HashSet<Vertex>();
        for (Edge edge : super.outEdges(v)) {
            successors.add(edge.getV(v));
        }
        for (Edge edge : super.inEdges(v)) {
            successors.add(edge.getV(v));
        }
        return Iteration.iteration(successors.iterator());
    }

    @Override
    /** Returns an iterator over all predecessors of V. */
    public Iteration<Vertex> predecessors(Vertex v) {
        return successors(v);
    }

    @Override
    /** Returns the number of outgoing edges incident to V. Assumes V is one of
     *  my vertices.  */
    public int outDegree(Vertex v) {
        return super.inDegree(v) + super.outDegree(v);
    }

    @Override
    /** Returns the number of incoming edges incident to V. Assumes V is one of
     *  my vertices. */
    public int inDegree(Vertex v) {
        return outDegree(v);
    }
}
