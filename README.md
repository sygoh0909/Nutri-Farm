# Nutri-Farm 🌱

[![Scala 3](https://img.shields.io/badge/Scala-3.x-DE3423)](https://scala-lang.org)
[![ScalaFX](https://img.shields.io/badge/ScalaFX-21.0.0-blue)](https://scalafx.org)
[![Java](https://img.shields.io/badge/Java-21-007396)](https://openjdk.org)

## Description

Nutri-Farm is a GUI-based farming simulation game built with modern JVM technologies:

- **Scala 3** - For functional and expressive game logic
- **ScalaFX 21** - For reactive UI rendering with JavaFX
- **Java 21** - For performance and interoperability
- **SBT** - For streamlined dependency management

## Key Features

### 🌟 Authentication System
- Secure login/registration
- Password hashing with jBCrypt

### 🌱 Gardening Simulation
- Plant and harvest various crops
- Growth cycle management

### 📦 Inventory System
- View, filter, and search items
- Categorized storage

### 🧭 Navigation
- Intuitive menu system
- Scene transitions

## Prerequisites

- **Java 21 JDK** ([Download](https://adoptium.net/))
- **sbt** ([Installation Guide](https://www.scala-sbt.org/1.x/docs/Setup.html))
- (Optional) **IDE** with Scala support:
  - IntelliJ IDEA with Scala plugin
  - VS Code with Metals extension

## Getting Started

### Running the Application

#### From Command Line:
```bash
sbt run
```

#### From IDE Project Structure:
1. Navigate to: src/main/scala/program/MainProgram.scala
2. Click the run button (▶️)

## 📁 Project Structure 

```text
nutri-farm/
├── .github/                  # GitHub configuration files
├── .gitignore                # Git exclusion rules
├── build.sbt                 # Main build configuration
├── src/
│   ├── main/
│   │   ├── resources/        # Application assets
│   │   │   ├── garden.jpg    # Garden background
│   │   │   ├── home.jpg      # Home screen background
│   │   │   └── landing.jpg   # Landing page background
│   │   ├── scala/
│   │   │   ├── components/   # Reusable UI components
│   │   │   │   └── MenuButton.scala
│   │   │   ├── controller/   # Application controllers
│   │   │   │   ├── GardenController.scala
│   │   │   │   └── LandingController.scala
│   │   │   ├── css/          # Stylesheets
│   │   │   │   ├── garden.css
│   │   │   │   ├── global.css
│   │   │   │   ├── home.css
│   │   │   │   └── inventory.css
│   │   │   ├── db/           # Database layer
│   │   │   │   ├── DAO.scala
│   │   │   │   ├── DBConfig.scala
│   │   │   │   ├── FoodTable.scala
│   │   │   │   └── PlayerTable.scala
│   │   │   ├── gui/          # View components
│   │   │   │   ├── Garden.scala
│   │   │   │   ├── Home.scala
│   │   │   │   ├── Inventory.scala
│   │   │   │   └── Landing.scala
│   │   │   ├── logging/      # Logging utilities
│   │   │   │   └── GameLogger.scala
│   │   │   ├── model/        # Data models
│   │   │   │   ├── Crop.scala
│   │   │   │   ├── CropRegistry.scala
│   │   │   │   ├── FoodItem.scala
│   │   │   │   └── Player.scala
│   │   │   ├── program/      # Main entry point
│   │   │   │   └── MainProgram.scala
│   │   │   └── utils/        # Utility functions
│   │   │       └── FilterUtils.scala
│   │   └── logging/          # Application logs
│   │       ├── gardencontroller5-2025-08-08.log
│   │       └── gardencontroller5-2025-08-09.log
│   └── test/                 # Unit tests
├── target/                   # Compiled classes and build artifacts
├── project/                  # SBT configuration files
│   ├── target/               # SBT build outputs
│   └── *.sbt                 # Additional build configs
├── idea/                     # IntelliJ IDEA configuration (gitignored)
└── External Libraries/       # IDE-managed dependencies (auto-generated)
```

## Technical References

### Core Technologies

| Technology | Documentation |
|------------|--------------|
| [**Scala 3**](https://scala-lang.org) | [Official Documentation](https://docs.scala-lang.org/scala3/) |
| [**ScalaFX**](https://scalafx.org) | [API Documentation (v21)](https://scalafx.org/api/21.0.0-R32/) |
| [**Java 21**](https://openjdk.org/projects/jdk/21/) | [JDK API](https://docs.oracle.com/en/java/javase/21/docs/api/) |
| [**jBCrypt**](https://github.com/jeremyh/jBCrypt) | [GitHub Repository](https://github.com/jeremyh/jBCrypt) |
| [**Slick**](https://scala-slick.org) | [Database Library Docs](https://scala-slick.org/doc/3.5.0-M1/) |

### Additional References

#### Scala Concepts
- [Futures and Promises](https://docs.scala-lang.org/overviews/core/futures.html)
- [Generic Classes](https://docs.scala-lang.org/tour/generic-classes.html)

#### Java/ScalaFX APIs
- [ScalaFX Javadoc (v24)](https://javadoc.io/doc/org.scalafx/scalafx_3/latest/index.html)
- [ScalaFX Legacy API (v8)](https://scalafx.org/api/8.0/index.html#package)
- [Java SE 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/index.html) (Compatibility Reference)

