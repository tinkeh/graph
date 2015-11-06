package make;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/** Initial class for the 'make' program.
 *  @author Austin Gandy
 */
public final class Main {

    /** Entry point for the CS61B make program.  ARGS may contain options
     *  and targets:
     *      [ -f MAKEFILE ] [ -D FILEINFO ] TARGET1 TARGET2 ...
     */
    public static void main(String... args) {
        String makefileName;
        String fileInfoName;

        if (args.length == 0) {
            usage();
        }

        makefileName = "Makefile";
        fileInfoName = "fileinfo";

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-f")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    makefileName = args[a];
                }
            } else if (args[a].equals("-D")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    fileInfoName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }
        ArrayList<String> targets = new ArrayList<String>();

        for (; a < args.length; a += 1) {
            targets.add(args[a]);
        }

        make(makefileName, fileInfoName, targets);
    }

    /** Carry out the make procedure using MAKEFILENAME as the makefile,
     *  taking information on the current file-system state from FILEINFONAME,
     *  and building TARGETS, or the first target in the makefile if TARGETS
     *  is empty.
     *  */
    private static void make(String makefileName, String fileInfoName,
                             List<String> targets) {
        try {
            _currentTime = (int) -System.currentTimeMillis();
            HashMap<String, HashSet<String>> dependencies =
                    new HashMap<String, HashSet<String>>();
            HashMap<String, Integer> ages =
                    new HashMap<String, Integer>();
            Scanner fileInfo = new Scanner(new File(fileInfoName));
            String nextLine;
            HashSet<String> fileInfoNames = new HashSet<String>();
            HashMap<String, String> commands = new HashMap<String, String>();
            if (fileInfo.hasNextLine()) {
                nextLine = fileInfo.nextLine();
                nextLine.replace("\\s+", "");
            } else {
                usage();
            }
            while (fileInfo.hasNextLine()) {
                nextLine = fileInfo.nextLine();
                String[] info = nextLine.split("\\s+");
                ages.put(info[0], Integer.parseInt(info[1]));
                fileInfoNames.add(info[0]);
            }
            fileInfo.close();
            File scannerFile = new File(makefileName);
            Scanner makeFile = new Scanner(scannerFile);
            HashSet<String> dependentOn = new HashSet<String>();
            while (makeFile.hasNextLine()) {
                String line = makeFile.nextLine();
                if (!line.matches("^\\s$") && !line.startsWith("#")
                        && !line.equals("")) {
                    String[] currentLine = line.split("\\s+");
                    currentLine[0] = currentLine[0].replace(":", "");
                    if (dependencies.containsKey(currentLine[0])) {
                        dependentOn = dependencies.get(currentLine[0]);
                    }
                    fileInfoNames.add(currentLine[0]);
                    for (int i = 1; i < currentLine.length; i += 1) {
                        dependentOn.add(currentLine[i]);
                        fileInfoNames.add(currentLine[i]);
                    }
                    if (makeFile.hasNextLine()) {
                        dependencies.put(currentLine[0], dependentOn);
                        commands.put(currentLine[0], makeFile.nextLine());
                        dependentOn = new HashSet<String>();
                    } else {
                        usage();
                    }
                }
            }
            for (String target : targets) {
                assemble(target, target, dependencies, fileInfoNames, ages,
                        commands);
            }
            makeFile.close();
        } catch (FileNotFoundException | NumberFormatException
                | IndexOutOfBoundsException e) {
            usage();
        }
    }

    /** Creates the graph associated with the dependence file. Takes in a TARGET
     *  INITIALTARGET, DEPENDENCIES, FILEINFONAMES, NAMEDATE, and COMMANDS. */
    private static void assemble(String target, String initialTarget,
            HashMap<String, HashSet<String>> dependencies,
            HashSet<String> fileInfoNames,
            HashMap<String, Integer> nameDate,
            HashMap<String, String> commands) {
        if (target != null && !fileInfoNames.contains(target)) {
            usage();
        }
        HashSet<String> prereqs = dependencies.get(target);
        if (prereqs != null) {
            for (String prereq : prereqs) {
                boolean made = false;
                for (String built : _built) {
                    if (built.equals(prereqs)) {
                        made = true;
                    }
                }
                if (prereq.equals(initialTarget)) {
                    usage();
                } else if ((!made || _first) && (nameDate.get(prereq) == null
                        || (nameDate.get(prereq) != null
                        && nameDate.get(target) != null
                        && nameDate.get(target) > nameDate.get(prereq)))) {
                    _built.add(prereq);
                    nameDate.put(prereq, _currentTime);
                    _currentTime += 1;
                    assemble(prereq, initialTarget, dependencies,
                            fileInfoNames, nameDate, commands);
                } else {
                    continue;
                }
                _first = false;
            }
            System.out.println(commands.get(target));
        }
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println("Something went wrong. I give up. Sorry.");
        System.exit(1);
    }

    /** first call to make. */
    private static boolean _first = true;

    /** ArrayList used to keep track of what has been built. */
    private static ArrayList<String> _built = new ArrayList<String>();

    /** int of current time. used to assign higher times to updated files. */
    private static int _currentTime;
}
