<div align="center">
  <img width="100%" src="https://capsule-render.vercel.app/api?type=blur&height=280&color=0:d8dee9,100:2e3440&text=Rush%20Hour%20Solver%20%E2%9C%A8&fontColor=81a1c1&fontSize=50&animation=twinkling&" />
</div>

<p align="center">
  <img src="https://img.shields.io/badge/Status-Completed-green" />
  <img src="https://img.shields.io/badge/Recent_Build-Release-brightgreen" />
  <img src="https://img.shields.io/badge/Version-1.0.0-brightgreen" />
  <img src="https://img.shields.io/badge/License-MIT-yellowgreen" />
  <img src="https://img.shields.io/badge/Built_With-Java-blue" />
</p>

<h1 align="center">
  <img src="https://readme-typing-svg.herokuapp.com?font=Fira+Code&pause=500&color=81a1c1&center=true&vCenter=true&width=600&lines=Muhammad+Kinan+Arkansyaddad;Rafael+Marchel+Darma+Wijaya" alt="Typing SVG" />
</h1>

---

## 📦 Table of Contents

- [✨ Overview](#-overview)
- [⚙️ Features](#️-features)
- [📥 Installation](#-installation)
- [🚀 Usage](#-usage)
- [📂 Project Structure](#-project-structure)
- [👤 Author](#-author)

---

## ✨ Overview
**Rush Hour Solver** is an application that solves Rush Hour puzzles using various pathfinding algorithms. The Rush Hour puzzle involves sliding cars on a grid to help the primary car escape through an exit, with the constraint that cars can only move along their orientation axis.

This application implements multiple search algorithms and heuristic functions to find optimal solutions, and provides an elegant visualization of the solution steps.

---

## ⚙️ Features

- Multiple Search Algorithms: Choose between UCS, Greedy Best-First Search, A*, or IDA* algorithms
- Multiple Heuristic Functions: Select from Distance, Blocker Count, or Combined heuristics
- Performance Statistics: View solution time, nodes explored, and step count
- Interactive Solution Playback: Step through the solution or watch it play automatically
- Adjustable Animation Speed: Control how fast the solution plays back
- User-Friendly Interface: Modern, intuitive GUI built with JavaFX
- Puzzle File Loading: Load and solve puzzles from text files

---

## 📥 Installation

### 🔧 Prerequisites

- Java Development Kit (JDK) 21 or higher

### 📦 Quick Install

```bash
# Clone the repository
git clone https://github.com/kin-ark/Tucil3_13523146_13523152.git
cd Tucil3_13523146_13523152

# Run with Gradle
./gradlew run
```

```bash
# Download the jar file
cd downloaded_jar_folder

# Run the jar file
java -jar RushHourSolver.jar
```

---

## 🚀 Usage

### Using the Interface
1. Select an Algorithm:
  - UCS: Uniform Cost Search (guarantees optimal solution)
  - Greedy Best First: Faster but may not be optimal
  - A*: Balance between speed and optimality

2. Select a Heuristic (for Greedy and A*):
  - Distance: Estimates based on primary car's distance to exit
  - Blocker Count: Considers the number of blocking vehicles
  - Combined: Uses both distance and blocker count

3. Load a Puzzle File:
Click "Select Puzzle File" and choose a valid puzzle file
Puzzle files should follow the format described below
```
[rows] [columns]
[number of vehicles]
[puzzle grid]
[exit position marker 'K']
```
Example
```
6 6
11
AAB..F
..BCDF
GPPCDFK
GH.III
GHJ...
LLJMM.
```

- P represents the primary vehicle (must be aligned with exit)
- K marks the exit position (must be on the border)
- . represents empty spaces
- Other letters represent different vehicles

4. Visualize the Solution:
Use the playback controls to step through the solution
Adjust the speed slider to control animation speed
View statistics about the solution and search process

---

## 📂 Project Structure

```bash
Tucil3_13523146_13523152/
├── app/                    # Main application module
│   ├── src/                # Source code
│   │   ├── main/          
│   │   │   ├── java/rush_hour/
│   │   │   │   ├── App.java               # Entry point
│   │   │   │   ├── gui/                   # UI components
│   │   │   │   │   └── GameSolverGUI.java # Main GUI
│   │   │   │   ├── io/                    # Input/Output
│   │   │   │   │   └── InputReader.java   # Puzzle file reader
│   │   │   │   ├── model/                 # Models
│   │   │   │   │   ├── GameBoard.java     # Game board state
│   │   │   │   │   ├── GameEnums.java     # Enumerations
│   │   │   │   │   ├── GamePiece.java     # Vehicle representation
│   │   │   │   │   └── GameState.java     # Game state
│   │   │   │   └── solver/                # Solving algorithms
│   │   │   │       ├── GameSolver.java    # Main solver
│   │   │   │       ├── algorithm/         # Search algorithms
│   │   │   │       ├── comparator/        # State comparators
│   │   │   │       └── heuristic/         # Heuristic functions
│   │   ├── test/                          # Test code
│   │   │   └── resources/                 # Test puzzles
├── bin/                    # Compiled classes
│   ├── classes/            # Class files
│   └── jar/                # JAR files
└── doc/                    # Documentation
```

---

## 👤 Author

<table align="center">
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/V-Kleio"><img style="border-radius: 20%" src="https://avatars.githubusercontent.com/u/101655336?v=4" width="100px;" alt="V-Kleio"/><br /><sub><b>Rafael Marchel Darma Wijaya</b></sub></a><br /></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/kin-ark"><img style="border-radius: 20%" src="https://avatars.githubusercontent.com/u/88976627?v=4" width="100px;" alt="kin-ark"/><br /><sub><b>Muhammad Kinan Arkansyaddad</b></sub></a><br /></td>
    </tr>
  </tbody>
</table>

<div align="center" style="color:#6A994E;"> 🌿 Crafted with care | 2025 🌿</div>
