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

import java.util.ArrayList;
import java.util.List;

@Copyright(Copyright.c2014)
public class AdvancedWeapon {

    private List<Shot> shots;
    private int maxCost;
    private final WeaponType wtype;
    private Location origin;

    public Location getOrigin() {
        return origin;
    }

    AdvancedWeapon(WeaponType type, int max, int row, int col) {
        this.wtype = type;
        this.maxCost = max;
        this.origin = new Location(row, col);
    }

    public synchronized final WeaponType getType() {
        return wtype;
    }

    public int getNumberOfShots() {
        return shots.size();
    }

    public final Location getLocation(int n) {
        return shots.get(n).location;
    }

    public int getCost(int n) {
        return shots.get(n).cost;
    }

    public int getMaximumCost() {
        return maxCost;
    }

    private static class Shot {
        final Location location;
        final int cost;

        Shot(Location l, int cost) {
            this.location = l;
            this.cost = cost;
        }
    }

    public static class Factory {

        public static AdvancedWeapon create(WeaponType weaponType, int row, int col, Direction direction) {
            switch (weaponType) {
            case BOMB: {
                return createBomb(row, col);
            }
            case TORPEDO: {
                return createTorpedo(row, col, direction);
            }
            case MISSILE: {
                return createMissile(row, col, direction);
            }
            case ROCKET: {
                return createRocket(row, col, direction);
            }

            default: {
                throw new UnsupportedOperationException();
            }
            }
        }

        static AdvancedWeapon createBomb(int row, int col) {
            AdvancedWeapon bomb = new AdvancedWeapon(WeaponType.BOMB, 1, row, col);
            bomb.shots = new ArrayList<AdvancedWeapon.Shot>();
            bomb.shots.add(new Shot(new Location(row, col), 1));
            return bomb;
        }

        static private Location _TORPEDO_DELTAS[] = new Location[] { new Location(0, 0), new Location(0, 1),
                new Location(0, 2), new Location(0, 3), new Location(0, 4), new Location(0, 5), };

        static private int _TORPEDO_COSTS[] = new int[] { 1, 1, 3, 3, 5, 5, 6 // max
                                                                              // cost
        };

        static AdvancedWeapon createTorpedo(int row, int col, Direction direction) {
            AdvancedWeapon torpedo = new AdvancedWeapon(WeaponType.TORPEDO, _TORPEDO_COSTS[_TORPEDO_COSTS.length - 1], row, col);
            torpedo.shots = new ArrayList<AdvancedWeapon.Shot>();
            for (int i = 0; i < _TORPEDO_DELTAS.length; i++) {
                Location delta = _TORPEDO_DELTAS[i];
                switch (direction) {
                case EAST:
                    break;
                case SOUTH:
                    delta = flip(delta);
                    break;
                case WEST:
                    delta = negateY(delta);
                    break;
                case NORTH:
                    delta = flip(delta);
                    delta = negateX(delta);
                    break;
                }
                torpedo.shots.add(new Shot(new Location(row + delta.getRow(), col + delta.getCol()), _TORPEDO_COSTS[i]));
            }
            return torpedo;
        }

        static private Location _MISSILE_DELTAS[] = new Location[] { new Location(0, 0), new Location(0, 1),
                new Location(0, 2), new Location(0, 3), new Location(1, 3), new Location(2, 3), new Location(3, 3),
                new Location(3, 2), new Location(3, 1), new Location(3, 0), new Location(2, 0), new Location(1, 0), };

        static AdvancedWeapon createMissile(int row, int col, Direction direction) {
            AdvancedWeapon missile = new AdvancedWeapon(WeaponType.MISSILE, _MISSILE_COSTS[_MISSILE_COSTS.length - 1], row, col);
            missile.shots = new ArrayList<AdvancedWeapon.Shot>();
            for (int i = 0; i < _MISSILE_DELTAS.length; i++) {
                Location delta = _MISSILE_DELTAS[i];
                switch (direction) {
                case EAST:
                    break;
                case SOUTH:
                    delta = flip(delta);
                    delta = negateY(delta);
                    break;
                case WEST:
                    delta = negateX(delta);
                    delta = negateY(delta);
                    break;
                case NORTH:
                    delta = flip(delta);
                    delta = negateX(delta);
                    break;
                }
                missile.shots.add(new Shot(new Location(row + delta.getRow(), col + delta.getCol()), _MISSILE_COSTS[i]));
            }
            return missile;
        }

        static private int _MISSILE_COSTS[] = new int[] { 2, 2, 2, 6, 6, 6, 9, 9, 9, 11, 11, 11, 12, };

        static private Location _ROCKET_DELTAS[] = new Location[] { new Location(0, 4), new Location(-1, 4),
                new Location(1, 4), new Location(-2, 4), new Location(2, 4), };

        static AdvancedWeapon createRocket(int row, int col, Direction direction) {
            AdvancedWeapon rocket = new AdvancedWeapon(WeaponType.ROCKET, _ROCKET_COSTS[_ROCKET_COSTS.length - 1], row, col);
            rocket.shots = new ArrayList<AdvancedWeapon.Shot>();
            for (int i = 0; i < _ROCKET_DELTAS.length; i++) {
                Location delta = _ROCKET_DELTAS[i];
                switch (direction) {
                case EAST:
                    break;
                case SOUTH:
                    delta = flip(delta);
                    delta = negateY(delta);
                    break;
                case WEST:
                    delta = negateX(delta);
                    delta = negateY(delta);
                    break;
                case NORTH:
                    delta = flip(delta);
                    delta = negateX(delta);
                    break;
                }
                rocket.shots.add(new Shot(new Location(row + delta.getRow(), col + delta.getCol()), _ROCKET_COSTS[i]));
            }
            return rocket;
        }

        // @todo what are the costs for rocket?
        static private int _ROCKET_COSTS[] = new int[] { 2, 2, 2, 4, 4, 5, // max
                                                                           // cost
        };

        static private Location flip(Location l) {
            return new Location(l.getCol(), l.getRow());
        }

        static private Location negateX(Location l) {
            return new Location(-l.getRow(), l.getCol());
        }

        static private Location negateY(Location l) {
            return new Location(l.getRow(), -l.getCol());
        }
    }
}
