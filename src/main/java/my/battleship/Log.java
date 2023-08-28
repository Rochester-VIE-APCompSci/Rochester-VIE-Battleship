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

import java.io.PrintStream;
import java.util.Date;

@Copyright(Copyright.c2014)
public class Log {

    public static final int Silent = 0;
    public static final int Error = 1;
    public static final int Event = 2;
    public static final int Warning = 3;
    public static final int Debug = 4;
    public static final int GoryDetail = 5;

    public static String ANNOTATIONS[] = { "S", "E", "V", "W", "D", "*" };

    private static Log _instance = new Log();

    private int loggingLevel = Silent;
    private PrintStream log = System.out;

    public static Log instance() {
        return _instance;
    }

    public void setLoggingLevel(int level) {
        if ((level >= 0) && (level <= GoryDetail)) {
            this.loggingLevel = level;
        } else {
            System.err.println("(W) Logging level could not be set to: " + level + " from here: ");
            new Exception().printStackTrace(System.err);
        }
    }

    public int getLoggingLevel() {
        return this.loggingLevel;
    }

    public void log(Object o, int level) {
        if ((level <= loggingLevel) && (level > Silent)) {
            Date current = new Date();
            if (o instanceof Throwable) {
                log.print(String.format("%1$tF %2$tT.%3$tL [%4$-16.16s] [%5$1s] ", current, current, current, Thread
                        .currentThread().getName(), ANNOTATIONS[level]));
                ((Throwable) o).printStackTrace(this.log);
            } else {
                log.println(String.format("%1$tF %2$tT.%3$tL [%4$-16.16s] [%5$1s] %6$s", current, current, current, Thread
                        .currentThread().getName(), ANNOTATIONS[level], o.toString()));

            }
        }
    }

    private Log() {
    }
}
