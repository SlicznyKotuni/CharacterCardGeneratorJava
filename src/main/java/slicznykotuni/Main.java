package slicznykotuni;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Character Card Generator ===");
            System.out.println("1. Wygeneruj charakterystyki postaci");
            System.out.println("2. Wygeneruj karty postaci");
            System.out.println("3. Wykonaj pełny proces (1 + 2)");
            System.out.println("0. Wyjście");
            System.out.print("\nWybierz opcję: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.println("\nGenerowanie charakterystyk postaci...");
                        RPGCardGenerator generator = new RPGCardGenerator();
                        generator.generateCharacters();
                        System.out.println("Charakterystyki zostały wygenerowane do pliku characters.csv");
                        break;

                    case "2":
                        System.out.println("\nGenerowanie kart postaci...");
                        CardMaker cardMaker = new CardMaker();
                        cardMaker.generateFromFile("characters.csv");
                        System.out.println("Karty postaci zostały wygenerowane do folderu output/");
                        break;

                    case "3":
                        System.out.println("\nRozpoczynam pełny proces generowania...");
                        // Generowanie charakterystyk
                        RPGCardGenerator gen = new RPGCardGenerator();
                        gen.generateCharacters();
                        System.out.println("Charakterystyki zostały wygenerowane.");

                        // Generowanie kart
                        CardMaker maker = new CardMaker();
                        maker.generateFromFile("characters.csv");
                        System.out.println("Karty postaci zostały wygenerowane.");
                        break;

                    case "0":
                        System.out.println("Do widzenia!");
                        return;

                    default:
                        System.out.println("Nieprawidłowa opcja!");
                }
            } catch (Exception e) {
                System.err.println("Wystąpił błąd: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}