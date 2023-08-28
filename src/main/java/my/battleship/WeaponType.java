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
 * WeaponType classifies the weapons in an advanced game.
 *
 */
@Copyright(Copyright.c2014)
public enum WeaponType {

    /**
     * BOMB - a single cell shot, just like the standard game. The direction
     * argument is ignored. You have unlimited bombs.
     */
    BOMB,
    
    /** 
     * TORPEDO - fire at up to six cells in a straight line starting at (row,
     * col) in the specified direction. The first cell hit is returned in the
     * ShotReply; at most one cell is hit. The cost is as follows: 1 shot if you
     * hit in the first 1 or 2 cells. 3 shots if you hit in the 3rd or 4th cell;
     * 5 shots if you hit in the 5th or 6th cell; 6 shots if you miss
     * altogether.
     */
    TORPEDO,
    
    /**
     * MISSILE - travels 3 cells in the specified direction, then turns right
     * and travels another 3 cells, then right again for 3 cells and finally
     * right again for another three. That is, it travels the exterior of a 4x4
     * square. If a ship is hit, no additional cells are traversed and the hit
     * cell is returned in the ShotReply. The cost is as follows: - 2 shots if
     * hit in the first leg 3 of cells. - 6 shots if hit in the second leg of 3
     * cells. - 9 shots if hit in the third leg of 3 cells - 11 shots if hit in
     * the fourth leg of 3 cells. - 12 shots if missed altogether
     */
    MISSILE,
    
    /**
     * ROCKET - jumps over 5 spaces in the specified direction (no hits) and
     * then covers 5 cells at the T, working from the middle out. The cost is 2
     * if hit in the middle three cells, 4 if hit in the the outer two cells and
     * 5 if there are no hits.
     */
    ROCKET,
    
 }
