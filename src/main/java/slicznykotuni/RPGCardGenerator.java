package slicznykotuni;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class RPGCardGenerator {
    private static final String CHARACTERS_DIR = "assets/characters";
    private static final String GIRL_NAMES_FILE = "imiona_girl.txt";
    private static final String BOY_NAMES_FILE = "imiona_boy.txt";
    private static final String OUTPUT_CSV = "characters.csv";

    // Definicje tagów dla teł
    private static final Map<String, List<String>> BACKGROUNDS = new HashMap<>() {{
        put("fantasy", Arrays.asList("knight", "fantasy", "elf", "elven", "kobold"));
        put("mroczny", Arrays.asList("necromancy", "dark", "evil", "demon", "horns"));
        put("forest", Arrays.asList("animal", "beast", "catboy", "catgirl", "furry"));
        put("tech", Arrays.asList("ancient", "technology", "sci-fi", "futuristic"));
        put("magic", Arrays.asList("magic", "wizard", "spells", "monster", "casts"));
        put("secret", new ArrayList<>()); // Domyślne tło dla niepasujących tagów
    }};

    // Definicje tagów dla języków
    private static final Map<String, List<String>> LANGUAGES = new HashMap<>() {{
        put("Rh'lo", Arrays.asList("ancient", "older", "runes", "rune", "magic", "arcane"));
        put("Common", Arrays.asList("knight", "elven", "elf", "magic", "fantasy", "rpg"));
        // ... dodaj pozostałe języki
    }};

    // Tagi dla typów obrażeń (broń)
    private static final Map<String, List<String>> WEAPON_TAGS = new HashMap<>() {{
        put("slash", Arrays.asList("sword", "axe", "dagger", "katana", "twohanded_sword"));
        put("puncture", Arrays.asList("dart", "darts", "kunai", "spear", "polearm"));
        put("impact", Arrays.asList("staff", "mace", "hammer", "fists", "martial_arts"));
    }};

    private static final List<String> SHIELD_TAGS = Arrays.asList(
            "shield", "heavy_shield", "kite_shield", "buckler", "tower_shield"
    );

    private static final Map<String, List<String>> ARMOR_TAGS = new HashMap<>() {{
        put("slash", Arrays.asList("heavy_armor", "plate_armor", "shoulder_armor"));
        put("puncture", Arrays.asList("armor", "leather_armor", "padded_armor"));
        put("impact", Arrays.asList("padded_armor", "gambeson", "heavy_armor"));
    }};

    private static final List<String> LOW_ARMOR_TAGS = Arrays.asList(
            "naked", "clothes", "loincloth", "barefeet", "shirt", "tunic", "robe"
    );

    private static final List<String> HEALTH_POSITIVE_TAGS = Arrays.asList(
            "muscular", "strong", "huge", "large", "tall"
    );

    private static final List<String> HEALTH_NEGATIVE_TAGS = Arrays.asList(
            "petite", "skinny", "small", "little", "fragile"
    );

    // Metody pomocnicze
    private List<String> loadNames(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    private List<String> loadTags(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return Arrays.asList(content.split(","));
    }

    private String determineGender(List<String> tags) {
        if (tags.contains("1girl")) {
            return "girl";
        } else if (tags.contains("1boy")) {
            return "boy";
        }
        return new Random().nextBoolean() ? "girl" : "boy";
    }

    private String getRandomName(String gender, List<String> girlNames, List<String> boyNames) {
        Random random = new Random();
        return gender.equals("girl")
                ? girlNames.get(random.nextInt(girlNames.size()))
                : boyNames.get(random.nextInt(boyNames.size()));
    }
    private String determineBackground(List<String> tags) {
        for (Map.Entry<String, List<String>> entry : BACKGROUNDS.entrySet()) {
            if (entry.getValue().stream().anyMatch(tags::contains)) {
                return entry.getKey();
            }
        }
        return "secret";
    }

    private int calculateHealth(String gender, List<String> tags) {
        Random random = new Random();
        int baseHealth = gender.equals("boy")
                ? random.nextInt(66) + 25  // 25-90 dla chłopców
                : random.nextInt(61) + 15; // 15-75 dla dziewczyn

        // Modyfikatory zdrowia
        for (String tag : tags) {
            if (HEALTH_POSITIVE_TAGS.contains(tag)) {
                baseHealth += 10;
            } else if (HEALTH_NEGATIVE_TAGS.contains(tag)) {
                baseHealth -= 15;
            }
        }

        return Math.max(10, Math.min(baseHealth, 99));
    }

    private List<String> determineDamageTypes(List<String> tags) {
        Set<String> damageTypes = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : WEAPON_TAGS.entrySet()) {
            if (entry.getValue().stream().anyMatch(tags::contains)) {
                damageTypes.add(entry.getKey());
            }
        }
        if (damageTypes.isEmpty()) {
            damageTypes.add("impact");
        }
        return new ArrayList<>(damageTypes);
    }

    private class WeaponInfo {
        String weapon1Image;
        int weapon1Value;
        String weapon2Image;
        int weapon2Value;
        String weapon3Image;
        int weapon3Value;

        WeaponInfo(String w1i, int w1v, String w2i, int w2v, String w3i, int w3v) {
            weapon1Image = w1i;
            weapon1Value = w1v;
            weapon2Image = w2i;
            weapon2Value = w2v;
            weapon3Image = w3i;
            weapon3Value = w3v;
        }
    }

    private WeaponInfo determineWeapons(List<String> tags) {
        Random random = new Random();
        List<String> damageTypes = determineDamageTypes(tags);
        boolean hasShield = tags.stream().anyMatch(SHIELD_TAGS::contains);

        if (hasShield) {
            String weapon1Image = damageTypes.get(0);
            String weapon2Image = "shield";
            int weapon1Value = random.nextInt(11) + 2; // 2-12
            int weapon2Value = random.nextInt(10) + 1; // 1-10
            return new WeaponInfo(weapon1Image, weapon1Value, weapon2Image, weapon2Value,
                    "impact", random.nextInt(6) + 3); // 3-8
        } else {
            if (damageTypes.size() >= 2) {
                String weapon1Image = damageTypes.get(0);
                String weapon2Image = damageTypes.get(1);
                int weapon1Value = random.nextInt(10) + 1; // 1-10
                int weapon2Value = random.nextInt(6) + 3;  // 3-8
                return new WeaponInfo(weapon1Image, weapon1Value, weapon2Image, weapon2Value,
                        "impact", random.nextInt(6) + 3);
            } else {
                String weapon1Image = damageTypes.get(0);
                int weapon1Value = random.nextInt(5) + 8; // 8-12
                return new WeaponInfo(weapon1Image, weapon1Value, weapon1Image, weapon1Value,
                        "impact", random.nextInt(6) + 3);
            }
        }
    }

    private class ArmorInfo {
        int slashArmor;
        int punctureArmor;
        int impactArmor;

        ArmorInfo(int slash, int puncture, int impact) {
            slashArmor = slash;
            punctureArmor = puncture;
            impactArmor = impact;
        }
    }

    private ArmorInfo determineArmor(List<String> tags) {
        Random random = new Random();
        Map<String, Integer> armorValues = new HashMap<>() {{
            put("slash", random.nextInt(11));     // 0-10
            put("puncture", random.nextInt(11));  // 0-10
            put("impact", random.nextInt(11));    // 0-10
        }};

        // Modyfikatory pancerza
        for (String tag : tags) {
            for (Map.Entry<String, List<String>> entry : ARMOR_TAGS.entrySet()) {
                if (entry.getValue().contains(tag)) {
                    armorValues.merge(entry.getKey(), 2, Integer::sum);
                }
            }
        }

        // Obniżenie odporności dla tagów wskazujących na brak pancerza
        if (tags.stream().anyMatch(LOW_ARMOR_TAGS::contains)) {
            armorValues.replaceAll((k, v) -> (int)(v * 0.7));
        }

        return new ArmorInfo(
                armorValues.get("slash"),
                armorValues.get("puncture"),
                armorValues.get("impact")
        );
    }

    private int calculateEvade(int armorSum) {
        if (armorSum >= 30 && armorSum <= 36) return 0;
        if (armorSum >= 27 && armorSum <= 29) return 1;
        if (armorSum >= 24 && armorSum <= 26) return 2;
        if (armorSum >= 18 && armorSum <= 23) return 3;
        if (armorSum >= 14 && armorSum <= 18) return 4;
        if (armorSum >= 8 && armorSum <= 13) return 5;
        if (armorSum >= 3 && armorSum <= 7) return 6;
        return 0;
    }

    private List<String> determineLanguages(List<String> tags) {
        Set<String> possibleLanguages = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : LANGUAGES.entrySet()) {
            if (entry.getValue().stream().anyMatch(tags::contains)) {
                possibleLanguages.add(entry.getKey());
            }
        }

        if (possibleLanguages.isEmpty()) {
            possibleLanguages.add("Common");
        }

        Random random = new Random();
        int numLanguages = Math.min(random.nextInt(4) + 1, possibleLanguages.size());

        List<String> selectedLanguages = new ArrayList<>(possibleLanguages);
        Collections.shuffle(selectedLanguages);
        selectedLanguages = selectedLanguages.subList(0, numLanguages);

        // Uzupełnienie pustymi wartościami do 4 języków
        while (selectedLanguages.size() < 4) {
            selectedLanguages.add("");
        }

        return selectedLanguages;
    }
    public void generateCharacters() {
        try {
            List<String> girlNames = loadNames(GIRL_NAMES_FILE);
            List<String> boyNames = loadNames(BOY_NAMES_FILE);
            List<List<String>> characters = new ArrayList<>();

            // Nagłówki CSV
            List<String> headers = Arrays.asList(
                    "name", "background", "image", "health",
                    "weapon_1_image", "weapon_1", "weapon_2_image", "weapon_2", "weapon_3_image", "weapon_3",
                    "armor_1_image", "armor_1", "armor_2_image", "armor_2", "armor_3_image", "armor_3",
                    "evade", "language_1", "language_2", "language_3", "language_4"
            );
            characters.add(headers);

            File charactersDir = new File(CHARACTERS_DIR);
            for (File file : charactersDir.listFiles((dir, name) -> name.endsWith(".txt"))) {
                List<String> tags = loadTags(file.getPath());
                String imageName = file.getName().replace(".txt", "");

                String gender = determineGender(tags);
                String name = getRandomName(gender, girlNames, boyNames);
                String background = determineBackground(tags);
                int health = calculateHealth(gender, tags);

                WeaponInfo weapons = determineWeapons(tags);
                ArmorInfo armor = determineArmor(tags);
                int armorSum = armor.slashArmor + armor.punctureArmor + armor.impactArmor;
                int evade = calculateEvade(armorSum);

                List<String> languages = determineLanguages(tags);

                List<String> character = Arrays.asList(
                        name, background, imageName, String.valueOf(health),
                        weapons.weapon1Image, String.valueOf(weapons.weapon1Value),
                        weapons.weapon2Image, String.valueOf(weapons.weapon2Value),
                        weapons.weapon3Image, String.valueOf(weapons.weapon3Value),
                        "slash", String.valueOf(armor.slashArmor),
                        "puncture", String.valueOf(armor.punctureArmor),
                        "impact", String.valueOf(armor.impactArmor),
                        String.valueOf(evade),
                        languages.get(0), languages.get(1), languages.get(2), languages.get(3)
                );

                characters.add(character);
            }

            // Zapisywanie do CSV
            try (PrintWriter writer = new PrintWriter(new File(OUTPUT_CSV))) {
                for (List<String> character : characters) {
                    writer.println(String.join(",", character));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RPGCardGenerator generator = new RPGCardGenerator();
        generator.generateCharacters();
    }
}