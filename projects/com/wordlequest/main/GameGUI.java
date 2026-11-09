package com.wordlequest.main;

import com.wordlequest.game.WordSelector;
import com.wordlequest.game.WordleGame;
import com.wordlequest.level.Level;
import com.wordlequest.level.LevelManager;

import javafx.application.Application;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.stage.Stage;

public class GameGUI extends Application {

    private Stage window;
    private Scene splashScene;
    private Scene mainMenuScene;
    private Scene levelSelectScene;
    private Scene gameScene;

    private LevelManager lm;
    private WordSelector ws;
    private WordleGame game;
    private Level currentLevel;

    private int currentWordNumber = 1;
    private String currentSecretWord;
    private int currentAttemptRow = 0;


    private static final int MAX_ROWS = 5;

    private Timeline gameTimeline;
    private int secondsRemaining = 60;

    private GridPane wordGrid;
    private Label[][] gridCells;
    private TextField guessInputField;
    private Button enterButton;
    private Label statusBannerLabel;
    private Label gameFeedbackLabel;
    private Button actionNextButton;

    private MediaPlayer backgroundMusic;
    private AudioClip clickSound;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;

        this.lm = new LevelManager();
        this.ws = new WordSelector();


        try {
            String musicPath = getClass().getResource("/resources/bg_music2.mp3").toExternalForm();
            Media media = new Media(musicPath);
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusic.setVolume(0.4);
            backgroundMusic.play();

            String clickPath = getClass().getResource("/resources/click.mp3").toExternalForm();
            clickSound = new AudioClip(clickPath);
            clickSound.setVolume(0.7);
        } catch (Exception e) {
            System.out.println("Audio track resources not found. Sound initialization skipped.");
        }

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #FFFFFF;");

        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/resources/agh_logo.png"));
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitWidth(550);
            logoView.setPreserveRatio(true);
            logoView.setOpacity(0.0);
            root.getChildren().add(logoView);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), logoView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            PauseTransition hold = new PauseTransition(Duration.seconds(1.5));

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), logoView);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            SequentialTransition animationSequence = new SequentialTransition(fadeIn, hold, fadeOut);
            animationSequence.setOnFinished(e -> launchMainMenuScreen());
            animationSequence.play();
        } catch (Exception e) {
            System.out.println("Logo graphics file missing. Skipping directly to main menu.");
            launchMainMenuScreen();
        }

        splashScene = new Scene(root, 700, 500);
        window.setTitle("WordleQuest - AGH Productions");
        window.setScene(splashScene);
        window.show();
    }

    private void launchMainMenuScreen() {
        VBox menuLayout = new VBox(25);
        menuLayout.setPadding(new Insets(50));
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setStyle("-fx-background-color: #121213;");

        Label titleLabel = new Label("WORDLE QUEST");
        titleLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #538d4e;");

        Label subtitleLabel = new Label("A Semester Object-Oriented Project");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #818384;");

        Button playButton = new Button("PLAY GAME");
        playButton.setPrefWidth(220);
        playButton.setPrefHeight(45);
        playButton.setStyle(
                "-fx-background-color: #538d4e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 5px; -fx-cursor: hand;");
        playButton.setOnAction(e -> {
            playButtonSound();
            openLevelSelectionScreen();
        });

        Button exitButton = new Button("EXIT");
        exitButton.setPrefWidth(220);
        exitButton.setPrefHeight(45);
        exitButton.setStyle(
                "-fx-background-color: #3a3a3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 5px; -fx-cursor: hand;");
        exitButton.setOnAction(e -> {
            playButtonSound();
            if (backgroundMusic != null)
                backgroundMusic.stop();
            window.close();
        });

        menuLayout.getChildren().addAll(titleLabel, subtitleLabel, playButton, exitButton);
        mainMenuScene = new Scene(menuLayout, 700, 500);
        window.setScene(mainMenuScene);
    }

    private void openLevelSelectionScreen() {
        if (gameTimeline != null) {
            gameTimeline.stop();
        }

        VBox selectorContainer = new VBox(20);
        selectorContainer.setPadding(new Insets(30));
        selectorContainer.setAlignment(Pos.CENTER);
        selectorContainer.setStyle("-fx-background-color: #121213;");

        Label headerLabel = new Label("SELECT A LEVEL");
        headerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        GridPane levelsGrid = new GridPane();
        levelsGrid.setHgap(15);
        levelsGrid.setVgap(15);
        levelsGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < lm.getTotalLevels(); i++) {
            Level activeLevel = lm.getLevel(i);
            int levelNum = activeLevel.getLevelNumber();

            Button levelTileButton = new Button(String.valueOf(levelNum));
            levelTileButton.setPrefSize(75, 75);

            if (activeLevel.isUnlocked()) {
                levelTileButton.setStyle(
                        "-fx-background-color: #538d4e; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
                levelTileButton.setOnAction(e -> {
                    playButtonSound();
                    currentLevel = activeLevel;
                    currentWordNumber = 1;
                    ws.clearHistory();
                    loadActiveGameBoard();
                });
            } else {
                levelTileButton.setStyle(
                        "-fx-background-color: #272729; -fx-text-fill: #565758; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8px;");
                levelTileButton.setDisable(true);
            }

            int column = i % 5;
            int row = i / 5;
            levelsGrid.add(levelTileButton, column, row);
        }

        ScrollPane scrollWrapper = new ScrollPane(levelsGrid);
        scrollWrapper.setFitToWidth(true);
        scrollWrapper.setPrefHeight(320);
        scrollWrapper
                .setStyle("-fx-background: #121213; -fx-background-color: #121213; -fx-border-color: transparent;");

        Button returnMenuButton = new Button("BACK TO MAIN MENU");
        returnMenuButton.setPrefWidth(220);
        returnMenuButton.setStyle(
                "-fx-background-color: #3a3a3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 5px; -fx-cursor: hand;");
        returnMenuButton.setOnAction(e -> {
            playButtonSound();
            window.setScene(mainMenuScene);
        });

        selectorContainer.getChildren().addAll(headerLabel, scrollWrapper, returnMenuButton);
        levelSelectScene = new Scene(selectorContainer, 700, 500);
        window.setScene(levelSelectScene);
    }

    private void loadActiveGameBoard() {
        try {
            currentSecretWord = ws.getWord(currentLevel.getWordLength());
        } catch (Exception e) {
            currentSecretWord = "apple";
        }

        game = new WordleGame(currentSecretWord, 5);
        currentAttemptRow = 0;

        VBox gameRoot = new VBox(15);
        gameRoot.setPadding(new Insets(20));
        gameRoot.setAlignment(Pos.CENTER);
        gameRoot.setStyle("-fx-background-color: #121213;");

        statusBannerLabel = new Label();
        statusBannerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        wordGrid = new GridPane();
        wordGrid.setHgap(8);
        wordGrid.setVgap(8);
        wordGrid.setAlignment(Pos.CENTER);

        int cols = currentLevel.getWordLength();
        gridCells = new Label[MAX_ROWS][cols];

        for (int r = 0; r < MAX_ROWS; r++) {
            for (int c = 0; c < cols; c++) {
                Label cell = new Label("");
                cell.setPrefSize(45, 45);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle(
                        "-fx-border-color: #3a3a3c; -fx-border-width: 2px; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
                gridCells[r][c] = cell;
                wordGrid.add(cell, c, r);
            }
        }

        guessInputField = new TextField();
        guessInputField.setPromptText("Enter guess...");
        guessInputField.setMaxWidth(160);
        guessInputField.setPrefHeight(35);
        guessInputField.setStyle(
                "-fx-background-color: #3a3a3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center;");
        guessInputField.setOnAction(e -> handleUserGuessInput());

        enterButton = new Button("ENTER");
        enterButton.setPrefHeight(35);
        enterButton.setPrefWidth(90);
        enterButton.setStyle(
                "-fx-background-color: #565758; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        enterButton.setOnAction(e -> handleUserGuessInput());

        HBox controlRow = new HBox(10, guessInputField, enterButton);
        controlRow.setAlignment(Pos.CENTER);

        gameFeedbackLabel = new Label("Good Luck!");
        gameFeedbackLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #818384; -fx-font-weight: bold;");

        actionNextButton = new Button("NEXT WORD");
        actionNextButton.setVisible(false);
        actionNextButton.setStyle(
                "-fx-background-color: #538d4e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 25px; -fx-cursor: hand;");
        actionNextButton.setOnAction(e -> processNextWordOrLevelShift());

        Button exitToHubButton = new Button("QUIT TO MAP");
        exitToHubButton.setStyle(
                "-fx-background-color: #3a3a3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6px 15px; -fx-cursor: hand;");
        exitToHubButton.setOnAction(e -> {
            playButtonSound();
            openLevelSelectionScreen();
        });

        gameRoot.getChildren().addAll(statusBannerLabel, wordGrid, controlRow, gameFeedbackLabel, actionNextButton,
                exitToHubButton);
        gameScene = new Scene(gameRoot, 700, 500);
        window.setScene(gameScene);

        setupAndStartTimer();
    }

    private void handleUserGuessInput() {
        playButtonSound();
        String guess = guessInputField.getText().trim().toLowerCase();
        int expectedLength = currentLevel.getWordLength();

        if (guess.length() != expectedLength) {
            gameFeedbackLabel.setText("Error: Must be exactly " + expectedLength + " letters!");
            gameFeedbackLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            return;
        }

        boolean isCorrect = game.playRound(guess);
        String secret = currentSecretWord.toLowerCase();
        String[] tileStyles = new String[expectedLength];
        boolean[] secretLettersMatched = new boolean[expectedLength];

        for (int i = 0; i < expectedLength; i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                tileStyles[i] = "-fx-background-color: #538d4e; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;";
                secretLettersMatched[i] = true;
            }
        }

        for (int i = 0; i < expectedLength; i++) {
            if (tileStyles[i] != null)
                continue;

            char guessChar = guess.charAt(i);
            boolean foundYellowMatch = false;

            for (int j = 0; j < expectedLength; j++) {
                if (!secretLettersMatched[j] && secret.charAt(j) == guessChar) {
                    tileStyles[i] = "-fx-background-color: #b59f3b; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;";
                    secretLettersMatched[j] = true;
                    foundYellowMatch = true;
                    break;
                }
            }

            if (!foundYellowMatch) {
                tileStyles[i] = "-fx-background-color: #3a3a3c; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;";
            }
        }

        for (int i = 0; i < expectedLength; i++) {
            Label cell = gridCells[currentAttemptRow][i];
            cell.setText(String.valueOf(guess.charAt(i)).toUpperCase());
            cell.setStyle(tileStyles[i]);
        }

        currentAttemptRow++;
        guessInputField.clear();

        if (isCorrect) {
            gameFeedbackLabel.setText(" EXCELLENT! You guessed the word correctly!");
            gameFeedbackLabel.setStyle("-fx-text-fill: #538d4e; -fx-font-weight: bold;");
            triggerEndOfRoundState(true);
        } else if (game.isOutOfAttempts() || currentAttemptRow >= MAX_ROWS) {
            gameFeedbackLabel.setText(" GAME OVER! Word was: " + currentSecretWord.toUpperCase());
            gameFeedbackLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            triggerEndOfRoundState(false);
        } else {
            gameFeedbackLabel.setText("Keep guessing!");
            gameFeedbackLabel.setStyle("-fx-text-fill: #818384;");
        }
    }

    private void triggerEndOfRoundState(boolean won) {
        if (gameTimeline != null) {
            gameTimeline.stop();
        }

        guessInputField.setDisable(true);
        enterButton.setDisable(true);
        actionNextButton.setVisible(true);

        if (won) {
            if (currentWordNumber >= currentLevel.getTotalWords()) {
                actionNextButton.setText("CLAIM LEVEL VICTORY");
            } else {
                actionNextButton.setText("NEXT WORD");
            }
        } else {
            actionNextButton.setText("RETRY LEVEL");
            actionNextButton.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 25px;");
        }
    }

    private void processNextWordOrLevelShift() {
        playButtonSound();

        if (actionNextButton.getText().equals("RETRY LEVEL")) {
            currentWordNumber = 1;
            ws.clearHistory();
            loadActiveGameBoard();
            return;
        }

        if (currentWordNumber >= currentLevel.getTotalWords()) {
            lm.completeCurrentLevel();
            openLevelSelectionScreen();
        } else {
            currentWordNumber++;
            loadActiveGameBoard();
        }
    }

    private void playButtonSound() {
        if (clickSound != null) {
            clickSound.play();
        }
    }

    private void setupAndStartTimer() {
        if (gameTimeline != null) {
            gameTimeline.stop();
        }

        int levelNum = currentLevel.getLevelNumber();
        if (levelNum < 10) {
            secondsRemaining = 60;
        } else if (levelNum < 20) {
            secondsRemaining = 100;
        } else if (levelNum <= 25) {
            secondsRemaining = 150;
        } else {
            secondsRemaining = 60;
        }

        updateTimerLabelDisplay();

        gameTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    secondsRemaining--;
                    updateTimerLabelDisplay();
                    if (secondsRemaining <= 0) {
                        handleTimerExpiration();
                    }
                }));
        gameTimeline.setCycleCount(Timeline.INDEFINITE);
        gameTimeline.play();
    }

    private void updateTimerLabelDisplay() {
        if (statusBannerLabel != null && currentLevel != null) {

            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;

            statusBannerLabel.setText(String.format("LEVEL %d  │  WORD %d OF %d  (Length: %d)  │  ⏱️ %d:%02d",
                    currentLevel.getLevelNumber(),
                    currentWordNumber,
                    currentLevel.getTotalWords(),
                    currentLevel.getWordLength(),
                    minutes,
                    seconds));
        }
    }

    private void handleTimerExpiration() {
        if (gameTimeline != null) {
            gameTimeline.stop();
        }
        playButtonSound();
        gameFeedbackLabel.setText("⏱️TIME EXPIRED! Restarting current word...");
        gameFeedbackLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(ev -> loadActiveGameBoard());
        pause.play();
    }

    public static void main(String[] args) {

        System.setProperty("prism.order", "sw");

        launch(args);
    }
}
