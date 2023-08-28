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
package demo;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import my.battleship.PlatformImpl;

public class Demo {

    public static void main(String[] args) throws ParserConfigurationException {

        JFileChooser chooser = null;
        chooser = new JFileChooser("jars");
        FileNameExtensionFilter jarFilter = new FileNameExtensionFilter("Solutions", "jar");
        chooser.setFileFilter(jarFilter);
        chooser.setMultiSelectionEnabled(true);

        while(true) {
            int result = chooser.showDialog(null, "Play Board(s)");
            if (result == JFileChooser.APPROVE_OPTION) {
              File[] jars = chooser.getSelectedFiles();

              try {
                URLClassLoader child = new URLClassLoader(new URL[] {jars[0].toURI().toURL()});
                Class classToLoad = Class.forName("student.player.TemplatePlayer", true, child);
                PlatformImpl.runWithBoardPrompt(classToLoad, true);
              } catch (Throwable t) {
                System.err.println("Exception caught: " + t);
              }
            } else {
                break;
            }
        }
    }
}
