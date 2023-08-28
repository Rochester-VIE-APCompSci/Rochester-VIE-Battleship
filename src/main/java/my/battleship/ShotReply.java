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
 * ShotReply provides detailed status for a shot attempt in the advanced game.
 */
@Copyright(Copyright.c2014)
public class ShotReply {

    private ShotStatus status;
    private final int row;
    private final int col;
    private final int cost;
    private final String shipName;

    public ShotReply(ShotStatus s, int r, int c, int cost, String shipName) {
        this.status = s;
        this.row = r;
        this.col = c;
        this.cost = cost;
        this.shipName = shipName;
    }

    /**
     * @return the ShotStatus associated with this reply.
     */
    public final ShotStatus getStatus() {
        return status;
    }

    /**
     * Provides the row coordinate of what you hit, provided that you actually
     * hit something.
     * 
     * @return When the value of {@link #getStatus} is {@link ShotStatus#HIT},
     *         {@link ShotStatus#SUNK_SHIP}, {@link ShotStatus#SUNK_ALL_YOU_WIN}
     *         , {@link ShotStatus#SUNK_ALL_YOU_TIE} or
     *         {@link ShotStatus#SUNK_ALL_YOU_LOSE}, returns the row coordinate
     *         of the hit. For all other ShotStatus values, the returned value
     *         is undefined.
     */
    public final int getRow() {
        return row;
    }

    /**
     * Provides the column coordinate of what you hit, provided that you
     * actually hit something.
     * 
     * @return When the value of {@link #getStatus} is {@link ShotStatus#HIT},
     *         {@link ShotStatus#SUNK_SHIP}, {@link ShotStatus#SUNK_ALL_YOU_WIN}
     *         , {@link ShotStatus#SUNK_ALL_YOU_TIE} or
     *         {@link ShotStatus#SUNK_ALL_YOU_LOSE}, returns the column
     *         coordinate of the hit. For all other ShotStatus values, the
     *         returned value is undefined.
     */

    public final int getCol() {
        return col;
    }

    /**
     * Provides the cost of your shot attempt.
     * 
     * @return The cost of the shot associated with this reply. The value
     *         depends on the weapon type used and whether and where you hit
     *         something.  If you missed the board, the maximum shot cost for
     *         the associated weapon type is incurred. For now, cost is always 1
     */
    public final int getCost() {
        return cost;
    }

    /**
     * Tells you what you sunk, provided that you sunk something.
     * 
     * @return When the value of {@link #getStatus} is
     *         {@link ShotStatus#SUNK_SHIP}, {@link ShotStatus#SUNK_ALL_YOU_WIN}
     *         , {@link ShotStatus#SUNK_ALL_YOU_TIE} or
     *         {@link ShotStatus#SUNK_ALL_YOU_LOSE}, returns the name of the
     *         ship that was sunk. For all other ShotStatus values, the value
     *         returned is undefined.
     */
    public final String getShipName() {
        return shipName;
    }

    void setStatus(ShotStatus s) {
        this.status = s;
    }

    public String toString() {
        return "ShotReply(" + status + ", (" + row + "," + col + "), " + cost + ", " + shipName + ")";
    }

}
