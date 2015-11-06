package graph;

import java.util.ArrayList;
import org.junit.Test;
//import ucb.junit.textui;
import static org.junit.Assert.*;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your graph package per se (that is, it must be
 * possible to remove them and still have your package work). */

/** Unit tests for the graph package.
 *  @author Austin Gandy
 */
public class Testing {

    /** Run all JUnit tests in the graph package. */
    /*public static void main(String[] ignored) {
        System.exit(textui.runClasses(graph.Testing.class,
                graph.GraphsTest.class, graph.TraversalTest.class));
    }*/

    @Test
    public void emptyGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
        UndirectedGraph u = new UndirectedGraph();
        assertEquals("Initial graph has vertices", 0, u.vertexSize());
        assertEquals("Initial graph has edges", 0, u.edgeSize());
    }

    @Test
    public void simpleGraph() {
        DirectedGraph<Integer, String> g = new DirectedGraph<Integer, String>();
        g.add(1);
        g.add(2);
        assertEquals("Adding and counting vertices works", 2, g.vertexSize());
        g.add(3);
        g.add(4);
        for (int i = 1; i < 5; i += 1) {
            assertEquals("inDegree works", 0, g.inDegree(g.new Vertex(i)));
        }
        for (int i = 1; i < 5; i += 1) {
            for (int j = 1; j < 5; j += 1) {
                assertEquals("Edge contains method works for simple vals.",
                        false, g.contains(g.new Vertex(i), g.new Vertex(j)));
            }
        }
    }

    @Test
    public void testUndirectedEdges() {
        UndirectedGraph<Integer, String> g =
                new UndirectedGraph<Integer, String>();
        Graph<Integer, String>.Vertex one = g.add(1);
        Graph<Integer, String>.Vertex two = g.add(2);
        Graph<Integer, String>.Vertex three = g.add(3);
        Graph<Integer, String>.Vertex four = g.add(4);
        g.add(one, two, "A");
        g.add(one, three, "B");
        g.add(two, three, "C");
        g.add(two, four, "D");
        g.add(three, four, "E");
        assertEquals("inDegree is outDegree", g.inDegree(three),
                g.outDegree(three));
        assertEquals("Degree is inDegree", g.degree(three), g.inDegree(three));
        assertEquals("Degree is acurate", g.degree(three), 3);
    }

    private int countTotal(ArrayList<Graph<Integer, String>.Vertex> vertices,
            Graph<Integer, String> g) {
        int totalDeg = 0;
        for (Graph<Integer, String>.Vertex vertex : vertices) {
            totalDeg += g.degree(vertex);
        }
        return totalDeg;
    }

    @Test
    public void testUndirectedRemove() {
        UndirectedGraph<Integer, String> g =
                new UndirectedGraph<Integer, String>();
        Graph<Integer, String>.Vertex one = g.add(1);
        Graph<Integer, String>.Vertex two = g.add(2);
        Graph<Integer, String>.Vertex three = g.add(3);
        Graph<Integer, String>.Vertex four = g.add(4);
        ArrayList<Graph<Integer, String>.Vertex> vertices =
                new ArrayList<Graph<Integer, String>.Vertex>();
        vertices.add(one);
        vertices.add(two);
        vertices.add(three);
        vertices.add(four);
        g.add(one, two, "A");
        g.add(one, three, "B");
        g.add(two, three, "C");
        g.add(two, four, "D");
        g.add(three, four, "E");
        int total = countTotal(vertices, g);
        g.remove(one, four);
        int totalDegree = countTotal(vertices, g);
        assertEquals("total degree remains the same.", total, totalDegree);
        g.remove(one, three);
        assertEquals("Correct edge removed", 1, g.degree(one));
    }

    @Test
    public void testDirectedRemove() {
        DirectedGraph<Integer, String> g = new DirectedGraph<Integer, String>();
        Graph<Integer, String>.Vertex one = g.add(1);
        Graph<Integer, String>.Vertex two = g.add(2);
        Graph<Integer, String>.Vertex three = g.add(3);
        Graph<Integer, String>.Vertex four = g.add(4);
        ArrayList<Graph<Integer, String>.Vertex> vertices =
                new ArrayList<Graph<Integer, String>.Vertex>();
        vertices.add(one);
        vertices.add(two);
        vertices.add(three);
        vertices.add(four);
        g.add(one, two, "A");
        g.add(one, three, "B");
        g.add(two, three, "C");
        g.add(two, four, "D");
        g.add(three, four, "E");
        int total = countTotal(vertices, g);
        g.remove(one, four);
        int totalDegree = countTotal(vertices, g);
        assertEquals("total degree remains the same.", total, totalDegree);
        g.remove(one, three);
        assertEquals("Correct edge removed", 1, g.degree(one));
    }
}
