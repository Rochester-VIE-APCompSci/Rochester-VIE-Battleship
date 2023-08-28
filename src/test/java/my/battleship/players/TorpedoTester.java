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
package my.battleship.players;

import my.battleship.AdvancedPlatform;
import my.battleship.Copyright;
import my.battleship.Direction;
import my.battleship.Log;
import my.battleship.Platform;
import my.battleship.Player;
import my.battleship.ShotReply;
import my.battleship.ShotStatus;
import my.battleship.WeaponType;

@Copyright(Copyright.c2014)
public class TorpedoTester implements Player {

    public static boolean failureDetected = false;
    public static boolean testCompletedNormally = false;

    @Override
    public String getName() {
        return "TorpedoTester";
    }

    @Override
    public String getSchool() {
        return "TorpedoTester";
    }

    private Shot shots[] = new Shot[] { new Shot(0, 0, Direction.EAST, ShotStatus.MISS, 6),
            new Shot(1, 0, Direction.EAST, ShotStatus.HIT, 1, 1, 0), new Shot(2, 0, Direction.EAST, ShotStatus.HIT, 1, 2, 1),
            new Shot(3, 0, Direction.EAST, ShotStatus.HIT, 3, 3, 2), new Shot(4, 0, Direction.EAST, ShotStatus.HIT, 3, 4, 3),
            new Shot(5, 0, Direction.EAST, ShotStatus.HIT, 5, 5, 4),
            new Shot(6, 0, Direction.EAST, ShotStatus.SUNK_SHIP, 5, 6, 5, "ship-5"),

            new Shot(8, 0, Direction.SOUTH, ShotStatus.HIT, 1, 8, 0), new Shot(8, 1, Direction.SOUTH, ShotStatus.HIT, 1, 9, 1),
            new Shot(8, 2, Direction.SOUTH, ShotStatus.HIT, 3, 10, 2),
            new Shot(8, 3, Direction.SOUTH, ShotStatus.HIT, 3, 11, 3),
            new Shot(8, 4, Direction.SOUTH, ShotStatus.HIT, 5, 12, 4),
            new Shot(8, 5, Direction.SOUTH, ShotStatus.SUNK_SHIP, 5, 13, 5, "ship-11"),
            new Shot(8, 6, Direction.SOUTH, ShotStatus.MISS, 6),

            new Shot(0, 12, Direction.WEST, ShotStatus.HIT, 1, 0, 12),
            new Shot(1, 12, Direction.WEST, ShotStatus.HIT, 1, 1, 11),
            new Shot(2, 12, Direction.WEST, ShotStatus.HIT, 3, 2, 10),
            new Shot(3, 12, Direction.WEST, ShotStatus.HIT, 3, 3, 9), new Shot(4, 12, Direction.WEST, ShotStatus.HIT, 5, 4, 8),
            new Shot(5, 12, Direction.WEST, ShotStatus.SUNK_SHIP, 5, 5, 7, "ship-17"),
            new Shot(6, 12, Direction.WEST, ShotStatus.MISS, 6),

            new Shot(13, 7, Direction.NORTH, ShotStatus.HIT, 1, 13, 7),
            new Shot(13, 8, Direction.NORTH, ShotStatus.HIT, 1, 12, 8),
            new Shot(13, 9, Direction.NORTH, ShotStatus.HIT, 3, 11, 9),
            new Shot(13, 10, Direction.NORTH, ShotStatus.HIT, 3, 10, 10),
            new Shot(13, 11, Direction.NORTH, ShotStatus.HIT, 5, 9, 11),
            new Shot(13, 12, Direction.NORTH, ShotStatus.SUNK_SHIP, 5, 8, 12, "ship-23"),
            new Shot(13, 13, Direction.NORTH, ShotStatus.MISS, 6),

            // Bad Shots

            new Shot(0, 0, Direction.WEST, ShotStatus.MISSED_BOARD, 6),
            new Shot(0, 1, Direction.WEST, ShotStatus.MISSED_BOARD, 6),
            new Shot(0, 2, Direction.WEST, ShotStatus.MISSED_BOARD, 6),
            new Shot(0, 3, Direction.WEST, ShotStatus.MISSED_BOARD, 6),
            new Shot(0, 4, Direction.WEST, ShotStatus.MISSED_BOARD, 6),

            new Shot(-1, 0, Direction.EAST, ShotStatus.MISSED_BOARD, 6),
            new Shot(0, -1, Direction.SOUTH, ShotStatus.MISSED_BOARD, 6),
            new Shot(0, Integer.MAX_VALUE, Direction.NORTH, ShotStatus.MISSED_BOARD, 6),
            new Shot(Integer.MAX_VALUE, 0, Direction.WEST, ShotStatus.MISSED_BOARD, 6),
            new Shot(0,Integer.MAX_VALUE, Direction.EAST, ShotStatus.MISSED_BOARD, 6),

    };

    @Override
    public void startGame(Platform p) {
        AdvancedPlatform ap = (AdvancedPlatform) p;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {

        }

        for (int i = 0; i < shots.length; i++) {

            if (i == 37) {
                Log.instance().log("debug", Log.Debug);
               // Log.instance().setLoggingLevel(Log.GoryDetail);
            }
            Log.instance().log("Lobbing " + shots[i], Log.Event);

            if (shots[i].r == Integer.MAX_VALUE) {
                shots[i].r = ap.getNumberOfRows();
            } else if (shots[i].r == Integer.MAX_VALUE-1) {
                shots[i].r = ap.getNumberOfRows()-1;
            }
            if (shots[i].c == Integer.MIN_VALUE-1) {
                shots[i].c = ap.getNumberOfCols()-1;
            }

            ShotReply reply = ap.shoot(shots[i].r, shots[i].c, WeaponType.TORPEDO, shots[i].d);

            if (reply.getStatus() != shots[i].st) {
                Log.instance().log(
                        "Torpedo test [" + i + "] Status (exp) " + shots[i].st + " vs. " + reply.getStatus() + " (act)",
                        Log.Error);
                failureDetected = true;
            }

            if (reply.getCost() != shots[i].cost) {
                Log.instance().log(
                        "Torpedo test [" + i + "] Cost (exp) " + shots[i].cost + " vs. " + reply.getCost() + " (act)",
                        Log.Error);
                failureDetected = true;
            }

            switch (reply.getStatus()) {
            case MISS:
            case MISSED_BOARD:
                break; // do nothing
            default: {

                if (reply.getStatus() != ShotStatus.MISS) {
                    if ((reply.getCol() != shots[i].hc) || (reply.getRow() != shots[i].hr)) {
                        Log.instance().log(
                                "Torpedo test [" + i + "] Hit (exp) (" + shots[i].hr + "," + shots[i].hc + ") vs. ("
                                        + reply.getRow() + "," + reply.getCol() + ") (act)", Log.Error);
                        failureDetected = true;
                    }
                    if (reply.getStatus() == ShotStatus.SUNK_SHIP) {
                        if (!shots[i].sunk.equals(reply.getShipName())) {
                            failureDetected = true;
                            Log.instance().log(
                                    "Bad sunken ship name: (exp) " + shots[i].sunk + " vs. " + reply.getShipName() + " (act)",
                                    Log.Error);
                        }
                    }
                }

                break;
            }
            }

            // assertTrue(reply.getStatus() == ShotStatus.MISS);
        }

        boolean game_over = false;
        for (int r = 0, R = ap.getNumberOfRows(); r < R && !game_over; r++) {
            for (int c = 0, C = ap.getNumberOfCols(); c < C && !game_over; c++) {
                game_over = (ap.shoot(r, c).getStatus() == ShotStatus.SUNK_ALL_YOU_WIN);
            }
        }

        testCompletedNormally = true;
    }

    static class Shot {
        int r;
        int c;
        final Direction d;
        final ShotStatus st;
        final int cost;
        final int hr;
        final int hc;
        final String sunk;

        Shot(int r, int c, Direction d, ShotStatus status, int cost) {
            this.r = r;
            this.c = c;
            this.d = d;
            this.st = status;
            this.cost = cost;
            this.hr = -1;
            this.hc = -1;
            this.sunk = null;
        }

        Shot(int r, int c, Direction d, ShotStatus status, int cost, int hr, int hc) {
            this.r = r;
            this.c = c;
            this.d = d;
            this.st = status;
            this.cost = cost;
            this.hr = hr;
            this.hc = hc;
            this.sunk = null;

        }

        Shot(int r, int c, Direction d, ShotStatus status, int cost, int hr, int hc, String sunk) {
            this.r = r;
            this.c = c;
            this.d = d;
            this.st = status;
            this.cost = cost;
            this.hr = hr;
            this.hc = hc;
            this.sunk = sunk;
        }

        public String toString() {
            return "Shot{ (" + r + "," + c + ":" + d + ") => stat=" + st + ", cost=" + cost + " (" + hr + "," + hc + ") sunk="
                    + sunk + "}";
        }
    }

}
