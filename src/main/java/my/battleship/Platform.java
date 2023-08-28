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
import java.util.Map;

/**
 * Defines the interface to the Platform. A Player instance uses this interface
 * to determine the board dimensions and to shoot at ships.
 */
@Copyright(Copyright.c2014)
public interface Platform {

	/**
	 * Query the number of rows in the current game board.
	 * 
	 * @return int
	 */
	public int getNumberOfRows();

	/**
	 * Query the number of columns in the current game board.
	 * 
	 * @return int
	 */
	public int getNumberOfCols();

	/**
	 * Shoot a bomb at the specified location.
	 * 
	 * @param row
	 *            -- rows range from 0 to getNumberOfRows() - 1.
	 * @param col
	 *            -- cols range from 0 to getNumberOfCols() - 1.
	 * @return ShotReply -- the result of the single shot, hit, miss, ... See
	 *         {@link ShotReply}
	 */
	public ShotReply shoot(int row, int col);

	/**
	 * Obtain a list of ships in the current board.
	 * 
	 * @return list of ships
	 */
	List<Ship> listShips();

	/**
	 * Obtain an unmodifiable list of ships that have been sunk on the current
	 * board.
	 * 
	 * The list is sorted such that the most recent ship that has been sunk is
	 * at position 0 in the list.
	 * 
	 * @return list of sunk ships
	 */
	List<Ship> listSunkShips();

}
