package trip;

import graph.UndirectedGraph;
import graph.Graph;
import graph.Graphs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/** Initial class for the 'trip' program.
 *  @author Austin Gandy
 */
public final class Main {

    /** Entry point for the CS61B trip program.  ARGS may contain options
     *  and targets:
     *      [ -m MAP ] [ -o OUT ] [ REQUEST ]
     *  where MAP (default Map) contains the map data, OUT (default standard
     *  output) takes the result, and REQUEST (default standard input) contains
     *  the locations along the requested trip.
     */
    public static void main(String... args) {
        String mapFileName = "Map";
        String out = null;
        String requestFileName = null;
        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-m")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    mapFileName = args[a];
                }
            } else if (args[a].equals("-o")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    out = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }
        if (a == args.length - 1) {
            requestFileName = args[a];
        } else if (a > args.length) {
            usage();
        }
        if (requestFileName != null) {
            try {
                System.setIn(new FileInputStream(requestFileName));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s.%n", requestFileName);
                System.exit(1);
            }
        }
        if (out != null) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(out), true));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s for writing.%n", out);
                System.exit(1);
            }
        }
        addDirection();
        Scanner in = new Scanner(System.in);
        while (true) {
            String line = in.nextLine().replace(",", "");
            if (line.equals("EOF")) {
                break;
            }
            String[] dest = line.split("\\s+");
            trip(mapFileName, dest);
        }
        in.close();
    }

    /** adds directions to _directions. */
    private static void addDirection() {
        _directions.add("NS");
        _directions.add("SN");
        _directions.add("WE");
        _directions.add("EW");
    }
    /** Creates a graph representing the map the user specifies in the file
     *  MAPFILENAME. */
    private static void makeMap(String mapFileName) {
        try {
            Scanner mapScanner = new Scanner(new File(mapFileName));
            _map = new UndirectedGraph<Place, Road>();
            mapScanner.useDelimiter("\\r?\\n");
            String line;
            Place currentPlace;
            Road currentRoad;
            Graph<Place, Road>.Vertex placeVertex;
            _places = new HashMap<String, Graph<Place, Road>.Vertex>();
            if (!mapScanner.hasNextLine()) {
                mapScanner.close();
                return;
            }
            while (mapScanner.hasNextLine()) {
                line = mapScanner.nextLine();
                String[] words = line.split("\\s");
                if (words.length == 1 && words[0].equals("")) {
                    continue;
                }
                if (words[0].equals("L") && words.length == 4
                        && !_places.containsKey(words[1])) {
                    currentPlace = new Place(words[1],
                               Double.parseDouble(words[2]),
                               Double.parseDouble(words[3]));
                    placeVertex = _map.add(currentPlace);
                    _places.put(words[1], placeVertex);
                } else if (words[0].equals("R") && words.length == 6
                        && _places.containsKey(words[1])
                        && _places.containsKey(words[5])
                        && _directions.contains(words[4])) {
                    currentRoad = new Road(words[2], words[4],
                            _places.get(words[5]), _places.get(words[1]),
                            Double.parseDouble(words[3]));
                    _map.add(_places.get(words[1]), _places.get(words[5]),
                            currentRoad);
                } else {
                    usage();
                }
            }
            mapScanner.close();
        } catch (FileNotFoundException  e) {
            usage();
        } catch (NumberFormatException e) {
            usage();
        }
    }

    /** Print a trip for the request on the standard input to the standard
     *  output, using the map data in MAPFILENAME. Also takes in DEST.
     */
    private static void trip(String mapFileName, String[] dest) {
        if (!_mapMade) {
            makeMap(mapFileName);
            _mapMade = true;
        }
        ArrayList<Graph<Place, Road>.Edge> whereTo =
                new ArrayList<Graph<Place, Road>.Edge>();
        for (int i = 0; i < dest.length - 1; i += 1) {
            whereTo = (ArrayList<Graph<Place, Road>.Edge>)
                    Graphs.shortestPath(_map, _places.get(dest[i]),
                            _places.get(dest[i + 1]), new EuclidDistancer());
            int num = 1;
            Graph<Place, Road>.Vertex comingFrom = _places.get(dest[i]);
            Road road;
            Road lastRoad = null;
            String lastRoadName = null;
            String lastRoadDirection = null;
            Double currentDistance = 0.0;
            System.out.println("From " + comingFrom.getLabel().getName() + ":");
            System.out.println();
            String roadDirection = "";
            boolean firstTime = true;
            for (Graph<Place, Road>.Edge edge : whereTo) {
                road = edge.getLabel();
                if (lastRoad != null) {
                    roadDirection = lastRoad.getWayTo();
                }
                if (lastRoadName != null && lastRoadName.equals(road.getName())
                        && lastRoadDirection != null
                        && lastRoadDirection.equals(roadDirection)
                        && !firstTime) {
                    currentDistance += road.getDistance();
                    comingFrom = edge.getV(comingFrom);
                    lastRoadName = road.getName();
                    lastRoadName = road.getName();
                    lastRoad = road;
                    continue;
                } else if (!firstTime) {
                    System.out.println((double) Math.round(
                           currentDistance * 10) / 10 + " miles.");
                }
                System.out.print(num + ". Take " + road.getName() + " "
                        + road.getDirection(comingFrom.getLabel()) + " for ");
                currentDistance = road.getDistance();
                comingFrom = edge.getV(comingFrom);
                lastRoadDirection = road.getWayTo();
                lastRoadName = road.getName();
                firstTime = false;
                num += 1;
                lastRoad = road;
            }
            System.out.println((double) Math.round(currentDistance * 10)
                    / 10 + " miles to " + dest[i + 1]);
            System.out.println();
        }
    }

    /** checks to see if LIST has repeating edge labels next to each other going
     *  in the same direction. */
    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.err.println("Something went wrong. I give up.");
        System.exit(1);
    }

    /** True if the map for this running of the program has been made. */
    private static boolean _mapMade;
    /** list of cardinal direction pairs. */
    private static ArrayList<String> _directions = new ArrayList<String>();
    /** Graph that the user builds. */
    private static Graph<Place, Road> _map;
    /** maps place names to vertices on _map. */
    private static HashMap<String, Graph<Place, Road>.Vertex> _places;

}
