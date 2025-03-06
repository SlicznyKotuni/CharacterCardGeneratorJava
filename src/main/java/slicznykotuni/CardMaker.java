package slicznykotuni;

import com.opencsv.CSVReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


public class CardMaker {

    private final Dimension cardSize = new Dimension(912, 1368);
    private final Dimension characterSize = new Dimension(800, 700);
    private final Map<String, String> assetsPath = new HashMap<>();
    private final Map<String, Map<String, Object>> uiConfig = new HashMap<>();

    public CardMaker() {
        initializeAssetsPath();
        initializeUiConfig();
    }

    private void initializeAssetsPath() {
        assetsPath.put("backgrounds", "assets/backgrounds");
        assetsPath.put("characters", "assets/characters");
        assetsPath.put("weapons", "assets/weapons");
        assetsPath.put("armors", "assets/armors");
        assetsPath.put("languages", "assets/languages");
        assetsPath.put("ui", "assets/ui_elements");
        assetsPath.put("fonts", "assets/fonts");
        assetsPath.put("output", "output");
    }

    private void initializeUiConfig() {
        // Weapons configuration
        Map<String, Object> weaponsConfig = new HashMap<>();
        weaponsConfig.put("positions", Arrays.asList(new int[]{50, 300}, new int[]{50, 430}));
        weaponsConfig.put("size", new int[]{120, 120});
        weaponsConfig.put("label_offset", new int[]{40, 40});
        weaponsConfig.put("font_size", 46);
        uiConfig.put("weapons", weaponsConfig);

        // Armors configuration
        Map<String, Object> armorsConfig = new HashMap<>();
        armorsConfig.put("positions", Arrays.asList(new int[]{742, 300}, new int[]{742, 430}, new int[]{742, 560}));
        armorsConfig.put("size", new int[]{120, 120});
        armorsConfig.put("label_offset", new int[]{40, 40});
        armorsConfig.put("font_size", 46);
        uiConfig.put("armors", armorsConfig);

        // Health configuration
        Map<String, Object> healthConfig = new HashMap<>();
        healthConfig.put("position", new int[]{732, 55});
        healthConfig.put("size", new int[]{150, 150});
        healthConfig.put("font_size", 76);
        uiConfig.put("health", healthConfig);

        // Evade configuration
        Map<String, Object> evadeConfig = new HashMap<>();
        evadeConfig.put("position", new int[]{50, 55});
        evadeConfig.put("size", new int[]{150, 150});
        evadeConfig.put("font_size", 76);
        uiConfig.put("evade", evadeConfig);

        // Name plate configuration
        Map<String, Object> namePlateConfig = new HashMap<>();
        namePlateConfig.put("size", new int[]{600, 100});
        namePlateConfig.put("position", new int[]{(912 - 600) / 2, 1050});
        uiConfig.put("name_plate", namePlateConfig);

        // Name configuration
        Map<String, Object> nameConfig = new HashMap<>();
        nameConfig.put("position", new int[]{912 / 2, 1110});
        nameConfig.put("font_size", 50);
        nameConfig.put("max_length", 24);
        uiConfig.put("name", nameConfig);

        // Languages configuration
        Map<String, Object> languagesConfig = new HashMap<>();
        languagesConfig.put("icon_size", new int[]{150, 150});
        languagesConfig.put("max_count", 4);
        languagesConfig.put("margin", 30);
        languagesConfig.put("position_y", 1200);
        uiConfig.put("languages", languagesConfig);
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
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

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

            // Create a new image for the character with the same dimensions
            BufferedImage characterWithAlpha = new BufferedImage(character.getWidth(), character.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = characterWithAlpha.createGraphics();

            // Enable anti-aliasing for smoother edges
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create a gradient mask
            int width = character.getWidth();
            int height = character.getHeight();
            int margin = 100;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int alpha = 255; // Default full opacity

                    if (x < margin || x > width - margin || y < margin || y > height - margin) {
                        // Calculate the alpha value for the gradient
                        int distToEdge = Math.min(Math.min(x, width - x), Math.min(y, height - y));
                        alpha = (int) (255 * (double) distToEdge / margin);
                        alpha = Math.max(0, Math.min(255, alpha)); // Clamp between 0 and 255
                    }

                    // Get the color from the original character image
                    int color = character.getRGB(x, y);

                    // Create a new color with the calculated alpha value
                    int newColor = (alpha << 24) | (color & 0x00FFFFFF);

                    // Set the pixel color in the new image
                    characterWithAlpha.setRGB(x, y, newColor);
                }
            }

            g.drawImage(characterWithAlpha, 0, 0, null);
            g.dispose();

            Graphics2D cardGraphics = card.createGraphics();
            int x = (cardSize.width - characterSize.width) / 2 - 20;
            int y = 50;
            cardGraphics.drawImage(characterWithAlpha, x, y, characterSize.width, characterSize.height, null);
            cardGraphics.dispose();

        } catch (IOException e) {
            throw new IOException("Failed to load character image: " + characterImg, e);
        }
    }


    private void addCombatElements(BufferedImage card, Map<String, String> data) {
        try {
            addWeapons(card, data);
            addArmors(card, data);
            addHealth(card, data);
            addEvade(card, data);
        } catch (Exception e) {
            System.err.println("Error adding combat elements: " + e.getMessage());
        }
    }

    private void addWeapons(BufferedImage card, Map<String, String> data) throws IOException {
        for (int i = 1; i <= 2; i++) {
            String weaponImageKey = "weapon_" + i + "_image";
            String weaponValueKey = "weapon_" + i;
            if (data.containsKey(weaponImageKey) && data.get(weaponImageKey) != null && !data.get(weaponImageKey).isEmpty() &&
                    data.containsKey(weaponValueKey) && data.get(weaponValueKey) != null && !data.get(weaponValueKey).isEmpty()) {
                addWeapon(card, data.get(weaponImageKey), data.get(weaponValueKey), (int[]) ((java.util.List<?>) uiConfig.get("weapons").get("positions")).get(i - 1));
            }
        }
    }

    private void addArmors(BufferedImage card, Map<String, String> data) throws IOException {
        for (int i = 1; i <= 3; i++) {
            String armorImageKey = "armor_" + i + "_image";
            String armorValueKey = "armor_" + i;
            if (data.containsKey(armorImageKey) && data.get(armorImageKey) != null && !data.get(armorImageKey).isEmpty() &&
                    data.containsKey(armorValueKey) && data.get(armorValueKey) != null && !data.get(armorValueKey).isEmpty()) {
                addArmor(card, data.get(armorImageKey), data.get(armorValueKey), (int[]) ((java.util.List<?>) uiConfig.get("armors").get("positions")).get(i - 1));
            }
        }
    }

    private void addWeapon(BufferedImage card, String imgName, String value, int[] position) throws IOException {
        try {
            String weaponPath = assetsPath.get("weapons") + "/" + imgName + ".png";
            BufferedImage weaponImg = ImageIO.read(new File(weaponPath));
            int[] size = (int[]) uiConfig.get("weapons").get("size");
            weaponImg = resizeImage(weaponImg, size[0], size[1]);

            BufferedImage weaponWithBackground = new BufferedImage(weaponImg.getWidth(), weaponImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = weaponWithBackground.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = size[0] / 2;
            int centerY = size[1] / 2;

            Font font = new Font("Arial", Font.BOLD, (int) uiConfig.get("weapons").get("font_size"));
            FontMetrics fm = g.getFontMetrics(font);
            int padding = 8;
            int textWidth = fm.stringWidth(value);
            int textHeight = fm.getHeight();
            int backgroundWidth = textWidth + 2 * padding;
            int backgroundHeight = textHeight + 2 * padding;
            int circleRadius = Math.max(backgroundWidth, backgroundHeight) / 2;

            // Draw semi-transparent background
            g.setColor(new Color(0, 0, 0, 160));
            Ellipse2D.Double circle = new Ellipse2D.Double(centerX - circleRadius, centerY - circleRadius, 2 * circleRadius, 2 * circleRadius);
            g.fill(circle);

            g.drawImage(weaponImg, 0, 0, null);

            // Add glowing text
            drawGlowingText(g, value, centerX, centerY + fm.getAscent() / 2, font, Color.WHITE, Color.RED, 1);

            g.dispose();

            Graphics2D cardGraphics = card.createGraphics();
            cardGraphics.drawImage(weaponWithBackground, position[0], position[1], null);
            cardGraphics.dispose();

        } catch (IOException e) {
            System.err.println("Weapon image not found: " + imgName);
        }
    }

    private void addArmor(BufferedImage card, String imgName, String value, int[] position) throws IOException {
        try {
            String armorPath = assetsPath.get("armors") + "/" + imgName + ".png";
            BufferedImage armorImg = ImageIO.read(new File(armorPath));
            int[] size = (int[]) uiConfig.get("armors").get("size");
            armorImg = resizeImage(armorImg, size[0], size[1]);

            BufferedImage armorWithBackground = new BufferedImage(armorImg.getWidth(), armorImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = armorWithBackground.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = size[0] / 2;
            int centerY = size[1] / 2;

            Font font = new Font("Arial", Font.BOLD, (int) uiConfig.get("armors").get("font_size"));
            FontMetrics fm = g.getFontMetrics(font);
            int padding = 8;
            int textWidth = fm.stringWidth(value);
            int textHeight = fm.getHeight();
            int backgroundWidth = textWidth + 2 * padding;
            int backgroundHeight = textHeight + 2 * padding;
            int circleRadius = Math.max(backgroundWidth, backgroundHeight) / 2;

            // Draw semi-transparent background
            g.setColor(new Color(0, 0, 0, 160));
            Ellipse2D.Double circle = new Ellipse2D.Double(centerX - circleRadius, centerY - circleRadius, 2 * circleRadius, 2 * circleRadius);
            g.fill(circle);

            g.drawImage(armorImg, 0, 0, null);

            // Add glowing text
            drawGlowingText(g, value, centerX, centerY + fm.getAscent() / 2, font, Color.WHITE, Color.RED, 1);

            g.dispose();

            Graphics2D cardGraphics = card.createGraphics();
            cardGraphics.drawImage(armorWithBackground, position[0], position[1], null);
            cardGraphics.dispose();

        } catch (IOException e) {
            System.err.println("Armor image not found: " + imgName);
        }
    }

    private void addHealth(BufferedImage card, Map<String, String> data) throws IOException {
        try {
            String healthIconPath = assetsPath.get("ui") + "/health_icon.png";
            BufferedImage healthIcon = ImageIO.read(new File(healthIconPath));
            int[] size = (int[]) uiConfig.get("health").get("size");
            healthIcon = resizeImage(healthIcon, size[0], size[1]);

            // Create a new image to draw on
            BufferedImage healthWithText = new BufferedImage(healthIcon.getWidth(), healthIcon.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = healthWithText.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculate the center of the icon
            int centerX = size[0] / 2;
            int centerY = size[1] / 2;

            g.drawImage(healthIcon, 0, 0, null);

            // Add glowing text
            Font font = new Font("Arial", Font.BOLD, (int) uiConfig.get("health").get("font_size"));
            drawGlowingText(g, data.get("health"), centerX, centerY + g.getFontMetrics(font).getAscent() / 2, font, Color.WHITE, Color.RED, 1);

            g.dispose();

            Graphics2D cardGraphics = card.createGraphics();
            cardGraphics.drawImage(healthWithText, ((int[]) uiConfig.get("health").get("position"))[0], ((int[]) uiConfig.get("health").get("position"))[1], null);
            cardGraphics.dispose();

        } catch (IOException e) {
            throw new IOException("Failed to load health icon.", e);
        }
    }


    private void addEvade(BufferedImage card, Map<String, String> data) throws IOException {
        try {
            String evadeIconPath = assetsPath.get("ui") + "/evade.png";
            BufferedImage evadeIcon = ImageIO.read(new File(evadeIconPath));
            int[] size = (int[]) uiConfig.get("evade").get("size");
            evadeIcon = resizeImage(evadeIcon, size[0], size[1]);

            // Create a new image to draw on
            BufferedImage evadeWithText = new BufferedImage(evadeIcon.getWidth(), evadeIcon.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = evadeWithText.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculate the center of the icon
            int centerX = size[0] / 2;
            int centerY = size[1] / 2;

            g.drawImage(evadeIcon, 0, 0, null);

            // Add glowing text
            Font font = new Font("Arial", Font.BOLD, (int) uiConfig.get("evade").get("font_size"));
            drawGlowingText(g, data.get("evade"), centerX, centerY + g.getFontMetrics(font).getAscent() / 2, font, Color.WHITE, Color.RED, 1);

            g.dispose();

            Graphics2D cardGraphics = card.createGraphics();
            cardGraphics.drawImage(evadeWithText, ((int[]) uiConfig.get("evade").get("position"))[0], ((int[]) uiConfig.get("evade").get("position"))[1], null);
            cardGraphics.dispose();

        } catch (IOException e) {
            throw new IOException("Failed to load evade icon.", e);
        }
    }

    private void addNamePlate(BufferedImage card, Map<String, String> data) throws IOException {
        try {
            String namePlatePath = assetsPath.get("ui") + "/name_plate.png";
            BufferedImage namePlate = ImageIO.read(new File(namePlatePath));
            int[] size = (int[]) uiConfig.get("name_plate").get("size");
            namePlate = resizeImage(namePlate, size[0], size[1]);

            Graphics2D g = card.createGraphics();
            int[] position = (int[]) uiConfig.get("name_plate").get("position");
            g.drawImage(namePlate, position[0], position[1], null);

            String name = data.get("name");
            Font font = new Font("Arial", Font.BOLD, (int) uiConfig.get("name").get("font_size"));
            FontMetrics fm = g.getFontMetrics(font);
            int textWidth = fm.stringWidth(name);
            int x = cardSize.width / 2;
            int y = position[1] + size[1] / 2 + fm.getAscent() / 2;


            drawGlowingText(g, name, x, y, font, Color.WHITE, Color.RED, 2);

            g.dispose();
        } catch (IOException e) {
            throw new IOException("Failed to load name plate.", e);
        }
    }

    private void addLanguages(BufferedImage card, Map<String, String> data) {
        java.util.List<String> languages = new java.util.ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String languageKey = "language_" + i;
            if (data.containsKey(languageKey) && data.get(languageKey) != null && !data.get(languageKey).isEmpty()) {
                languages.add(data.get(languageKey));
            }
        }

        int numLangs = Math.min(languages.size(), (int) uiConfig.get("languages").get("max_count"));

        if (numLangs == 0) {
            return;
        }

        int[] iconSize = (int[]) uiConfig.get("languages").get("icon_size");
        int margin = (int) uiConfig.get("languages").get("margin");
        int totalWidth = (numLangs * iconSize[0] + (numLangs - 1) * margin);
        int startX = (cardSize.width - totalWidth) / 2;
        int positionY = (int) uiConfig.get("languages").get("position_y");

        try {
            Graphics2D g = card.createGraphics();

            for (int i = 0; i < numLangs; i++) {
                String lang = languages.get(i);
                String langImgPath = assetsPath.get("languages") + "/" + lang + ".png";
                BufferedImage langImg = ImageIO.read(new File(langImgPath));
                langImg = resizeImage(langImg, iconSize[0], iconSize[1]);

                int x = startX + i * (iconSize[0] + margin);
                g.drawImage(langImg, x, positionY, null);
            }

            g.dispose();
        } catch (IOException e) {
            System.err.println("Language image not found");
        }
    }

    private void saveCard(BufferedImage card, String name) {
        try {
            File outputDir = new File(assetsPath.get("output"));
            if (!outputDir.exists()) outputDir.mkdirs();

            // Check if the file already exists
            File outputFile = new File(outputDir, name + ".png");
            if (outputFile.exists()) {
                int index = 1;
                while (true) {
                    outputFile = new File(outputDir, name + "_" + index + ".png");
                    if (!outputFile.exists()) {
                        break;
                    }
                    index++;
                }
            }

            ImageIO.write(card, "png", outputFile);
            System.out.println("Saved card: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save card: " + name);
            e.printStackTrace();
        }
    }

    private BufferedImage resizeImage(BufferedImage img, int width, int height) {
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImg.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
        return resizedImg;
    }

    private void drawGlowingText(Graphics2D g, String text, int x, int y, Font font, Color textColor, Color glowColor, int glowRadius) {
        g.setFont(font);
        g.setColor(glowColor);

        for (int i = -glowRadius; i <= glowRadius; i++) {
            for (int j = -glowRadius; j <= glowRadius; j++) {
                if (i * i + j * j <= glowRadius * glowRadius) {
                    g.drawString(text, x + i - g.getFontMetrics().stringWidth(text) / 2, y + j);
                }
            }
        }

        g.setColor(textColor);
        g.drawString(text, x - g.getFontMetrics().stringWidth(text) / 2, y);
    }


    public static void main(String[] args) {
        CardMaker generator = new CardMaker();
        generator.generateFromFile("characters.csv");
    }
}