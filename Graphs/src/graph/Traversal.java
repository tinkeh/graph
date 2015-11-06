package graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular set of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.  The client can dictate an ordering on
 *  the fringe, determining which item is next removed, by which kind
 *  of traversal is requested.
 *     + A depth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at one end.  It also revisits the node
 *       itself after traversing all successors by calling the
 *       postVisit method on it.
 *     + A breadth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at different ends.  It also revisits the node
 *       itself after traversing all successors as for depth-first
 *       traversals.
 *     + A general traversal treats the fringe as an ordered set, as
 *       determined by a Comparator argument.  There is no postVisit
 *       for this type of traversal.
 *  As vertices are added to the fringe, the traversal calls a
 *  preVisit method on the vertex.
 *
 *  Generally, the client will extend Traversal, overriding the visit,
 *  preVisit, and postVisit methods, as desired (by default, they do nothing).
 *  Any of these methods may throw StopException to halt the traversal
 *  (temporarily, if desired).  The preVisit method may throw a
 *  RejectException to prevent a vertex from being added to the
 *  fringe, and the visit method may throw a RejectException to
 *  prevent its successors from being added to the fringe.
 *  @author Austin Gandy
 */
public class Traversal<VLabel, ELabel> {

    /** Perform a traversal of G over all vertices reachable from V.
     *  ORDER determines the ordering in which the fringe of
     *  untraversed vertices is visited.  The effect of specifying an
     *  ORDER whose results change as a result of modifications made during the
     *  traversal is undefined. */
    public void traverse(Graph<VLabel, ELabel> G,
                         Graph<VLabel, ELabel>.Vertex v,
                         Comparator<VLabel> order) {
        checkInfo(G, "generic");
        _lastTraversal = "generic";
        _lastOrder = order;
        Graph<VLabel, ELabel>.Vertex currVert = v;
        Graph<VLabel, ELabel>.Vertex nextVert;
        _currentOrder = order;
        Comparator<Graph<VLabel, ELabel>.Vertex> vertCompare =
                new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                    @Override
                    public int compare(Graph<VLabel, ELabel>.Vertex x1,
                            Graph<VLabel, ELabel>.Vertex x2) {
                        return _currentOrder.compare(x1.getLabel(),
                                x2.getLabel());
                    }
                };
        try {
            visit(v);
            _visited.add(v);
            _nonPost.add(v);
            PriorityQueue<Graph<VLabel, ELabel>.Vertex> fringe =
                    new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(5,
                            vertCompare);
            addAdjacent(G, v, fringe);
            while (_visited.size() < G.vertexSize() && fringe.size() > 0) {
                currVert = fringe.poll();
                visit(currVert);
                _visited.add(currVert);
                for (Graph<VLabel, ELabel>.Edge edge : G.edges(currVert)) {
                    nextVert = edge.getV(currVert);
                    try {
                        preVisit(edge, nextVert);
                    } catch (RejectException e) {
                        continue;
                    } catch (StopException e) {
                        _finalEdge = edge;
                        _finalVertex = null;
                        _graph = G;
                        return;
                    }
                    fringe.add(nextVert);
                }
            }
            checkPost(G);
            _visited.clear();
            _nonPost.clear();
            _posted.clear();
        } catch (StopException e) {
            _finalVertex = currVert;
            _finalEdge = null;
            _graph = G;
            return;
        }
    }

    /** Performs a depth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it or removed from it at one end in
     *  an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void depthFirstTraverse(Graph<VLabel, ELabel> G,
                                   Graph<VLabel, ELabel>.Vertex v) {
        _lastTraversal = "dft";
        _visited.clear();
        _justStopped = false;
        try {
            depthFirstTraverseHelper(G, v);
        } catch (StopException e) {
            return;
        }
    }

    /** Takes care of the recursion for the public version of
     * depthFirstTraverse. starts recursing on V then goes to the neighbors of V
     * in G. Recursively @throws StopException for up to the parent. */
    private void depthFirstTraverseHelper(Graph<VLabel, ELabel> G,
            Graph<VLabel, ELabel>.Vertex v) throws StopException {
        visit(v);
        _visited.add(v);
        for (Graph<VLabel, ELabel>.Edge edge : G.edges(v)) {
            try {
                preVisit(edge, edge.getV(v));
                if (!_visited.contains(edge.getV(v))) {
                    _visited.add(v);
                    depthFirstTraverseHelper(G, edge.getV(v));
                }
            } catch (RejectException e) {
                continue;
            } catch (StopException e) {
                if (_justStopped) {
                    _justStopped = false;
                    _finalEdge = edge;
                    _finalVertex = edge.getV(v);
                }
                throw new StopException();
            }
        }
        postVisit(v);
    }

    /** Performs a breadth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it at one end and removed from it at the
     *  other in an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void breadthFirstTraverse(Graph<VLabel, ELabel> G,
                                     Graph<VLabel, ELabel>.Vertex v) {
        checkInfo(G, "bft");
        Graph<VLabel, ELabel>.Vertex currVert = v;
        Graph<VLabel, ELabel>.Vertex nextVert;
        LinkedList<Graph<VLabel, ELabel>.Vertex> fringe =
                new LinkedList<Graph<VLabel, ELabel>.Vertex>();
        LinkedList<Boolean> shouldVisit = new LinkedList<Boolean>();
        try {
            visit(currVert);
            _visited.add(currVert);
            for (Graph<VLabel, ELabel>.Edge edge : G.edges(currVert)) {
                try {
                    preVisit(edge, edge.getV(edge.getV(currVert)));
                    fringe.add(edge.getV(currVert));
                    shouldVisit.add(true);
                } catch (RejectException e) {
                    continue;
                } catch (StopException e) {
                    _finalEdge = edge;
                    _finalVertex = null;
                }
            }
            fringe.add(currVert);
            shouldVisit.add(false);
            while (_visited.size() < G.vertexSize() && fringe.size() > 0) {
                currVert = fringe.poll();
                if (shouldVisit.poll()) {
                    addStuff(fringe, shouldVisit, currVert, false);
                } else if (successorsVisited(currVert, G)) {
                    postVisit(currVert);
                } else {
                    addStuff(fringe, shouldVisit, currVert, true);
                    continue;
                }
                for (Graph<VLabel, ELabel>.Edge edge : G.edges(currVert)) {
                    nextVert = edge.getV(currVert);
                    try {
                        if (!_visited.contains(nextVert)) {
                            preVisit(edge, nextVert);
                            fringe.add(nextVert);
                            shouldVisit.add(true);
                        }
                    } catch (RejectException e) {
                        continue;
                    } catch (StopException e) {
                        saveStuff(edge, currVert, G);
                        return;
                    }
                }
            }
            while (fringe.size() > 0) {
                postVisit(fringe.poll());
            }
            clearStuff();
        } catch (StopException e) {
            _finalVertex = currVert;
            _finalEdge = null;
            _graph = G;
        }
    }

    /** Saves _finaleEdge to EDGE, _finalVertex to VERT and _graph to G. */
    private void saveStuff(Graph<VLabel, ELabel>.Edge edge,
            Graph<VLabel, ELabel>.Vertex vert, Graph<VLabel, ELabel> G) {
        _finalEdge = edge;
        _finalVertex = vert;
        _graph = G;
    }
    /** takes in SHOULDVISIT and FRINGE linkedlists, and VERT and VISIT
     *  vals to add to these lists. Adds vals to lists. */
    private void addStuff(LinkedList<Graph<VLabel, ELabel>.Vertex> fringe,
            LinkedList<Boolean> shouldVisit,
            Graph<VLabel, ELabel>.Vertex vert, boolean visit) {
        fringe.add(vert);
        shouldVisit.add(visit);
        if (!visit) {
            visit(vert);
            _visited.add(vert);
        }
    }


    /** clears variables. */
    private void clearStuff() {
        _visited.clear();
        _nonPost.clear();
        _posted.clear();
    }

    /** returns true if all the successors of V in G have been visited. */
    private Boolean successorsVisited(Graph<VLabel, ELabel>.Vertex v,
            Graph<VLabel, ELabel> G) {
        for (Graph<VLabel, ELabel>.Vertex vert : G.successors(v)) {
            if (!_visited.contains(vert)) {
                return false;
            }
        }
        return true;
    }

    /** Clears instance variables if the previous call to a traversal left
     *  extraneous  information in our fields. This is the case if G is not
     *  equal to _graph, or if TRAVERSAL is different than _lastTraversal. */
    private void checkInfo(Graph<VLabel, ELabel> G, String traversal) {
        if ((!G.equals(null) && !G.equals(_graph))
                || (!_lastTraversal.equals(null)
                        && !_lastTraversal.equals(traversal))) {
            _nonPost.clear();
            _visited.clear();
        }
        _lastTraversal = traversal;
    }

    /** adds all adjacent vertices in G of V to FRINGE. */
    private void addAdjacent(Graph<VLabel, ELabel> G,
            Graph<VLabel, ELabel>.Vertex v,
            PriorityQueue<Graph<VLabel, ELabel>.Vertex> fringe) {
        for (Graph<VLabel, ELabel>.Vertex vert : G.neighbors(v)) {
            fringe.add(vert);
        }
    }

    /** Iterates through visited vertices, calls postVisit on any vertices whose
     *  neighbors in G have already been visit. */
    private void checkPost(Graph<VLabel, ELabel> G) {
        ArrayList<Graph<VLabel, ELabel>.Vertex> delete =
                new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        for (Graph<VLabel, ELabel>.Vertex v : _nonPost) {
            if (_posted.containsAll(toList(G.successors(v)))
                    || _visited.containsAll(toList(G.successors(v)))) {
                postVisit(v);
                _posted.add(v);
                delete.add(v);
            }
        }
        for (Graph<VLabel, ELabel>.Vertex del : delete) {
            _nonPost.remove(del);
        }
    }

    /** Changes an Iteration ITER to a list and returns that list. */
    private ArrayList<Graph<VLabel, ELabel>.Vertex> toList(
            Iteration<Graph<VLabel, ELabel>.Vertex> iter) {
        ArrayList<Graph<VLabel, ELabel>.Vertex> list =
                new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        for (Graph<VLabel, ELabel>.Vertex v : iter) {
            list.add(v);
        }
        return list;
    }

    /** Continue the previous traversal starting from V.
     *  Continuing a traversal means that we do not traverse
     *  vertices that have been traversed previously. */
    public void continueTraversing(Graph<VLabel, ELabel>.Vertex v) {
        switch (_lastTraversal) {
        case "bft":
            breadthFirstTraverse(_graph, v);
            break;
        case "dft":
            depthFirstTraverse(_graph, v);
            break;
        default:
            traverse(_graph, v, _lastOrder);
        }
    }

    /** If the traversal ends prematurely, returns the Vertex argument to
     *  preVisit, visit, or postVisit that caused a Visit routine to
     *  return false.  Otherwise, returns null. */
    public Graph<VLabel, ELabel>.Vertex finalVertex() {
        return _finalVertex;
    }

    /** If the traversal ends prematurely, returns the Edge argument to
     *  preVisit that caused a Visit routine to return false. If it was not
     *  an edge that caused termination, returns null. */
    public Graph<VLabel, ELabel>.Edge finalEdge() {
        return _finalEdge;
    }

    /** Returns the last graph argument to a traverse routine, or null if none
     *  of these methods have been called. */
    protected Graph<VLabel, ELabel> theGraph() {
        return _graph;
    }

    /** Method to be called when adding the node at the other end of E from V0
     *  to the fringe. If this routine throws a StopException,
     *  the traversal ends.  If it throws a RejectException, the edge
     *  E is not traversed. The default does nothing.
     */
    protected void preVisit(Graph<VLabel, ELabel>.Edge e,
                            Graph<VLabel, ELabel>.Vertex v0) {
    }

    /** Method to be called when visiting vertex V.  If this routine throws
     *  a StopException, the traversal ends.  If it throws a RejectException,
     *  successors of V do not get visited from V. The default does nothing. */
    protected void visit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** Method to be called immediately after finishing the traversal
     *  of successors of vertex V in pre- and post-order traversals.
     *  If this routine throws a StopException, the traversal ends.
     *  Throwing a RejectException has no effect. The default does nothing.
     */
    protected void postVisit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** The Vertex (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Vertex _finalVertex;
    /** The Edge (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Edge _finalEdge;
    /** The last graph traversed. */
    protected Graph<VLabel, ELabel> _graph;
    /** String that indicates which traversal was the last one called. Important
     *  for continueTraversal. */
    private String _lastTraversal;
    /** ArrayList of already visited vertices. */
    private ArrayList<Graph<VLabel, ELabel>.Vertex> _visited =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
    /** ArrayList of vertices that have been visited, but haven't been
     * postVisited. */
    private ArrayList<Graph<VLabel, ELabel>.Vertex> _nonPost =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
    /** Comparator used to order a generic traversal. */
    private Comparator<VLabel> _lastOrder;
    /** List of vertices that have been postVisited. */
    private ArrayList<Graph<VLabel, ELabel>.Vertex> _posted =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
    /** the current ordering for _edges. */
    private Comparator<VLabel> _currentOrder;
    /** boolean used to determine whether or not to reset _finalEdge and
     * _finalVertex when recursing through a depth first traversal. */
    private Boolean _justStopped;

}
