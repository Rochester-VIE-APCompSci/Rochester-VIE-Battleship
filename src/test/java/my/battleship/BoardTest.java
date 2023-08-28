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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

@Copyright(Copyright.c2014)
public class BoardTest {

    static void log(Object po) {
        System.out.println(po);
    }

    @Test
    public void testBattleshipPrintGrid() throws Exception {
        Board board = new Board(6, 3);
        board.outputGrid(); // No ship

        // Add a ship and set some board spaces to shot.
        GameBoardShip ship1 = new GameBoardShip(2, 3, "ship1");
        board.placeShipOnBoard(ship1);
        // Shoot some spaces.
        for (int r = 0; r < 3; r++)
            for (int c = 1; c < 3; c++) {
                board.shoot(r, c);
            }
        board.outputGrid();
    }

    @Test
    public void testGetMaxBoardRowsCols() {
        Board board = new Board(6, 3);
        assertEquals(6, board.getMaxBoardRows());
        assertEquals(3, board.getMaxBoardCols());
    }

    @Test
    public void testPlaceShipOnBoardRandom() throws Exception {
        Board board = new Board(6, 4);

        GameBoardShip ship1 = new GameBoardShip(2, 3, "ship1");
        board.placeShipOnBoard(ship1);
        log("\nGrid after 1 ship added:");
        board.outputGrid();

        // Verify a ship that is on the board cannot be added again.
        try {
            board.placeShipOnBoard(ship1);
            fail("Expected a board configuration error did not occur.");
        } catch (GameConfigError e) {
            log("\nExpected board configuration error occurred:\n" + e.toString());
        }

        // Add more ships and review test exceptions when they occur: was the
        // exception because the ship did not fit anywhere on the board or
        // was it because the random location logic and number of retries
        // failed to find space for the ship?

        GameBoardShip ship2 = new GameBoardShip(2, 4, "ship2");
        board.placeShipOnBoard(ship2);
        log("\nGrid after 2 ships added:");
        board.outputGrid();

        GameBoardShip ship3 = new GameBoardShip(3, 1, "ship3");
        board.placeShipOnBoard(ship3);
        log("\nGrid after 3 ship added:");
        board.outputGrid();

        GameBoardShip ship4 = new GameBoardShip(1, 3, "ship4");
        board.placeShipOnBoard(ship4);
        log("\nGrid after 4 ship added:");
        board.outputGrid();

        // Create a ship that will not fit on the board
        // to verify a config exception.
        GameBoardShip shipTooLarge = new GameBoardShip(100, 100, "tooLarge");
        try {
            board.placeShipOnBoard(shipTooLarge);
            fail("Expected a board configuration error did not occur.");
        } catch (GameConfigError e) {
            log("\nExpected board configuration error occurred:\n" + e.toString());
        }
    }

    @Test
    public void testPlaceShipOnBoardSetLocation() throws Exception {
        Board board = new Board(6, 4);

        GameBoardShip ship1 = new GameBoardShip(2, 3, "ship1");
        board.placeShipOnBoard(ship1, new Location(0, 0), true);
        log("\nGrid after 1 ship added:");
        board.outputGrid();

        try {
            board.placeShipOnBoard(ship1, new Location(0, 0), true);
            fail("Expected a board configuration error did not occur.");
        } catch (GameConfigError e) {
            log("\nExpected board configuration error occurred:\n" + e.toString());
        }

        // Verify boundary error is detected with a ship that will fit on the
        // board.
        GameBoardShip ship2 = new GameBoardShip(1, 4, "ship2");
        try {
            board.placeShipOnBoard(ship2, new Location(1, 3), false);
            fail("Expected a board configuration error did not occur.");
        } catch (GameConfigError e) {
            log("\nExpected board configuration error occurred:\n" + e.toString());
        }

        // Verify boundary placement.
        GameBoardShip ship3 = new GameBoardShip(1, 4, "ship3");
        // Place horizontally starting at 1,3. It should span rows 1 to 4 in
        // column 3
        // Recall, row and column numbering starts at zero not 1.
        board.placeShipOnBoard(ship3, new Location(1, 3), true);
        log("\nGrid after 2 ships added:");
        board.outputGrid();

        // Create a ship that will not fit on the board
        // to verify a config exception.
        GameBoardShip shipTooLarge = new GameBoardShip(100, 100, "tooLarge");
        try {
            board.placeShipOnBoard(shipTooLarge, new Location(2, 4), true);
            fail("Expected a board configuration error did not occur.");
        } catch (GameConfigError e) {
            log("\nExpected board configuration error occurred:\n" + e.toString());
        }
    }

    @Test
    public void testShoot() {
        Board board = new Board(6, 4);
        GameBoardShip ship1 = new GameBoardShip(2, 3, "ship1");
        // Place ship at a specific location.
        board.placeShipOnBoard(ship1, new Location(0, 0), true);

        ShotStatus sStatus = board.shoot(5, 1);
        assertTrue(sStatus == ShotStatus.MISS);
        sStatus = board.shoot(100, 100);
        assertTrue(sStatus == ShotStatus.MISSED_BOARD);
        sStatus = board.shoot(2, 1);
        assertTrue(sStatus == ShotStatus.HIT);
        System.out.println(board.getGameStatistics());
        
        Board board2 = new Board(6, 4);
        GameBoardShip onlyShip = new GameBoardShip(1, 1, "onlyShip");
        // Place ship 1x1 ship in last location on the board.
        board2.placeShipOnBoard(onlyShip, new Location(5, 3), true);
        shootAll(board2);
        assertEquals(ShotStatus.SUNK_ALL_YOU_WIN, board2.getLastShotStatus());
    }

    @Test
    public void testOutputGameStatistics() {
        Board board = new Board(6, 4);
        GameBoardShip ship1 = new GameBoardShip(2, 3, "ship1");
        board.placeShipOnBoard(ship1);
        log("\nGrid after 1 ship added:");
        board.outputGrid();
        System.out.println(board.getGameStatistics());
    }

    /**
     * Test the battleship constructor that takes in a board configuration
     * properties file. There are two types of input files: 1) Random ship
     * placement. 2) Specific ship placement. Superset of 1.
     */
    @Test
    public void testCtorConfigFileInput() {
        // Test properties file for randomly placed ships.
        Board board = new Board("testBoards/battleshipGame1Config.properties");
        // The following asserts are dependent on the values in the properties
        // file above.
        assertEquals(20, board.getMaxBoardRows());
        assertEquals(10, board.getMaxBoardCols());
        assertEquals(3, board.getNumShipsOnBoard());
        board.outputGrid();
        log("");

        // Test properties file for specifically placed ships.
        board = new Board("testBoards/battleshipGameEasy1ManuallyPlace.properties");
        // The following asserts are dependent on the values in the properties
        // file above.
        assertEquals(8, board.getMaxBoardRows());
        assertEquals(10, board.getMaxBoardCols());
        assertEquals(3, board.getNumShipsOnBoard());
        board.outputGrid();
    }

    void shootAll(Board board) {
        for (int r = 0; r < board.getMaxBoardRows(); r++)
            for (int c = 0; c < board.getMaxBoardCols(); c++)
                board.shoot(r, c);
    }

    @Test
    public void testCloneability() {
        try {
            Board board1 = new Board("testBoards/battleshipGameEasy1ManuallyPlace.properties");
            Board board2 = (Board) board1.clone();
            assertEquals(board1.getMaxBoardRows(), board2.getMaxBoardRows());
            assertEquals(board1.getMaxBoardCols(), board2.getMaxBoardCols());

            board1.outputGrid();
            board2.outputGrid();
            for (int r = 0, R = board1.getMaxBoardRows(); r < R; r++) {
                for (int c = 0, C = board2.getMaxBoardCols(); c < C; c++) {
                    ShotStatus status1 = board1.shoot(r, c);
                    ShotStatus status2 = board2.shoot(r, c);
                    assertEquals(status1, status2);
                    @SuppressWarnings("unused")
                    String stats1 = board1.getGameStatistics();
                    @SuppressWarnings("unused")
                    String stats2 = board2.getGameStatistics();
                    // stats strings may differ because containers (e.g. ship
                    // containers) may have different order.
                    // assertEquals(stats1, stats2);
                }
            }
        } catch (CloneNotSupportedException e) {
            fail("Board not cloneable details=" + e.getMessage());
        }

    }
}
