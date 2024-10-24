package com.davidjmacdonald.improved_anvils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImprovedAnvils implements ModInitializer {
    public static final String MOD_ID = "assets/improved_anvils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final GameRules.Key<GameRules.BooleanRule> REPAIR_NETHERITE_WITH_DIAMONDS =
            GameRuleRegistry.register(
                    "repairNetheriteWithDiamonds",
                    GameRules.Category.MISC,
                    GameRuleFactory.createBooleanRule(true)
            );

    public static int getTotalPlayerXP(PlayerEntity player) {
        var level = player.experienceLevel;
        var points = player.getNextLevelExperience() * player.experienceProgress;

        var square = level * level;
        if (level <= 16) {
            return (int) (points + square + 6 * level);
        } else if (level <= 31) {
            return (int) (points + (2.5 * square - 40.5 * level + 360.0));
        } else {
            return (int) (points + (4.5 * square - 162.5 * level + 2220.0));
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Starting Improved Anvils");
    }
}
