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

import java.util.Map;

import my.battleship.AdvancedPlatform;
import my.battleship.Copyright;
import my.battleship.Direction;
import my.battleship.Log;
import my.battleship.Platform;
import my.battleship.Player;
import my.battleship.WeaponType;

@Copyright(Copyright.c2014)
public class GetWeaponCountsTester implements Player {

    public static boolean failureDetected = false;

    @Override
    public String getName() {
        return "GetWeaponCountsTester";
    }

    @Override
    public String getSchool() {
        return "ListShipsTester";
    }

    @Override
    public void startGame(Platform p) {
        AdvancedPlatform ap = (AdvancedPlatform) p;
        Map<WeaponType, Integer> q = null;
        
        for (int i = 5; i > -1; i--) {
            q = ap.getWeaponCounts();
            if (q.get(WeaponType.MISSILE) != ((i > 0) ? i : 0)) {
                failureDetected = true;
                Log.instance().log("" + WeaponType.MISSILE + " (exp) " + ((i > 0) ? i : 0) + " vs. " + q.get(WeaponType.MISSILE) + " (exp)", Log.Error);
            }
            ap.shoot(0, 0, WeaponType.MISSILE, Direction.EAST);
        }

        for (int i = 4; i > -1; i--) {
            q = ap.getWeaponCounts();
            if (q.get(WeaponType.TORPEDO) != ((i > 0) ? i : 0)) {
                failureDetected = true;
                Log.instance().log("" + WeaponType.TORPEDO + " (exp) " + ((i > 0) ? i : 0) + " vs. " + q.get(WeaponType.MISSILE) + " (exp)", Log.Error);
            }
            ap.shoot(0, 0, WeaponType.TORPEDO, Direction.EAST);
        }
        
        for (int i = 3; i > -1; i--) {
            q = ap.getWeaponCounts();
            if (q.get(WeaponType.ROCKET) != ((i > 0) ? i : 0)) {
                failureDetected = true;
                Log.instance().log("" + WeaponType.ROCKET + " (exp) " + ((i > 0) ? i : 0) + " vs. " + q.get(WeaponType.MISSILE) + " (exp)", Log.Error);
            }
            ap.shoot(2, 0, WeaponType.ROCKET, Direction.EAST);
        }

        for (int i = 10; i > -1; i--) {
            q = ap.getWeaponCounts();
            if (q.get(WeaponType.BOMB) != Integer.MAX_VALUE) {
                failureDetected = true;
                Log.instance().log("" + WeaponType.BOMB + " (exp) " + Integer.MAX_VALUE + " vs. " + q.get(WeaponType.MISSILE) + " (exp)", Log.Error);
            }
            ap.shoot(0, 0, WeaponType.BOMB, Direction.EAST);
        }


        ap.shoot(9, 9);
    }
}
