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
package com.ibm.battleship.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import my.battleship.Copyright;
import my.battleship.Direction;
import my.battleship.Log;
import my.battleship.Platform;
import my.battleship.AdvancedPlatform;
import my.battleship.PlatformImpl;
import my.battleship.Player;
import my.battleship.Ship;
import my.battleship.ShotReply;
import my.battleship.WeaponType;

@Copyright(Copyright.c2014)
public class ProbabilityDensity implements Player {

    private int R; // number of rows
    private int C; // number of cols
    private List<Ship> shipsRemaining;
    private int density[][];
    private boolean tried[][];
    private Random rand = new Random();

    public void startGame(Platform platform) {
        Log.instance().log("ProbabilityDensity is an advanced player ... use --advanced.", Log.Error);
    }

    public String getName() {
        return "Density";
    }

    public String getSchool() {
        return "Gustavus";
    }

    private List<Coord> highPriorityTargets = new ArrayList<ProbabilityDensity.Coord>();

    public void startAdvancedGame(Platform p) {
        AdvancedPlatform ap = (AdvancedPlatform) p;
        init(ap);

        boolean done = false;

        while (!done) {

            // If there is a high priority target on the list, we will use it.  Otherwise
            // use probability density to pick the next target:
            
            Coord target = nextPriorityTarget();

            if (target == null) {
                target = nextDensityTarget();
            }

            Log.instance().log("Targeting " + target + " legal=" + isLegal(target), Log.Debug);
            ShotReply reply = ap.shoot(target.r, target.c, WeaponType.BOMB, Direction.EAST);

            tried[target.r][target.c] = true;
            switch (reply.getStatus()) {
            case MISS:
                break;
            case SUNK_ALL_YOU_WIN:
            case SUNK_ALL_YOU_LOSE:
            case SUNK_ALL_YOU_TIE:
                done = true;
                break;
            case SUNK_SHIP:
                Ship toBeRemoved = null;
                for (Ship ship : this.shipsRemaining) {
                    if (ship.getName().equals(reply.getShipName())) {
                        toBeRemoved = ship;
                    }
                }
                this.shipsRemaining.remove(toBeRemoved);
                break;
            case HIT:
                prioritize(target);
                break;
            default:
                // do nothing
                break;
            }
        }
    }

    private void prioritize(Coord x) {
        highPriorityTargets.add(new Coord(x.r - 1, x.c - 1));
        highPriorityTargets.add(new Coord(x.r - 1, x.c));
        highPriorityTargets.add(new Coord(x.r - 1, x.c + 1));
        highPriorityTargets.add(new Coord(x.r, x.c - 1));
        highPriorityTargets.add(new Coord(x.r, x.c + 1));
        highPriorityTargets.add(new Coord(x.r + 1, x.c - 1));
        highPriorityTargets.add(new Coord(x.r + 1, x.c));
        highPriorityTargets.add(new Coord(x.r + 1, x.c + 1));
    }

    private Coord nextDensityTarget() {
        computeDensity();
        List<Coord> max = findMaxDensity();

        Coord example = max.get(0);
        Log.instance().log("Density = " + density[example.r][example.c] + " @ " + max.size() + " locations", Log.Debug);

        int n = rand.nextInt(max.size());

        Coord choice = max.get(n);
        return choice;
    }

    boolean isLegal(Coord coord) {
        return (coord != null) && (coord.r >= 0) && (coord.r < R) && (coord.c >= 0) && (coord.c < C);
    }

    boolean wasTried(Coord c) {
        return tried[c.r][c.c];
    }

    private Coord nextPriorityTarget() {
        while (highPriorityTargets.size() > 0) {
            Coord result = highPriorityTargets.remove(0);
            if (isLegal(result) && !wasTried(result))
                return result;
        }
        return null;
    }

    private void computeDensity() {
        this.density = new int[R][C];
        for (Ship ship : shipsRemaining) {
            int L = ship.getLength();
            int W = ship.getWidth();
            for (int r = 0; r < R; r++) {
                for (int c = 0; c < C; c++) {
                    if (couldFit(r, c, L, W))
                        plusOne(r, c, L, W);
                    if (couldFit(r, c, W, L))
                        plusOne(r, c, W, L);
                }
            }
        }
    }

    private void plusOne(int r, int c, int L, int W) {
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < W; j++) {
                this.density[r + i][c + j]++;
            }
        }
    }

    private List<Coord> findMaxDensity() {
        List<Coord> result = new ArrayList<ProbabilityDensity.Coord>();
        int max = Integer.MIN_VALUE;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (density[r][c] > max) {
                    result.clear();
                    max = density[r][c];
                    result.add(new Coord(r, c));
                } else if (density[r][c] == max) {
                    result.add(new Coord(r, c));
                }
            }
        }
        return result;
    }

    private boolean couldFit(int r, int c, int length, int width) {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                if (((r + i) >= R) || ((c + j) >= C)) {
                    return false;
                }
                if (tried[r + i][c + j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void init(Platform ap) {
        this.R = ap.getNumberOfRows();
        this.C = ap.getNumberOfCols();
        this.tried = new boolean[R][C];
        this.shipsRemaining = ap.listShips();
    }

    private static class Coord {
        @Override
        public String toString() {
            return "(" + r + "," + c + ")";
        }

        int r;
        int c;

        Coord(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    public static void main(String args[]) {
        PlatformImpl.main(new String[] { "--board", "ClassicGame.properties", "--player",
                "com.ibm.battleship.players.ProbabilityDensity", "--player", "com.ibm.battleship.players.Adjacent",
                "--advanced", "--timeout", "300000", "--N", "10", });
        // "--gui", "--delay", "50", "--debug"
    }

}
