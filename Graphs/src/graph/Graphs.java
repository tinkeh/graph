package graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/** Assorted graph algorithms.
 *  @author Austin Gandy
 */
public final class Graphs {

    /** A class to hold information on already visited nodes. */
    private static class Node<VLabel, ELabel> {

        /** Passes VERTEX, PATH, DISTANCECOST, and HEURISTIC into me. */
        Node(Graph<VLabel, ELabel>.Vertex vertex,
                ArrayList<Graph<VLabel, ELabel>.Edge> path,
                double distanceCost, double heuristic) {
            _vertex = vertex;
            _path = path;
            _distanceCost = distanceCost;
            _heuristic = heuristic;
            _totalCost = _distanceCost + _heuristic;
            _vweighterSet = false;
        }

        /** return _vertex. */
        public Graph<VLabel, ELabel>.Vertex getVertex() {
            return _vertex;
        }

        /** sets _vweighter to VWEIGHTER. */
        public void setVweighter(Weighter<? super VLabel> vweighter) {
            _vweighter = vweighter;
            _vweighterSet = true;
        }
        /** returns _vweighter. */
        public Weighter<? super VLabel> getVweighter() {
            return _vweighter;
        }
        /** returns _distanceCost. */
        public double getDistanceCost() {
            return _distanceCost;
        }

        /** Sets the weight of the label of _vertex to WEIGHT. */
        public void setWeight(double weight) {
            if (_vweighterSet) {
                _vweighter.setWeight(_vertex.getLabel(), weight);
            }
        }

        /** returns _totalCost. */
        public double getTotalCost() {
            return _totalCost;
        }

        /** returns _path. */
        public ArrayList<Graph<VLabel, ELabel>.Edge> getPath() {
            return _path;
        }

        /** Compares the vertex of me to the vertex of NODE. Returns true if
         *  these are the same. */
        public boolean sameLocation(Node<VLabel, ELabel> node) {
            return _vertex.equals(node.getVertex());
        }

        /** the vertex of this node. */
        private Graph<VLabel, ELabel>.Vertex _vertex;
        /** the list of edges used to get to me. */
        private ArrayList<Graph<VLabel, ELabel>.Edge> _path =
                new ArrayList<Graph<VLabel, ELabel>.Edge>();
        /** Cost to get from the start to me. */
        private double _distanceCost;
        /** heuristic value from me to the goal vertex. */
        private double _heuristic;
        /** sum of distanceCost and heuristic. */
        private double _totalCost;
        /** vweighter of _vertex. */
        private Weighter<? super VLabel> _vweighter;
        /** true iff vweighter has been set. */
        private boolean _vweighterSet;
    }

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the edge weighter EWEIGHTER.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, uses VWEIGHTER to set the weight of vertex v
     *  to the weight of a minimal path from V0 to v, for each v in
     *  the returned path and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *              < minimum path length from V0 to V1. //why is this not <=?
     *  The final weights of other vertices are not defined.  If V1 is
     *  unreachable from V0, returns null and sets the minimum path weights of
     *  all reachable nodes.  The distance to a node unreachable from V0 is
     *  Double.POSITIVE_INFINITY. */
    public static <VLabel, ELabel> List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 Graph<VLabel, ELabel>.Vertex V1,
                 Distancer<? super VLabel> h,
                 Weighter<? super VLabel> vweighter,
                 Weighting<? super ELabel> eweighter) {
        HashSet<Node<VLabel, ELabel>> closed =
                new HashSet<Node<VLabel, ELabel>>();
        Comparator<Node<VLabel, ELabel>> compare =
                new Comparator<Node<VLabel, ELabel>>() {

                    @Override
                    public int compare(Node<VLabel, ELabel> x1,
                                Node<VLabel, ELabel> x2) {
                        if (x1.getTotalCost() < x2.getTotalCost()) {
                            return 1;
                        } else if (x1.getTotalCost() > x2.getTotalCost()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                };

        PriorityQueue<Node<VLabel, ELabel>> open =
                new PriorityQueue<Node<VLabel, ELabel>>(0, compare);
        Node<VLabel, ELabel> firstNode = new Node<VLabel, ELabel>(V0,
                new ArrayList<Graph<VLabel, ELabel>.Edge>(), 0.0,
                h.dist(V0.getLabel(), V1.getLabel()));
        Node<VLabel, ELabel> currentNode = null;
        firstNode.setVweighter(vweighter);
        open.add(firstNode);
        Node<VLabel, ELabel> nextNode;
        while (open.size() > 0) {
            currentNode = open.poll();
            if (currentNode.equals(V1)) {
                break;
            }
            Graph<VLabel, ELabel>.Vertex vert;
            ArrayList<Graph<VLabel, ELabel>.Edge> path = null;
            for (Graph<VLabel, ELabel>.Edge e
                    : G.edges(currentNode.getVertex())) {
                vert = e.getV(currentNode.getVertex());
                System.arraycopy(currentNode.getPath(), 0, path, 0,
                        currentNode.getPath().size());
                nextNode = new Node<VLabel, ELabel>(e.getV(vert), path,
                        currentNode.getDistanceCost()
                        + eweighter.weight(e.getLabel()),
                        h.dist(e.getV(vert).getLabel(), V1.getLabel()));
                nextNode.setVweighter(currentNode.getVweighter());
                if (!openHasBetter(open, nextNode)
                        && !closedHasBetter(closed, nextNode)) {
                    nextNode.setWeight(nextNode.getDistanceCost());
                    open.add(nextNode);
                }
            }
            closed.add(currentNode);
        }
        return currentNode.getPath();
    }

    /** Determines whether there exists a node in OPEN that has the same vertex
     *  as nextNode.getVertex() with a lower totalCost. Returns a boolean.
     *  There are VLABEL types and ELABEL types. Checks if CLOSED has a better
     *  option. Looks at NEXTNODE. */
    private static <VLabel, ELabel> boolean closedHasBetter(
            HashSet<Node<VLabel, ELabel>> closed,
            Node<VLabel, ELabel> nextNode) {
        if (closed.size() == 0) {
            return false;
        } else {
            for (Node<VLabel, ELabel> node : closed) {
                if (node.sameLocation(nextNode)
                        && node.getTotalCost() > nextNode.getTotalCost()) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Determines whether there exists a node in OPEN that has the same vertex
     *  as nextNode.getVertex() with a lower totalCost. Returns a boolean.
     *  There are VLABEL types and ELABEL types.Checks NEXTNODE. */
    private static <VLabel, ELabel> boolean openHasBetter(
            PriorityQueue<Node<VLabel, ELabel>> open,
            Node<VLabel, ELabel> nextNode) {
        if (open.size() == 0) {
            return false;
        } else {
            for (Node<VLabel, ELabel> node : open) {
                if (node.getVertex().equals(nextNode.getVertex())
                        && node.getTotalCost() < nextNode.getTotalCost()) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the weights of its edge labels.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, sets the weight of vertex v to the weight of
     *  a minimal path from V0 to v, for each v in the returned path
     *  and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *           < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.
     *
     *  This function has the same effect as the 6-argument version of
     *  shortestPath, but uses the .weight and .setWeight methods of
     *  the edges and vertices themselves to determine and set
     *  weights. If V1 is unreachable from V0, returns null and sets
     *  the minimum path weights of all reachable nodes.  The distance
     *  to a node unreachable from V0 is Double.POSITIVE_INFINITY. */
    public static <VLabel extends Weightable, ELabel extends Weighted>
    List<Graph<VLabel, ELabel>.Edge> shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 Graph<VLabel, ELabel>.Vertex V1, Distancer<? super VLabel> h) {
        HashSet<Node<VLabel, ELabel>> closed =
                new HashSet<Node<VLabel, ELabel>>();
        Comparator<Node<VLabel, ELabel>> compare =
                new Comparator<Node<VLabel, ELabel>>() {
                @Override
                public int compare(Node<VLabel, ELabel> x1,
                        Node<VLabel, ELabel> x2) {
                    if (x1.getTotalCost() > x2.getTotalCost()) {
                        return 1;
                    } else if (x1.getTotalCost() < x2.getTotalCost()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            };
        PriorityQueue<Node<VLabel, ELabel>> open =
                new PriorityQueue<Node<VLabel, ELabel>>(10 + 1, compare);
        Node<VLabel, ELabel> currentNode = new Node<VLabel, ELabel>(V0,
                new ArrayList<Graph<VLabel, ELabel>.Edge>(), 0.0,
                h.dist(V0.getLabel(), V1.getLabel()));
        ArrayList<Graph<VLabel, ELabel>.Edge> path =
                new ArrayList<Graph<VLabel, ELabel>.Edge>();
        open.add(currentNode);
        while (!open.isEmpty()) {
            currentNode = open.poll();
            closed.add(currentNode);
            if (currentNode.getVertex().equals(V1)) {
                break;
            }
            Node<VLabel, ELabel> nextNode;
            Graph<VLabel, ELabel>.Vertex vert;
            for (Graph<VLabel, ELabel>.Edge e
                    : G.edges(currentNode.getVertex())) {
                vert = e.getV(currentNode.getVertex());
                path = new ArrayList<Graph<VLabel, ELabel>.Edge>(
                        currentNode.getPath());
                path.add(e);
                nextNode = new Node<VLabel, ELabel>(vert, path,
                        currentNode.getDistanceCost()
                        + e.getLabel().weight(),
                        h.dist(e.getV(vert).getLabel(),
                                V1.getLabel()));
                if (openHasBetter(open, nextNode)) {
                    continue;
                } else if (closedHasBetter(closed, nextNode)) {
                    continue;
                } else {
                    nextNode.getVertex().getLabel().setWeight(
                            nextNode.getDistanceCost());
                    if (!open.contains(nextNode)) {
                        open.add(nextNode);
                    }
                }
            }
        }
        return currentNode.getPath();
    }

    /** Returns a distancer whose dist method always returns 0. */
    public static final Distancer<Object> ZERO_DISTANCER =
        new Distancer<Object>() {
            @Override
            public double dist(Object v0, Object v1) {
                return 0.0;
            }
        };
}
