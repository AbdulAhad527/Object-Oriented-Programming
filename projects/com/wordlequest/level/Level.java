/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wordlequest.level;

/**
 *
 * @author ML
 */
public class Level {
    private int levelNumber;
    private int wordLength;
    private int totalWords;
    private boolean unlocked;
    private boolean completed;
    
    public Level(int levelNumber,int totalWords)
    {
    this.levelNumber=levelNumber;
    this.totalWords=totalWords;
    this.wordLength=3+(levelNumber-1)/3;
    this.unlocked=(levelNumber==1);
    this.completed=false;
    }  
    public int getLevelNumber()
    {
   return levelNumber;
    }
    public int getWordLength()
    {
    return wordLength;
    }
      public int getTotalWords()
    {
    return totalWords;
    }
    public void setUnlocked(boolean unlocked)
    {
    this.unlocked=unlocked;
    }  
    public boolean isUnlocked()
    {
    return unlocked;
    }
     public void setCompleted(boolean completed)
    {
    this.completed=completed;
    }  
     public boolean isCompleted()
    {
    return completed;
    }
    
}
