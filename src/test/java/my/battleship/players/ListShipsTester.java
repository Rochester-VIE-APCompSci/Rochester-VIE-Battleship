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

import static org.junit.Assert.assertTrue;

import java.util.List;

import my.battleship.Copyright;
import my.battleship.GameBoardShip;
import my.battleship.Log;
import my.battleship.Platform;
import my.battleship.Player;
import my.battleship.Ship;
import my.battleship.ShotStatus;

@Copyright(Copyright.c2014)
public class ListShipsTester implements Player {

    public static boolean failureDetected = false;

    @Override
    public String getName() {
        return "ListShipsTester";
    }

    @Override
    public String getSchool() {
        return "ListShipsTester";
    }

    @Override
    public void startGame(Platform ap) {

        List<Ship> ships = ap.listShips();

        if (ships.size() != 5) {
            Log.instance().log("Expected list size of 5 but got " + ships.size(), Log.Error);
            failureDetected = true;
        }
        assertTrue("ship count == 5 ? " + ships.size(), ships.size() == 5);
        for (Ship s : ships) {
            
            if (s instanceof GameBoardShip) {
                failureDetected = true;
                Log.instance().log("Ooops this ship is a GameBoardShip", Log.Error);
            }
            if ("PT".equals(s.getName())) {
                if ((s.getLength() != 2) || (s.getWidth() != 1)) {
                    failureDetected = true;
                    Log.instance().log("failure for " + s.toString(), Log.Error);
                }
            } else if ("submarine".equals(s.getName()) || "cruiser".equals(s.getName()))  {
                    if ((s.getLength() != 3) || (s.getWidth() != 1)) {
                        failureDetected = true;
                        Log.instance().log("failure for " + s.toString(), Log.Error);
                    }
            } else if ("battleship".equals(s.getName())) {
                if ((s.getLength() != 4) || (s.getWidth() != 1)) {
                    failureDetected = true;
                    Log.instance().log("failure for " + s.toString(), Log.Error);
                }
            } else if ("carrier".equals(s.getName())) {
                if ((s.getLength() != 5) || (s.getWidth() != 1)) {
                    failureDetected = true;
                    Log.instance().log("failure for " + s.toString(), Log.Error);
                }
            } else {
                failureDetected = true;
                Log.instance().log("Unexpected ship: " + s, Log.Error);
            }
        }

        boolean game_over = false;
        for (int r = 0, R = ap.getNumberOfRows(); r < R && !game_over; r++) {
            for (int c = 0, C = ap.getNumberOfCols(); c < C && !game_over; c++) {
                game_over = (ap.shoot(r, c).getStatus() == ShotStatus.SUNK_ALL_YOU_WIN);
            }
        }
    }
}
