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

import java.util.List;

@Copyright(Copyright.c2014)
public class GameBoardShip extends Ship {

      // Occupied space is not null if the ship object is on the gameboard.
    List<GameSpace> _occupiedSpace = null;

    public GameBoardShip(int pLength, int pWidth, String name) {
        super(pLength, pWidth, name);
    }

    public String getDimensionString() {
        return (getLength() + " x " + getWidth());
    }

    public int getNumSpacesShot() {
        int spaceShotCount = 0;
        if (_occupiedSpace != null) {
            for (GameSpace gameSpace : _occupiedSpace) {
                if (gameSpace.isShot())
                    spaceShotCount++;
            }
        }
        return spaceShotCount;
    }

    public boolean isSunk() {
        if (_occupiedSpace != null && getNumSpacesShot() == _occupiedSpace.size()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isShipOnGameBoard() {
        return _occupiedSpace != null;
    }
    
    public boolean isHorizontal() {
      if (_occupiedSpace.size() >= 2) {
        return _occupiedSpace.get(0).getLocation().getCol() != _occupiedSpace.get(1).getLocation().getCol();
      }
      return true;
    }
    public boolean isVertical() {
      return !isHorizontal();
    }

    void setOccupiedSpace(List<GameSpace> pOccupiedSpace) {
        _occupiedSpace = pOccupiedSpace;
    }
    
}
