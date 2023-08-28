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

import org.junit.Test;

@Copyright(Copyright.c2014)
public class GameSpaceTest {

    @Test
    public void testGameSpaceLocation() {
        Location loc = new Location(100, 200);
        assertEquals(100, loc.getRow());
        assertEquals(200, loc.getCol());
    }

    @Test
    public void testGetLocation() {
        GameSpace gSpace = new GameSpace(100, 200);
        assertEquals(100, gSpace.getLocation().getRow());
        assertEquals(200, gSpace.getLocation().getCol());
    }

    @Test
    public void testShoot() {
        GameSpace gSpace = new GameSpace(new Location(4, 8));

        assertFalse(gSpace.isShot());
        assertEquals(0, gSpace.getShotCount());

        gSpace.shoot();
        assertTrue(gSpace.isShot());
        gSpace.shoot();
        assertEquals(2, gSpace.getShotCount());
    }

    @Test
    public void testSetHasShip() {
        GameSpace gSpace = new GameSpace(new Location(4, 8));

        assertFalse(gSpace.hasShip());

        gSpace.setShip(new GameBoardShip(2, 4, "ship"));
        assertTrue(gSpace.hasShip());
    }

}
