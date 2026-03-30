# CombatArena_SC2002

A turn-based combat arena project for SC2002, implemented in Java with an object-oriented design split across combatants, actions, status effects, items, and the battle engine.

## Current Repository Status

This repository currently contains the core battle domain and engine code:

- `src/entities`: players, enemies, and shared combatant behavior
- `src/actions`: player actions such as basic attack, defend, item use, and special skill use
- `src/effects`: status effects used across turns
- `src/items`: single-use battle items
- `src/engine`: battle loop, turn order, and level configuration
- `src/interfaces`: shared abstractions used by the system

The `Person 3` scope is implemented and regression-tested:

- `StunEffect`
- `DefendEffect`
- `SmokeBombEffect`
- `Potion`
- `PowerStone`
- `SmokeBomb`

At the moment, the repository does not yet include a full CLI `main` entry point for launching the game interactively. The battle engine and regression tests do compile and run.

## Requirements

- OpenJDK 17 or newer

Verified locally with:

```bash
java -version
```

```text
openjdk version "17.0.18"
```

## Compile The Project

From the repository root:

```bash
javac $(find src -name '*.java' | sort)
```

This compiles all source files under `src/`.

## Run Person 3 Regression Tests

A repeatable regression test suite for the `Person 3` responsibilities is included in:

- `tests/Person3RegressionTest.java`
- `scripts/test_person3.sh`

Run it from the repository root:

```bash
./scripts/test_person3.sh
```

Expected result:

```text
PASS: Shield Bash stuns an enemy that has not acted yet
PASS: Shield Bash stuns an enemy that already acted this round
PASS: Defend lasts for current and next round only
PASS: Smoke Bomb blocks damage and inventory empties after item use
PASS: Potion heals but never exceeds max HP
PASS: Power Stone triggers Warrior skill without changing cooldown
PASS: Power Stone triggers Wizard skill and preserves Arcane Blast scaling

All Person 3 regression tests passed.
```

## What The Tests Cover

The current regression suite checks the following:

- stun timing for enemies that have and have not already acted in the round
- defend duration across the current and next round
- smoke bomb protection duration
- potion healing cap at max HP
- power stone triggering special skills without changing cooldown
- wizard Arcane Blast attack scaling after kills
- item availability after both inventory items are consumed

## Project Files

- `docx/`: assignment brief and team responsibility notes
- `scripts/`: helper scripts for local verification
- `tests/`: regression tests

## Notes

- There is no Maven or Gradle build file in this repository yet, so compilation and test execution are done with `javac` and shell scripts.
- `.class` output and temporary test artifacts under `out/` are ignored by `.gitignore`.
