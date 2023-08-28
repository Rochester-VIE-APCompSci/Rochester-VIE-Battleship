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
package com.ibm.battleship.tools;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import my.battleship.Copyright;
import my.battleship.Log;

@Copyright(Copyright.c2014)
public class Probe {

    public static void main(String args[]) {
        Log.instance().setLoggingLevel(Log.Event);
        if (args.length > 1) {
            Log.instance().setLoggingLevel(Log.Debug);
        }
        try {
            JarFile jar = new JarFile(args[0]);
            jar.entries();

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                String className = e.getName();
                if (!className.endsWith(".class"))
                    continue;
                if (className.indexOf("$") >= 0)
                    continue;

                className = className.substring(0, className.length() - ".class".length());
                className = className.replaceAll("/", ".");
                Log.instance().log("Probing " + className,  Log.Debug);

                try {
                    Class clazz = Class.forName(className);
                    Object obj = clazz.newInstance();
                  
                } catch (Exception ex) {
                    Log.instance().log(ex,  Log.Debug);
                }
            }
            jar.close();
        } catch (Exception e) {
            Log.instance().log(e, Log.Error);
        }
    }
}
