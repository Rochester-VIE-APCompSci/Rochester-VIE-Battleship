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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Implementation of the Platform interface. This is the main interface to start
 * and play the Battleship using the student-created Player class.
 */
@Copyright(Copyright.c2014)
public class PlatformImpl {

    private Board masterBoard = null;
    private boolean promptForBoards = false;
    private String boardConfiguration = "sampleBoards/Classic-1.properties";
    private List<PlayerConnection> players = new ArrayList<PlatformImpl.PlayerConnection>();
    private boolean gameOver = false;
    private Barrier barrier;
    private Set<PlayerConnection> winners = new HashSet<PlatformImpl.PlayerConnection>();
    private int numberOfGamesToPlay = 1;
    private ThreadMonitor threadMonitor = new ThreadMonitor();
    private boolean useGUI = false;
    protected static long delayInMillis = 0;
    protected final static long DEFAULT_DELAY = 300;
    private boolean showShipLocationsInGui = true;
    //private boolean advancedGame = true;
    private long playerThreadTimeout = 5 * 1000;
    private StatsPlugin statsPlugin = new DefaultStatsPlugin();
    private String stats = null;
    private static int nextId = 0;
    private String boards = null;

    /**
     * Main
     * 
     * @param args
     *            Note that the parameters are documented as a unit here,
     *            however the parameter name (e.g. --board) and the parameter
     *            value (e.g. sampleBoards/Classic-1.properties) must be passed
     *            as separate arguments in the args array.
     *            <dl>
     *            <dt>--board {board-name}</dt>
     *            <dd>File name of a board to play, such as
     *            sampleBoards/Classic-1.properties.</dd>
     * 
     *            <dt>--player {class-name-of-player}</dt>
     *            <dd>The fully-qualified class name (e.g.
     *            gage.simpson_homer.BadPlayer) of a player implementation. The
     *            player will play all of the boards specified.
     *            <p>
     *            Multiple players can be specified by repeating this parameter.
     *            </p>
     *            </dd>
     * 
     *            <dt>--boards {board-directory}</dt>
     *            <dd>The name of a directory containing one more boards to
     *            play. Any file whose extension is .properties will be loaded
     *            and played.</dd>
     * 
     *            <dt>--N {integer}</dt>
     *            <dd>The number of games to play. This argument is most useful
     *            with random board placement or with a player whose play is not
     *            predictable.</dd>
     * 
     *            <dt>--advanced</dt>
     *            <dd>Play the advanced game. The default is to play the basic
     *            game. When specified, any players specified must implement the
     *            AdvancedPlayer interface.</dd>
     * 
     *            <dt>--gui</dt>
     *            <dd>Show the GUI for each game.</dd>
     * 
     *            <dt>--delay {number-of-milliseconds}</dt>
     *            <dd>When --gui is specified, this delay slows the GUI updates
     *            so you can observe the progress of the game.</dd>
     * 
     *            <dt>--showShips {boolean}</dt>
     *            <dd>When --gui is specified, each ship will be shown on the
     *            board in a different color. Otherwise you will not know the
     *            position of the ships until they are hit or sunk. This can
     *            help you debug your player.</dd>
     *            </dl>
     */
    public static void main(String args[]) {

        Log.instance().setLoggingLevel(Log.Warning);
        Log.instance().log("Battleship Platform Version " + Version.getVersion() + "." + Version.getRevision(), Log.Event);
        Log.instance().log("Copyright 2014 International Business Machines Corporation", Log.Event);

        PlatformImpl platform = new PlatformImpl();
        boolean timeoutWasSet = false;

        Log.instance().log("Num arguments: " + args.length, Log.Event);
        for (int i = 0; i < args.length; i++) {
            Log.instance().log("arg[" + i + "]=" + args[i], Log.Event);
            if ("--board".equals(args[i])) {
                platform.setBoardConfiguration(args[++i]);
                Log.instance().log("arg[" + i + "]=" + args[i], Log.Event);
            } else if ("--boards".equals(args[i])) {
                platform.setBoards(args[++i]);
            } else if ("--prompt".equals(args[i])) {
              platform.setPromptForBoards(true);
            } else if ("--player".equals(args[i])) {
                platform.addPlayer(args[++i]);
                Log.instance().log("arg[" + i + "]=" + args[i], Log.Event);
            } else if ("--N".equals(args[i])) {
                platform.setNumberOfGamesToPlay(Integer.parseInt(args[++i]));
                Log.instance().log("arg[" + i + "]=" + args[i], Log.Event);
            } else if ("--gui".equals(args[i])) {
                platform.setUseGUI(true);
            } else if ("--debug".equals(args[i])) {
                Log.instance().setLoggingLevel(Log.instance().getLoggingLevel() + 1);
            } else if ("--delay".equals(args[i])) {
                platform.setDelayInMillis(Long.parseLong(args[++i]));
            } else if ("--showShips".equals(args[i])) {
                platform.showShipLocationsInGui = Boolean.parseBoolean(args[++i]);
                Log.instance().log("arg[" + i + "]=" + args[i], Log.Event);
            
//            } else if ("--advanced".equals(args[i])) {
//                platform.advancedGame = true;
            } else if ("--timeout".equals(args[i])) {
                timeoutWasSet = true;
                platform.setPlayerThreadTimeout(Long.parseLong(args[++i]));
                Log.instance().log("arg[" + i + "]=" + args[i], Log.Event);
            } else if ("--stats".equals(args[i])) {
                platform.setStats(args[++i]);
            } else {
                System.out.println("? " + args[i]);
                System.out.println("Usage: PlatformImpl [--board <config.file>] [--player <player-class]* ");
                System.out.println("                    [--N <num-games>] [--debug]*");
                System.out.println("                    [--gui] [--showShips {true|false}] [--delay <millis>]");
                System.out.println("                    [--advanced] [--timeout <millis>]");
                return;
            }
        }

        // Single player game ... disable thread timeout to support debugging
        if (!timeoutWasSet && platform.players.size() == 1) {
            platform.playerThreadTimeout = Long.MAX_VALUE / 1000;
        }

        try {
            platform.start();
        } catch (Throwable t) {
            Log.instance().log(t, Log.Error);
        }
    }

    public static void runWithBoardPrompt(Class<? extends Player> playerClass, boolean isGui) {
        onePlayerQuickStart(null, playerClass, isGui);
    }

    public static void runWithSpecificBoard(String board, Class<? extends Player> playerClass, boolean isGui) {
        onePlayerQuickStart(board, playerClass, isGui);
    }

    public static void runAllBoards(Class<? extends Player> playerClass, boolean isGui) {
        final String boardLocation = "sampleBoards";

        for (int i=1; i <=6; i++) {
            onePlayerQuickStart(boardLocation + "/Classic-" + i + ".properties", playerClass, isGui);
        }
    }

    /**
     * Convenience method for the students who invoke this via code, not via the
     * command line.
     * 
     * @param board
     *            Name of the board file: a resource name in the jar or the path
     *            to an external file.
     * @param playerClass
     *            The class object for the player implementation.
     * @param isGui
     *            true to display the GUI with a 200 msec delay between shots
     *            and show the placement of the ships on the board. This
     *            optimizes the GUI so you can observe the play of your Player
     *            implementation.
     * 
     */
    public static void onePlayerQuickStart(String board, Class<? extends Player> playerClass, boolean isGui) {
        Log.instance().setLoggingLevel(Log.Warning);

        PlatformImpl platform = new PlatformImpl();

        platform.playerThreadTimeout = Long.MAX_VALUE / 1000;

        if (board == null) {
          platform.setPromptForBoards(true);
        } else {
          platform.setBoardConfiguration(board);
        }
        
        platform.addPlayer(playerClass);

        if (isGui) {
            platform.setUseGUI(true);
            setDelayInMillis(DEFAULT_DELAY);
            platform.showShipLocationsInGui = true;
        }

        try {
            platform.start();
        } catch (Throwable t) {
            Log.instance().log(t, Log.Error);
        }
    }
    
    /**
     * Convenience method for the students who invoke this via code, not via the
     * command line. This invocation should prompt the user for a board to play.
     * 
     * @param playerClass
     *            The class object for the player implementation.
     * @param isGui
     *            true to display the GUI with a 200 msec delay between shots
     *            and show the placement of the ships on the board. This
     *            optimizes the GUI so you can observe the play of your Player
     *            implementation.
     * 
     */
    public static void onePlayerQuickStart(Class<? extends Player> playerClass, boolean isGui) {
      onePlayerQuickStart(null, playerClass, isGui);
    }

    public long getPlayerThreadTimeout() {
        return playerThreadTimeout;
    }

    public void setPlayerThreadTimeout(long playerThreadTimeout) {
        this.playerThreadTimeout = playerThreadTimeout;
    }

//    synchronized boolean isAdvancedGame() {
//        return advancedGame;
//    }
//
//    synchronized void setAdvancedGame(boolean advancedGame) {
//        this.advancedGame = advancedGame;
//    }

    public void setUseGUI(boolean useGUI) {
        this.useGUI = useGUI;
    }

    public void setPromptForBoards(boolean promptForBoards) {
      this.promptForBoards = promptForBoards;
    }
    
    public boolean isPromptForBoards() {
      return promptForBoards;
    }
    
    public int getNumberOfGamesToPlay() {
        return numberOfGamesToPlay;
    }

    public void setNumberOfGamesToPlay(int numberOfGamesToPlay) {
        this.numberOfGamesToPlay = numberOfGamesToPlay;
    }

    public String getBoardConfiguration() {
        return boardConfiguration;
    }

    public void setBoardConfiguration(String boardConfiguration) {
        this.boardConfiguration = boardConfiguration;
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
    }

    private synchronized boolean isGameOver() {
        return gameOver;
    }

    private static synchronized int nextId() {
        return nextId++;
    }

    private synchronized void setGameOver(boolean gameOver) {
        if ((Log.instance().getLoggingLevel() >= Log.GoryDetail) && gameOver) {
            Log.instance().log("Game Over via this:", Log.GoryDetail);
            Log.instance().log(new NullPointerException("Not really null"), Log.GoryDetail);
        }

        this.gameOver = gameOver;
    }

    public String getBoards() {
        return boards;
    }

    public void setBoards(String boards) {
        this.boards = boards;
    }

    @SuppressWarnings("unchecked")
    void start() {
        Log.instance().log("Starting PlatformImpl", Log.Debug);
        
        if (this.stats != null) {
            try {
                Class<StatsPlugin> stats = ((Class<StatsPlugin>) Class.forName(this.stats));
                this.statsPlugin = (StatsPlugin) stats.newInstance();
            } catch (Throwable t) {
                Log.instance().log(t, Log.Error);
            }
        }
        
        if (promptForBoards) {
          // If we're going to prompt, and a boards directory was not specified, set to default
          if (boards == null) {
            boards = "sampleBoards";
          }
        }

        if (boards != null) {
            File boardsDir = new File(boards);
            if (!boardsDir.isDirectory()) {
                Log.instance().log("Not a directory: " + boards, Log.Error);
                return;
            }
            
            JFileChooser chooser = null;
            chooser = new JFileChooser(boards);
            FileNameExtensionFilter boardFilter = new FileNameExtensionFilter("Board Properties", "properties");
            chooser.setFileFilter(boardFilter);
            chooser.setMultiSelectionEnabled(true);
            
            do {
              
              File games[] = null;
              
              if (promptForBoards) {
                // Prompt for file(s) in boards directory
                int result = chooser.showDialog(null, "Play Board(s)");
                if (result == JFileChooser.APPROVE_OPTION) {
                  games = chooser.getSelectedFiles();
                } else {
                  break;
                }
              } else {
                // All property files in boards directory
                games = boardsDir.listFiles(new FilenameFilter() {
                  @Override
                  public boolean accept(File dir, String name) {
                    return name.endsWith(".properties");
                  }
                });
              }
              if (games != null) {
                for (int n = 0; n < numberOfGamesToPlay; n++) {
                  for (int i = 0; i < games.length; i++) {
                    Log.instance().log("---- Game " + (i + 1) + "-" + n + " board: " + games[i].getName() + " ---", Log.Event);
                    this.boardConfiguration = games[i].getPath();
                    playGame();
                  }
                }
              }
            } while(promptForBoards); // if we're prompting, this will loop until we get a non-approve action and break. Otherwise, we'll have iterated this loop once
        } else {
            for (int i = 0; i < numberOfGamesToPlay; i++) {
                Log.instance().log("---- Game " + (i + 1) + " board: " + boardConfiguration + " ---", Log.Event);
                playGame();
            }
        }
    }

    private void playGame() {

        Log.instance().log("Starting game", Log.Debug);
        int playerCount = players.size();
        int screenId = 0;
        this.barrier = new Barrier(playerCount);
        this.masterBoard = new Board(boardConfiguration);
        this.winners.clear();
        this.statsPlugin.setBoardSize(this.masterBoard.getMaxBoardRows(), this.masterBoard.getMaxBoardCols());
        setGameOver(true);

        for (PlayerConnection player : this.players) {
            setGameOver(false);
            player.start(screenId, playerCount);
            screenId++;
        }

        while (!isGameOver()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                // ignore
            }
        }

        for (PlayerConnection player : this.players) {
            try {
                Log.instance().log("Waiting for player thread " + player.player.getName() + " to die ...", Log.Debug);
                player.getThread().join(getPlayerThreadTimeout() + 1000);
            } catch (InterruptedException e) {
                Log.instance().log(e, Log.Error);
            }
        }

        collateWinners();
    }

    private void collateWinners() {
        if (winners.size() == 1) {
            PlayerConnection pc = winners.iterator().next();
            Log.instance().log(
                    pc.player.getName() + " is the winner! Shots:" + pc.getShotTotal(),
                    Log.Event);
        } else {

            // Arrange winners by shot total:
            Map<Long, List<PlayerConnection>> collation = new HashMap<Long, List<PlayerConnection>>();
            for (PlayerConnection pc : winners) {
                if (!collation.containsKey(pc.getShotTotal())) {
                    collation.put(pc.getShotTotal(), new ArrayList<PlayerConnection>());
                }
                collation.get(pc.getShotTotal()).add(pc);
            }

            // Sort the shot totals:
            List<Long> shotTotals = new ArrayList<Long>();
            shotTotals.addAll(collation.keySet());
            Collections.sort(shotTotals);

            // Product the list
            int ordinal = 1;

            for (Long t : shotTotals) {
                List<PlayerConnection> players = collation.get(t);
                boolean isTie = players.size() > 1;
                for (PlayerConnection pc : players) {
                    String showing;
                    long shotTotal;
                    if (pc.getShotTotal() == Long.MAX_VALUE) {
                        showing = "DQ";
                        shotTotal = 9999;
                    } else {
                        showing = String.format("%1$s%2$-2d", (isTie) ? "T" : " ", ordinal);
                        shotTotal = pc.getShotTotal();
                    }

                    String result = String.format("| %1$3s | %2$-24s | %3$4d | %4$-24s |", showing, pc.player.getName(),
                            shotTotal, pc.player.getSchool());
                    Log.instance().log(result, Log.Event);
                    statsPlugin.accumulateDataPoint(pc.getName(), pc.player, pc.getShotTotal(), ordinal, players.size());

                }

                ordinal += players.size();
            }
            statsPlugin.dump();
        }
    }

    public void addPlayer(String className) {
        players.add(new PlayerConnection(className));
    }

    public void addPlayer(Class<? extends Player> clazz) {
        players.add(new PlayerConnection(clazz));
    }

    synchronized void gameOver(PlayerConnection pc) {
        winners.add(pc);
        if (winners.size() == players.size()) {
            setGameOver(true);
        }
    }

    /**
     * PlayerConnection provides threading and state for a single player.
     */
    @Copyright(Copyright.c2014)
    private class PlayerConnection implements AdvancedPlatform, Runnable {

        private String playerClass = null;
        private Class<? extends Player> playerClassObj = null;
        private Player player;
        private Thread thread = null;
        private Board board = null;
        private GUI gui = null;
        private boolean disqualified;
        private int uniqueId;

        PlayerConnection(String pc) {
            this.playerClass = pc;
            this.uniqueId = nextId();
        }

        PlayerConnection(Class<? extends Player> clazz) {
            this(clazz.getCanonicalName());
            playerClassObj = clazz;
        }

        synchronized Thread getThread() {
            return thread;
        }

        synchronized void setThread(Thread thread) {
            this.thread = thread;
        }

        String getName() {
            return this.player.getName() + " [" + uniqueId + "]";
        }

        void start(int screenId, int playerCount) {

            // Clone the master board
            try {
                this.board = (Board) masterBoard.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e.getMessage());
            }

            // Instantiate the player object:
            try {
                Log.instance().log("Spawning player from class " + this.playerClass, Log.Debug);
                Class<?> clazz = this.playerClassObj != null ? playerClassObj : Class.forName(this.playerClass);
                this.player = (Player) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                throw new GameConfigError("Could not find player class \"" + this.playerClass
                        + "\".  Please check your classpath.");
            } catch (IllegalAccessException e) {
                throw new GameConfigError("Could not access your Player constructor.  Does it exist and is it public?");
            } catch (InstantiationException e) {
                throw new GameConfigError("Could not instantiate your Player object.  Do you have a public constructor?");
            } catch (ClassCastException e) {
                throw new GameConfigError("Player class " + playerClass + " does not implement interface "
                        + Player.class.getName() + ".");
            }

//            if ((advancedGame) && !(this.player instanceof FlexiblePlayer)) {
//                throw new GameConfigError("Player class " + playerClass + " is not an AdvancedPlayer");
//            }

            // Create the gui
            if (useGUI) {
                // gui = new GUI(this.player.getScreenName(),
                // board.getMaxBoardRows(), board.getMaxBoardCols());
                gui = new GUI(this.player.getName(), board.getMaxBoardRows(), board.getMaxBoardCols(), screenId,
                        playerCount);
                if (showShipLocationsInGui) {
                    gui.showShips(this.board);
                }
            }
            // Spin up a thread for this player.
            Log.instance().log("Spawning thread for player " + this.player.getName(), Log.Debug);
            Thread t = new Thread(this);
            t.setName(this.player.getName());
            t.setDaemon(true);
            t.start();
            setThread(t);
        }

        public long getShotTotal() {
            return isDisqualified() ? Long.MAX_VALUE : this.board.getShotTotal();
        }

        public int getNumberOfRows() {
            return this.board.getMaxBoardRows();
        }

        public int getNumberOfCols() {
            return this.board.getMaxBoardCols();
        }

        public ShotReply shoot(int row, int col) {
            return shoot(row, col, WeaponType.BOMB, Direction.NORTH);
            
            /* Old implementation that returned ShotStatus.
            Log.instance().log("(>) shoot(" + row + "," + col + ":" + (this.getShotTotal() + 1) + ")", Log.GoryDetail);

            if (isDisqualified()) {
                return ShotStatus.SUNK_ALL_YOU_LOSE;
            }
            // Do not allow access via any thread other than the connected
            // thread ... i.e. don't allow them to multi-thread their guessing.

            checkThread();

            // Take the shot:
            ShotStatus status = this.board.shoot(row, col);

            if (useGUI) {
                gui.recordShot(row, col, status, 1);
            }

            // Wait here for everyone. This is required in order to avoid a
            // thread leak --
            // Setting game over before everyone is known to have taken their
            // nth shot
            // could result in some thread seeing game over status on their
            // previous
            // shot (race condition).

            waitForAll();

            // Check for game over conditions:
            if (status == ShotStatus.SUNK_ALL_YOU_WIN) {
                Log.instance().log("All ships sunk at shot " + this.getShotTotal(), Log.Event);
                gameOver(this);
            } else if (this.getShotTotal() > (this.getNumberOfRows() * this.getNumberOfCols())) {
                Log.instance().log("Max shots exceeded ... giving up", Log.Event);
                gameOver(this);
                status = ShotStatus.SUNK_ALL_YOU_LOSE;
            }

            // Wait again to see if anyone is reporting game over:
            waitForAll();

            // If the game is over, determine if we won, lost or tied
            if (isGameOver()) {
                if (status == ShotStatus.SUNK_ALL_YOU_WIN) {
                    if (winners.size() > 1) {
                        status = ShotStatus.SUNK_ALL_YOU_TIE;
                    }
                } else {
                    status = ShotStatus.SUNK_ALL_YOU_LOSE;
                }
            }

            if (getDelayInMillis() > 0) {
                try {
                    Thread.sleep(getDelayInMillis());
                } catch (InterruptedException ie) {
                    Log.instance().log(ie, Log.Error);
                }
            }

            threadMonitor.updateState(this, ThreadMonitor.IN_USER_CODE);
            Log.instance().log("shoot(" + row + "," + col + ":" + this.getShotTotal() + ") -> " + status, Log.Debug);
            // Return status to the user:
            return status;
            */
        }

        public void run() {
            try {
                setDisqualified(false);
                threadMonitor.updateState(this, ThreadMonitor.IN_USER_CODE);
//                if (advancedGame) {
                    this.player.startGame(this);
//                } else {
//                    throw new Throwable("Application error: only use FlexiblePlatform to run the game.");
//                    //this.player.startGame(this);
//                }

                // We are done but others may not be. Spin for up to the number
                // of cells in the board ... this should allow enough time for
                // even bad players to complete. This also effectively becomes
                // an overall timeout for the game.

                if (!winners.contains(this)) {
                    setDisqualified(true);
                    gameOver(this);
                    spin();
                } else {
                    spinFor(getNumberOfCols() * getNumberOfRows());
                    setGameOver(true);
                }
            } catch (Throwable t) {
                // If the player throws an exception ... we must deal with it
                Log.instance().log("Player thread has died", Log.Error);
                Log.instance().log(t, Log.Error);
                setDisqualified(true);
                gameOver(this);
                spin();
            } finally {
                threadMonitor.stopMonitoring(this);
            }
        }
 
        private void spin() {
            while (!isGameOver()) {
                try {
                    barrier.waitForAll();
                } catch (InterruptedException e) {
                    Log.instance().log(e, Log.Error);
                }
            }
        }

        private void spinFor(int n) {
            threadMonitor.updateState(this, ThreadMonitor.BARRIERED);
            for (int i = 0; (i < n * 2) && !isGameOver(); i++) {
                try {
                    barrier.waitForAll();
                } catch (InterruptedException e) {
                    Log.instance().log(e, Log.Warning);
                }
            }
        }

        private void waitForAll() {
            try {
                threadMonitor.updateState(this, ThreadMonitor.BARRIERED);
                barrier.waitForAll();
                threadMonitor.updateState(this, ThreadMonitor.OUT_OF_BARRIER);
            } catch (InterruptedException ie) {
                Log.instance().log(ie, Log.Warning);
            }
        }

        private void checkThread() {
            if (!getThread().equals(Thread.currentThread())) {
                gameOver(this); // @todo fix me
                throw new IllegalStateException("(E) Threading violation for " + playerClass + " => " + player.getName());
            }
        }

        @Override
        public ShotReply shoot(int row, int col, WeaponType wtype, Direction direction) {

            Log.instance().log(
                    "(>) shoot(" + row + "," + col + ", " + wtype + ", " + direction + " : " + (this.getShotTotal() + 1) + ")",
                    Log.GoryDetail);

            // if (wtype == WeaponType.BOMB) {
            // ShotStatus status = shoot(row, col);
            // ShotReply reply = new ShotReply(status, row, col, 1);
            // return reply;
            // }

            // Do not allow access via any thread other than the connected
            // thread ... i.e. don't allow them to multi-thread their guessing.

            checkThread();

//            if (!advancedGame) {
//                Log.instance().log("Platform mismatch.", Log.Error);
//                throw new IllegalStateException("This is not an advanced game");
//            }

            AdvancedWeapon weapon = AdvancedWeapon.Factory.create(wtype, row, col, direction);
            ShotReply reply = board.shoot(weapon);

            if (useGUI) {

                boolean done = false;
                int cost = reply.getCost();

                for (int i = 0, N = weapon.getNumberOfShots(); (i < N) && !done; i++) {
                    Location loc = weapon.getLocation(i);
                    ShotStatus status = ShotStatus.MISS;
                    if ((reply.getStatus() != ShotStatus.MISS) && (reply.getRow() == loc.getRow())
                            && (reply.getCol() == loc.getCol())) {
                        status = reply.getStatus();
                        done = true;
                    }
                    gui.recordShot(loc.getRow(), loc.getCol(), status, cost);
                    cost = 0;
                    if (getDelayInMillis() > 0) {
                        try {
                            Thread.sleep(getDelayInMillis() / 4);
                        } catch (InterruptedException ie) {
                            Log.instance().log(ie, Log.Error);
                        }
                    }
                }
            }

            spinFor(reply.getCost() - 1);

            // Wait here for everyone. This is required in order to avoid a
            // thread leak. Setting game over before everyone is known to
            // have taken their nth shot could result in some thread seeing
            // game over status on their previous shot (race condition).

            waitForAll();

            // Check for game over conditions:
            if (reply.getStatus() == ShotStatus.SUNK_ALL_YOU_WIN) {
                Log.instance().log("All ships sunk at shot " + this.getShotTotal(), Log.Event);
                gameOver(this);
            } else if (this.getShotTotal() >= (this.getNumberOfRows() * this.getNumberOfCols())) {
                Log.instance().log("Max shots exceeded ... giving up", Log.Event);
                gameOver(this);
                reply.setStatus(ShotStatus.SUNK_ALL_YOU_LOSE);
                if(useGUI) {
                    gui.setGameOverYouLose();
                }
            }

            // Wait again to see if anyone is reporting game over:
            waitForAll();

            // If the game is over, determine if we won, lost or tied
            if (isGameOver()) {
                if (reply.getStatus() == ShotStatus.SUNK_ALL_YOU_WIN) {
                    if (winners.size() > 1) {
                        reply.setStatus(ShotStatus.SUNK_ALL_YOU_TIE);
                    }
                } else if (reply.getStatus() == ShotStatus.SHOT_AFTER_GAME_OVER) {
                    // do nothing
                } else {
                    reply.setStatus(ShotStatus.SUNK_ALL_YOU_LOSE);
                }
            }

            if (getDelayInMillis() > 0) {
                try {
                    Thread.sleep(getDelayInMillis());
                } catch (InterruptedException ie) {
                    Log.instance().log(ie, Log.Error);
                }
            }
            threadMonitor.updateState(this, ThreadMonitor.IN_USER_CODE);
            Log.instance().log(
                    "shoot(" + row + "," + col + "," + wtype + "," + direction + ":" + this.getShotTotal() + ") -> " + reply,
                    Log.Debug);

            // Return status to the user:
            return reply;
        }

        @Override
        public final List<Ship> listShips() {
            return this.board.listShips();
        }

        @Override
        public Map<WeaponType, Integer> getWeaponCounts() {
            return this.board.getWeaponCounts();
        }

        synchronized boolean isDisqualified() {
            return disqualified;
        }

        synchronized void setDisqualified(boolean disqualified) {
            this.disqualified = disqualified;
        }

  @Override
  public List<Ship> listSunkShips() {
   return this.board.listSunkShips();
  }
    }

    /**
     * A data point for the thread monitor:
     */
    private static class Datum {
        int lastStatus;
        long timeOfLastUpdate;

        Datum(int st, long ts) {
            lastStatus = st;
            timeOfLastUpdate = ts;
        }
    }

    /**
     * The ThreadMonitor will watch over user connections and evict any that are
     * hung
     */
    @Copyright(Copyright.c2014)
    private class ThreadMonitor implements Runnable {

        final static int BARRIERED = 2;
        final static int OUT_OF_BARRIER = 3;
        final static int IN_USER_CODE = 4;

        Map<PlayerConnection, Datum> state = Collections.synchronizedMap(new HashMap<PlatformImpl.PlayerConnection, Datum>());

        ThreadMonitor() {
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.setName("ThreadMonitor");
            t.start();
        }

        void updateState(PlayerConnection conn, int s) {
            if (!state.containsKey(conn)) {
                Log.instance().log("starting monitoring of " + conn, Log.GoryDetail);
                Log.instance().log(new NullPointerException(), Log.GoryDetail);
            }
            state.put(conn, new Datum(s, System.currentTimeMillis()));
        }

        void stopMonitoring(PlayerConnection conn) {
            Log.instance().log("ending monitoring of " + conn, Log.GoryDetail);
            Log.instance().log(new NullPointerException(), Log.GoryDetail);
            state.remove(conn);
        }

        @Override
        public void run() {

            List<PlayerConnection> hungPlayers = new ArrayList<PlatformImpl.PlayerConnection>();

            while (true) {

                try {
                    // Sweep through the connections and look for any that have
                    // not
                    // been updated in a while:
                    long current = System.currentTimeMillis();
                    int threadsInBarriers = 0;

                    for (PlayerConnection p : state.keySet()) {

                        Datum d = state.get(p);

                        if (d != null) {
                            switch (d.lastStatus) {
                            case IN_USER_CODE: {
                                if ((current - d.timeOfLastUpdate) > getPlayerThreadTimeout()) {
                                    Log.instance().log(
                                            "User thread " + p.player.getName() + " has been stuck for "
                                                    + (current - d.timeOfLastUpdate) + " millis.", Log.Error);
                                    hungPlayers.add(p);
                                }
                                break;
                            }

                            default: {
                                if (d.lastStatus == BARRIERED)
                                    threadsInBarriers++;
                                if ((current - d.timeOfLastUpdate) > getPlayerThreadTimeout()) {
                                    Log.instance().log(
                                            "User thread " + p.player.getName() + " stuck in state " + d.lastStatus,
                                            Log.Debug);
                                }
                            }
                            }
                        }
                    }

                    Log.instance().log("There are " + hungPlayers.size() + " hung players out of " + state.size() + ".",
                            Log.GoryDetail);

                    if (hungPlayers.size() > 0) {
                        // For any hung threads, create proxies that will take
                        // their
                        // place in the barriers:

                        for (PlayerConnection p : hungPlayers) {
                            Log.instance().log("Disconnecting " + p.player.getName(), Log.Event);
                            new Proxy(p);
                            p.setDisqualified(true);
                            stopMonitoring(p);
                            gameOver(p);
                        }

                        hungPlayers.clear();
                    } else if (isGameOver() && (threadsInBarriers > 0)) {
                        Log.instance().log("Resetting barrier to release " + threadsInBarriers + " threads.", Log.Debug);
                        barrier.reset();
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }
                } catch (Throwable t) {
                    // Dont allow the monitor to die ....log and go on
                    Log.instance().log(t, Log.Warning);
                }
            }
        }
    }

    @Copyright(Copyright.c2014)
    private class Proxy implements Runnable {

        PlayerConnection conn;

        Proxy(PlayerConnection conn) {
            this.conn = conn;
            Thread t = new Thread(this);
            t.setName("Proxy-for-" + conn.player.getName());
            t.setDaemon(true);
            t.start();
            conn.setThread(t);
        }

        @Override
        public void run() {
            try {
                Log.instance().log("Starting proxy for " + conn + " gameOver=" + isGameOver(), Log.Event);
                while (!isGameOver()) {
                    barrier.waitForAll();
                }
                Log.instance().log("Proxy ended for " + conn + " gameOver=" + isGameOver(), Log.Event);
                threadMonitor.stopMonitoring(conn);

            } catch (InterruptedException e) {
                Log.instance().log(e, Log.Error);
                Log.instance().log("Proxy abended for " + conn, Log.Event);

            }
        }

    }

    public static synchronized long getDelayInMillis() {
        return delayInMillis;
    }

    public static synchronized void setDelayInMillis(long delay) {
        delayInMillis = delay;
    }

    private static class DefaultStatsPlugin implements StatsPlugin {

        @Override
        public void setBoardSize(int rows, int cols) {
            // TODO Auto-generated method stub

        }

        Map<String, DefaultStatsDatum> data = new HashMap<String, PlatformImpl.DefaultStatsPlugin.DefaultStatsDatum>();
        Map<String, DefaultStatsDatum> bySchool = new HashMap<String, PlatformImpl.DefaultStatsPlugin.DefaultStatsDatum>();

        public void accumulateDataPoint(String key, Player player, long shotCount, int rank, int share) {

            Log.instance().log(
                    "DefaultStatsPlugin.accumulate key=" + key + " player=" + player.getName() + " shots=" + shotCount
                            + " rank=" + rank + " share=" + share, Log.Debug);

            double points = 0.0;

            switch (rank) {
            case 1: {
                switch (share) {
                case 1:
                    points = 3.0;
                    break;
                case 2:
                    points = 3.0 + 2.0;
                    break;
                default:
                    points = 6.0;
                }
                break;
            }
            case 2: {
                points = (share > 1) ? 3.0 : 2.0;
                break;
            }
            case 3: {
                points = 1.0;
                break;
            }
            default:
                points = 0.0;
            }

            points /= share;

            if (points > 0.0) {
                if (!data.containsKey(key)) {
                    data.put(key, new DefaultStatsDatum(key, player));
                }
                data.get(key).pointTotal += points;

                String schoolKey = (player.getSchool() != null) ? player.getSchool().toLowerCase().trim() : "???";

                // Hack - deal with different variants of the Rochester schools:
                if (schoolKey.startsWith("mayo")) {
                    schoolKey = "Mayo";
                } else if (schoolKey.startsWith("cent")) {
                    schoolKey = "Century";
                } else if (schoolKey.startsWith("john")) {
                    schoolKey = "John Marshall";
                }
                if (!bySchool.containsKey(schoolKey)) {
                    bySchool.put(schoolKey, new DefaultStatsDatum(schoolKey, null));
                }
                bySchool.get(schoolKey).pointTotal += points;
            }
        }

        public void dump() {
            individualLeaderboard();
            schoolLeaderboard();
        }

        void individualLeaderboard() {

            // Sort the leaderboard:
            List<DefaultStatsDatum> leaderboard = new ArrayList<PlatformImpl.DefaultStatsPlugin.DefaultStatsDatum>();
            leaderboard.addAll(data.values());
            Collections.sort(leaderboard, new Comparator<DefaultStatsDatum>() {
                public int compare(my.battleship.PlatformImpl.DefaultStatsPlugin.DefaultStatsDatum p1,
                        my.battleship.PlatformImpl.DefaultStatsPlugin.DefaultStatsDatum p2) {
                    double diff = p1.pointTotal - p2.pointTotal;
                    return (diff > 0) ? 1 : ((diff < 0) ? -1 : 0);
                }
            });
            Collections.reverse(leaderboard);

            double grandTotal = 0.0;
            for (DefaultStatsDatum p : leaderboard) {
                grandTotal += p.pointTotal;
            }

            Log.instance().log("+--------------------------+----------------+---------+---------+", Log.Event);
            Log.instance().log(String.format("| %1$-24s | %2$-14s | %3$7s | %4$6s |", "PLAYER", "SCHOOL", "POINTS", "PERCENT"),
                    Log.Event);
            Log.instance().log("+--------------------------+----------------+---------+---------+", Log.Event);

            for (DefaultStatsDatum p : leaderboard) {
                String skool = p.player.getSchool();
                if (skool == null) {
                    skool = "?";
                } else if (skool.length() > 14) {
                    skool = skool.substring(0, 14);
                }

                Log.instance().log(                   
                        String.format("| %1$-24s | %2$-14s | %3$7.1f | %4$6.3f%% |", p.key, skool, p.pointTotal,
                                p.pointTotal * 100.0 / grandTotal), Log.Event);
            }
            Log.instance().log("+--------------------------+----------------+---------+---------+", Log.Event);

        }

        void schoolLeaderboard() {

            // Sort the leaderboard:
            List<DefaultStatsDatum> leaderboard = new ArrayList<PlatformImpl.DefaultStatsPlugin.DefaultStatsDatum>();
            leaderboard.addAll(bySchool.values());
            Collections.sort(leaderboard, new Comparator<DefaultStatsDatum>() {
                public int compare(my.battleship.PlatformImpl.DefaultStatsPlugin.DefaultStatsDatum p1,
                        my.battleship.PlatformImpl.DefaultStatsPlugin.DefaultStatsDatum p2) {
                    double diff = p1.pointTotal - p2.pointTotal;
                    return (diff > 0) ? 1 : ((diff < 0) ? -1 : 0);
                }
            });
            Collections.reverse(leaderboard);

            double grandTotal = 0.0;
            for (DefaultStatsDatum p : leaderboard) {
                grandTotal += p.pointTotal;
            }

            String sep = "+------------------+---------+---------+";
            Log.instance().log(sep, Log.Event);
            Log.instance().log(String.format("| %1$-16s | %2$7s | %3$6s |", "SCHOOL", "POINTS", "PERCENT"), Log.Event);
            Log.instance().log(sep, Log.Event);

            for (DefaultStatsDatum p : leaderboard) {
                Log.instance().log(
                        String.format("| %1$-16s | %2$7.1f | %3$6.3f%% |", p.key, p.pointTotal, p.pointTotal * 100.0
                                / grandTotal), Log.Event);
            }
            Log.instance().log(sep, Log.Event);
        }

        private static class DefaultStatsDatum {
            String key;
            Player player;
            double pointTotal;

            DefaultStatsDatum(String k, Player pc) {
                this.key = k;
                this.player = pc;
                this.pointTotal = 0.0;
            }
        }

    }
}
