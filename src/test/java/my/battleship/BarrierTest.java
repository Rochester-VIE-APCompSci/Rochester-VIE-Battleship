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

import java.util.Date;

import static org.junit.Assert.*;

import org.junit.Test;

@Copyright(Copyright.c2014)
public class BarrierTest {

    private int masterCount = 0;
    private int numberOfSlaves = 16;
    private long numberOfIterations = 10000;
    private Barrier barrier;

    public static void main(String[] args) {

        BarrierTest test = new BarrierTest();

        for (int i = 0; i < args.length; i++) {
            if ("--t".equals(args[i])) {
                test.numberOfSlaves = Integer.parseInt(args[++i]);
            } else if ("--n".equals(args[i])) {
                test.numberOfIterations = Long.parseLong(args[++i]);
            }
        }

        test.testBarriers();
    }

    @Test
    public void testBarriers() {
        try {
            this.barrier = new Barrier(numberOfSlaves + 1);
            Thread t[] = new Thread[numberOfSlaves];
            for (int i = 0; i < t.length; i++) {
                t[i] = new Thread(new Slave(numberOfIterations));
                t[i].setName("Slave-" + i);
                t[i].start();
            }
            for (int i = 0; i < numberOfIterations; i++) {
                barrier.waitForAll();
                setMasterCount(getMasterCount() + 1);
                barrier.waitForAll();
                if (((i + 1) % 100000) == 0) {
                    log("" + (i + 1) + " iterations complete.");
                }
            }
            for (int i = 0; i < t.length; i++) {
                t[i].join();
            }
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            fail();
        }
    }

    void log(String msg) {
        Date d = new Date();
        System.out
                .println(String.format("%1$tF %2$tT.%3$tL [%4$-16.16s] %5$s", d, d, d, Thread.currentThread().getName(), msg));
    }

    public synchronized int getMasterCount() {
        return masterCount;
    }

    public synchronized void setMasterCount(int masterCount) {
        this.masterCount = masterCount;
    }

    @Copyright(Copyright.c2014)
    private class Slave implements Runnable {

        long numIterations = 0;

        Slave(long n) {
            this.numIterations = n;
        }

        @Override
        public void run() {

            try {
                log("starting ...");
                for (int i = 1; i <= numIterations; i++) {
                    barrier.waitForAll();
                    barrier.waitForAll();
                    // int mc = getMasterCount();
                    if (i != getMasterCount()) {
                        System.out.println("Error on iteration " + i + " --> " + getMasterCount());
                        return;
                    }
                }
                log("shutting down.");
            } catch (Throwable t) {
                t.printStackTrace(System.out);
            }
        }
    }

}
