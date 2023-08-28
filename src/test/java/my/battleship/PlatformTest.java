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

import org.junit.Ignore;

import my.battleship.players.PlayerWhoQuits;

import org.junit.Test;

@Copyright(Copyright.c2014)
public class PlatformTest {

    static Object dropbox = null;

    public static synchronized Object getDropbox() {
        return dropbox;
    }

    public static synchronized void setDropbox(Object dropbox) {
        PlatformTest.dropbox = dropbox;
    }

    @Test
    public void testInvalidClass() {
        PlatformImpl p = new PlatformImpl();
        try {
            p.addPlayer("this.class.does.not.Exist");
            p.start();
            fail("Should not get here.");
        } catch (GameConfigError e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testPrivateCtor() {
        PlatformImpl p = new PlatformImpl();
        try {
            p.addPlayer("my.battleship.players.PrivateCtor");
            p.start();
            fail("Should not get here.");
        } catch (GameConfigError e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testNotAPlayer() {
        PlatformImpl p = new PlatformImpl();
        try {
            p.addPlayer("my.battleship.players.NotAPlayer");
            p.start();
            fail("Should not get here.");
        } catch (GameConfigError e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Ignore("Test may not valid since start() is now used for the combined FlexiblePlatform.")
    public void testThreadViolator() {
        Violator violator = new Violator();
        Thread t = new Thread(violator);
        t.start();
        
        PlatformImpl p = new PlatformImpl();
        p.setBoardConfiguration("sampleBoards/Classic-Random.properties");
        p.addPlayer("my.battleship.players.ThreadViolator");
        p.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue(violator.passed);
    }

    private static class Violator implements Runnable {

        boolean passed = false;

        @Override
        public void run() {
            while (getDropbox() == null)
                ;
            Platform platform = (Platform) getDropbox();
            try {
                platform.shoot(0, 0);
            } catch (IllegalStateException e) {
                passed = true;
            }
            setDropbox(null);
        }

    }

    @Test
    public void testGuyWhoThrowsException() {
        Log.instance().setLoggingLevel(Log.Warning);
        PlatformImpl p = new PlatformImpl();
        try {

            p.setBoardConfiguration("sampleBoards/Classic-Random.properties");
            p.addPlayer("my.battleship.players.PlayerWhoThrowsException");
            p.addPlayer("kellog.simpson_bart.ASlightlyBetterPlayer");
            p.addPlayer("kellog.simpson_bart.ASlightlyBetterPlayer");
            p.start();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGuyWhoHangs() {
        Log.instance().setLoggingLevel(Log.Warning);
        PlatformImpl p = new PlatformImpl();
        try {
            p.setBoardConfiguration("sampleBoards/Classic-Random.properties");
            p.addPlayer("my.battleship.players.PlayerWhoHangs");
            p.addPlayer("kellog.simpson_bart.ASlightlyBetterPlayer");
            p.addPlayer("kellog.simpson_bart.ASlightlyBetterPlayer");
            p.start();
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void testGuyWhoQuits() {
        Log.instance().setLoggingLevel(Log.Debug);
        PlatformImpl p = new PlatformImpl();
        try {
            p.setBoardConfiguration("sampleBoards/Classic-Random.properties");
            p.addPlayer("kellog.simpson_bart.ASlightlyBetterPlayer");
            p.addPlayer("my.battleship.players.PlayerWhoQuits");
            p.setStats("my.battleship.players.PlayerWhoQuits");

            p.start();
            assertFalse(PlayerWhoQuits.testFailed);
        } catch (Exception e) {
            fail();
        }
    }
}
