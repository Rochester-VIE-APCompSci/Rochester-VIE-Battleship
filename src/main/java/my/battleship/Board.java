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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

@Copyright(Copyright.c2014)
public class Board {

    private static String OPTIONAL_SHIPS_START_POINTS = "shipsStartPoints";
    private static String OPTIONAL_SHIPS_ORIENTATIONS = "shipsOrientations";
    private static String OPTIONAL_SHIPS_NAMES = "shipsNames";

    // NOTE!! When true, show un-hit ship locations on the output grid board.
    // Set to false to turn off showing the grid locations.
    boolean _showUnhitShips = true;

    private Random _randomNum = new Random();

    protected GameSpace[][] _gameGrid = null;

    // Note: _shotsTotal could be calculated by adding all gamespace shots,
    // out of range shots, and after game shots.
    private long _shotsTotal = 0;
    private long _shotsOutOfGameRange = 0;
    private long _shotsAfterGameOver = 0;

    private int _advancedWeaponsCache[] = new int[WeaponType.values().length];
    private Set<GameBoardShip> _shipsOnGameBoard = new HashSet<GameBoardShip>();

    private ShotStatus _lastShotStatus = ShotStatus.MISS;
    private List<Ship> sunkShips = new LinkedList<Ship>();
    
    /**
     * Main Board constuctor.
     * 
     * @param gamePropertiesFile
     *            -- path and file name to the battleship game properties file.
     *            See 'battleshipGame1Config.properties' for an example.
     */

    public Board(String gamePropertiesFile) {
        initialize(gamePropertiesFile);
    }

    // Constructor for internal package use.
    Board(int row, int col) {
        Log.instance().log("In ctor for " + this.getClass().getName(), Log.Debug);
        createGameBoard(row, col);
    }

    private Board() {
    }

    final List<Ship> listSunkShips() {
     return Collections.unmodifiableList(sunkShips);
    }
    
    final List<Ship> listShips() {
        List<Ship> result = new ArrayList<Ship>();
        for (GameBoardShip s : _shipsOnGameBoard) {
            result.add(new Ship(s.getLength(), s.getWidth(), s.getName()));
        }
        return result;
    }

    
    
    
    Set<GameBoardShip> getShipsOnGameBoard() {
        return _shipsOnGameBoard;
    }

    public long getShotTotal() {
        return _shotsTotal;
    }

    public void set_shotsTotal(long _shotsTotal) {
        this._shotsTotal = _shotsTotal;
    }

    private void createGameBoard(int row, int col) {
        // Create the game grid/board and the objects that represent each space.
        _gameGrid = new GameSpace[row][col];
        // For each row...
        for (int r = 0; r < _gameGrid.length; r++) {
            // For each column...
            for (int c = 0; c < _gameGrid[0].length; c++) {
                _gameGrid[r][c] = new GameSpace(r, c);
            }
        }
    }

    int getAllShipsHits() {
        int totalHits = 0;
        for (GameBoardShip ship : _shipsOnGameBoard) {
            totalHits += ship.getNumSpacesShot();
        }
        return totalHits;
    }

    int getMaxBoardCols() {
        return _gameGrid[0].length;
    }

    int getMaxBoardRows() {
        return _gameGrid.length;
    }

    public int getNumShipsOnBoard() {
        return _shipsOnGameBoard.size();
    }

    public float calculateShotEfficiencyPercentage() {
        if (_shotsTotal == 0)
            return 0;
        else
            return ((float) getAllShipsHits() / _shotsTotal) * 100;
    }

    String createOutputStringForShips() {
        String shipSummary;
        StringBuffer sb = new StringBuffer();
        sb.append("Number of ships = " + _shipsOnGameBoard.size() + "\n");

        // Create status for each ship.
        for (GameBoardShip ship : _shipsOnGameBoard) {
            if (ship.isSunk())
                shipSummary = "Sunk";
            else if (ship.getNumSpacesShot() == 0)
                shipSummary = "No hits";
            else
                shipSummary = "Some hits -- " + ship.getNumSpacesShot() + "spaces shot out of " + ship.getLength()
                        * ship.getWidth();

            sb.append("  ** Ship summary: " + shipSummary + ".  Size = " + ship.getDimensionString() + "\n");
        }
        return sb.toString();
    }

    /**
     * Gets the game's stats including board size, total number of shots,
     * shot efficiency, and ship summaries
     *
     * @return A string containing all the game statistics
     */
    public String getGameStatistics() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("Board size:  " + getMaxBoardRows() + " x " + getMaxBoardCols() + "\n");
        sb.append("Total Number of shots: " + _shotsTotal + "\n");
        sb.append("Shot efficiency (ship hits / total shots) = " + calculateShotEfficiencyPercentage() + "%\n");
        sb.append(createOutputStringForShips() + "\n");

        if (_shotsOutOfGameRange > 0) {
            sb.append("**** shots out of game board range = " + _shotsOutOfGameRange);
        }
        if (_shotsAfterGameOver > 0) {
            sb.append("**** shots after game was over (all ships sunk) = " + _shotsAfterGameOver);
        }

        return sb.toString();
    }

    /**
     * Place a ship on the board.
     * 
     * @param pShip
     *            -- ship to place on the board
     * @param pStartLocation
     *            -- the start location (GameSpace) for the ship. The ship will
     *            be placed starting at the pStartLocation's row and column and
     *            will extend to rows+ and columns+. If null, the ship is
     *            randomly placed on the board.
     * @param pLengthHorizontal
     *            -- Ignored if pStartLocation==null, otherwise, true results in
     *            the ship's length dimension to span columns; false results in
     *            length spanning rows.
     */
    void placeShipOnBoard(GameBoardShip pShip, Location pStartLocation, boolean pLengthHorizontal) {

        if (pShip.isShipOnGameBoard())
            throw new GameConfigError("**** Config error:  cannot place a ship on the board that is already on the board.");

        // Verify ship will fit on the board -- needed for radom placement.
        // Note: since the ship may get placed vertically or horizontally, both
        // dimensions
        // must not be greater than the game board boundaries.
        if (pShip.getLength() > getMaxBoardCols() || pShip.getLength() > getMaxBoardRows()
                || pShip.getWidth() > getMaxBoardCols() || pShip.getWidth() > getMaxBoardRows())
            throw new GameConfigError("**** Config error:  ship dimensions (" + pShip.getLength() + " x " + pShip.getWidth()
                    + ") are too big.  The ship must be able to fit on the "
                    + "board in both the horizontal and vertical positions.");

        // Find an unoccupied board area big enough a for the ship.

        List<GameSpace> shipGameSpaces = new ArrayList<GameSpace>();
        // If the ship is not placed randomly on the board, attempt
        // to find space only once.
        int attempts = 1;
        if (pStartLocation == null) {
            // Arbitrarily bound the random search for an open area to add the
            // ship
            // to the number of game board grid spaces * 2.
            attempts = getMaxBoardRows() * getMaxBoardCols() * 2;
        }
        for (int i = 0; i < attempts; i++) {

            int rowsNeeded, colsNeeded;
            int rowStart, colStart;
            boolean horizontal;
            if (pStartLocation == null) {
                // Randomly choose ship orientation.
                // Horizontal==true results in the ship's length dimension to
                // span
                // columns.
                horizontal = _randomNum.nextBoolean();
                if (horizontal) {
                    rowsNeeded = pShip.getWidth();
                    colsNeeded = pShip.getLength();
                } else {
                    rowsNeeded = pShip.getLength();
                    colsNeeded = pShip.getWidth();
                }
                // Find a random row and col location starting point where
                // the ship will fit. Note, ship will be placed from
                // rowStart and span to rowStart+rowsNeeded-1
                // colStart and span to colStart+colsNeeded-1
                // Recall that Random nextInt(n) results in numbers from 0 to
                // n-1.
                rowStart = _randomNum.nextInt(getMaxBoardRows() - rowsNeeded + 1);
                colStart = _randomNum.nextInt(getMaxBoardCols() - colsNeeded + 1);
            } else {
                // Place ship at a specific location--not random.
                if (pLengthHorizontal) {
                    rowsNeeded = pShip.getWidth();
                    colsNeeded = pShip.getLength();
                } else {
                    rowsNeeded = pShip.getLength();
                    colsNeeded = pShip.getWidth();
                }
                rowStart = pStartLocation.getRow();
                colStart = pStartLocation.getCol();
                // Verify ship will fit in selected start location with the
                // selected orientation.
                if (rowsNeeded + rowStart > getMaxBoardRows() || colsNeeded + colStart > getMaxBoardCols()) {
                    throw new GameConfigError("**** Config error:  ship dimensions (" + pShip.getLength() + " x "
                            + pShip.getWidth() + ") will not fit at start row " + rowStart + " and span " + rowsNeeded
                            + " rows and at start col " + colStart + " and span " + colsNeeded + " columns.");
                }
            }

            // Now check that space is available.
            for (int r = rowStart; r < rowsNeeded + rowStart; r++) {
                for (int c = colStart; c < colsNeeded + colStart; c++) {
                    GameSpace gSpace = _gameGrid[r][c];
                    if (!gSpace.hasShip()) {
                        shipGameSpaces.add(gSpace);
                    } else {
                        // Space is already occupied so break out of this loop.
                        break;
                    }
                }
            }
            // Was enough open space found for the ship?
            if (shipGameSpaces.size() == (rowsNeeded * colsNeeded)) {
                // Yes, space found for ship, so update spaces and ship status.
                for (GameSpace gSpace : shipGameSpaces) {
                    gSpace.setShip(pShip);
                }
                pShip.setOccupiedSpace(shipGameSpaces);
                break; // Exit loop looking for ship board spaces.
            } else {
                // Clear the list and start another random location search for
                // space.
                shipGameSpaces.clear();
            }
        }

        if (shipGameSpaces.size() == 0) {
            String messageFixedPlacement = "";
            if (pStartLocation != null) {
                messageFixedPlacement = " at fixed placement starting row=" + pStartLocation.getRow() + " and col="
                        + pStartLocation.getCol();
            }
            throw new GameConfigError("Unable to find gameboard space for ship " + messageFixedPlacement + " with length "
                    + pShip.getLength() + " and width " + pShip.getWidth() + " and horizontal orientation=" + pLengthHorizontal);
        }
        _shipsOnGameBoard.add(pShip);
    }

    void placeShipOnBoard(GameBoardShip pShip) {
        placeShipOnBoard(pShip, (Location) null, true);
    }

    public void outputGrid() {
        System.out.println("\nBattleship Grid (row and column numbering start at zero). +"
                + " '-' = space may have a ship (shown when hit); 's' = ship; 'h' = space hit/shot.");
        System.out
                .println("  Examples:  -- = space with no ship & not hit; sh = space with ship & hit; -h = space with no ship & hit.\n");

        // For each row...
        for (int r = 0; r < getMaxBoardRows(); r++) {
            // For each column...
            for (int c = 0; c < getMaxBoardCols(); c++) {
                GameSpace gSpace = _gameGrid[r][c];

                // NOTE! Don't show un-hit ship location unless enabled for
                // testing.
                StringBuffer outChars = new StringBuffer();
                if (gSpace.hasShip() && (gSpace.isShot() || _showUnhitShips))

                    outChars.append("s"); // s for ship.
                else
                    outChars.append("-"); // - for blank space or to hide un-hit
                // ship

                // Add "h" to indicate space with a shot or another "-" for no
                // shots
                // yet.
                if (gSpace.isShot())
                    outChars.append("h"); // h for hit.
                else
                    outChars.append("-"); // - for no shot/hit.

                System.out.print(outChars + "  ");
            }
            System.out.println("");
        }
    }
    
    
    /**
     * Shoot a battleship game board location that may or may not contains a
     * ship.
     * 
     * @param row
     *            -- rows range from 0 to game row length - 1.
     * @param col
     *            -- columns range from 0 to game column length - 1.
     * @return The status of this shot
     * @see ShotStatus
     */
    public ShotStatus shoot(int row, int col) {
        _shotsTotal++;
        if ((_lastShotStatus == ShotStatus.SUNK_ALL_YOU_LOSE) || (_lastShotStatus == ShotStatus.SUNK_ALL_YOU_TIE)
                || (_lastShotStatus == ShotStatus.SUNK_ALL_YOU_WIN) || (_lastShotStatus == ShotStatus.SHOT_AFTER_GAME_OVER)) {
            Log.instance().log("**** Game is already over.  All ships have been sunk.", Log.Event);
            _shotsAfterGameOver++;
            return _lastShotStatus = ShotStatus.SHOT_AFTER_GAME_OVER;
        }

        ShotStatus status = null;
        if (row > getMaxBoardRows() - 1 || col > getMaxBoardCols() - 1 || row < 0 || col < 0) {
            status = ShotStatus.MISSED_BOARD;
            _shotsOutOfGameRange++;
        } else {
            GameSpace gSpace = _gameGrid[row][col];
            
            // Check if ship is already sunk
            boolean alreadySunk = false;
            if(gSpace.hasShip()) {
                alreadySunk = gSpace.getShip().isSunk();
            }

            gSpace.shoot();
            // If space has a ship, this shot was a hit.
            if (gSpace.hasShip()) {
                if (gSpace.getShip().isSunk() && !alreadySunk) {
                    GameBoardShip sunkShip = gSpace.getShip();
                    this.sunkShips.add(0, new Ship(sunkShip.getLength(),
                                        sunkShip.getWidth(),
                                        sunkShip.getName()));
                 
                    // Determine if game is over.
                    if (isAllShipsSunk()) {
                        status = ShotStatus.SUNK_ALL_YOU_WIN;
                    } else {
                        status = ShotStatus.SUNK_SHIP;
                    }
                } else {
                    status = ShotStatus.HIT;
                }
            } else {
                status = ShotStatus.MISS;
                // TODO Consider adding near miss (+/- 1) logic.
            }
        }
        return _lastShotStatus = status;
    }

    public synchronized int getRemainingWeapons(WeaponType wtype) {
        return _advancedWeaponsCache[wtype.ordinal()];
    }

    public synchronized int decrementRemainingWeapons(WeaponType wtype) {
        return _advancedWeaponsCache[wtype.ordinal()]--;
    }

    private boolean isLegalLocation(Location l) {
        return isLegalLocation(l.getRow(), l.getCol());
    }

    public boolean isLegalLocation(int r, int c) {
        return (r >= 0) && (r < getMaxBoardRows()) && (c >= 0) && (c < getMaxBoardCols());

    }

    private boolean gameAlreadyOver() {
        return (_lastShotStatus == ShotStatus.SUNK_ALL_YOU_WIN) || (_lastShotStatus == ShotStatus.SUNK_ALL_YOU_TIE)
                || (_lastShotStatus == ShotStatus.SUNK_ALL_YOU_LOSE);
    }

    public ShotReply shoot(AdvancedWeapon weapon) {

        long shotTotal = _shotsTotal; // shot chosts are different for advanced
                                      // weapons
        ShotStatus status = ShotStatus.MISS;

        int remaining = decrementRemainingWeapons(weapon.getType());
        Location loc = null;
        int cost = 0;
        String sunkenShip = null;
        if (remaining > 0) {
            if (isLegalLocation(weapon.getOrigin())) {
                boolean done = false;
                for (int i = 0, N = weapon.getNumberOfShots(); (i < N) && !done; i++) {
                    loc = weapon.getLocation(i);
                    if (!isLegalLocation(loc)) {
                        status = ShotStatus.MISSED_BOARD;
                        _shotsOutOfGameRange++;
                        cost = weapon.getMaximumCost();
                        done = true;
                    } else if (gameAlreadyOver()) {
                        done = true;
                        cost = weapon.getMaximumCost();
                        status = ShotStatus.SHOT_AFTER_GAME_OVER;
                    } else {

                        status = shoot(loc.getRow(), loc.getCol());
                        cost = weapon.getCost(i);
                        switch (status) {
                        case HIT:
                            done = true;
                            break;
                        case SUNK_ALL_YOU_WIN:
                        case SUNK_SHIP:
                            done = true;
                            sunkenShip = _gameGrid[loc.getRow()][loc.getCol()].getShip().getName();
                            break;
                        case MISSED_BOARD:
                        case SHOT_AFTER_GAME_OVER:
                            cost = weapon.getMaximumCost();
                            done = true;
                            break;
                        default:
                            break;
                        }
                    }
                    Log.instance().log("shoot(" + weapon + ") loc=" + loc + " status=" + status, Log.GoryDetail);
                }

                if (!done) {
                    cost = weapon.getMaximumCost();
                }
            } else {
                status = ShotStatus.MISSED_BOARD;
                cost = weapon.getMaximumCost();
            }
        } else {
            Log.instance().log("Attempted to fire " + weapon.getType() + " but there are none left.", Log.Event);
            cost = weapon.getMaximumCost();
        }
        _shotsTotal = shotTotal + cost;
        _lastShotStatus = status;
        return new ShotReply(status, (loc != null) ? loc.getRow() : -1, (loc != null) ? loc.getCol() : -1, cost, sunkenShip);
    }

    private boolean isAllShipsSunk() {
        int numSunkShips = 0;
        for (GameBoardShip ship : _shipsOnGameBoard) {
            if (ship.isSunk())
                numSunkShips++;
        }
        return _shipsOnGameBoard.size() == numSunkShips;
    }

    /**
     * Initialize the game based on the input game properties file.
     */
    private void initialize(String gamePropertiesFile) {

        Properties gameProps = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(gamePropertiesFile);
        } catch (IOException e) {
            try {
                inputStream = this.getClass().getResourceAsStream("/" + gamePropertiesFile);
            } catch (Exception e2) {
                Log.instance().log("**** Properties file load error!", Log.Error);
                throw new GameConfigError("**** Config error:  Problem loading game " + "configuration properties file '"
                        + gamePropertiesFile + "'.", e2);

            }
            if (inputStream == null) {
                Log.instance().log("**** Properties file load error!", Log.Error);
                throw new GameConfigError("**** Config error:  Problem loading game " + "configuration properties file '"
                        + gamePropertiesFile + "'.", e);
            }
        }

        try {
            gameProps.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            Log.instance().log("**** Properties file load error!", Log.Error);
            throw new GameConfigError("**** Config error:  Problem loading game " + "configuration properties file '"
                    + gamePropertiesFile + "'.", e);

        }
        // Configure gameboard size. Expect rowsxcolumns format.
        int numRows = 15, numCols = 15;
        String rowCols = getProperty(gameProps, "gameBoardRowsCols", true);
        if (rowCols != null && !rowCols.isEmpty()) {
            String[] rowsColsSplit = rowCols.split("x");
            numRows = Integer.parseInt(rowsColsSplit[0].trim());
            numCols = Integer.parseInt(rowsColsSplit[1].trim());;
        }
        
        createGameBoard(numRows, numCols);

        // Parse the ships property into an array of "LengthxWdith" strings.
        String[] shipsDim = getProperty(gameProps, "ships", false).split(",");

        // Get optional attributes for manually placed ships.
        String startPointsString = getProperty(gameProps, OPTIONAL_SHIPS_START_POINTS, true);
        String orientationsString = getProperty(gameProps, OPTIONAL_SHIPS_ORIENTATIONS, true);
        // log("shipsStartPoints = " + startPointsString);
        if (startPointsString != null && orientationsString == null) {
            throw new GameConfigError("**** Config error:  Game properties must specify '" + OPTIONAL_SHIPS_ORIENTATIONS
                    + "' key when '" + OPTIONAL_SHIPS_START_POINTS + "' key is defined.");
        }
        String[] shipsStartPoints = {};
        String[] shipsOrientations = {};

        if (startPointsString != null) {
            shipsStartPoints = startPointsString.trim().split(",");
            shipsOrientations = orientationsString.trim().split(",");
            if (shipsStartPoints.length != shipsOrientations.length) {
                throw new GameConfigError("**** Config error:  Game properties keys '" + OPTIONAL_SHIPS_ORIENTATIONS
                        + "' and '" + OPTIONAL_SHIPS_START_POINTS + "' must have the same number of comma separated values.");
            }
        }

        String propsShipNames = getProperty(gameProps, OPTIONAL_SHIPS_NAMES, true);
        String[] shipsNames = {};
        if (propsShipNames != null) {
            shipsNames = propsShipNames.split(",");
        } else {
            shipsNames = new String[shipsDim.length];
            for (int i = 0; i < shipsNames.length; i++) {
                shipsNames[i] = "ship-" + i;
            }
        }
        for (int i = 0; i < shipsDim.length; i++) {
            // log("Ship dimensions = " + shipsDim[i]);
            String[] lenWidth = shipsDim[i].split("x");
            GameBoardShip ship = new GameBoardShip(Integer.parseInt(lenWidth[0].trim()), Integer.parseInt(lenWidth[1].trim()),
                    shipsNames[i]);

            // Handle specific location placement properties.
            if (startPointsString != null) {
                // Specific ship placement.
                String[] startRowCol = shipsStartPoints[i].split("-");
                placeShipOnBoard(ship, new Location(Integer.parseInt(startRowCol[0]), Integer.parseInt(startRowCol[1])),
                        shipsOrientations[i].equalsIgnoreCase("h"));
            } else {
                // Random ship placement.
                placeShipOnBoard(ship);
            }
        }

        _advancedWeaponsCache[WeaponType.TORPEDO.ordinal()] = Integer.parseInt(gameProps.getProperty("numTorpedos", "5"));
        _advancedWeaponsCache[WeaponType.MISSILE.ordinal()] = Integer.parseInt(gameProps.getProperty("numMissiles", "5"));
        _advancedWeaponsCache[WeaponType.ROCKET.ordinal()] = Integer.parseInt(gameProps.getProperty("numRockets", "5"));
        _advancedWeaponsCache[WeaponType.BOMB.ordinal()] = Integer.MAX_VALUE;
    }

    /**
     * @return Ships on the game board.
     */
    public GameBoardShip[] getShips() {
        return (GameBoardShip[]) _shipsOnGameBoard.toArray();
    }

    // Utility to get a property and handle null values that should not occur.
    private String getProperty(Properties prop, String pKey, boolean optional) {
        String value = prop.getProperty(pKey);
        if (value == null && !optional) {
            throw new GameConfigError("**** Config error: Game properties file did not have a value for key '" + pKey + "'");
        }
        return value;
    }

    public ShotStatus getLastShotStatus() {
        return _lastShotStatus;
    }

    protected Object clone() throws CloneNotSupportedException {

        Board gridClone = new Board();
        int R = getMaxBoardRows();
        int C = getMaxBoardCols();

        // Clone the game grid:
        gridClone._gameGrid = new GameSpace[R][C];
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                gridClone._gameGrid[r][c] = new GameSpace(r, c);
            }
        }

        // Now clone the ships. This is a bit icky.
        for (GameBoardShip ship : _shipsOnGameBoard) {
            GameBoardShip shipClone = new GameBoardShip(ship.getLength(), ship.getWidth(), ship.getName());
            shipClone.setOccupiedSpace(new ArrayList<GameSpace>());
            gridClone._shipsOnGameBoard.add(shipClone);
            for (GameSpace space : ship._occupiedSpace) {
                int x = space.getLocation().getRow();
                int y = space.getLocation().getCol();
                shipClone._occupiedSpace.add(gridClone._gameGrid[x][y]);
                gridClone._gameGrid[x][y].setShip(shipClone);
            }
        }
        for (int i = 0; i < _advancedWeaponsCache.length; i++) {
            gridClone._advancedWeaponsCache[i] = _advancedWeaponsCache[i];
        }

        return gridClone;
    }

    public Map<WeaponType, Integer> getWeaponCounts() {
        Map<WeaponType, Integer> result = new HashMap<WeaponType, Integer>();
        for (WeaponType wt : WeaponType.values()) {
            result.put(wt, (wt != WeaponType.BOMB) ? _advancedWeaponsCache[wt.ordinal()] : Integer.MAX_VALUE);
        }
        return result;
    }
}
