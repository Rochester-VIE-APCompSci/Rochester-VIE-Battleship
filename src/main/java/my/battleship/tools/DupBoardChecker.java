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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import my.battleship.Log;

public class DupBoardChecker {

    private Path boardDir;
    private List<Board> boards = new ArrayList<>();

    public static void main(String[] args) throws ParserConfigurationException {

        DupBoardChecker checker = new DupBoardChecker();

        Log.instance().setLoggingLevel(Log.Event);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--directory")) {
                checker.boardDir = Paths.get(args[++i]);
            } else if ("--debug".equals(args[i])) {
                Log.instance().setLoggingLevel(Log.Debug);
            } else {
                System.out.println("Unknown argument: " + args[i]);
                printHelp();
                return;
            }
        }

        if (checker.boardDir != null) {
            checker.checkBoards();
        } else {
            System.out.println("Must specify --directory flag");
            printHelp();
        }
    }

    public static void printHelp() {
        System.out.println("Usage: DupBoardChecker --directory <dir> ");
        System.out.println("         [--debug] [--help]");
    }

    private class Board{
        String name;
        int numRows;
        int numCols;
        boolean[][] spaces;

        private Board(String name, int rows, int cols) {
            this.name = name;
            numRows = rows;
            numCols = cols;
            spaces = new boolean[numRows][numCols];
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || !(obj instanceof Board)) {
                return false;
            } else if (this == obj) {
                return true;
            }

            boolean eq = true;

            Board other = (Board) obj;
            if(numCols == other.numCols && numRows == other.numRows) {
                for(int i = 0; i < numRows; ++i) {
                    for(int j = 0; j < numCols; ++j) {
                        if(spaces[i][j] != other.spaces[i][j]) {
                            eq = false;
                        }
                    }
                }
            }
            return eq;
        }
    }

    private void checkBoards() throws ParserConfigurationException  {
        if (boardDir != null) {
            System.out.println("Checking for duplicate boards in " + boardDir.toString());

            System.out.println("Loading boards...");
            try {
                Stream<Path> directoryStream = Files.walk(boardDir, Integer.MAX_VALUE);
                List<Path> filePaths = directoryStream.filter(Files::isRegularFile).collect(Collectors.toList());
                directoryStream.close();
                for (Path f : filePaths) {
                    System.out.println("Loading board " + f.toString());
                    
                    Properties gameProps = new Properties();
                    InputStream inputStream = new FileInputStream(f.toString());
                    gameProps.load(inputStream);
                    inputStream.close();

                    
                    // Configure gameboard size. Expect rowsxcolumns format.
                    int numRows = 15, numCols = 15;

                    String rowCols = gameProps.getProperty("gameBoardRowsCols");
                    if (rowCols != null && !rowCols.isEmpty()) {
                        String[] rowsColsSplit = rowCols.split("x");
                        numRows = Integer.parseInt(rowsColsSplit[0].trim());
                        numCols = Integer.parseInt(rowsColsSplit[1].trim());;
                    }

                    Board b = new Board(f.toString(), numRows, numCols);
                    boards.add(b);

                    // Parse the ships property into an array of "LengthxWdith" strings.
                    String[] shipsDim = gameProps.getProperty("ships").split(",");

                    // Get optional attributes for manually placed ships.
                    String startPointsString = gameProps.getProperty("shipsStartPoints");
                    String orientationsString = gameProps.getProperty("shipsOrientations");
                    String[] shipsStartPoints = {};
                    String[] shipsOrientations = {};

                    if (startPointsString == null || orientationsString == null) {
                        throw new ParserConfigurationException("shipsStartPoints and shipsOrientations are required");
                    }
                    shipsStartPoints = startPointsString.trim().split(",");
                    shipsOrientations = orientationsString.trim().split(",");
                    if (shipsStartPoints.length != shipsOrientations.length) {
                        throw new ParserConfigurationException("Mismatched number of ship start points and orientations");
                    }

                    for (int i = 0; i < shipsDim.length; i++) {
                        String[] lenWidth = shipsDim[i].split("x");
                        int length = Integer.parseInt(lenWidth[0]);
                        int width = Integer.parseInt(lenWidth[1]);

                        // Specific ship placement.
                        String[] startRowCol = shipsStartPoints[i].split("-");
                        int r = Integer.parseInt(startRowCol[0]);
                        int c = Integer.parseInt(startRowCol[1]);
                        placeShipOnBoard(b, r, c, Math.max(length, width), shipsOrientations[i].equalsIgnoreCase("h"));
                    }
                }
            } catch (IOException ioe) {
                Log.instance().log(ioe, Log.Error);
                return;
            }

            System.out.println("Comparing boards...");
            List<String> duplicates = new ArrayList<>();
            for (int i = 0; i < boards.size(); ++i) {
                Board b = boards.get(i);
                for (int j = i+1; j < boards.size(); ++j) {
                    Board o = boards.get(j);
                    if(b.equals(o)) {
                        duplicates.add("Found duplicate: " + b.name + " and " + o.name);
                    }
                }
            }
            if(duplicates.size() > 0) {
                duplicates.stream().forEach(p -> System.out.println(p));
            } else {
                System.out.println("Found no duplicates");
            }  
        }
    }

    private void placeShipOnBoard(Board b, int row, int col, int length, boolean isHorizontal) {
        if(isHorizontal) {
            for(int i = 0; i < length ; ++i) {
                b.spaces[row][col + i] = true;
            }
        } else {
            for(int i = 0; i < length ; ++i) {
                b.spaces[row + i][col] = true;
            }
        }
    }
}
