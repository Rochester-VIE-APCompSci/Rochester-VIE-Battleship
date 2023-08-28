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

import my.battleship.Direction;
import my.battleship.Platform;
import my.battleship.AdvancedPlatform;
import my.battleship.PlatformImpl;
import my.battleship.Player;
import my.battleship.Ship;
import my.battleship.WeaponType;

public class AdvancedWeaponsDemo implements Player {

    public String getName() {
        return "Lisa Simpson-Milhouse";
    }

    public String getSchool() {
        return "Springfield";
    }
    
    @Override
    public void startGame(Platform p) {

      AdvancedPlatform ap = (AdvancedPlatform) p;

        System.out.println("The board dimensions are: " + ap.getNumberOfRows() + "x" + ap.getNumberOfCols());
        System.out.println("Here are the ships:");
        for (Ship ship : ap.listShips()) {
            System.out.println("  " + ship.getName() + " (" + ship.getLength() + "x" + ship.getWidth() + ")");
        }

        System.out.println("Weapons remaining:");
        for (WeaponType wtype : ap.getWeaponCounts().keySet()) {
            System.out.println(wtype + " -> " + ap.getWeaponCounts().get(wtype));
        }

        // Demonstrate each of the advanced weapons types in
        // each direction:

        ap.shoot(0, 0, WeaponType.TORPEDO, Direction.EAST);
        ap.shoot(1, 5, WeaponType.TORPEDO, Direction.WEST);
        ap.shoot(2, 0, WeaponType.TORPEDO, Direction.SOUTH);
        ap.shoot(7, 1, WeaponType.TORPEDO, Direction.NORTH);

        ap.shoot(3, 3, WeaponType.MISSILE, Direction.EAST);
        ap.shoot(11, 6, WeaponType.MISSILE, Direction.WEST);
        ap.shoot(13, 6, WeaponType.MISSILE, Direction.SOUTH);
        ap.shoot(6, 8, WeaponType.MISSILE, Direction.NORTH);

        ap.shoot(12, 14, WeaponType.ROCKET, Direction.EAST);
        ap.shoot(12, 14, WeaponType.ROCKET, Direction.SOUTH);
        ap.shoot(12, 14, WeaponType.ROCKET, Direction.WEST);
        ap.shoot(12, 14, WeaponType.ROCKET, Direction.NORTH);

        ap.shoot(12, 14, WeaponType.BOMB, Direction.EAST);
    }

    public static void main(String[] args) {

        String[] runargs = new String[] { 
                "--player", AdvancedWeaponsDemo.class.getName(), 
                "--advanced", 
                "--board", "sampleBoards/AdvancedWeaponsDemo.properties", 
                "--gui", "--delay", "1000" };

        PlatformImpl.main(runargs);
    }
}
