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
 * Purpose: Inner class for game location coordinates.
 */
class Location {

    private int _row;
    private int _col;

    // Note, in the future, consider adding "layer" dimension.
    public Location(int row, int col) {
        _row = row;
        _col = col;
    }

    public int getRow() {
        return _row;
    }

    public int getCol() {
        return _col;
    }

}

// TODO May need to change the scope of methods to package/private to avoid
// cheating.

@Copyright(Copyright.c2014)
public class GameSpace {

    private Location _location;
    private GameBoardShip _ship = null;
    private int _shotCount = 0;

    public GameSpace(Location location) {
        _location = location;
    }

    public GameSpace(int row, int col) {
        this(new Location(row, col));
    }

    public void shoot() {
        _shotCount++;
    }

    public boolean isShot() {
        return _shotCount > 0;
    }

    public boolean hasShip() {
        return _ship != null;
    }

    public int getShotCount() {
        return _shotCount;
    }

    public Location getLocation() {
        return _location;
    }

    public GameBoardShip getShip() {
        return _ship;
    }

    public void setShip(GameBoardShip pShip) {
        _ship = pShip;
    }

}
