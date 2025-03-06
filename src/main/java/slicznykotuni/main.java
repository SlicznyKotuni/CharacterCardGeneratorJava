package slicznykotuni;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.application.Platform;

public class main extends Application {

    private TextArea logArea;

    @Override
    public void start(Stage primaryStage) {
        // Ustawienia głównego okna
        primaryStage.setTitle("Character Card Generator");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        // Panel główny
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2A2A2A;");

        // Nagłówek
        Label titleLabel = new Label("Character Card Generator");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setPadding(new Insets(20));

        StackPane headerPane = new StackPane(titleLabel);
        headerPane.setStyle("-fx-background-color: #1A1A1A;");
        headerPane.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 30, 30), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 2, 0))));

        root.setTop(headerPane);

        // Panel przycisków
        GridPane buttonGrid = createButtonGrid();
        root.setCenter(buttonGrid);

        // Panel logów
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setStyle("-fx-control-inner-background: #3A3A3A; -fx-text-fill: white;");
        logArea.setPrefHeight(150);

        VBox logPanel = new VBox(new Label("Log"), logArea);
        logPanel.setPadding(new Insets(10));
        logPanel.setStyle("-fx-background-color: #222222;");
        ((Label)logPanel.getChildren().get(0)).setTextFill(Color.WHITE);

        root.setBottom(logPanel);

        // Tworzenie sceny
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        log("Aplikacja uruchomiona pomyślnie!");
    }

    private GridPane createButtonGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(30));

        // Efekt cienia dla przycisków
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(255, 30, 30, 0.7));
        shadow.setRadius(10);

        // Styl dla przycisków
        String buttonStyle =
                "-fx-background-color: #3A3A3A;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #FF1E1E;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;";

        String buttonHoverStyle =
                "-fx-background-color: #4A4A4A;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #FF5E5E;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;";

        // Przycisk 1: Generuj charakterystyki
        Button generateCharButton = createStyledButton("Generuj charakterystyki postaci", buttonStyle, buttonHoverStyle, shadow);
        generateCharButton.setPrefSize(300, 150);
        generateCharButton.setOnAction(e -> {
            log("Generowanie charakterystyk postaci...");
            new Thread(() -> {
                try {
                    RPGCardGenerator generator = new RPGCardGenerator();
                    generator.generateCharacters();
                    Platform.runLater(() -> log("Charakterystyki zostały wygenerowane do pliku characters.csv"));
                } catch (Exception ex) {
                    Platform.runLater(() -> log("Błąd: " + ex.getMessage()));
                    ex.printStackTrace();
                }
            }).start();
        });

        // Przycisk 2: Generuj karty
        Button generateCardsButton = createStyledButton("Generuj karty postaci", buttonStyle, buttonHoverStyle, shadow);
        generateCardsButton.setPrefSize(300, 150);
        generateCardsButton.setOnAction(e -> {
            log("Generowanie kart postaci...");
            new Thread(() -> {
                try {
                    CardMaker cardMaker = new CardMaker();
                    cardMaker.generateFromFile("characters.csv");
                    Platform.runLater(() -> log("Karty postaci zostały wygenerowane do folderu output/"));
                } catch (Exception ex) {
                    Platform.runLater(() -> log("Błąd: " + ex.getMessage()));
                    ex.printStackTrace();
                }
            }).start();
        });

        // Przycisk 3: Pełny proces
        Button fullProcessButton = createStyledButton("Wykonaj pełny proces", buttonStyle, buttonHoverStyle, shadow);
        fullProcessButton.setPrefSize(300, 150);
        fullProcessButton.setOnAction(e -> {
            log("Rozpoczynam pełny proces generowania...");
            new Thread(() -> {
                try {
                    // Generowanie charakterystyk
                    log("Generowanie charakterystyk...");
                    RPGCardGenerator gen = new RPGCardGenerator();
                    gen.generateCharacters();
                    Platform.runLater(() -> log("Charakterystyki zostały wygenerowane."));

                    // Generowanie kart
                    log("Generowanie kart...");
                    CardMaker maker = new CardMaker();
                    maker.generateFromFile("characters.csv");
                    Platform.runLater(() -> log("Karty postaci zostały wygenerowane."));

                    Platform.runLater(() -> log("Pełny proces zakończony pomyślnie!"));
                } catch (Exception ex) {
                    Platform.runLater(() -> log("Błąd: " + ex.getMessage()));
                    ex.printStackTrace();
                }
            }).start();
        });

        // Przycisk 4: Wyjście
        Button exitButton = createStyledButton("Wyjście", buttonStyle, buttonHoverStyle, shadow);
        exitButton.setPrefSize(300, 150);
        exitButton.setOnAction(e -> {
            log("Zamykanie aplikacji...");
            Platform.exit();
        });

        // Dodanie przycisków do siatki
        grid.add(generateCharButton, 0, 0);
        grid.add(generateCardsButton, 1, 0);
        grid.add(fullProcessButton, 0, 1);
        grid.add(exitButton, 1, 1);

        return grid;
    }

    private Button createStyledButton(String text, String style, String hoverStyle, DropShadow shadow) {
        Button button = new Button(text);
        button.setStyle(style);
        button.setEffect(shadow);

        // Efekty najechania myszą
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(style));

        return button;
    }

    private void log(String message) {
        logArea.appendText(message + "\n");
        // Przewiń do dołu
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}