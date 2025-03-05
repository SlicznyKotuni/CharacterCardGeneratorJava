package slicznykotuni;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RPGCardGenerator {
    private final Dimension cardSize = new Dimension(912, 1368);
    private final Dimension characterSize = new Dimension(800, 700);
    private final Map<String, Object> uiConfig;
    private final Map<String, String> assetsPath;
    private final Logger logger;
    private Font font;

    public RPGCardGenerator() {
        // Inicjalizacja konfiguracji UI
        uiConfig = initUIConfig();

        // Inicjalizacja ścieżek do zasobów
        assetsPath = initAssetsPath();

        // Konfiguracja loggera
        logger = setupLogging();

        // Wczytanie czcionki
        font = loadFont();
    }

    private Map<String, Object> initUIConfig() {
        Map<String, Object> config = new HashMap<>();

        // Konfiguracja broni
        Map<String, Object> weaponsConfig = new HashMap<>();
        weaponsConfig.put("positions", new Point[]{new Point(50, 300), new Point(50, 430)});
        weaponsConfig.put("size", new Dimension(120, 120));
        weaponsConfig.put("label_offset", new Point(40, 40));
        weaponsConfig.put("font_size", 46);
        config.put("weapons", weaponsConfig);

        // Konfiguracja pancerza
        Map<String, Object> armorsConfig = new HashMap<>();
        armorsConfig.put("positions", new Point[]{new Point(742, 300), new Point(742, 430), new Point(742, 560)});
        armorsConfig.put("size", new Dimension(120, 120));
        armorsConfig.put("label_offset", new Point(40, 40));
        armorsConfig.put("font_size", 46);
        config.put("armors", armorsConfig);

        // Konfiguracja zdrowia
        Map<String, Object> healthConfig = new HashMap<>();
        healthConfig.put("position", new Point(732, 55));
        healthConfig.put("size", new Dimension(150, 150));
        healthConfig.put("font_size", 76);
        config.put("health", healthConfig);

        // Konfiguracja uniku
        Map<String, Object> evadeConfig = new HashMap<>();
        evadeConfig.put("position", new Point(50, 55));
        evadeConfig.put("size", new Dimension(150, 150));
        evadeConfig.put("font_size", 76);
        config.put("evade", evadeConfig);

        // Konfiguracja tabliczki z nazwą
        Map<String, Object> namePlateConfig = new HashMap<>();
        namePlateConfig.put("size", new Dimension(600, 100));
        namePlateConfig.put("position", new Point((912 - 600) / 2, 1050));
        config.put("name_plate", namePlateConfig);

        // Konfiguracja nazwy
        Map<String, Object> nameConfig = new HashMap<>();
        nameConfig.put("position", new Point(912 / 2, 1110));
        nameConfig.put("font_size", 50);
        nameConfig.put("max_length", 24);
        config.put("name", nameConfig);

        // Konfiguracja języków
        Map<String, Object> languagesConfig = new HashMap<>();
        languagesConfig.put("icon_size", new Dimension(150, 150));
        languagesConfig.put("max_count", 4);
        languagesConfig.put("margin", 30);
        languagesConfig.put("position_y", 1200);
        config.put("languages", languagesConfig);

        return config;
    }

    private Map<String, String> initAssetsPath() {
        Map<String, String> paths = new HashMap<>();
        paths.put("backgrounds", "assets/backgrounds");
        paths.put("characters", "assets/characters");
        paths.put("weapons", "assets/weapons");
        paths.put("armors", "assets/armors");
        paths.put("languages", "assets/languages");
        paths.put("ui", "assets/ui_elements");
        paths.put("fonts", "assets/fonts");
        paths.put("output", "output");
        return paths;
    }

    private Logger setupLogging() {
        Logger logger = Logger.getLogger("RPGCardGenerator");
        try {
            Files.createDirectories(Paths.get("logs"));
            FileHandler fileHandler = new FileHandler("logs/card_generator.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Failed to setup logging: " + e.getMessage());
        }
        return logger;
    }

    private Font loadFont() {
        try {
            File fontFile = new File(assetsPath.get("fonts") + "/PressJobs.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return font.deriveFont(24f);
        } catch (FontFormatException | IOException e) {
            logger.warning("Failed to load font: " + e.getMessage());
            return new Font("Arial", Font.PLAIN, 24);
        }
    }

    public void generateFromFile(String csvPath) {
        try (FileReader reader = new FileReader(csvPath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                try {
                    Map<String, String> data = record.toMap();
                    validateData(data);
                    BufferedImage card = createCard(data);
                    saveCard(card, data.get("name"));
                } catch (Exception e) {
                    logger.warning("Failed to generate card for record: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading CSV file: " + e.getMessage());
        }
    }

    private BufferedImage createCard(Map<String, String> data) throws IOException {
        // Wczytanie tła
        File backgroundFile = new File(assetsPath.get("backgrounds") + "/" + data.get("background") + ".png");
        BufferedImage card = ImageIO.read(backgroundFile);

        // Dodanie postaci
        card = addCharacter(card, data.get("image"));

        // Dodanie elementów bojowych
        card = addCombatElements(card, data);

        // Dodanie tabliczki z nazwą
        card = addNamePlate(card, data);

        // Dodanie języków
        card = addLanguages(card, data);

        return card;
    }

    private BufferedImage addCharacter(BufferedImage card, String characterImg) {
        try {
            File characterFile = new File(assetsPath.get("characters") + "/" + characterImg + ".png");
            BufferedImage character = ImageIO.read(characterFile);

            // Obliczenie pozycji postaci
            int x = (cardSize.width - characterSize.width) / 2 - 20;
            int y = 50;

            // Utworzenie nowego obrazu z przezroczystością
            BufferedImage result = new BufferedImage(card.getWidth(), card.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = result.createGraphics();

            // Rysowanie tła
            g.drawImage(card, 0, 0, null);

            // Rysowanie postaci
            g.drawImage(character, x, y, characterSize.width, characterSize.height, null);

            g.dispose();
            return result;
        } catch (IOException e) {
            logger.warning("Failed to add character image: " + e.getMessage());
            return card;
        }
    }

    private BufferedImage addCombatElements(BufferedImage card, Map<String, String> data) {
        card = addWeapons(card, data);
        card = addArmors(card, data);
        card = addHealth(card, data);
        card = addEvade(card, data);
        return card;
    }

    // Implementacja pozostałych metod...

    private void validateData(Map<String, String> data) throws IllegalArgumentException {
        String[] requiredFields = {
                "name", "background", "image", "health",
                "weapon_1_image", "weapon_1",
                "armor_1_image", "armor_1",
                "evade"
        };

        for (String field : requiredFields) {
            if (!data.containsKey(field) || data.get(field) == null || data.get(field).isEmpty()) {
                throw new IllegalArgumentException("Missing required field: " + field);
            }
        }

        try {
            Integer.parseInt(data.get("health"));
            Integer.parseInt(data.get("evade"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Health and evade must be numbers");
        }

        // Walidacja wartości broni i pancerzy
        for (int i = 1; i <= 2; i++) {
            String weaponKey = "weapon_" + i;
            if (data.containsKey(weaponKey) && !data.get(weaponKey).isEmpty()) {
                try {
                    Integer.parseInt(data.get(weaponKey));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Weapon " + i + " value must be a number");
                }
            }
        }

        for (int i = 1; i <= 3; i++) {
            String armorKey = "armor_" + i;
            if (data.containsKey(armorKey) && !data.get(armorKey).isEmpty()) {
                try {
                    Integer.parseInt(data.get(armorKey));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Armor " + i + " value must be a number");
                }
            }
        }
    }

    private void saveCard(BufferedImage card, String name) {
        try {
            Files.createDirectories(Paths.get(assetsPath.get("output")));
            File outputFile = new File(assetsPath.get("output") + "/" + name + ".png");

            // Sprawdzenie, czy plik już istnieje
            if (outputFile.exists()) {
                int index = 1;
                while (true) {
                    File newOutputFile = new File(assetsPath.get("output") + "/" + name + "_" + index + ".png");
                    if (!newOutputFile.exists()) {
                        outputFile = newOutputFile;
                        break;
                    }
                    index++;
                }
            }

            ImageIO.write(card, "png", outputFile);
            logger.info("Saved card: " + outputFile.getPath());
        } catch (IOException e) {
            logger.warning("Failed to save card: " + e.getMessage());
        }
    }

    // Metoda główna do testowania
    public static void main(String[] args) {
        RPGCardGenerator generator = new RPGCardGenerator();
        generator.generateFromFile("characters.csv");
    }
}