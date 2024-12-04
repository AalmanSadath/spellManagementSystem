# spellManagementSystem

This repository contains the implementation of a **Spell Management System**, which allows users to manage and execute operations on a collection of spells. The system supports features like learning spells, defining prerequisites, enumerating learned spells, and resolving cyclic dependencies in spell prerequisites.

---

## Files

### 1. **BuildSpellbook.java**
- This file contains the main logic for managing spells and their relationships.
- Key Features:
  - **Add Spell Prerequisites (`PREREQ`)**: Define prerequisite spells for a given spell.
  - **Learn Spells (`LEARN`)**: Learn a specific spell and its dependencies.
  - **Forget Spells (`FORGET`)**: Forget a learned spell while considering dependency relationships.
  - **Enumerate Learned Spells (`ENUM`)**: List all learned spells.
  - **Cycle Detection**: Detect and handle cycles in spell prerequisites.
  - **Cycle Resolution**: Suggest fixes by removing the largest or smallest cycle in prerequisites.
  - **File Input/Output**: Read specifications from files and compare execution outputs to expected solutions.

### 2. **Spell.java**
- A utility class representing individual spells and their relationships.
- Attributes:
  - `name`: The name of the spell.
  - `reqs`: A list of prerequisite spells.
  - `isReqFor`: A list of spells for which this spell is a prerequisite.
  - `explicitlyLearned`: A flag indicating if the spell was explicitly learned.

---

## How It Works

### 1. **Spell Management**
   - Each spell is represented by an instance of the `Spell` class.
   - Spells can have dependencies (`PREREQ`) that form a graph structure.

### 2. **Operations**
   - Commands like `LEARN`, `FORGET`, `PREREQ`, and `ENUM` are executed using the `BuildSpellbook` class.
   - Supports batch execution of commands through file input.

### 3. **Cycle Detection and Resolution**
   - Detects cyclic dependencies in spell prerequisites.
   - Suggests fixes by removing the largest or smallest cycle.

### 4. **File-Based Execution**
   - Read specifications from a file and execute commands sequentially.
   - Compare execution outputs with expected solutions using `compareExecWSoln`.

---

## Usage

### Command Execution
- Supported commands:
  - `PREREQ <spell> <prerequisite_1> ... <prerequisite_n>`: Defines prerequisites for a spell.
  - `LEARN <spell>`: Learns a spell and its prerequisites.
  - `FORGET <spell>`: Forgets a spell if no other spells depend on it.
  - `ENUM`: Lists all learned spells.
  - `END`: Terminates the execution.

### Input/Output
1. Create a text file with commands (e.g., `commands.txt`).
2. Use the `readSpecsFromFile` method to load commands.
3. Execute commands using methods like `execNSpecs` or `execNSpecswCheck`.
4. Compare output with the solution using `compareExecWSoln`.

---

## Example

### Input (`commands.txt`)
```
PREREQ Fireball MagicEssence
LEARN Fireball
ENUM
END
```

### Output
```
PREREQ Fireball MagicEssence
   Learning MagicEssence
   Learning Fireball
ENUM
   MagicEssence
   Fireball
END
```

---
