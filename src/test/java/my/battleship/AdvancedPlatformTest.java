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

import static org.junit.Assert.*;
import my.battleship.players.GetWeaponCountsTester;
import my.battleship.players.PlayerWhoQuits;
import my.battleship.players.ListShipsTester;
import my.battleship.players.TorpedoTester;

import org.junit.Test;

@Copyright(Copyright.c2014)
public class AdvancedPlatformTest {

    @Test
    public void testNotAdvancedPlayer() {
        PlatformImpl p = new PlatformImpl();
        try {
           // p.setAdvancedGame(true);
            p.addPlayer("edu.rochester.mn.aldrich.homer.simpson.BadPlayer");
            p.start();
            fail("Should not get here.");
        } catch (GameConfigError e) {
            System.out.println("PASS: " + e.getMessage());
        }
    }

    @Test
    public void testListShips() {
        Log.instance().setLoggingLevel(Log.Warning);
        PlatformImpl p = new PlatformImpl();
        try {
            //p.setAdvancedGame(true);
            p.setBoardConfiguration("javaTest/ListShips.properties");
            p.addPlayer("my.battleship.players.ListShipsTester");
            p.start();
            assertFalse(ListShipsTester.failureDetected);
        } catch (GameConfigError e) {
            fail("Should not get here.");
        }

    }

    @Test
    public void testWeaponsQuery() {
        Log.instance().setLoggingLevel(Log.Debug);
        PlatformImpl p = new PlatformImpl();
        try {
            //p.setAdvancedGame(true);
           
            p.setBoardConfiguration("javaTest/GetWeaponCountsTest.properties");
            p.addPlayer("my.battleship.players.GetWeaponCountsTester");
            p.start();
            assertFalse(GetWeaponCountsTester.failureDetected);
        } catch (GameConfigError e) {
            fail("Should not get here.");
        }

    }

    @Test
    public void testTorpedos() {
        Log.instance().setLoggingLevel(Log.Warning);
        PlatformImpl p = new PlatformImpl();
        try {
            p.setBoardConfiguration("javaTest/TorpedoTest.properties");
            //p.setAdvancedGame(true);

            //p.setUseGUI(true);
            //p.setDelayInMillis(300);

            p.addPlayer("my.battleship.players.TorpedoTester");
            //p.setUseGUI(true);
            p.start();
            assertFalse(TorpedoTester.failureDetected);
            assertTrue(TorpedoTester.testCompletedNormally);

            System.out.println("end-of-test");
        } catch (GameConfigError e) {
            e.printStackTrace();
            fail("Should not get here.");
        }
    }

    @Test
    public void testMissiles() {
        Log.instance().setLoggingLevel(Log.Warning);
        PlatformImpl p = new PlatformImpl();
        try {
            p.setBoardConfiguration("javaTest/MissileTest.properties");
           // p.setAdvancedGame(true);

            // p.setUseGUI(true);
            // p.setDelayInMillis(50);

            p.addPlayer("my.battleship.players.MissileTester");
            p.start();
            assertFalse(TorpedoTester.failureDetected);
            System.out.println("end-of-test");
        } catch (GameConfigError e) {
            e.printStackTrace();
            fail("Should not get here.");
        }
    }

    @Test
    public void testGuyWhoQuits() {
        Log.instance().setLoggingLevel(Log.Debug);
        PlatformImpl p = new PlatformImpl();
        try {
            p.addPlayer("my.battleship.players.APlayer");
            p.addPlayer("my.battleship.players.PlayerWhoQuits");
            p.setBoardConfiguration("sampleBoards/Classic-Random.properties");
            p.setStats("my.battleship.players.PlayerWhoQuits");

            p.start();
            assertFalse(PlayerWhoQuits.testFailed);
        } catch (Exception e) {
            fail();
        }
    }

}
