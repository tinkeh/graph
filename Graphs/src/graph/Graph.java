package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may make changes that don't affect the API as seen
 * from outside the graph package:
 *   + You may make methods in Graph abstract, if you want different
 *     implementations in DirectedGraph and UndirectedGraph.
 *   + You may add bodies to abstract methods, modify existing bodies,
 *     or override inherited methods.
 *   + You may change parameter names, or add 'final' modifiers to parameters.
 *   + You may private and package private members.
 *   + You may add additional non-public classes to the graph package.
 */

/** Represents a general graph whose vertices are labeled with a type
 *  VLABEL and whose edges are labeled with a type ELABEL. The
 *  vertices are represented by the inner type Vertex and edges by
 *  inner type Edge.  A graph may be directed or undirected.  For
 *  an undirected graph, outgoing and incoming edges are the same.
 *  Graphs may have self edges and may have multiple edges between vertices.
 *
 *  The vertices and edges of the graph, the edges incident on a
 *  vertex, and the neighbors of a vertex are all accessible by
 *  iterators.  Changing the graph's structure by adding or deleting
 *  edges or vertices invalidates these iterators (subsequent use of
 *  them is undefined.)
 *  @author Austin Gandy
 */
public abstract class Graph<VLabel, ELabel> {

    /** Initializes a new Graph. */
    public Graph() {
        _everything = new HashMap<Vertex, ArrayList<Edge>>();
    }

    /** Represents one of my vertices. */
    public class Vertex {

        /** A new vertex with LABEL as the value of getLabel(). */
        Vertex(VLabel label) {
            _label = label;
        }

        /** Returns the label on this vertex. */
        public VLabel getLabel() {
            return _label;
        }

        @Override
        public String toString() {
            return String.valueOf(_label);
        }

        /** The label on this vertex. */
        private final VLabel _label;

    }

    /** Represents one of my edges. */
    public class Edge {

        /** An edge (V0,V1) with label LABEL.  It is a directed edge (from
         *  V0 to V1) in a directed graph. */
        Edge(Vertex v0, Vertex v1, ELabel label) {
            _label = label;
            _v0 = v0;
            _v1 = v1;
        }

        /** Returns the label on this edge. */
        public ELabel getLabel() {
            return _label;
        }

        /** Return the vertex this edge exits. For an undirected edge, this is
         *  one of the incident vertices. */
        public Vertex getV0() {
            return _v0;
        }

        /** Return the vertex this edge enters. For an undirected edge, this is
         *  the incident vertices other than getV1(). */
        public Vertex getV1() {
            return _v1;
        }

        /** Returns the vertex at the other end of me from V.  */
        public final Vertex getV(Vertex v) {
            if (v == _v0) {
                return _v1;
            } else if (v == _v1) {
                return _v0;
            } else {
                throw new
                    IllegalArgumentException("Vertex not incident to edge");
            }
        }

        @Override
        public String toString() {
            return String.format("(%s,%s):%s", _v0, _v1, _label);
        }

        /** Endpoints of this edge.  In directed edges, this edge exits _V0
         *  and enters _V1. */
        private final Vertex _v0, _v1;

        /** The label on this edge. */
        private final ELabel _label;

    }

    /*=====  Methods and variables of Graph =====*/

    /** Returns the number of vertices in me. */
    public int vertexSize() {
        return _everything.keySet().size();
    }

    /** Returns the number of edges in me. */
    public int edgeSize() {
        int size = 0;
        for (ArrayList<Edge> edges : _everything.values()) {
            size += edges.size();
        }
        return Integer.valueOf(size / 2);
    }

    /** Returns true iff I am a directed graph. */
    public abstract boolean isDirected();

    /** Returns the number of outgoing edges incident to V. Assumes V is one of
     *  my vertices.  */
    public int outDegree(Vertex v) {
        int degree = 0;
        for (Edge edge : _everything.get(v)) {
            if (edge.getV0().equals(v)) {
                degree += 1;
            }
        }
        return degree;
    }

    /** Returns the number of incoming edges incident to V. Assumes V is one of
     *  my vertices. */
    public int inDegree(Vertex v) {
        int degree = 0;
        ArrayList<Edge> edges = _everything.get(v);
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getV1().equals(v)) {
                    degree += 1;
                }
            }
            return degree;
        } else {
            return 0;
        }

    }

    /** Returns outDegree(V). This is simply a synonym, intended for
     *  use in undirected graphs. */
    public final int degree(Vertex v) {
        return _everything.get(v).size();
    }

    /** Returns true iff there is an edge (U, V) in me with any label. */
    public boolean contains(Vertex u, Vertex v) {
        ArrayList<Edge> edges = _everything.get(u);
        if (edges != null) {
            for (Edge edge : _everything.get(u)) {
                if (edge.getV0().equals(v) || edge.getV1().equals(v)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /** Returns true iff there is an edge (U, V) in me with label LABEL. */
    public boolean contains(Vertex u, Vertex v,
                            ELabel label) {
        for (Edge edge : _everything.get(u)) {
            if ((edge.getV0().equals(v) || edge.getV1().equals(v))
                    && edge.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }

    /** Returns a new vertex labeled LABEL, and adds it to me with no
     *  incident edges. */
    public Vertex add(VLabel label) {
        Vertex vertex = new Vertex(label);
        _everything.put(vertex, new ArrayList<Edge>());
        return vertex;
    }

    /** Returns an edge incident on FROM and TO, labeled with LABEL
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to,
                    ELabel label) {
        Edge edge = new Edge(from, to, label);
        _edges.add(edge);
        ArrayList<Edge> edgeListOne = _everything.get(from);
        ArrayList<Edge> edgeListTwo = _everything.get(to);
        edgeListOne.add(edge);
        edgeListTwo.add(edge);
        _everything.put(from, edgeListOne);
        _everything.put(to, edgeListTwo);
        return edge;
    }

    /** Returns an edge incident on FROM and TO with a null label
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to) {
        return add(from, to, null);
    }

    /** Remove V and all adjacent edges, if present. */
    public void remove(Vertex v) {
        ArrayList<Edge> removing;
        for (Edge edge : _everything.get(v)) {
            if (edge.getV0().equals(v)) {
                removing = _everything.get(edge.getV1());
                removing.remove(edge);
                _edges.remove(edge);
                _everything.put(edge.getV1(), removing);
            } else {
                removing = _everything.get(edge.getV0());
                removing.remove(edge);
                _edges.remove(edge);
                _everything.put(edge.getV1(), removing);
            }
        }
        _everything.remove(v);
    }

    /** Remove E from me, if present.  E must be between my vertices,
     *  or the result is undefined.  */
    public void remove(Edge e) {
        if (_edges.contains(e)) {
            _edges.remove(e);
            Vertex v1 = e.getV1();
            Vertex v0 = e.getV0();
            ArrayList<Edge> oneEdges = _everything.get(v1);
            ArrayList<Edge> zeroEdges = _everything.get(v0);
            oneEdges.remove(e);
            zeroEdges.remove(e);
            _everything.put(v1, oneEdges);
            _everything.put(v0, zeroEdges);
        }
    }

    /** Remove all edges from V1 to V2 from me, if present.  The result is
     *  undefined if V1 and V2 are not among my vertices.  */
    public void remove(Vertex v1, Vertex v2) {
        ArrayList<Edge> removing;
        ArrayList<Edge> toRemove = new ArrayList<Edge>();
        for (Edge e : _everything.get(v1)) {
            if (e.getV(v1).equals(v2)) {
                removing = _everything.get(v2);
                removing.remove(e);
                _edges.remove(e);
                toRemove.add(e);
                _everything.put(v2, removing);
            }
        }
        removing = _everything.get(v1);
        for (Edge e : toRemove) {
            removing.remove(e);
            _edges.remove(e);
        }
        _everything.put(v1, removing);
    }

    /** Returns an Iterator over all vertices in arbitrary order. */
    public Iteration<Vertex> vertices() {
        return Iteration.iteration(_everything.keySet().iterator());
    }

    /** Returns an iterator over all successors of V. */
    public Iteration<Vertex> successors(Vertex v) {
        ArrayList<Vertex> successors = new ArrayList<Vertex>();
        for (Edge edge : _everything.get(v)) {
            if (edge.getV0().equals(v)) {
                successors.add(edge.getV1());
            }
        }
        return Iteration.iteration(successors.iterator());
    }

    /** Returns an iterator over all predecessors of V. */
    public Iteration<Vertex> predecessors(Vertex v) {
        ArrayList<Vertex> predecessors = new ArrayList<Vertex>();
        for (Edge edge : _everything.get(v)) {
            if (edge.getV1().equals(v)) {
                predecessors.add(edge.getV0());
            }
        }
        return Iteration.iteration(predecessors.iterator());
    }

    /** Returns successors(V).  This is a synonym typically used on
     *  undirected graphs. */
    public final Iteration<Vertex> neighbors(Vertex v) {
        return successors(v);
    }

    /** Returns an iterator over all edges in me. */
    public Iteration<Edge> edges() {
        return Iteration.iteration(_edges);
    }

    /** Returns iterator over all outgoing edges from V. */
    public Iteration<Edge> outEdges(Vertex v) {
        if (isDirected()) {
            ArrayList<Edge> edges = new ArrayList<Edge>();
            ArrayList<Edge> allEdges = _everything.get(v);
            if (allEdges != null) {
                for (Edge edge : allEdges) {
                    if (edge.getV0().equals(v)) {
                        edges.add(edge);
                    }
                }
                return Iteration.iteration(edges.iterator());
            } else {
                return Iteration.iteration(new ArrayList<Edge>());
            }
        } else {
            return Iteration.iteration(_everything.get(v));
        }
    }

    /** Returns iterator over all incoming edges to V. */
    public Iteration<Edge> inEdges(Vertex v) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Edge edge : _everything.get(v)) {
            if (edge.getV1().equals(v)) {
                edges.add(edge);
            }
        }
        return Iteration.iteration(edges.iterator());
    }

    /** Returns outEdges(V). This is a synonym typically used
     *  on undirected graphs. */
    public final Iteration<Edge> edges(Vertex v) {
        return outEdges(v);
    }

    /** Returns the natural ordering on T, as a Comparator.  For
     *  example, if stringComp = Graph.<Integer>naturalOrder(), then
     *  stringComp.compare(x1, y1) is <0 if x1<y1, ==0 if x1=y1, and >0
     *  otherwise. */
    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder()
    {
        return new Comparator<T>() {
            @Override
            public int compare(T x1, T x2) {
                return x1.compareTo(x2);
            }
        };
    }

    /** Cause subsequent calls to edges() to visit or deliver
     *  edges in sorted order, according to COMPARATOR. Subsequent
     *  addition of edges may cause the edges to be reordered
     *  arbitrarily.  */
    public void orderEdges(Comparator<ELabel> comparator) {
        final Comparator<ELabel> comp = comparator;
        Comparator<Edge> edgeCompare = new Comparator<Edge>() {
            @Override
            public int compare(Edge x1, Edge x2) {
                return comp.compare(x1.getLabel(), x2.getLabel());
            }
        };
        Collections.sort(_edges, edgeCompare);
    }

    /** HashMap with vertices as keys and adjacent edges as values. */
    private HashMap<Vertex, ArrayList<Edge>> _everything;

    /** ArrayList that stores all of the edges in me. */
    private ArrayList<Edge> _edges = new ArrayList<Edge>();

}
