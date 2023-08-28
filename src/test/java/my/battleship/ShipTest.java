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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

@Copyright(Copyright.c2014)
public class ShipTest {

    static void log(Object po) {
        System.out.println(po);
    }

    @Test
    public void testSetOccupiedSpaceIsSunk() {
        GameBoardShip ship1 = new GameBoardShip(4, 2, "ship1"); // Ship of length 4 and width 2.
        assertFalse(ship1.isSunk());
        assertEquals(0, ship1.getNumSpacesShot());

        // Fake out the ship is on a game board by setting the space
        // and set the space "shot".
        GameSpace[] gSpace = new GameSpace[ship1.getLength() * ship1.getWidth()];
        for (int i = 0; i < gSpace.length; i++) {
            gSpace[i] = new GameSpace(0, i);
            gSpace[i].shoot();
        }
        ship1.setOccupiedSpace(Arrays.asList(gSpace));
        assertEquals(gSpace.length, ship1.getNumSpacesShot());
        assertTrue(ship1.isSunk());
    }

}
