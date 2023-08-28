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
public class MissileTester implements Player {

    public static boolean failureDetected = false;

    @Override
    public String getName() {
        return "MissileTester";
    }

    @Override
    public String getSchool() {
        return "MissileTester";
    }

    private Shot shots[] = new Shot[] { 
            new Shot(0, 0, Direction.EAST, ShotStatus.SUNK_SHIP, 2, 0, 0),
            new Shot(4, 0, Direction.EAST, ShotStatus.SUNK_SHIP, 2, 4, 1),
            new Shot(8, 0, Direction.EAST, ShotStatus.SUNK_SHIP, 2, 8, 2),
            
            new Shot(0, 4, Direction.EAST, ShotStatus.SUNK_SHIP, 6, 0, 7),
            new Shot(4, 4, Direction.EAST, ShotStatus.SUNK_SHIP, 6, 5, 7),
            new Shot(8, 4, Direction.EAST, ShotStatus.SUNK_SHIP, 6, 10, 7),
  
            new Shot(0, 8, Direction.EAST, ShotStatus.SUNK_SHIP, 9, 3, 11),
            new Shot(4, 8, Direction.EAST, ShotStatus.SUNK_SHIP, 9, 7, 10),
            new Shot(8, 8, Direction.EAST, ShotStatus.SUNK_SHIP, 9, 11, 9),

            new Shot(0,12, Direction.EAST, ShotStatus.SUNK_SHIP,11, 3, 12),
            new Shot(4,12, Direction.EAST, ShotStatus.SUNK_SHIP,11, 6, 12),
            new Shot(8,12, Direction.EAST, ShotStatus.SUNK_SHIP,11, 9 ,12),

            new Shot(13,15, Direction.WEST, ShotStatus.SUNK_ALL_YOU_WIN, 2, 13, 15),

    };

    @Override
    public void startGame(Platform p) {
        AdvancedPlatform ap = (AdvancedPlatform) p;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {

        }
        for (int i = 0; i < shots.length; i++) {

            ShotReply reply = ap.shoot(shots[i].r, shots[i].c, WeaponType.MISSILE, shots[i].d);

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

            if (reply.getStatus() != ShotStatus.MISS) {
                if ((reply.getCol() != shots[i].hc) || (reply.getRow() != shots[i].hr)) {
                    Log.instance().log(
                            "Torpedo test [" + i + "] Hit (exp) (" + shots[i].hr + "," + shots[i].hc + ") vs. ("
                                    + reply.getRow() + "," + reply.getCol() + ") (act)", Log.Error);
                    failureDetected = true;
                }
            }

            // assertTrue(reply.getStatus() == ShotStatus.MISS);
        }

//        boolean game_over = false;
//        for (int r = 0, R = ap.getNumberOfRows(); r < R && !game_over; r++) {
//            for (int c = 0, C = ap.getNumberOfCols(); c < C && !game_over; c++) {
//                game_over = (ap.shoot(r, c) == ShotStatus.SUNK_ALL_YOU_WIN);
//            }
//        }
    }

    static class Shot {
        final int r;
        final int c;
        final Direction d;
        final ShotStatus st;
        final int cost;
        final int hr;
        final int hc;

        Shot(int r, int c, Direction d, ShotStatus status, int cost) {
            this.r = r;
            this.c = c;
            this.d = d;
            this.st = status;
            this.cost = cost;
            this.hr = -1;
            this.hc = -1;
        }

        Shot(int r, int c, Direction d, ShotStatus status, int cost, int hr, int hc) {
            this.r = r;
            this.c = c;
            this.d = d;
            this.st = status;
            this.cost = cost;
            this.hr = hr;
            this.hc = hc;
        }

    }

}
