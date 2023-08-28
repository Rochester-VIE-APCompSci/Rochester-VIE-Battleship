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

@Copyright(Copyright.c2014)
public class Barrier {

    private long numThreads;
    private long count;
    private long barrierId = 0;

    Barrier(int n) {
        this.numThreads = n;
        this.count = n;
    }

    synchronized void waitForAll() throws InterruptedException {

        long myCount = --count;
        if (myCount > 0) {
            long myId = barrierId;
            do {
                wait();
            } while (myId == barrierId);
        } else {
            count = numThreads;
            barrierId++;
            notifyAll();
        }
    }
  
    synchronized long getCount() {
        return count;
    }

    synchronized void reset() {
        count = numThreads;
        barrierId++;
        notifyAll();
    }
}
