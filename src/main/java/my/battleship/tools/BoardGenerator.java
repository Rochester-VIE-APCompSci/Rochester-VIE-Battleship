/*
 * Copyright (c) 2014,2017 IBM Corporation
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package my.battleship.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

import my.battleship.Copyright;
import my.battleship.Log;

@Copyright(Copyright.c2014)
public class BoardGenerator {

    enum LayoutType {
        RANDOM, PERIMETER, SPACED, CLUSTERED, TOUCHING, UNDEFINED
    }

    private LayoutType requestedLayout = LayoutType.UNDEFINED;
    private LayoutType actualLayout = LayoutType.UNDEFINED;

    private boolean showBoard = false;
    private String inputBoard = null;
    private int numberOfRows = 15;
    private int numberOfCols = 15;
    private Random random = new Random();
    private List<Ship> ships = new ArrayList<BoardGenerator.Ship>();
    private boolean occupied[][];
    private long seed = System.currentTimeMillis();
    private PrintStream outFile = System.out;
    private int numberOfBoards = 1;
    private String outDir = null;
    private boolean shipNamesArePresent = false;
    private int minRows = 15;
    private int maxRows = 100;
    private int minCols = 15;
    private int maxCols = 100;
    private int minShips = 1;
    private int maxShips = 10;

    public static void main(String[] args) {

        BoardGenerator generator = new BoardGenerator();

        Log.instance().setLoggingLevel(Log.Event);
        for (int i = 0; i < args.length; i++) {
            if ("--board".equals(args[i])) {
                generator.inputBoard = args[++i];
            } else if ("--seed".equals(args[i])) {
                generator.seed = Long.parseLong(args[++i]);
            } else if ("--show".equals(args[i])) {
                generator.showBoard = true;
            } else if ("--layout".equals(args[i])) {
                String tp = args[++i];
                if ("random".equals(tp)) {
                    generator.requestedLayout = LayoutType.RANDOM;
                } else if ("perimeter".equals(tp)) {
                    generator.requestedLayout = LayoutType.PERIMETER;
                } else if ("spaced".equals(tp)) {
                    generator.requestedLayout = LayoutType.SPACED;
                } else if ("clustered".equals(tp)) {
                    generator.requestedLayout = LayoutType.CLUSTERED;
                } else {
                    System.out.println("(E) unknown layout: " + tp + ".  Try --help.");
                    return;
                }
            } else if ("--N".equals(args[i])) {
                generator.numberOfBoards = Integer.parseInt(args[++i]);
            } else if ("--dir".equals(args[i])) {
                generator.outDir = args[++i];
            } else if ("--debug".equals(args[i])) {
                Log.instance().setLoggingLevel(Log.Debug);
            } else {
                System.out.println("Usage: BoardGenerator --board <file> [--show] [--help]");
                System.out.println("         [--layout {random, perimeter, spaced, clustered}]");
                System.out.println("         [--dir <directory>] [--N <num-boards>]");
                return;
            }
        }

        generator.generate();
    }

    public void generate() {
        try {
            Log.instance().log("seed= " + this.seed, Log.Event);
            random.setSeed(seed);
            for (int i = 0; i < numberOfBoards; i++) {

                Log.instance().log("Generating board " + i, Log.Event);
                loadProperties();
                try {
                    placeShips();

                    if (outDir != null) {
                        File path = new File(this.inputBoard);
                        String shortName = path.getName();
                        shortName = shortName.substring(0, shortName.indexOf(".properties"));
                        File out = new File(outDir, shortName + "-" + actualLayout + "-" + i + ".properties");
                        this.outFile = new PrintStream(out);
                    }

                    if (showBoard) {
                        showBoard();
                    }
                    writeFile();

                    if (outDir != null) {
                        this.outFile.close();
                    }
                } catch (ConstraintViolation cv) {
                    Log.instance().log(cv, Log.Debug);
                }

            }
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }
    }

    private void loadProperties() throws IOException, FileNotFoundException {

        Properties props = new Properties();
        props.load(new FileReader(inputBoard));

        String dimensions = props.getProperty("gameBoardRowsCols");
        if (dimensions != null) {
            StringTokenizer toks = new StringTokenizer(dimensions, "x");
            this.numberOfRows = Integer.parseInt(toks.nextToken());
            this.numberOfCols = Integer.parseInt(toks.nextToken());
        } else {
            this.numberOfRows = this.minRows + this.random.nextInt(this.maxRows - this.minRows);
            this.numberOfCols = this.minCols + this.random.nextInt(this.maxCols - this.minCols);
            Log.instance().log("random board: " + this.numberOfRows + "x" + this.numberOfCols, Log.Debug);
        }

        this.ships.clear();
        String allShips = props.getProperty("ships");
        if (allShips != null) {
            StringTokenizer toks = new StringTokenizer(allShips, ",");
            while (toks.hasMoreTokens()) {
                dimensions = toks.nextToken();
                StringTokenizer dimToks = new StringTokenizer(dimensions, "x");
                Ship ship = new Ship();
                ship.length = Integer.parseInt(dimToks.nextToken());
                ship.width = Integer.parseInt(dimToks.nextToken());
                ships.add(ship);
            }
        } else {
            int numShips = this.minShips + this.random.nextInt(this.maxShips - this.minShips);
            // Keep ship dimension underneath the shorter side of the board
            int maxLength = min(this.numberOfRows, this.numberOfCols) - 1;
            maxLength = min(maxLength, 15);
            for (int i = 0; i < numShips; i++) {
                Ship ship = new Ship();
                ship.length = 1 + this.random.nextInt(maxLength);
                ship.width = 1 + this.random.nextInt(ship.length);
                ships.add(ship);
                Log.instance().log("random ship: " + ship.length + "x" + ship.width, Log.Debug);
            }
        }

        String allShipsNames = props.getProperty("shipsNames");
        shipNamesArePresent = (allShipsNames != null);
        if (allShipsNames != null) {
            StringTokenizer toks = new StringTokenizer(allShipsNames, ",");
            int i = 0;
            while (toks.hasMoreTokens()) {
                ships.get(i++).name = toks.nextToken();
            }
        }

        this.occupied = new boolean[this.numberOfRows][this.numberOfCols];

        Log.instance().log("Rows:" + numberOfRows + " Cols:" + numberOfCols + " Ships:" + ships.size(), Log.Debug);

    }

    private static int min(int a, int b) {
        return a < b ? a : b;
    }

    @Copyright(Copyright.c2014)
    private interface Layout {
        void placeShips() throws ConstraintViolation;
    }

    private Layout createLayout() {
        if (requestedLayout == LayoutType.UNDEFINED) {
            switch (random.nextInt(4)) {
            case 0:
                actualLayout = LayoutType.RANDOM;
                break;
            case 1:
                actualLayout = LayoutType.PERIMETER;
                break;
            case 2:
                actualLayout = LayoutType.SPACED;
                break;
            case 3:
                actualLayout = LayoutType.CLUSTERED;
                break;
            }
        } else {
            actualLayout = requestedLayout;
        }
        switch (actualLayout) {
        case RANDOM:
            return new RandomLayout();
        case PERIMETER:
            return new PerimeterLayout();
        case SPACED:
            return new SpacedLayout();
        case CLUSTERED:
            return new ClusteredLayout();
        default:
            throw new UnsupportedOperationException("Layout " + actualLayout + " not yet supported.");
        }
    }

    private void placeShips() throws ConstraintViolation {
        createLayout().placeShips();
    }

    private boolean placeRandomly(Ship s) {

        boolean placed = false;
        s.x = random.nextInt(numberOfRows - s.length);
        s.y = random.nextInt(numberOfCols - s.width);

        placed = s.fits(this);

        if (placed) {
            s.occupy(this);
        }
        return placed;
    }

    private void shuffleShips() {
        // shuffle the ships
        for (int i = 0; i < 52; i++) {
            int i1 = random.nextInt(ships.size());
            int i2 = random.nextInt(ships.size());
            Ship tmp = ships.get(i1);
            ships.set(i1, ships.get(i2));
            ships.set(i2, tmp);
        }
    }

    private void showBoard() {

        outFile.println("# seed= " + this.seed);
        outFile.println("# layout= " + this.actualLayout);
        for (int r = 0; r < numberOfRows; r++) {
            StringBuffer buff = new StringBuffer();
            buff.append(String.format(" %1$2d |", r));
            for (int c = 0; c < numberOfCols; c++) {
                buff.append((occupied[r][c] ? "*" : " ") + "|");
            }
            outFile.println("# " + buff.toString());
        }
    }

    private void writeFile() {

        outFile.println("gameBoardRowsCols=" + numberOfRows + "x" + numberOfCols);

        int n = 0;
        outFile.print("ships=");
        for (Ship s : ships) {
            if (n > 0)
                outFile.print(",");
            if (s.vertical)
                outFile.print("" + s.length + "x" + s.width);
            else
                outFile.print("" + s.width + "x" + s.length);

            n++;
        }
        outFile.println();

        n = 0;
        outFile.print("shipsStartPoints=");
        for (Ship s : ships) {
            if (n > 0)
                outFile.print(",");
            outFile.print("" + s.x + "-" + s.y);
            n++;
        }
        outFile.println();

        outFile.print("shipsOrientations=");
        n = 0;
        for (Ship s : ships) {
            if (n > 0)
                outFile.print(",");
            outFile.print(s.vertical ? "v" : "h");
            n++;
        }
        outFile.println();

        if (shipNamesArePresent) {
            outFile.print("shipsNames=");
            n = 0;
            for (Ship s : ships) {
                if (n > 0)
                    outFile.print(",");
                outFile.print(s.name);
                n++;
            }
            outFile.println();

        }

    }

    class Ship {

        int x;
        int y;
        int length;
        int width;
        boolean vertical = true;
        String name = null;

        Ship() {
            
        }
        Ship(Ship s) {
            this.x = s.x;
            this.y = s.y;
            this.length = s.length;
            this.width = s.width;
            this.vertical = s.vertical;
            this.name = s.name;
        }
        
        public String toString() {
            return "(" + x + "," + y + ":" + length + "," + width + ")";
        }

        void randomizeOrientation(BoardGenerator boardGenerator) {
            if (boardGenerator.random.nextBoolean()) {
                int tmp = length;
                length = width;
                width = tmp;
                vertical = vertical ? false : true;
            }
        }

        boolean isVertical(BoardGenerator boardGenerator) {
            return length > width;
        }

        boolean onBoard(BoardGenerator boardGenerator) {
            return (x >= 0) && (x + length <= boardGenerator.numberOfRows) && (y >= 0)
                    && (y + width <= boardGenerator.numberOfCols);
        }

        void occupy(BoardGenerator boardGenerator) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < width; j++) {
                    boardGenerator.occupied[x + i][y + j] = true;
                }
            }
        }

        boolean fits(BoardGenerator boardGenerator) {
            Log.instance().log("fits? " + this, Log.Debug);
            for (int r = 0; r < length; r++) {
                for (int c = 0; c < width; c++) {
                    if (boardGenerator.occupied[x + r][y + c]) {
                        return false;
                    }
                }
            }
            return true;
        }

        boolean isSpaced(BoardGenerator boardGenerator) {
            // check leg above the ship
            if ((x - 1) >= 0) {
                for (int i = y - 1; i < y + width; i++) {
                    if ((i >= 0) && (i < boardGenerator.numberOfCols) && boardGenerator.occupied[x - 1][i])
                        return false;
                }
            }
            // and below the ship
            if ((x + length) < boardGenerator.numberOfRows) {
                for (int i = y - 1; i < y + width; i++) {
                    if ((i >= 0) && (i < boardGenerator.numberOfCols) && boardGenerator.occupied[x + length][i])
                        return false;
                }
            }
            // and to the left:
            if ((y - 1) >= 0) {
                for (int i = x - 1; i < x + length; i++) {
                    if ((i >= 0) && (i < boardGenerator.numberOfRows) && boardGenerator.occupied[i][y - 1])
                        return false;
                }
            }
            // and to the right:
            if ((y + width) < boardGenerator.numberOfCols) {
                for (int i = x - 1; i < x + length; i++) {
                    if ((i >= 0) && (i < boardGenerator.numberOfRows) && boardGenerator.occupied[i][y + width])
                        return false;
                }
            }

            return true;
        }
    }

    @SuppressWarnings("serial")
    private static class ConstraintViolation extends Exception {

    }

    @Copyright(Copyright.c2014)
    private class RandomLayout implements Layout {

        private final int MAXTRIES = 100;

        public void placeShips() throws ConstraintViolation {

            for (Ship s : ships) {

                int attemptsRemaining = MAXTRIES;

                Log.instance().log("RandomLayout placing " + s, Log.Debug);
                boolean placed = false;
                do {
                    s.randomizeOrientation(BoardGenerator.this);
                    placed = placeRandomly(s);
                    if (--attemptsRemaining == 0) {
                        throw new ConstraintViolation();
                    }
                } while (!placed);
            }
        }
    }

    @Copyright(Copyright.c2014)
    private class PerimeterLayout implements Layout {

        private final int MAXTRIES = 100;

        public void placeShips() throws ConstraintViolation {

            for (Ship s : ships) {

                Log.instance().log("PerimeterLayout placing " + s, Log.Debug);

                int attemptsRemaining = MAXTRIES;

                boolean placed = false;
                do {

                    s.randomizeOrientation(BoardGenerator.this);

                    if (s.isVertical(BoardGenerator.this)) {
                        s.x = random.nextInt(numberOfRows - s.length);
                        s.y = random.nextBoolean() ? 0 : numberOfCols - s.width;
                    } else {
                        s.y = random.nextInt(numberOfCols - s.width);
                        s.x = random.nextBoolean() ? 0 : numberOfRows - s.length;
                    }

                    placed = s.fits(BoardGenerator.this);

                    if (placed) {
                        s.occupy(BoardGenerator.this);
                    } else if (--attemptsRemaining == 0) {
                        throw new ConstraintViolation();
                    }

                } while (!placed);
            }
        }
    }

    @Copyright(Copyright.c2014)
    private class SpacedLayout implements Layout {

        private int MAXTRIES = 100;

        public void placeShips() throws ConstraintViolation {

            for (Ship s : ships) {

                int attemptsRemaining = MAXTRIES;

                boolean placed = false;
                do {

                    Log.instance().log("SpacedLayout placing " + s, Log.Debug);

                    s.randomizeOrientation(BoardGenerator.this);

                    s.x = random.nextInt(numberOfRows - s.length);
                    s.y = random.nextInt(numberOfCols - s.width);

                    placed = s.fits(BoardGenerator.this) && s.isSpaced(BoardGenerator.this);

                    if (placed) {
                        s.occupy(BoardGenerator.this);
                    } else if (--attemptsRemaining == 0) {
                        throw new ConstraintViolation();
                    }
                } while (!placed);
                // showBoard();

            }
        }
    }

    @Copyright(Copyright.c2014)
    private class ClusteredLayout implements Layout {

        private final int MAXTRIES = 200;

        public void placeShips() throws ConstraintViolation {

            shuffleShips();

            int clusterSize = 2 + random.nextInt(ships.size());
            int count = 0;
            Ship previous = null;

            Log.instance().log("clusterSize:" + clusterSize, Log.Debug);
            for (Ship s : ships) {

                Log.instance().log("ClusterLayout placing " + s, Log.Debug);

                int attemptsRemaining = MAXTRIES;
                boolean placed = false;

                if ((count % clusterSize) == 0) {
                    do {
                        s.randomizeOrientation(BoardGenerator.this);
                        placed = placeRandomly(s);
                        if (--attemptsRemaining == 0) {
                            throw new ConstraintViolation();
                        }
                    } while (!placed);
                    previous = s;
                } else {

                    // Find all locations above, below, right and left that
                    // might fit
                    List<Ship> possibilities = new ArrayList<BoardGenerator.Ship>();
                    s.randomizeOrientation(BoardGenerator.this);
                    
                    for (int c = previous.y - s.width + 1; c < previous.y + previous.width; c++) {
                        
                        Ship above = new Ship(s);
                        above.x = previous.x - s.length;
                        above.y = c;

                        if (above.onBoard(BoardGenerator.this) && above.fits(BoardGenerator.this)) {
                            possibilities.add(above);
                        }
                        
                        Ship below = new Ship(s);
                        below.x = previous.x + previous.length;
                        below.y = c;
                        if (below.onBoard(BoardGenerator.this) && below.fits(BoardGenerator.this)) {
                            possibilities.add(below);
                        }
                    }
                    
                    for (int r = previous.x - s.length + 1; r < previous.x + previous.length; r++) {
                        
                        Ship left = new Ship(s);
                        left.y = previous.y - s.width;
                        left.x = r;
                        if (left.onBoard(BoardGenerator.this) && left.fits(BoardGenerator.this)) {
                            possibilities.add(left);
                        }
                        
                        Ship right = new Ship(s);
                        right.x = r;
                        right.y = previous.y + previous.width;
                        if (right.onBoard(BoardGenerator.this) && right.fits(BoardGenerator.this)) {
                            possibilities.add(right);
                        }
                    }

                    if (possibilities.size() > 0) {
                        Ship selection = possibilities.get(random.nextInt(possibilities.size()));
                        s.x = selection.x;
                        s.y = selection.y;
                        s.occupy(BoardGenerator.this);
                    } else {
                        while (!placed) {
                            s.randomizeOrientation(BoardGenerator.this);
                            placed = placeRandomly(s);
                            if (--attemptsRemaining == 0) {
                                throw new ConstraintViolation();
                            }
                        }
                    }
                }
                count++;
                //showBoard();
            }
        }
    }
}
