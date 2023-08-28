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
import my.battleship.Log;
import my.battleship.Platform;
import my.battleship.Player;
import my.battleship.StatsPlugin;

@Copyright(Copyright.c2014)
public class PlayerWhoQuits implements Player, StatsPlugin {

    @Override
    public void setBoardSize(int rows, int cols) {
        // TODO Auto-generated method stub
        
    }

    public static boolean testFailed = false;

    @Override
    public void startGame(Platform ap) {
        // dont do anything
    }

    @Override
    public String getName() {
        return "Quitter";
    }

    @Override
    public String getSchool() {
        return "QSchool";
    }

    @Override
    public void accumulateDataPoint(String key, Player player, long shotCount, int rank, int share) {
        Log.instance().log(key + " shots=" + shotCount + " rank=" + rank + " share=" + share, Log.Event);
        if (player.getName().equals(this.getName())) {
            if (rank == 1) {
                Log.instance().log("Unexpected rank", Log.Error);
                testFailed = true;
            }
            if (shotCount != Long.MAX_VALUE) {
                Log.instance().log("Unexpected shot count", Log.Error);
                testFailed = true;
            }
        }
    }

    @Override
    public void dump() {
        // TODO Auto-generated method stub

    }

}
