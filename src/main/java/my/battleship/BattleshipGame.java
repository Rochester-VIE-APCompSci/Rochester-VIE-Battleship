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
package my.battleship;

import java.util.Scanner;

@Copyright(Copyright.c2014)
public class BattleshipGame {

    @Copyright(Copyright.c2014)
    public enum Status {
        NOT_INITIALIZED, GAME_ACTIVE, // Game initialized and not all ships
                                      // sunk.
        GAME_OVER_ALL_SUNK; // Game over occurs when all ships are sunk.
    }

    private Status _gameStatus = Status.NOT_INITIALIZED;

    // NOTE!! When true, show un-hit ship locations on the output grid board.
    // Set to false to turn off showing the grid locations.
    boolean _showUnhitShips = true;

    private Board _gameBoard;

    static void log(Object po) {
        System.out.println(po);
    }

    /**
     * Main battleship game constuctor.
     * 
     * @param gamePropertiesFile
     *            -- path and file name to the battleship game properties file.
     *            See 'battleshipGame1Config.properties' for an example.
     */
    public BattleshipGame(String gamePropertiesFile) {
        _gameBoard = new Board(gamePropertiesFile);
    }

    BattleshipGame.Status getGameStatus() {
        return _gameStatus;
    }

    /**
     * Output the game status to standard out and, if specified, the output file
     * specified in the properties file.
     */
    public void outputGameStatistics() {
        // TODO fix me
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("Overall game status:  " + _gameStatus + "\n");
        sb.append(_gameBoard.getGameStatistics());
        log(sb);
        // TODO send output to file if specified.
    }

    /**
     * Prints board info and layout
     */
    public void outputGrid() {
        _gameBoard.outputGrid();
    }

    /**
     * Shoot a battleship game board location that may or may not contains a
     * ship.
     * 
     * @param row
     *            -- rows range from 0 to game row length - 1.
     * @param col
     *            -- columns range from 0 to game column length - 1.
     *
     * @return Status of shot
     * @see ShotStatus
     */
    public ShotStatus shoot(int row, int col) {
        return _gameBoard.shoot(row, col);
    }

    /**
     * Simple text based battleship game driver.
     * 
     * @param args
     *            [0] = battleship game definition properties file.
     */
    public static void main(String[] args) {

        log("In main of " + BattleshipGame.class.getName());
        System.out.println(BattleshipGame.class.getResource("."));
        
        if (args == null || args.length != 1) {
            log("Usage: my.battleship.BattleshipGame <game definition properties file>");
            return;
        }

        // BattleshipGame game = new BattleshipGame(
        // "battleshipGameEasy1RandomPlace.properties");
        BattleshipGame game = new BattleshipGame(args[0]);
        game._showUnhitShips = true; // for debug, show ships

        // Simple interactive text commands.
        Scanner scanner = new Scanner(System.in);
        while (true) {
            log("To shoot, enter shot coordinates in format 'row,col' starting at 0,0.  For grid, enter 'grid'.  For games statistics, enter 'stats'. To quit, 'q'");
            String input = scanner.nextLine().trim();
            // Is input digits comma digits for row and column?
            if (input.matches("\\d+,\\d+")) {
                String[] rowCol = input.split(",");
                ShotStatus shotStat = game.shoot(Integer.parseInt(rowCol[0]), Integer.parseInt(rowCol[1]));
                log("Your shot result = " + shotStat);
            } else if (input.startsWith("grid")) {
                game.outputGrid();
            } else if (input.startsWith("stats")) {
                game.outputGameStatistics();
            } else if (input.startsWith("q")) {
                game.outputGrid();
                game.outputGameStatistics();
                log("\n**** Player quit game -- thanks for playing :-) ****");
                break;
            } else {
                log("Invalid input.  Please try again.");
            }
        }
        // Game is over at this point.
        scanner.close();
    }

}
