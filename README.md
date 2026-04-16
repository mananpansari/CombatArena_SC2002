# CombatArena_SC2002

## Description

CombatArena_SC2002 is a turn-based combat arena game developed in Java for SC2002. The project is designed using object-oriented principles and is structured into modular components, including combatants, actions, status effects, items, a battle engine, and shared interfaces.

In the game, the player selects a character and battles enemies across multiple rounds. Players can perform basic attacks, defend to increase survivability, use special skills with cooldowns, and consume items to gain advantages during combat. The system supports speed-based turn order, round-based effect handling, and multiple difficulty configurations.

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

