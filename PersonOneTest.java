assertEqual("Wizard ATK = 70 after 2 kills (50+10+10)", 70, wiz.getAttack());
        assertEqual("Arcane blast bonus ATK = 20", 20, wiz.getArcaneBlastBonusAtk());
        assertTrue("WolfA dead", !wolfA.isAlive());
        assertTrue("WolfB dead", !wolfB.isAlive());
    }

    // ── Goblin / Wolf Tests ───────────────────────────────────────────────────

    static void testGoblinStats() {
        Goblin g = new Goblin("C");
        assertEqual("Goblin HP = 55", 55, g.getHp());
        assertEqual("Goblin ATK = 35", 35, g.getAttack());
        assertEqual("Goblin DEF = 15", 15, g.getDefense());
        assertEqual("Goblin SPD = 25", 25, g.getSpeed());
        assertEqual("Goblin name = 'Goblin C'", "Goblin C", g.getName());
        assertEqual("Goblin type = 'Goblin'", "Goblin", g.getEnemyType());
    }

    static void testWolfStats() {
        Wolf w = new Wolf("A");
        assertEqual("Wolf HP = 40", 40, w.getHp());
        assertEqual("Wolf ATK = 45", 45, w.getAttack());
        assertEqual("Wolf DEF = 5", 5, w.getDefense());
        assertEqual("Wolf SPD = 35", 35, w.getSpeed());
        assertEqual("Wolf name = 'Wolf A'", "Wolf A", w.getName());
    }

    // ── Skill Cooldown Tests ──────────────────────────────────────────────────

    static void testSkillCooldown() {
        Warrior w = new Warrior(new ArrayList<>());
        assertTrue("Skill ready at start", w.isSkillReady());
        assertEqual("Cooldown = 0 at start", 0, w.getSkillCooldown());

        w.startSkillCooldown();
        assertEqual("Cooldown = 3 after use", 3, w.getSkillCooldown());
        assertTrue("Skill NOT ready on cooldown", !w.isSkillReady());

        w.decrementCooldown();
        assertEqual("Cooldown = 2 after 1 decrement", 2, w.getSkillCooldown());
        w.decrementCooldown();
        assertEqual("Cooldown = 1 after 2 decrements", 1, w.getSkillCooldown());
        w.decrementCooldown();
        assertEqual("Cooldown = 0 after 3 decrements", 0, w.getSkillCooldown());
        assertTrue("Skill ready again", w.isSkillReady());

        // Decrement below 0 should not go negative
        w.decrementCooldown();
        assertEqual("Cooldown stays at 0, not negative", 0, w.getSkillCooldown());
    }

    static void testPowerStoneCooldownUnchanged() {
        Warrior w = new Warrior(new ArrayList<>());
        w.startSkillCooldown(); // cooldown = 3
        w.decrementCooldown();  // cooldown = 2

        // PowerStone calls executeSkillEffect() directly (no cooldown change)
        Goblin g = new Goblin("A");
        List<ICombatant> targets = new ArrayList<>();
        targets.add(g);
        w.executeSkillEffect(targets); // does NOT call startSkillCooldown

        assertEqual("Cooldown unchanged at 2 after PowerStone use", 2, w.getSkillCooldown());
    }

    // ── Inventory Tests ───────────────────────────────────────────────────────

    static void testInventoryUsage() {
        List<IItem> items = new ArrayList<>();
        items.add(new DummyItem("Potion"));
        items.add(new DummyItem("Smoke Bomb"));

        Warrior w = new Warrior(items);
        assertTrue("Has items at start", w.hasItemsLeft());
        assertEqual("Inventory size = 2", 2, w.getInventory().size());

        w.useItem(0, new ArrayList<>());
        assertEqual("Inventory size = 1 after use", 1, w.getInventory().size());

        w.useItem(0, new ArrayList<>());
        assertEqual("Inventory size = 0 after second use", 0, w.getInventory().size());
        assertTrue("No items left", !w.hasItemsLeft());
    }

    // ── Polymorphism Test ─────────────────────────────────────────────────────

    static void testPolymorphism() {
        // BattleEngine only sees ICombatant — verify everything works through the interface
        List<ICombatant> combatants = new ArrayList<>();
        combatants.add(new Warrior(new ArrayList<>()));
        combatants.add(new Wizard(new ArrayList<>()));
        combatants.add(new Goblin("A"));
        combatants.add(new Wolf("B"));