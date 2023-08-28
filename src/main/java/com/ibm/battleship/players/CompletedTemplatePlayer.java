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
// If you create any additional classes to help you, ensure they are in this package
package com.ibm.battleship.players;

import my.battleship.Platform;
import my.battleship.Player;
import my.battleship.ShotReply;
import my.battleship.PlatformImpl;
import my.battleship.ShotStatus;

/**
 * Example template for the Player interface that can be copied and
 * updated as needed.  Make sure to change the package name.
 */
public class CompletedTemplatePlayer implements Player {

    String myName = "myFirstName myLastName"; // TODO: Enter your name (first last)

    public CompletedTemplatePlayer() {
        // constructor
    }

    @Override
    /**
     * This method implements the game.
     * Methods can be called using the platform to shoot and retrieve the results of the shot.
     * @see Platform
     */
    public void startGame(Platform platform) {
        
    	// TODO Add your code here to play the game.
        
       

        /*
         * This is an example algorithm that shoots from top left to bottom right.
         * This is not a great solution, you should make it better!
         */
        ROW_LOOP: for(int row = 0; row < platform.getNumberOfRows(); row++) {
            for (int col = 0; col < platform.getNumberOfCols(); col++) {
        	ShotReply shotReply = platform.shoot(row, col);
        	ShotStatus status = shotReply.getStatus();

                switch (status) {
                case HIT:
                    break;
                case SUNK_SHIP:
                	/* Get information about the ship just sunk and print its name */
                	System.out.println("You sunk my " + 
                            platform.listSunkShips().get(0).getName()
                            );
                    break;
                case MISS:
                    break;
                case SUNK_ALL_YOU_WIN:
                	/* Get information about the ship just sunk and print its name */
                	System.out.println("You won by sinking  my " + 
                            platform.listSunkShips().get(0).getName()
                            );
                	break ROW_LOOP;
                
                case SHOT_AFTER_GAME_OVER:
                case MISSED_BOARD:
                	// if you shoot after the game or miss the board, your program has a bug in it
                    break ROW_LOOP; 
                 default:
                    break;
                }

            }
        }
    }

    @Override
    public String getName() {
        return myName; 
    }

    @Override
    public String getSchool() {
        // TODO Enter your school name here
        return null;
    }

    public static void main(String[] args) {
        System.out.println("In main...");

        // The following allows you to run the game from this player class's main 
        // method versus running from the command line.  To work, the battleship.jar
        // must be included in your environment's classpath.
        PlatformImpl.onePlayerQuickStart(
                // The name of the battleship game board properties you want to use.  
                // TODO Change as desired. This board has fixed locations for the ships.
                "sampleBoards/Classic-1.properties",
                // The Class object representing your player class (this class). The battleship
                // game uses this to plug your code into the game.
                CompletedTemplatePlayer.class,
                // true to show the GUI. This optimizes the GUI so you can observe the play of your Player implementation.
                true);
        
    }

}
