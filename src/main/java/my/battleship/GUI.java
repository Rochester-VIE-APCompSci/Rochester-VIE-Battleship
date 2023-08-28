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

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@Copyright(Copyright.c2014)
public class GUI {
  
  private GridEntry[][] guiGrid;
  private JFrame frame;
  private JPanel gridPanel;
  private JPanel buttonPanel;
  private Board gameBoard;
  boolean enableInput = true;
  private String title = "";
  private int totalShots = 0;
  int maxBoardRows;
  int maxBoardCols;
  private boolean showingShips = false;
  TitleBlinker titleBlinker = null;
  private boolean paused = true;
  private boolean stepOnce = false;

  public static void main(String args[]) {
    new GUI(args[0], "GUI GAME");
  }
  
  public GUI(String gamePropertiesFile, String title) {
    this.title = title;
    gameBoard = new Board(gamePropertiesFile);
    maxBoardRows = gameBoard.getMaxBoardRows();
    maxBoardCols = gameBoard.getMaxBoardCols();
    initializeGui(maxBoardRows, maxBoardCols, 0, 1);
    gameBoard._showUnhitShips = true;
    gameBoard.outputGrid();
  }
  
  public GUI(String title, int maxBoardRows, int maxBoardCols, int screenId, int screenCount) {
    this.maxBoardRows = maxBoardRows;
    this.maxBoardCols = maxBoardCols;
    this.title = title;
    initializeGui(maxBoardRows, maxBoardCols, screenId, screenCount);
    enableInput = false;
    PlatformImpl.setDelayInMillis(PlatformImpl.DEFAULT_DELAY);
  }
  
  public void showShips(Board b) {
    gameBoard = b; // save the board for later GUI work
    showingShips = true;
    
    for (GameBoardShip ship : b.getShipsOnGameBoard()) {
      int i = 0;
      for (GameSpace space : ship._occupiedSpace) {
        guiGrid[space.getLocation().getRow()][space.getLocation().getCol()].setIcon(ImageFactory.getIconForShip(ship, i++));
      }
    }
  }
  


  public void recordShot(int row, int col, ShotStatus status, int cost) {
    
    // Check pause loop and hold thread until step or play is clicked
    while(true) {
      if (isPaused()) {
        if(doStepOnce()) {
          break;
        }
        try {
          String currentTitle = frame.getTitle();
          if (!currentTitle.startsWith("PAUSED")) {
            frame.setTitle("PAUSED - " + frame.getTitle());
          }
          Thread.sleep(200);
        } catch (Exception e) {}
      } else {
        break;
      }
    }

    if ((row < 0) || (row >= maxBoardRows) || (col < 0) || (col >= maxBoardCols)) {
      Log.instance().log("Ignoring illegal coordinates", Log.Debug);
      return;
    }
    GridEntry target = guiGrid[row][col];
    
    int shotCount = target.incrementCount();
    
    // If showing ships we'll have a gameBoard instance.
    if (showingShips) {
      if (!gameBoard._gameGrid[row][col].hasShip() || !gameBoard._gameGrid[row][col].getShip().isSunk()) {
        // background should be water. make it lighter as more shots are fired at it (foamy)
        target.setBackground(new Color(1-1.0f/(1+shotCount), 1.0f, 1.0f)); // brighter CYAN for water
      }
    }

    switch (status) {
      case MISS:
        if (showingShips) {
        target.setIcon(ImageFactory.getIconForMiss());
      } else {
        target.setBackground(Color.WHITE);
      }
        break;
      case HIT:
        if (showingShips) {
        target.setIcon(ImageFactory.getIconForShipHit(target.getIcon()));
      } else {
        target.setBackground(Color.ORANGE);
      }
        break;
      case SUNK_ALL_YOU_TIE:
      case SUNK_ALL_YOU_WIN:
        target.setBackground(Color.RED);
        if (status == ShotStatus.SUNK_ALL_YOU_WIN) {
          title = " *** WINNER *** " + title;
        } else {
          title = " *** TIE *** " + title;
        }
        if (titleBlinker == null) {
          titleBlinker = new TitleBlinker(frame, title + " totalShots=" + (totalShots+cost));
          titleBlinker.start();
        }
      case SUNK_SHIP:
        target.setIcon(ImageFactory.getIconForShipHit(guiGrid[row][col].getIcon()));
        if (gameBoard != null) {
          for (GameSpace space : gameBoard._gameGrid[row][col].getShip()._occupiedSpace) {
            if (row == space.getLocation().getRow() && col == space.getLocation().getCol()) {
              target.setBackground(Color.RED);
            } else {
              guiGrid[space.getLocation().getRow()][space.getLocation().getCol()].setBackground(Color.ORANGE);
            }
          }
        } else {
          target.setBackground(Color.RED);
        }
        break;
      default:
        break;
    }
    totalShots += cost;
    frame.setTitle(title + " totalShots=" + totalShots);
    
  }
  
  public void setGameOverYouLose() {
    if (titleBlinker == null) {
      titleBlinker = new TitleBlinker(frame, "*** Game Over - No shots left - totalShots=" + totalShots);
      titleBlinker.start();
    }  
  }
  /* shoot method used in solo game */
  /* private so that it will not be used externally from this */
  private void shoot(int row, int col) {
    ShotStatus status = gameBoard.shoot(row, col);
    recordShot(row, col, status, 1);
    
  }
  
  public void initializeGui(int height, int width, int screenId, int screenCount) {
    frame = new JFrame();
    gridPanel = new JPanel();
    buttonPanel = new JPanel();
    JButton pauseButton = new JButton("Pause");
    JButton playButton = new JButton("Play");
    JButton stepButton = new JButton("Step");

    if (screenCount > 1) {
      // Figure out how to divide the screens
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Dimension dimension = toolkit.getScreenSize();
      //System.out.println("Bounds =" + dimension.getWidth() + "x" + dimension.getHeight());
      int xCount;
      int yCount;
      // Decide how many pieces to divide the screen into by
      // taking the square root and rounding up.
      xCount =  (int) ( Math.sqrt(screenCount) + 0.9999); 
      yCount = xCount; 
      int xSize = (int) (dimension.getWidth() / xCount); 
      int ySize = (int) (dimension.getHeight() / yCount); 
      int xPosition = (screenId % xCount) * xSize ; 
      int yPosition = ((screenId / yCount ) % yCount) * ySize ;
      
      Dimension sizeDimension = new Dimension(xSize, ySize);
      frame.setPreferredSize(sizeDimension); 
      frame.setLocation(xPosition, yPosition);
    }
    
    if (screenCount > 1) {
      // Figure out how to divide the screens
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Dimension dimension = toolkit.getScreenSize();
      //System.out.println("Bounds =" + dimension.getWidth() + "x" + dimension.getHeight());
      int xCount;
      int yCount;
      // Decide how many pieces to divide the screen into by
      // taking the square root and rounding up.
      xCount = (int) (Math.sqrt(screenCount) + 0.9999999);
      yCount = xCount;
      int xSize = (int) (dimension.getWidth() / xCount);
      int ySize = (int) (dimension.getHeight() / yCount);
      int xPosition = (screenId % xCount) * xSize;
      int yPosition = ((screenId / yCount) % yCount) * ySize;
      
      Dimension sizeDimension = new Dimension(xSize, ySize);
      frame.setPreferredSize(sizeDimension);
      frame.setLocation(xPosition, yPosition);
    }
    
    gridPanel.setLayout(new GridLayout(height, width));

    guiGrid = new GridEntry[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        guiGrid[i][j] = new GridEntry(i, j);
        
        guiGrid[i][j].setBackground(Color.CYAN);
        guiGrid[i][j].setIcon(ImageFactory.getIconForWaves());
        guiGrid[i][j].addActionListener(new ButtonActionListener(i, j));
        
        gridPanel.add(guiGrid[i][j]);
      }
    }
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

    pauseButton.addActionListener(new PauseActionListener());
    playButton.addActionListener(new PlayActionListener());
    stepButton.addActionListener(new StepActionListener());

    buttonPanel.add(pauseButton);
    buttonPanel.add(playButton);
    buttonPanel.add(stepButton);
    
    frame.getContentPane().add(gridPanel, BorderLayout.NORTH);
    frame.getContentPane().add(new JSeparator(), BorderLayout.CENTER);
    frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setTitle(title + " totalShots=" + totalShots);
    frame.setVisible(true);
    
  }
  
  private synchronized void setPaused(boolean doPause) {
    paused = doPause;
  }

  private synchronized boolean isPaused() {
    return paused;
  }

  private synchronized void stepOnce() {
    stepOnce = true;
  }

  private synchronized boolean doStepOnce() {
    boolean oldVal = stepOnce;
    stepOnce = false;
    return oldVal;
  }

  class ButtonActionListener implements ActionListener {
    int row;
    int column;
    
    ButtonActionListener(int row, int column) {
      this.row = row;
      this.column = column;
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (enableInput) {
        shoot(row, column);
      }
    }
    
  }
  
  class PauseActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent arg0) {
      setPaused(true);
      PlatformImpl.setDelayInMillis(PlatformImpl.DEFAULT_DELAY);
    }
  }

  class PlayActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent arg0) {
      setPaused(false);
      PlatformImpl.setDelayInMillis(PlatformImpl.getDelayInMillis() / 2);
    }
  }

  class StepActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent arg0) {
      stepOnce();
    }
  }

  class TitleBlinker extends Thread {
    private JFrame frame;
    private String title;
    
    public TitleBlinker(JFrame frame, String title) {
      super();
      setDaemon(true);
      this.frame = frame;
      this.title = title;
    }
    
    public void run() {
      try {
        while (true) {
          frame.setTitle(title);
          Thread.sleep(750);
          frame.setTitle("");
          Thread.sleep(250);
        }
      } catch (Exception e) {
        
      }
    }
  }
  
  class GridEntry extends JButton {
    private static final long serialVersionUID = -3384616041396747947L;
    int row = 0;
    int column = 0;
    int accessCount = 0;
    
    public GridEntry(int row, int column) {
      super();
      this.row = row;
      this.column = column;
      setPreferredSize(new Dimension(32, 32));
      setMargin(new Insets(0, 0, 0, 0));
      setBorder(BorderFactory.createEmptyBorder());
      //setText("" + row + "," + column + " " + accessCount);
    }
    
    public int incrementCount() {
      accessCount++;
      //setText("" + row + "," + column + " " + accessCount);
      return accessCount;
    }
  }
}
