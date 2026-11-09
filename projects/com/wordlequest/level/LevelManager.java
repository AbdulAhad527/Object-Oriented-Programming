/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wordlequest.level;

/**
 *
 * @author ML
 */
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
public class LevelManager {
private Level[] Levels;
private int currentLevelIndex;
private final String saveFileName = "save_progress.txt";

public LevelManager()
{
Levels=new Level[25];
for(int i=0;i<25;i++)
{
int levelNumber=i+1;
int totalWords=5;
Levels[i]=new Level(levelNumber,totalWords);

}
loadProgress();
}
public Level getLevel(int index)
{ 
return Levels[index];
} 
public int getTotalLevels() 
{
        return Levels.length;
}
public void setCurrentLevelIndex(int index)
{
if(index>=0 && index<25)
{
currentLevelIndex=index;    
}
saveProgress();
}
public int getCurrentLevelIndex()
{
return currentLevelIndex;
}



public void loadProgress()
{
File file=new File(saveFileName);
if(!file.exists())
{
this.currentLevelIndex = 0;
return;
}
 try (Scanner fileScanner = new Scanner(file))
  {
            
    if (fileScanner.hasNextInt()) 
    {          
    int savedLevelNumber = fileScanner.nextInt();
    currentLevelIndex=savedLevelNumber-1;
    }
  }
  catch(Exception e)
  {

    System.out.println("Could not load progress. Starting from Level 1.");
    this.currentLevelIndex = 0;
    }
  }
public void saveProgress()
{
try(PrintWriter writer =new PrintWriter(new File(saveFileName)))
{
writer.println(currentLevelIndex+1);
}
catch(Exception e)
{
System.out.println("Could not save progress to file.");    
}
}
public void completeCurrentLevel() {
Levels[currentLevelIndex].setCompleted(true);
int nextLevelIndex = currentLevelIndex + 1;
if (nextLevelIndex < Levels.length)
 { 
        Levels[nextLevelIndex].setUnlocked(true);
        setCurrentLevelIndex(nextLevelIndex);
    }
}

}
