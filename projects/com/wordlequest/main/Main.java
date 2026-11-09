package com.wordlequest.main;

import com.wordlequest.game.WordSelector;
import com.wordlequest.game.WordleGame;
import com.wordlequest.level.Level;
import com.wordlequest.level.LevelManager;
import com.wordlequest.menu.MainMenu;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        MainMenu menu = new MainMenu();
        LevelManager lm = new LevelManager();
        WordSelector ws = new WordSelector();
        Scanner input = new Scanner(System.in);

        int choice = menu.showMenu();
        if (choice == 1) {
            for (int i = lm.getCurrentLevelIndex(); i < lm.getTotalLevels(); i++) {
                Level level = lm.getLevel(i);
                boolean levelCleared = false;

                while (!levelCleared) {
                    System.out.println("\n====================");
                    System.out.println("LEVEL " + level.getLevelNumber());
                    System.out.println("Word Length: " + level.getWordLength());
                    System.out.println("Words To Solve: " + level.getTotalWords());
                    System.out.println("====================");

                    int w = 0;
                    boolean levelFailed = false;
                    ws.clearHistory();

                    while (w < level.getTotalWords() && !levelFailed) {
                        System.out.println("\n====================");
                        System.out.println("WORD " + (w + 1) + " OF " + level.getTotalWords());
                        System.out.println("====================");

                        String word = ws.getWord(level.getWordLength());
                        WordleGame game = new WordleGame(word, 5);
                        boolean win = false;

                        while (!game.isOutOfAttempts() && !win) {
                            System.out.println("Enter Guess:");
                            String guess = input.next();
                            win = game.playRound(guess);

                            if (win) {
                                System.out.println("Correct Guess!");
                                w++;
                            }
                        }

                        if (!win) {
                            System.out.println("\n GAME OVER! You failed to guess the word: " + word.toUpperCase());
                            levelFailed = true;
                        }
                    }

                    if (!levelFailed) {
                        System.out.println("Level " + level.getLevelNumber() + " Completed successfully!");
                        lm.completeCurrentLevel();
                        levelCleared = true;
                    } else {
                        System.out.println("\nWould you like to try Level " + level.getLevelNumber() + " again?");
                        System.out.println("1. Yes, Retry Level");
                        System.out.println("2. No, Back to Menu");

                        int retryChoice = input.nextInt();
                        if (retryChoice != 1) {
                            System.out.println("Returning to main menu...");

                            return;
                        }
                        System.out.println("Restarting Level " + level.getLevelNumber() + "...");
                    }
                }
            }
        } else {
            System.out.println("Exit Game");
        }
    }
}
