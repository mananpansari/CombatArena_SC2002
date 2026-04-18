# CombatArena_SC2002

## Description

CombatArena_SC2002 is a turn-based combat arena game developed in Java for SC2002. The project is designed using object-oriented principles and is structured into modular components, including combatants, actions, status effects, items, a battle engine, and shared interfaces.

In the game, the player selects a character and battles enemies across multiple rounds. Players can perform basic attacks, defend to improve survivability, use special skills with cooldowns, and consume items during combat. The system supports speed-based turn order, round-based effect handling, and different difficulty configurations.

The overall design focuses on separation of concerns, extensibility, and maintainability, allowing new actions, effects, or items to be added with minimal changes to the existing system.

---

## Installation

To set up the project locally:

1. Clone the repository:
   git clone https://github.com/mananpansari/CombatArena_SC2002.git

2. Navigate into the project folder:
   cd CombatArena_SC2002

3. Compile all Java source files:
   javac $(find src -name "*.java" | sort)

4. Ensure Java 17 or newer is installed:
   java -version

---

## Usage

After compiling the project, the system can be run through the integrated game logic in the project or using an IDE such as Eclipse.

Typical usage flow:
- Choose a character (e.g. Warrior or Wizard)
- Select a difficulty level
- Enter turn-based combat with enemies
- Perform actions such as attack, defend, skill usage, or item usage
- Continue until all enemies are defeated or the player loses

---

## Dependencies

- Java Development Kit (JDK) 17 or newer

No external libraries are required. The project uses only standard Java.

---

## Notes

- This project is developed for SC2002 (Object-Oriented Design & Programming).
- The system is structured to demonstrate object-oriented design principles such as abstraction, modularity, and extensibility.
