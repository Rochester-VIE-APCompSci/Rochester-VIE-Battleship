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
public class PrivateCtor implements Player {

    private PrivateCtor() {

    }

    public void startGame(Platform platform) {
        throw new IllegalStateException("Should not get here");
    }

    public String getName() {
        throw new IllegalStateException("Should not get here");
    }

    @Override
    public String getSchool() {
        return "x";
    }

}
