package trip;

import graph.Weightable;

/** Represents a location on a graph. Used as the VLabel of the graph used to
 *  find trips and stuff.
 *  @author Austin Gandy
 */
class Place implements Weightable {


    /** It's a constructor. Takes in a NAME, XVAL, and YVAL. */
    Place(String name, double xVal, double yVal) {
        _name = name;
        _xVal = xVal;
        _yVal = yVal;
        _weight = 0.0;
    }

    /** returns _name. */
    String getName() {
        return _name;
    }

    /** returns _location's x coordinate. */
    double getX() {
        return _xVal;
    }

    /** returns _location's y coordinate. */
    double getY() {
        return _yVal;
    }

    /** my name. */
    private String _name;
    /** X coordinate of the location of me. */
    private Double _xVal;
    /** Y coordinate of the location of me. */
    private Double _yVal;
    /** the settable weight of me. Initially 0.0. */
    private Double _weight;

    @Override
    public double weight() {
        return _weight;
    }

    @Override
    public void setWeight(double w) {
        _weight = w;
    }

}

