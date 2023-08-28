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
package student.player;

import my.battleship.Platform;
import my.battleship.Player;
import my.battleship.ShotReply;
import my.battleship.PlatformImpl;
import my.battleship.ShotStatus;

/**
 * Example solution that can be modified as needed.
 * The main method will invoke the platform to play the game with an instance of
 * this TemplatePlayer class.
 * 
 */
public class TemplatePlayer implements Player {

    final static String myName = ""; // TODO: Enter your name
    final static String mySchool = ""; // TODO: Enter 'Mayo' or 'CTECH'
    final static String myClass = ""; // TODO: Enter '1st Period', '7th Period', or 'AP CSA')


    /**
     * A new instance of this class is created for each board.
     * You can perform pre-game setup and initialization here if you want to.
     */
    public TemplatePlayer() {
    }

    @Override
    /**
     * This method plays the game.
     * Methods can be called using the platform to shoot and retrieve the results of
     * the shot.
     * 
     * @see Platform
     */
    public void startGame(Platform platform) {

        /*************************************************************************
         * TODO:
         * 
         * YOU SHOULD REPLACE THE CODE IN THIS METHOD WITH YOUR OWN SOLUTION.
         * 
         *************************************************************************/

        /*
         * This is an example algorithm that shoots from top left to bottom right.
         * This is not a great solution, you should make it better!
         */
        for (int row = 0; row < platform.getNumberOfRows(); row++) {
            for (int col = 0; col < platform.getNumberOfCols(); col++) {
                ShotReply shotReply = platform.shoot(row, col);
                ShotStatus status = shotReply.getStatus();

                if (status == ShotStatus.HIT) {

                } else if (status == ShotStatus.MISS) {

                } else if (status == ShotStatus.SUNK_SHIP) {
                    // Get information about the ship just sunk and print its name
                    System.out.println("You sunk my " + shotReply.getShipName());
                } else if (status == ShotStatus.SUNK_ALL_YOU_WIN) {
                    System.out.println("You sunk my " + platform.listSunkShips().get(0).getName());
                    System.out.println("You sunk all the ships! You win!");
                    return;
                } else if (status == ShotStatus.MISSED_BOARD) {
                    System.out.println("You missed the board! Your coordinates (" + row + "," + col + ") need to be within 0 and " + (platform.getNumberOfRows()-1));
                } else if (status == ShotStatus.SHOT_AFTER_GAME_OVER) {
                    System.out.println("The game is over, but you keep wanting to shoot. Your code needs to stop.");
                    return;
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
        return (mySchool + myClass);
    }

    /**
     * Invokes the platform to create an instance of the TemplatePlayer class and
     * use it to
     * play the game.
     * 
     * Note: If you rename or copy this class to a new name, you may have to update
     * this
     * method in the new/renamed class to use the correct class name.
     * 
     * @param args
     */
    public static void main(String[] args) {

        // The following allows you to run the game from this player class's main
        // method. Uncomment the line to run different ways. Be sure ONLY ONE line
        // is uncommented.

        // This version lets you select the board using a prompt so you can test
        // your strategy against different boards. Set the second parameter to false
        // to skip the GUI

        PlatformImpl.runWithBoardPrompt(TemplatePlayer.class, true);

        // This version lets you run against a single board without having to select
        // it. Set the second parameter to false to skip the GUI

        //PlatformImpl.runWithSpecificBoard("sampleBoards/Classic-1.properties", TemplatePlayer.class, true);

        // This version lets you run against all the provided boards
        // Set the second parameter to true to see the GUI

        //PlatformImpl.runAllBoards(TemplatePlayer.class, false);
    }

}
