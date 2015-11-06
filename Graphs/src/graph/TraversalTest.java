package graph;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class TraversalTest {

    /** Stub of a class used to test Traversal. */
    private class TraversalTester extends Traversal<String, String> {

        @Override
        /** Method to be called when adding the node at the other end of E from
         * V0 to the fringe. If this routine throws a StopException,
         *  the traversal ends.  If it throws a RejectException, the edge
         *  E is not traversed. The default does nothing.
         */
        protected void preVisit(Graph<String, String>.Edge e,
                                Graph<String, String>.Vertex v0) {
            if (v0.equals("stop") || e.equals("stop")) {
                throw new StopException();
            } else if (v0.equals("reject") || e.equals("reject")) {
                throw new RejectException();
            } else {
                System.out.println("preVisiting " + v0.getLabel());
            }
        }

        @Override
        /** Method to be called when visiting vertex V.  If this routine throws
         *  a StopException, the traversal ends.  If it throws a
         *  RejectExceptior, successors of V do not get visited from V.
         *  The default does nothing. */
        protected void visit(Graph<String, String>.Vertex v) {
            if (v.equals("stop")) {
                throw new StopException();
            } else if (v.equals("reject")) {
                throw new RejectException();
            } else {
                System.out.println("Visiting " + v.getLabel());
                _sentence += v.getLabel() + " ";
            }
        }

        @Override
        /** Method to be called immediately after finishing the traversal
         *  of successors of vertex V in pre- and post-order traversals.
         *  If this routine throws a StopException, the traversal ends.
         *  Throwing a RejectException has no effect. The default does nothing.
         */
        protected void postVisit(Graph<String, String>.Vertex v) {
            if (v.equals("stop")) {
                throw new StopException();
            } else if (v.equals("reject")) {
                throw new RejectException();
            } else {
                System.out.println("PostVisiting " + v.getLabel());
            }
        }

        /** Sentence accumulated as a graph gets traversed and nodes are
         * visited. */
        private String _sentence = "";

        /** returns _sentence. */
        public String getSentence() {
            return _sentence;
        }

        /** clears _sentence. */
        public void clearSentence() {
            _sentence = "";
        }
    }

    private void setupStrings() {
        _possibleBreadths = new ArrayList<String>();
        _possibleDepths = new ArrayList<String>();
        _possibleBreadths.add("I am me? a feel test graph ya ");
        _possibleBreadths.add("I am me? a feel test ya graph ");
        _possibleBreadths.add("I am me? feel a test graph ya ");
        _possibleBreadths.add("I am me? feel a test ya graph ");
        _possibleBreadths.add("I me? am a feel test graph ya ");
        _possibleBreadths.add("I me? am a feel test ya graph ");
        _possibleBreadths.add("I me? am feel a test graph ya ");
        _possibleBreadths.add("I me? am feel a test ya graph ");
        _possibleDepths.add("I am a test graph ya feel me? ");
        _possibleDepths.add("I am a test ya graph feel me? ");
        _possibleDepths.add("I am feel a test graph ya me? ");
        _possibleDepths.add("I am feel a test ya graph me? ");
        _possibleDepths.add("I me? am a test graph ya feel ");
        _possibleDepths.add("I me? am a test ya graph feel ");
        _possibleDepths.add("I me? am feel a test graph ya ");
        _possibleDepths.add("I me? am feel a test ya graph ");
    }

    private void setupGraph() {
        _graph = new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex I = _graph.add("I");
        Graph<String, String>.Vertex am = _graph.add("am");
        Graph<String, String>.Vertex a = _graph.add("a");
        Graph<String, String>.Vertex test = _graph.add("test");
        Graph<String, String>.Vertex graph = _graph.add("graph");
        Graph<String, String>.Vertex ya = _graph.add("ya");
        Graph<String, String>.Vertex feel = _graph.add("feel");
        Graph<String, String>.Vertex me = _graph.add("me?");
        _start = I;
        _graph.add(I, am);
        _graph.add(I, me);
        _graph.add(am, a);
        _graph.add(am, feel);
        _graph.add(a, test);
        _graph.add(test, graph);
        _graph.add(test, ya);
        _tester = new TraversalTester();
    }

    private void setupHilfGraph() {
        _graph = new UndirectedGraph<String, String>();
        _start = _graph.add("A");
        Graph<String, String>.Vertex a = _start;
        Graph<String, String>.Vertex b = _graph.add("B");
        Graph<String, String>.Vertex c = _graph.add("C");
        Graph<String, String>.Vertex d = _graph.add("D");
        Graph<String, String>.Vertex e = _graph.add("E");
        Graph<String, String>.Vertex f = _graph.add("F");
        Graph<String, String>.Edge ab = _graph.add(a, b, "ab");
        Graph<String, String>.Edge ac = _graph.add(a, c, "ac");
        Graph<String, String>.Edge ad = _graph.add(a, d, "ad");
        Graph<String, String>.Edge ed = _graph.add(d, e, "ed");
        Graph<String, String>.Edge df = _graph.add(d, f, "df");
    }

    private void setupTraversalLists() {
        _dftStar.add("A");
        _dftStar.add("prD");
        _dftStar.add("prC");
        _dftStar.add("prB");
        _dftStar.add("vD");
        _dftStar.add("prF");
        _dftStar.add("prE");
        _dftStar.add("vC");
        _dftStar.add("prE");
        _dftStar.add("vB");
        _dftStar.add("pvB");
        _dftStar.add("vF");
        _dftStar.add("pvF");
        _dftStar.add("vE");
        _dftStar.add("pvD");
    }

    @Test
    public void testTraversals() {
        setupStrings();
        setupGraph();
        _tester.breadthFirstTraverse(_graph, _start);
        assertTrue(_possibleBreadths.contains(_tester.getSentence()));
        _tester.clearSentence();
        System.out.println();
        _tester.depthFirstTraverse(_graph, _start);
        assertTrue(_possibleDepths.contains(_tester.getSentence()));
        System.out.println();
        setupHilfGraph();
        System.out.println();
        System.out.println("Breadth First");
        _tester.breadthFirstTraverse(_graph, _start);
        System.out.println();
        System.out.println("Depth First");
        _tester.depthFirstTraverse(_graph, _start);
    }

    /** list of possible results of a depth first traversal of _graph. */
    private ArrayList<String> _possibleBreadths;
    /** list of possible results of a breadth first traversal of _graph. */
    private ArrayList<String> _possibleDepths;
    /** A graph with strings as vertex labels. Represents something similar
     *  to a tree. */
    private UndirectedGraph<String, String> _graph;
    /** A new traversal tester. */
    private TraversalTester _tester;
    /** Starting vertex of the graph. */
    private Graph<String, String>.Vertex _start;
    /** correct ordering for DFT with starred edge. */
    private ArrayList<String> _dftStar = new ArrayList<String>();
    /** correct ordering DFT without starred edge. */
    private ArrayList<String> _dft = new ArrayList<String>();
    /** correct ordering of BFT according to lecure. */
    private ArrayList<String> _bft = new ArrayList<String>();
}
