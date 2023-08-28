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

public class ASlightlyBetterPlayer implements Player {

     public ASlightlyBetterPlayer() {
     }

    public void startGame(Platform platform) {

        Random rand = new Random();

        // For demonstration purposes, used a fixed seed which
        // results in the same sequence.  Comment this out
        // to see random shots in action.
        rand.setSeed(0);

        int rows = platform.getNumberOfRows();
        int cols = platform.getNumberOfCols();

        boolean alreadyAttempted[][] = new boolean[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                alreadyAttempted[r][c] = false;
            }
        }

        boolean gameOver = false;

        do {
            
            // Guess anywhere in the board, avoiding any cells that have
            // already been tried:

            int r, c;
            do {
                r = rand.nextInt(rows);
                c = rand.nextInt(cols);
            } while (alreadyAttempted[r][c] && Math.random() < 0.9); // 90% chance we'll pick a new target if we've already shot here (10% we'll shoot anyway)

            alreadyAttempted[r][c] = true;

    	    ShotReply shotReply = platform.shoot(r, c);	
    	    ShotStatus status = shotReply.getStatus();

            switch (status) {
            case HIT:
                break;
            case SUNK_SHIP:
                break;
            case MISS:
                break;
            case SUNK_ALL_YOU_WIN:
            case SUNK_ALL_YOU_LOSE:
            case SUNK_ALL_YOU_TIE:
               gameOver = true;
                break;
            case SHOT_AFTER_GAME_OVER:
            case MISSED_BOARD:
                gameOver = true; // Should not get here
                break;
             default:
                break;
            }
        } while (!gameOver);
    }

    public String getName() {
        return "Bartholomew Simpson";
    }

    public String getSchool() {
        return "Springfield";
    }

    public static void main(String args[]) {
        PlatformImpl.onePlayerQuickStart(ASlightlyBetterPlayer.class, true);
    }
}
