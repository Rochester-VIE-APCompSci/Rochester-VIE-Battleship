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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import my.battleship.Copyright;
import my.battleship.Platform;
import my.battleship.PlatformImpl;
import my.battleship.Player;
import my.battleship.ShotReply;
import my.battleship.ShotStatus;

@Copyright(Copyright.c2014)
public class Adjacent implements Player {

    static public boolean uniqueNames = false;
    static public int serialNumber;
    
    static public synchronized int nextSerialNumber() {
        return ++serialNumber;
    }

    private String name = "Adjacent";
    
    private int numRows = 0;
    private int numCols = 0;

    
    public Adjacent() {
        if (uniqueNames) {
            name = "Adjacent-" + nextSerialNumber();
        }
    }

    List<Coordinate> highPriorities = new ArrayList<Coordinate>();

    public void startGame(Platform platform) {

        Random rand = new Random();

        numRows = platform.getNumberOfRows();
        numCols = platform.getNumberOfCols();

        boolean alreadyAttempted[][] = new boolean[numRows][numCols];

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                alreadyAttempted[r][c] = false;
            }
        }

        boolean gameOver = false;

        do {

            Coordinate next;

            if (highPriorities.size() > 0) {
                next = highPriorities.remove(0);
            } else {

                int r, c;
                do {
                    r = rand.nextInt(numRows);
                    c = rand.nextInt(numCols);
                } while (alreadyAttempted[r][c]);
                next = new Coordinate(r, c);
            }

            if (isLegal(next.r, next.c) && !alreadyAttempted[next.r][next.c]) {

                alreadyAttempted[next.r][next.c] = true;

        	ShotReply shotReply = platform.shoot(next.r, next.c);    	
        	ShotStatus status = shotReply.getStatus();

                switch (status) {
                case HIT:
                    // Add all 8 adjacent cells to the priority
                    // queue ... we'll work out later if the are
                    // legal or already probed
                    
                    highPriorities.add(0, new Coordinate(next.r-1, next.c-1));
                    highPriorities.add(0, new Coordinate(next.r-1, next.c));
                    highPriorities.add(0, new Coordinate(next.r-1, next.c+1));
                    highPriorities.add(0, new Coordinate(next.r, next.c-1));
                    highPriorities.add(0, new Coordinate(next.r, next.c+1));
                    highPriorities.add(0, new Coordinate(next.r+1, next.c-1));
                    highPriorities.add(0, new Coordinate(next.r+1, next.c));
                    highPriorities.add(0, new Coordinate(next.r+1, next.c+1));

                   break;
                case SUNK_SHIP:
                    break;
                case MISS:
                    break;
                case SUNK_ALL_YOU_WIN:
                    gameOver = true;
                    break;
                case SHOT_AFTER_GAME_OVER:
                case MISSED_BOARD:
                    gameOver = true; // Should not get here
                    break;
                case SUNK_ALL_YOU_LOSE:
                    gameOver = true;
                    break;
                case SUNK_ALL_YOU_TIE:
                    gameOver = true;
                    break;
                default:
                    break;
                }
            }
        } while (!gameOver);
    }

    boolean isLegal(int r, int c) {
        return 
                (r >= 0) &&
                (r < numRows) &&
                (c >= 0) &&
                (c < numCols);
    }
    
    public String getName() {
        return name;
    }

    public String getSchool() {
        return "dropout";
    }

    static class Coordinate {
        int r;
        int c;

        Coordinate(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
    
    public static void main(String args[]) {
        PlatformImpl.main(
                new String[] { 
                        "--board", "ClassicGame.properties", 
                        "--player", "tem.Adjacent",  
                        "--player", "tem.Adjacent", 
//                        "--player", "tem.Adjacent",
//                        "--player", "tem.Adjacent",
//                        "--player", "tem.Adjacent",  
//                        "--player", "tem.Adjacent", 
//                        "--player", "tem.Adjacent",
//                        "--player", "tem.Adjacent",
//                        "--player", "tem.Adjacent",  
//                        "--player", "tem.Adjacent", 
//                        "--player", "tem.Adjacent",
//                        "--player", "tem.Adjacent",
//                        "--player", "tem.Adjacent",  
//                        "--player", "tem.Adjacent", 
//                        "--player", "tem.Adjacent",
//                        "--player", "tem.Adjacent",
                        "--N", "3",
                      //"--gui", "--delay", "50", 
                      //  "--debug"
                        } );
    }

}
