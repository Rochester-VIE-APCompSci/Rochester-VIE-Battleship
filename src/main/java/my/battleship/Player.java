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
 * Defines the interface between the platform and an individual player. In
 * addition to implementing this interface, each player's class must provide a
 * public, no-argument constructor.
 */

@Copyright(Copyright.c2014)
public interface Player {

    /**
     * Play a game.  
     * 
     * An implementation should repeatedly call {@link my.battleship.Platform#shoot(int, int)} until the
     * ShotStatus is one of the SUNK_ALL_* enumerations.  If you return prior to doing so,
     * you are effectively giving up.  If you take too long to decide where to shoot,
     * you may be removed from the game.
     * 
     * @param platform the Game platform (board).
     */
    void startGame(Platform platform);

    /**
     * Gets this player's name
     *
     * @return String of the player's name
     */

    String getName();

    /**
     * Gets this player's school, for instance "Century", "John Marshall", or "Mayo"
     *
     * @return String of the player's school
     */
    String getSchool();

}
