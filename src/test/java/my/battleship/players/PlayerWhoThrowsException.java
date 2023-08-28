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
package my.battleship.players;

import my.battleship.Copyright;
import my.battleship.Platform;
import my.battleship.Player;

@Copyright(Copyright.c2014)
public class PlayerWhoThrowsException implements Player {

    @SuppressWarnings("null")
    @Override
    public void startGame(Platform platform) {

        platform.shoot(0, 0); // 3 ...
        platform.shoot(0, 0); // 2 ...
        platform.shoot(0, 0); // 1 ...

        // Wait for a second ... this means that other threads are likely
        // already in a barrier
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String bad = null;

        if (bad.length() == 99)
            ; // kaboom
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public String getSchool() {
        // TODO Auto-generated method stub
        return null;
    }

}
