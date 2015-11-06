package graph;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

    /** Test class for the Graphs class.
     *  @author Austin Gandy */
public class GraphsTest {

    /** Stub class that implements the Weightable and Weighted interfaces
     *  used for testing of A* search algorithm
     * @author Austin Gandy */
    private class WeighsStuff implements Weightable, Weighted {

        /** returns weight. */
        @Override
        public double weight() {
            return _weight;
        }

        /** sets _weight to W. */
        @Override
        public void setWeight(double w) {
            _weight = w;
        }

        /** returns _num. */
        public int getNum() {
            return _num;
        }

        /** Sets _num to NUM. */
        public void setNum(int num) {
            _num = num;
        }

        private double _weight = 0;
        private int _num = 0;
    }

    /** Creates a graph to be traversed by the A* search algorithm.
     *  graph will look like this:
     * (weights of edges are written to the left of the edge.
     *             1
     *           2/5\
     *           2   3
     *         3/1\3/6\
     *         4   5   6
     *        1\ 2/
     *          7
     *  with the smallest traversal being 1 - 2 - 5 - 7. */
    private void setupGraph() {
        _graph = new DirectedGraph<WeighsStuff, WeighsStuff>();
        WeighsStuff vlabel = new WeighsStuff();
        vlabel.setNum(1);
        Graph<WeighsStuff, WeighsStuff>.Vertex one = _graph.add(vlabel);
        vlabel = new WeighsStuff();
        vlabel.setNum(2);
        Graph<WeighsStuff, WeighsStuff>.Vertex two = _graph.add(vlabel);
        vlabel = new WeighsStuff();
        vlabel.setNum(3);
        Graph<WeighsStuff, WeighsStuff>.Vertex three = _graph.add(vlabel);
        vlabel = new WeighsStuff();
        vlabel.setNum(4);
        Graph<WeighsStuff, WeighsStuff>.Vertex four = _graph.add(vlabel);
        vlabel = new WeighsStuff();
        vlabel.setNum(5);
        Graph<WeighsStuff, WeighsStuff>.Vertex five = _graph.add(vlabel);
        vlabel = new WeighsStuff();
        vlabel.setNum(6);
        Graph<WeighsStuff, WeighsStuff>.Vertex six = _graph.add(vlabel);
        vlabel = new WeighsStuff();
        vlabel.setNum(7);
        Graph<WeighsStuff, WeighsStuff>.Vertex seven = _graph.add(vlabel);
        WeighsStuff elabel = new WeighsStuff();
        addSpecialEdge(one, two, 2.0, elabel);
        addEdge(one, three, 5.0, elabel);
        addEdge(two, four, 3.0, elabel);
        addSpecialEdge(two, five, 1.0, elabel);
        addEdge(three, five, 3.0, elabel);
        addEdge(three, six, 6.0, elabel);
        addEdge(four, seven, 1.0, elabel);
        addSpecialEdge(five, seven, 2.0, elabel);
        vert.add(one);
        vert.add(seven);
    }

    /** Creates a stupidly intricate graph. I'd try to type it out here, but
     * that would take forever. the correct path is 1 - 3 - 6 - 11. */
    private void setupIntricateGraph() {
        _graph = new UndirectedGraph<WeighsStuff, WeighsStuff>();
        _edges.clear();
        vert.clear();
        Graph<WeighsStuff, WeighsStuff>.Vertex one = setVertex(1);
        Graph<WeighsStuff, WeighsStuff>.Vertex two = setVertex(2);
        Graph<WeighsStuff, WeighsStuff>.Vertex three = setVertex(3);
        Graph<WeighsStuff, WeighsStuff>.Vertex four = setVertex(4);
        Graph<WeighsStuff, WeighsStuff>.Vertex five = setVertex(5);
        Graph<WeighsStuff, WeighsStuff>.Vertex six = setVertex(6);
        Graph<WeighsStuff, WeighsStuff>.Vertex seven = setVertex(7);
        Graph<WeighsStuff, WeighsStuff>.Vertex eight = setVertex(8);
        Graph<WeighsStuff, WeighsStuff>.Vertex nine = setVertex(9);
        Graph<WeighsStuff, WeighsStuff>.Vertex ten = setVertex(10);
        Graph<WeighsStuff, WeighsStuff>.Vertex eleven = setVertex(11);
        WeighsStuff elabel = new WeighsStuff();
        addEdge(one, two, 1.0, elabel);
        addSpecialEdge(one, three, 2.0, elabel);
        addEdge(one, four, 3.0, elabel);
        addEdge(two, three, 2.0, elabel);
        addEdge(two, five, 4.0, elabel);
        addEdge(two, eleven, 12.0, elabel);
        addEdge(three, five, 3.0, elabel);
        addEdge(three, four, 2.0, elabel);
        addSpecialEdge(three, six, 2.0, elabel);
        addEdge(four, six, 2.0, elabel);
        addEdge(four, seven, 2.0, elabel);
        addEdge(five, six, 2.0, elabel);
        addEdge(five, eleven, 4.0, elabel);
        addSpecialEdge(six, eleven, 4.0, elabel);
        addEdge(six, ten, 4.0, elabel);
        addEdge(six, nine, 4.0, elabel);
        addEdge(six, eight, 3.0, elabel);
        addEdge(six, seven, 2.0, elabel);
        addEdge(seven, eight, 2.0, elabel);
        addEdge(eight, nine, 1.0, elabel);
        addEdge(nine, ten, 2.0, elabel);
        addEdge(ten, eleven, 3.0, elabel);
        vert.add(one);
        vert.add(eleven);
    }

    private void addEdge(Graph<WeighsStuff, WeighsStuff>.Vertex to,
            Graph<WeighsStuff, WeighsStuff>.Vertex from, double val,
            WeighsStuff elabel) {
        elabel = new WeighsStuff();
        elabel.setWeight(val);
        _graph.add(to, from, elabel);
    }

    private Graph<WeighsStuff, WeighsStuff>.Vertex setVertex(int num) {
        WeighsStuff vlabel = new WeighsStuff();
        vlabel.setNum(num);
        return _graph.add(vlabel);
    }
    private void addSpecialEdge(Graph<WeighsStuff, WeighsStuff>.Vertex to,
            Graph<WeighsStuff, WeighsStuff>.Vertex from, double val,
            WeighsStuff elabel) {
        elabel = new WeighsStuff();
        elabel.setWeight(val);
        _edges.add(_graph.add(to, from, elabel));
    }

    /*@Test
    public void testSimpleGraph() {
        setupGraph();
        ArrayList<Graph<WeighsStuff, WeighsStuff>.Edge> realEdges =
                (ArrayList<Graph<WeighsStuff, WeighsStuff>.Edge>)
                Graphs.shortestPath(_graph, vert.get(0), vert.get(1),
                Graphs.ZERO_DISTANCER);
        assertTrue(_edges.size() > 0);
        assertEquals(realEdges.size(), _edges.size());
        for (int i = 0; i < _edges.size(); i += 1) {
            assertEquals(realEdges.get(i), _edges.get(i));
        }
    }*/

    @Test
    /** Tests the more intricate graph. */
    public void testIntricateGraph() {
        setupIntricateGraph();
        ArrayList<Graph<WeighsStuff, WeighsStuff>.Edge> realEdges =
                (ArrayList<Graph<WeighsStuff, WeighsStuff>.Edge>)
                Graphs.shortestPath(_graph, vert.get(0), vert.get(1),
                Graphs.ZERO_DISTANCER);
        assertTrue(_edges.size() > 0);
        assertEquals(realEdges.size(), _edges.size());
        for (int i = 0; i < _edges.size(); i += 1) {
            assertEquals(realEdges.get(i), _edges.get(i));
        }
    }

    /** The graph being traversed. */
    private Graph<WeighsStuff, WeighsStuff> _graph;

    /** Correct traversal of the graph. */
    private ArrayList<Graph<WeighsStuff, WeighsStuff>.Edge> _edges =
            new ArrayList<Graph<WeighsStuff, WeighsStuff>.Edge>();

    /** contains V0 and V1. */
    private ArrayList<Graph<WeighsStuff, WeighsStuff>.Vertex> vert =
            new ArrayList<Graph<WeighsStuff, WeighsStuff>.Vertex>();
}
