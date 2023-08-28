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

import my.battleship.PlatformImpl;

public class InClassDemo {
    public static void main(String[] args) {
        PlatformImpl.main(new String[] {
                // Copy the Classic boards to a classicBoards directory under your project. 
                // These players do not like the advanced boards.
                "--boards", "classicBoards/", 
                "--player", BadPlayer.class.getName(), 
                "--player", ASlightlyBetterPlayer.class.getName(),
                "--player", "schoolname.lastname_firstname.CompletedTemplatePlayer",
                "--gui",
                "--showShips", "true",
                "--delay", "200"});
        
//        PlatformImpl.main(new String[] { 
//                "--boards", "advBoards/", 
//                "--player", "umr.simpson_lisa.PlayerWithAdvancedWeapons", 
//                "--player", "umr.simpson_lisa.AdvancedWeaponsDemo",
//                "--gui",
//                "--showShips", "true",
//                "--delay", "200",
//                "--advanced"});

    }
}
