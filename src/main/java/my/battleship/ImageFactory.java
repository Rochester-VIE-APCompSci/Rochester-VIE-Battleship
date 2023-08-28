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

import java.awt.Graphics;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;

public final class ImageFactory {
  static Map<String, BufferedImage> resourceMap = new HashMap<String, BufferedImage>();
  static AffineTransformOp rotate = new AffineTransformOp(AffineTransform.getRotateInstance(Math.PI/2, 16, 16), null);
  static ImageIcon missIcon = new darrylbu.icon.StretchIcon(getImageForResource("/images/Miss.png"), false);
  static ImageIcon wavesIcon = new darrylbu.icon.StretchIcon(getImageForResource("/images/Waves.png"), false);

  public static ImageIcon getIconForMiss() {
    return missIcon;
  }
  public static ImageIcon getIconForWaves() {
    return wavesIcon;
  }

  public static ImageIcon getIconForShipHit(Icon shipIcon) {
    
    BufferedImage compImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    Graphics g = compImg.getGraphics();
    g.drawImage(((ImageIcon)shipIcon).getImage(), 0, 0, null);
    g.drawImage(getImageForResource("/images/Explosion_color.png"), 0, 0, null);
    
    return new darrylbu.icon.StretchIcon(compImg, false);
  }
  public static ImageIcon getIconForShip(GameBoardShip ship, int idx){
    
    int length = ship.getLength();
    
    BufferedImage img;
    
    // Fetch the BufferedImage for the desired ship
    switch (length) {
      case 2: 
        if (ship.getName().equals("PT")) {
        img = getImageForResource("/images/Patrol_x2.png");
      } else {
        img = getImageForResource("/images/Destroyer_x2.png");
      }
      break;
      
      case 3: 
        if (ship.getName().equals("submarine")) {
        img = getImageForResource("/images/Submarine_x3.png");
      } else {
        img = getImageForResource("/images/Cruiser_x3.png");
      }
      break;
      case 4: img = getImageForResource("/images/Battleship_x4.png"); break;
      default: img = getImageForResource("/images/Carrier_x5.png"); break;
    }
    
    img = img.getSubimage(idx * 32, 0, 32, 32);
    
    if (ship.isVertical()) {
      // we need to rotate
      img = rotate.filter(img, null);
    }
    BufferedImage compImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    Graphics g = compImg.getGraphics();
    g.drawImage(getImageForResource("/images/Waves.png"), 0, 0, null);
    g.drawImage(img, 0, 0, null);
    
    return new darrylbu.icon.StretchIcon(compImg, false);
  }
  private static BufferedImage getImageForResource(String res) {
    if (!resourceMap.containsKey(res)) {
      try {
        resourceMap.put(res, ImageIO.read(ImageFactory.class.getResource(res)));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return resourceMap.get(res);
  }
}
