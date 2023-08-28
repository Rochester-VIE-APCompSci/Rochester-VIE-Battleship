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

import java.util.Map;

/**
 * Defines the interface to the Platform. A Player instance uses this interface
 * to determine the board dimensions and to shoot at ships.
 */
@Copyright(Copyright.c2014)
public interface AdvancedPlatform extends Platform {

	/**
	 * Shoot a weapon in an advanced game.
	 * 
	 * <p>
	 * <u>Note:</u> the targeted cells should be legal board locations otherwise
	 * you may expect a shot status of {@link ShotStatus#MISSED_BOARD} and the
	 * maximum incurred cost. This includes the origin point of a
	 * {@link WeaponType#ROCKET}
	 * 
	 * @param row row index to select
	 * @param col column index to select
	 * @param weaponType
	 *            identifies the type of weapon being used.
	 * @param direction
	 *            identifies the orientation of the weapon wherever this makes
	 *            sense.
	 * @return detailed status of your shot attempt.
	 */
	public ShotReply shoot(int row, int col, WeaponType weaponType,
			Direction direction);

	/**
	 * Obtain the number of remaining weapons, organized by weapon type.
	 * 
	 * @return map of weapons remaining
	 */
	public Map<WeaponType, Integer> getWeaponCounts();
}
