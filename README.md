# Character Card Generator 🎮✨

## Overview

Character Card Generator is a sophisticated Java application designed to create beautiful, RPG-style character cards from character images and their associated metadata. The program combines character artwork with dynamic stats, equipment, and language proficiencies to generate polished, game-ready cards.
![Alt text](https://github.com/SlicznyKotuni/CharacterCardGeneratorJava/blob/master/output/Zulthor%20Grimroot_3.png)
## Features

### 🎨 Visual Elements
- Dynamic character rendering with preserved aspect ratio
- Smooth edge transparency gradients
- Professional background integration
- Equipment icons with damage values
- Armor type indicators
- Health and evasion statistics
- Language proficiency indicators

### 🎲 Character Generation
- Automatic stat calculation based on character traits
- Dynamic weapon and armor assignment
- Intelligent background selection based on character tags
- Automatic language proficiency distribution
- Gender-appropriate name generation

### 🛠 Technical Features
- CSV-based character data management
- Batch processing capabilities
- High-quality image processing
- Configurable UI elements
- Error handling and logging

## Project Structure
CharacterCardGenerator/
├── assets/
│ ├── armors/
│ ├── backgrounds/
│ ├── characters/
│ ├── languages/
│ ├── ui_elements/
│ └── weapons/
├── src/
│ └── main/
│ └── java/
│ └── slicznykotuni/
│ ├── CardMaker.java
│ └── RPGCardGenerator.java
└── output/



## Requirements

- Java 11 or higher
- OpenCSV library
- Minimum 2GB RAM recommended

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/CharacterCardGeneratorJava.git
Navigate to the project directory:

cd CharacterCardGenerator
Compile the project:

javac -cp "lib/*" src/main/java/slicznykotuni/*.java
Usage
Basic Usage
Place character images in the assets/characters directory
Create corresponding .txt files with character tags
Run the generator:

java -cp "lib/*" slicznykotuni.RPGCardGenerator
Character Tags
Tags influence character statistics and equipment generation:


Background Tags: fantasy, mroczny, forest, tech, magic, secret
Weapon Tags: sword, axe, dagger, staff, spear
Armor Tags: heavy_armor, leather_armor, padded_armor
Physical Tags: muscular, strong, petite, skinny
Output
Generated cards are saved in the output directory as PNG files, named after the characters.

Configuration
Card Layout
Card Size: 912x1368 pixels
Character Area: 832x1216 pixels
Equipment Icons: 120x120 pixels
Stat Ranges
Health: 10-99
Weapon Damage: 1-12
Armor Values: 0-10
Evasion: 0-6

Made with ❤️ by SlicznyKotUni