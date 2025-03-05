package slicznykotuni;
import com.opencsv.CSVReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CardMaker {
    private final Dimension cardSize = new Dimension(912, 1368);
    private final Dimension characterSize = new Dimension(800, 700);
    private final Map<String, String> assetsPath = new HashMap<>();

    public CardMaker() {
        initializeAssetsPath();
    }

    private void initializeAssetsPath() {
        assetsPath.put("backgrounds", "assets/backgrounds");
        assetsPath.put("characters", "assets/characters");
        assetsPath.put("weapons", "assets/weapons");
        assetsPath.put("armors", "assets/armors");
        assetsPath.put("languages", "assets/languages");
        assetsPath.put("ui", "assets/ui_elements");
        assetsPath.put("output", "output");
    }

    public void generateFromFile(String csvPath) {
        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            String[] headers = reader.readNext(); // Read the header row
            String[] row;

            while ((row = reader.readNext()) != null) {
                Map<String, String> data = parseRow(headers, row);
                try {
                    validateData(data);
                    BufferedImage card = createCard(data);
                    saveCard(card, data.get("name"));
                } catch (Exception e) {
                    System.err.println("Error processing row for character: " + data.get("name"));
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to process CSV file: " + csvPath);
            e.printStackTrace();
        }
    }

    private Map<String, String> parseRow(String[] headers, String[] row) {
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            data.put(headers[i], row[i]);
        }
        return data;
    }

    private void validateData(Map<String, String> data) throws Exception {
        String[] requiredFields = {"name", "background", "image", "health", "weapon_1_image", "weapon_1", "armor_1_image", "armor_1", "evade"};
        for (String field : requiredFields) {
            if (!data.containsKey(field) || data.get(field) == null || data.get(field).isEmpty()) {
                throw new Exception("Missing required field: " + field);
            }
        }

        // Validate numeric fields
        try {
            Integer.parseInt(data.get("health"));
            Integer.parseInt(data.get("evade"));
        } catch (NumberFormatException e) {
            throw new Exception("Health and Evade must be numeric.");
        }
    }

    private BufferedImage createCard(Map<String, String> data) throws IOException {
        BufferedImage card;
        try {
            String backgroundPath = assetsPath.get("backgrounds") + "/" + data.get("background") + ".png";
            card = ImageIO.read(new File(backgroundPath));
        } catch (IOException e) {
            throw new IOException("Failed to load background image: " + data.get("background"), e);
        }

        Graphics2D g = card.createGraphics();
        addCharacter(card, data.get("image"));
        addCombatElements(card, data);
        addNamePlate(card, data);
        addLanguages(card, data);
        g.dispose();
        return card;
    }

    private void addCharacter(BufferedImage card, String characterImg) throws IOException {
        try {
            String characterPath = assetsPath.get("characters") + "/" + characterImg + ".png";
            BufferedImage character = ImageIO.read(new File(characterPath));
            Graphics2D g = card.createGraphics();
            int x = (cardSize.width - characterSize.width) / 2;
            int y = 50;
            g.drawImage(character, x, y, characterSize.width, characterSize.height, null);
            g.dispose();
        } catch (IOException e) {
            throw new IOException("Failed to load character image: " + characterImg, e);
        }
    }

    private void addCombatElements(BufferedImage card, Map<String, String> data) {
        try {
            addHealth(card, Integer.parseInt(data.get("health")));
            addEvade(card, Integer.parseInt(data.get("evade")));
        } catch (Exception e) {
            System.err.println("Error adding combat elements: " + e.getMessage());
        }
    }

    private void addHealth(BufferedImage card, int health) throws IOException {
        try {
            String healthIconPath = assetsPath.get("ui") + "/health_icon.png";
            BufferedImage healthIcon = ImageIO.read(new File(healthIconPath));
            Graphics2D g = card.createGraphics();
            int x = 732, y = 55;
            g.drawImage(healthIcon, x, y, 150, 150, null);
            g.setFont(new Font("Arial", Font.BOLD, 76));
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(health), x + 75, y + 100);
            g.dispose();
        } catch (IOException e) {
            throw new IOException("Failed to load health icon.", e);
        }
    }

    private void addEvade(BufferedImage card, int evade) throws IOException {
        try {
            String evadeIconPath = assetsPath.get("ui") + "/evade.png";
            BufferedImage evadeIcon = ImageIO.read(new File(evadeIconPath));
            Graphics2D g = card.createGraphics();
            int x = 50, y = 55;
            g.drawImage(evadeIcon, x, y, 150, 150, null);
            g.setFont(new Font("Arial", Font.BOLD, 76));
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(evade), x + 75, y + 100);
            g.dispose();
        } catch (IOException e) {
            throw new IOException("Failed to load evade icon.", e);
        }
    }

    private void addNamePlate(BufferedImage card, Map<String, String> data) throws IOException {
        try {
            String namePlatePath = assetsPath.get("ui") + "/name_plate.png";
            BufferedImage namePlate = ImageIO.read(new File(namePlatePath));
            Graphics2D g = card.createGraphics();
            int x = (cardSize.width - 600) / 2;
            int y = 1050;
            g.drawImage(namePlate, x, y, 600, 100, null);

            String name = data.get("name");
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(name);
            g.drawString(name, (cardSize.width - textWidth) / 2, y + 60);
            g.dispose();
        } catch (IOException e) {
            throw new IOException("Failed to load name plate.", e);
        }
    }

    private void addLanguages(BufferedImage card, Map<String, String> data) {
        // Simplified logic similar to the Python script for adding language icons.
    }

    private void saveCard(BufferedImage card, String name) {
        try {
            File outputDir = new File(assetsPath.get("output"));
            if (!outputDir.exists()) outputDir.mkdirs();
            File outputFile = new File(outputDir, name + ".png");
            ImageIO.write(card, "png", outputFile);
            System.out.println("Saved card: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save card: " + name);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RPGCardGenerator generator = new RPGCardGenerator();
        generator.generateFromFile("characters.csv");
    }
}