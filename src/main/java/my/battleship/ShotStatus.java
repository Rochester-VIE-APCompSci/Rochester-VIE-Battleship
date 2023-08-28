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

/**
 * Purpose: Battleship shot status for a board location that has been shot at.
 */
@Copyright(Copyright.c2014)
public enum ShotStatus {
	/**
	 * Shot hit part of a ship, the ship did not sink
	 */
    HIT, 
    /**
     * The shot did not hit or sink a ship
     */
    MISS, 
    /**
     * The shot missed the board
     */
    MISSED_BOARD, 
    /**
     * The shot hit a ship, and the ship is now sunk
     */
    SUNK_SHIP, 
    /**
     * The shot hit a ship, the ship sank, there are
     * no more ships to sink and the game is over.
     */
    SUNK_ALL_YOU_WIN,
    /**
     * A shot was made after the game ended.
     */
    SHOT_AFTER_GAME_OVER,
    
    /**
     * RESERVED FOR FUTURE USE:
     * This option is reserved for a player vs player game
     * The option is currently not returned as a shot status.
     */
    SUNK_ALL_YOU_LOSE, 
    /**
     * RESERVED FOR FUTURE USE:
     * This option is reserved for a player vs player game.
     * The option is currently not returend as a shot status
     */
    SUNK_ALL_YOU_TIE, // Your shot sunk the last ship but so did another
   // player's shot.

    ;
}
