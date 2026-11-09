package com.wordlequest.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class WordSelector {
    private final ArrayList<Integer> usedWordsIndices = new ArrayList<>();
    private final Random rand = new Random();

    public String getWord(int length) {
        String filePath = "word_lists/words" + length + ".txt";
        File file = new File(filePath);
        ArrayList<String> allWordsInFile = new ArrayList<>();

          try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    allWordsInFile.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error accessing " + filePath + ". Default word 'cat' returned.");
            return "cat";
        }

        if (allWordsInFile.isEmpty()) {
            return "cat";
        }

       ArrayList<Integer> freshWordsIndices = new ArrayList<>();
        for (int i = 0; i < allWordsInFile.size(); i++) {
            if (!usedWordsIndices.contains(i)) {
                freshWordsIndices.add(i);
            }
        }

         if (freshWordsIndices.isEmpty()) {
            usedWordsIndices.clear();
            for (int i = 0; i < allWordsInFile.size(); i++) {
                freshWordsIndices.add(i);
            }
        }

        int randomIndex = rand.nextInt(freshWordsIndices.size());
        int selectedWordIndex = freshWordsIndices.get(randomIndex);
        usedWordsIndices.add(selectedWordIndex);

        String targetLine = allWordsInFile.get(selectedWordIndex);
        return extractWordText(targetLine);
    }

    public String extractWordText(String line) {
        int dotIndex = line.indexOf(".");
        if (dotIndex == -1) {
            return line.trim().toLowerCase(); // Fallback if line doesn't use "1.word" format
        }
        return line.substring(dotIndex + 1).trim().toLowerCase();
    }

    public void clearHistory() {
        usedWordsIndices.clear();
    }
}
