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

import java.util.Random;

import my.battleship.Platform;
import my.battleship.PlatformImpl;
import my.battleship.Player;
import my.battleship.ShotReply;
import my.battleship.ShotStatus;

public class BadPlayer implements Player {

    public BadPlayer() {
    }

    public void startGame(Platform platform) {

        int rows = platform.getNumberOfRows();
        int cols = platform.getNumberOfCols();

        Random rand = new Random();

        // For demonstration purposes, used a fixed seed which
        // results in the same sequence.  Comment this out
        // to see random shots in action.
        rand.setSeed(0);

        boolean gameOver = false;

        do {
            // Guess anywhere in the board

	    int r = rand.nextInt(rows);
	    int c = rand.nextInt(cols);

	    ShotReply shotReply = platform.shoot(r, c);
	    ShotStatus status = shotReply.getStatus();

            switch (status) {
            case SUNK_ALL_YOU_WIN:
            case SUNK_ALL_YOU_LOSE:
            case SUNK_ALL_YOU_TIE:
                gameOver = true;
                break;
            case SHOT_AFTER_GAME_OVER:
            case MISSED_BOARD:
                // Should never get here! throw an exception so that we will
                // know if we do
                throw new IllegalStateException("Should not be here!");
            default:
                break;
            }
        } while (!gameOver);
    }

    public String getName() {
        return "Homer Simpson";
    }

    public String getSchool() {
        return "Springfield";
    }

    public static void main(String args[]) {
        PlatformImpl.onePlayerQuickStart("sampleBoards/Classic-1.properties", BadPlayer.class, true);
    }
}
