package com.wordlequest.game;

public class WordleGame {
    private final String targetWord;
    private int attempts;
    private final int maxAttempts;

    public WordleGame(String targetWord, int maxAttempts) {
        this.targetWord = targetWord.toLowerCase();
        this.attempts = 0;
        this.maxAttempts = maxAttempts;
    }

    public Boolean playRound(String guess) {
        if (isOutOfAttempts()) {
            System.out.println("No attempts remaining!");
            return false;
        }

        guess = guess.toLowerCase().trim();


        if (guess.length() != targetWord.length()) {
            System.out.println("Invalid word length! Expected " + targetWord.length() + " characters.");
            return false;
        }

        attempts++;
        char[] target = targetWord.toCharArray();
        char[] input = guess.toCharArray();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length; i++) {
            if (input[i] == target[i]) {
                result.append("[G]");
            } else if (targetWord.contains(String.valueOf(input[i]))) {
                result.append("[Y]");
            } else {
                result.append("[_]");
            }
        }

        System.out.println("Guess: " + guess);
        System.out.println("Result: " + result.toString());

        if (guess.equals(targetWord)) {
            return true;
        }

        if (isOutOfAttempts()) {
            System.out.println("Out of attempts! The word was: " + targetWord.toUpperCase());
        }
        return false;
    }

    public boolean isOutOfAttempts() {
        return attempts >= maxAttempts;
    }
}
