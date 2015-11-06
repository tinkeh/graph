package trip;

import graph.Distancer;

/** Calculates the distance between two places.
 *  @author Austin Gandy */
class EuclidDistancer implements Distancer<Place> {

    @Override
    public double dist(Place v0, Place v1) {
        return Math.sqrt(Math.pow(v0.getX() - v1.getX(), 2)
                + Math.pow(v0.getY() - v1.getY(), 2));
    }
}
