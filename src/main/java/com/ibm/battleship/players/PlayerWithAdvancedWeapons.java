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
import my.battleship.AdvancedPlatform;
import my.battleship.Platform;
import my.battleship.PlatformImpl;
import my.battleship.Player;
import my.battleship.Ship;
import my.battleship.ShotReply;
import my.battleship.WeaponType;


public class PlayerWithAdvancedWeapons implements Player {

    public String getName() {

        return "Lisa Simpson";

    }

    public String getSchool() {

        return "Springfield Elementary";

    }

    /** Function to play the advanced game
     * 
     * Note:
     * This method uses each of the weapon types, and calls a dumpShotResult method to 
     * display the results of the shot in the output.
     * 
     * However the method does not use these advanced weapons "smartly".
     * The starting locations for the shots are not well thought out, and in many cases the same square
     * be shot twice. Even worse, this code will continue shooting after the game is over.
     * 
     * You'll need to change this to do three things:
     * 1) Choose which weapons to use in a more optimal order.
     *    - There is no requirement to use all weapons or all shots of
     *      a weapon.
     * 2) Choose better starting squares and directions for each shot.
     * 3) Respond correctly to the results of a shot. 
     *     - Do not shoot the same square twice 
     *     - Stop shooting when the game ends
     *     - When a hit occurs, find a solution that efficiently sinks
     *       the ship that was just hit.
     * 
     * You can use this routine as an example of what methods are available, 
     * or as a demo for the various weapon types.
     * 
     * When you run this program, you can watch the results of the example shots in the GUI to see
     * how each of these weapons works.  You should also review the javadoc for the advanced weapons. 
     * (Open javadoc/index.html in a web browser and click AdvancedWeapon in the pane on the left-hand side).
     * 
     * You can review the javadoc for ShotReply to see how to respond to the result of a shot.
     * (Open javadoc/index.html in a web browser and click ShotReply in the pane on the left-hand side).
     */
    public void startGame(Platform p) {


      AdvancedPlatform ap = (AdvancedPlatform) p;

        /*
         * This block of code prints out the dimensions of the board on the console.
         */
        int rows = ap.getNumberOfRows();
        int cols = ap.getNumberOfCols();

        System.out.println("The board dimensions are: " + rows + "x" + cols);


        /*
         * This block of code prints out the sizes and dimensions of the ships.
         * The size and dimensions of the enemy fleet could be used to determine on which 
         * squares a ship could be possibly be placed.
         *  
         */
        System.out.println("Here are the ships:");
        for (Ship ship : ap.listShips()) {
            System.out.println(" " + ship.getName() + " (" + ship.getLength() + "x" + ship.getWidth() + ")");
        }


        /*
         * This prints the number shots that are available for each weapon type.
         */
        System.out.println("Weapons remaining:");
        for (WeaponType wtype : ap.getWeaponCounts().keySet()) {
            System.out.println(wtype + " -> " + ap.getWeaponCounts().get(wtype));
        }


        /* Local variable to hold the result of a shot */
        /* This method takes the reply and prints the info in it
         * But you will want to do more sophisticated processing using this object.
         */
        ShotReply sr;


        /* Demonstrate using a TORPEDO
         *  A torpedo shots up to six squares in a given direction (NORTH, SOUTH, EAST or WEST), 
         *  but stops sooner if it hits something.
         *
         *  This loop blindly shoots one torpedo for each row,
         *  starting from the first column and shooting in an EAST (left to right) direction.
         *  The code stops shooting when there are no more torpedos, or no more rows.
         *
         *  Since this will only cover a small number of squares in the left hand
         *  corner of the board, using torpedos this way is likely to not work very well 
         *  in practice....
         */
        for (int torpedo_shots = 0; 
                (ap.getWeaponCounts().get(WeaponType.TORPEDO) > 0  //  until no more torpedos 
                        &&  torpedo_shots < rows);                        //  OR the number of rows is exceeded
                torpedo_shots++ ) 
        {

            System.out.println("Torpedo Shot Number " + torpedo_shots);
            sr = ap.shoot(torpedo_shots, 0, WeaponType.TORPEDO, Direction.EAST);

            dumpShotResult(sr);
        }

        System.out.println("Out of Torpedos..Switching to missles");




        /*
         * Demonstrate using a missile
         * A missile shoots 4 squares in the direction specified, then turns clockwise for
         * another four, the clockwise for another four until it ends at the point 
         * it was fired from. It will stop if it hits something.
         *  
         *  In the fragment that follows, we start at the top right corner, 
         *  and fire missiles WEST (to the left) every four rows, until we 
         *  run out of missiles or there are no more rows. This means we're 
         *  shooting a box; (left, up, right, down).
         *  
         *  The math to calculate a starting square is a little odd looking;
         *  since the shot pattern goes west, a shot that starts at (3, 19) hits the squares
         *  (3,19), (3, 18), (3,17), (3,16), (2,16), (1, 16), (0,16), (0,17), (0,18), (0,19) 
         *  in that exact order (a clockwise pattern). Care must be taken not to shoot off the board.
         *  
         *  Using a missile this way covers a lot of squares on the right hand side of the
         *  board, but this may not be such a great approach, especially if ships are more likely
         *  to be in the middle of the board. 
         */
        for (int missile_shot = 0; 
                (ap.getWeaponCounts().get(WeaponType.MISSILE) > 0  // until no more missiles 
                        &&  ((missile_shot + 1) * 4 - 1 ) < rows  );      // OR until a shot would be taken
                // from beyond the last row
                missile_shot++ ) 
        {

            System.out.println("Shooting Missle #" + missile_shot);
            sr = ap.shoot(((missile_shot + 1) * 4 - 1), cols - 1, WeaponType.MISSILE, Direction.WEST);

            dumpShotResult(sr);
        }

        System.out.println("Out of missiles...switching to rockets");


        /*
         * Demo a rocket shot
         * A rocket  jumps over 5 spaces in the specified direction (no hits or misses) and
         *  then covers 5 cells in a T, working from the middle out. It will stop if it 
         *  hits something.
         *  
         *  In this example, the bottom row minus the current shot number is chosen as the row, 
         *  and the current shot + 2 is used as a starting column, a NORTH (up) direction
         *  is used. The result is a horizontal line that moves up and in each iteration.
         *  
         *  The code keeps shooting until there are no more rockets, or no more columns or no more rows.
         *  
         *  This shot pattern makes an interesting shape, but you will have to decide for yourself if it is a
         *  useful algorithm.
         */
        for (int rocket_shot = 0; 
                (ap.getWeaponCounts().get(WeaponType.ROCKET) > 0 &&   // until no more rockets
                        rocket_shot + 5 < cols &&                 // OR the shot would include a
                        // column that is off the board
                        rows - 1 - rocket_shot - 5 >= 0           // OR the shot would include a 
                        // row that is off the board
                        ); 
                rocket_shot++ ) 
        {

            System.out.println("Shooting rocket #" + rocket_shot);
            sr = ap.shoot(rows - 1 - rocket_shot, rocket_shot + 2, WeaponType.ROCKET, Direction.NORTH);

            dumpShotResult(sr);
        }

        System.out.println("Out of rockets...switching to bombs" );


        /* Demonstrate a Bomb	
         * Bombs are traditional shots, this just loops over the entire board.
         * 
         * Like the other code fragments, this loops makes no consideration
         * of whether a square has already been shot at.
         * 
         * The number of bombs is basically unlimited, so there is no check
         * in this code to stop if we run out.
         */
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                /* Note that the direction really doesn't matter for the BOMB type */
                sr = ap.shoot(r,  c, WeaponType.BOMB, Direction.EAST);

                /* Note: You may want to comment this next two lines out, if you don't want to see the result
                 * of every bomb in the console.		 */
                System.out.println(("Shooting Bomb at (" + r) + "," + c + ")");
                dumpShotResult(sr);
            }
        }


    }

    /**
     *  Function to dump out the results of a shot        
     *  A ShotReply contains a lot of good information  
     *  about the result of a shot attempt, this method
     *  provides an example of how to get that information and
     *  print it to the console.
     *                                                 
     * You'll also want to look at the javadoc for a ShotReply to 
     * see what methods are available and what they do.   
     * (Open javadoc/index.html in a web browser and click 
     *  ShotReply in the pane on the left-hand side).
     */
    private void dumpShotResult(ShotReply sr) {

        /* Print how the cost of the shot */
        System.out.println("That shot cost you " + sr.getCost());

        switch (sr.getStatus()) {
        case HIT:
            System.out.println("The shot was a hit at row " + sr.getRow() + " and column " + sr.getCol());		
            break;
        case SUNK_SHIP:
            /* When a ship sinks, this method tells us which one */
            System.out.println("You sunk a " + sr.getShipName());
            break;
        case MISS:
            System.out.println("Missed");;
            break;
        case SUNK_ALL_YOU_WIN:
            System.out.println("Every ship is sunk...You Win");
            break;
        case SUNK_ALL_YOU_LOSE:
            System.out.println("Every ship is sunk...but you lost");
            break;
        case SUNK_ALL_YOU_TIE:
            System.out.println("Every ship is sunk...You tied");
            break;
        case SHOT_AFTER_GAME_OVER:
            System.out.println("you messed up and shot after the game ended....");
            break;
        case MISSED_BOARD:
            System.out.println("you messed up and shot off the board....");
            break ; // Should not get here
        default:
            System.out.println("Un-expected response");
            break;
        }


    }

    public static void main(String[] args) {

        /* This is the driver routine into the battleship class.
         * 
         * It allows you to change
         *  - which board the game is played on by making modifications
         *  - the speed at which the GUI is updated
         */

        String[] runargs = new String[] {

                /* Do not change this line */
                "--player", PlayerWithAdvancedWeapons.class.getName(),

                /* Do not change this line */
                "--advanced",

                /* This line specifies which board file is used */
                "--board", "sampleBoards/AdvancedWeaponsDemo.properties",

                /* This turns on the GUI */
                "--gui",

                /* This line determines the length of time the GUI pauses between shots */
                /* You increase of decrease the value to suit your taste */
                "--delay", "200"

        };


        PlatformImpl.main(runargs);

    }

}

