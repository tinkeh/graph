package trip;

import graph.Graph;
import graph.Weighted;

/** Represents a road on a map.
 *  @author Austin Gandy
 *  */
class Road implements Weighted {

    /** Constructor takes in a NAME, DIRECTIONS (two cardinal direction strings)
     *  a place TO and a place FROM, and a DISTANCE. */
    Road(String name, String directions, Graph<Place, Road>.Vertex from,
            Graph<Place, Road>.Vertex to, Double distance) {
        _name = name;
        String toString = directions.substring(0, 1);
        if (toString.equals("N")) {
            _wayFrom = "north";
            _wayTo = "south";
        } else if (toString.equals("S")) {
            _wayFrom = "south";
            _wayTo = "north";
        } else if (toString.equals("E")) {
            _wayFrom = "east";
            _wayTo = "west";
        } else {
            _wayTo = "west";
            _wayFrom = "east";
        }
        _from = from.getLabel();
        _to = to.getLabel();
        _distance = distance;
    }

    /** returns which way along me we need to go to get from FROM. */
    String getDirection(Place from) {
        if (from.equals(_from)) {
            return _wayFrom;
        } else {
            return _wayTo;
        }
    }

    /** Returns the distance from _from to _to. */
    @Override
    public double weight() {
        return _distance;
    }

    /** returns name. */
    public String getName() {
        return _name;
    }

    /** returns _from. */
    public Place getFrom() {
        return _from;
    }

    /** returns _to. */
    public Place getTo() {
        return _to;
    }

    /** returns _wayTo. */
    public String getWayTo() {
        return _wayTo;
    }

    /** returns _distance. */
    public Double getDistance() {
        return _distance;
    }

    /** The name of the road. */
    private String _name;
    /** a cardinal direction. */
    private String _wayTo;
    /** a cardinal direction. */
    private String _wayFrom;
    /** the place we came from. */
    private Place _from;
    /** place we're going to. */
    private Place _to;
    /** distance on me from _from to _to or vice-versa. */
    private double _distance;

}

