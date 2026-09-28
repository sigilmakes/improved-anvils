package com.davidjmacdonald.improved_anvils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImprovedAnvils implements ModInitializer {
    public static final String MOD_ID = "improved_anvils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final GameRule<Boolean> REPAIR_NETHERITE_WITH_DIAMONDS =
        GameRuleBuilder.forBoolean(true).buildAndRegister(
            Identifier.fromNamespaceAndPath(MOD_ID, "repair_netherite_with_diamonds"));

    public static int getTotalPlayerXP(Player player) {
        return AnvilMath.totalExperience(player.experienceLevel, player.experienceProgress,
            player.getXpNeededForNextLevel());
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Starting Improved Anvils");
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            registerLegacyRuleAlias(dispatcher);
        });
    }

    // Share vanilla command executors so permission checks, feedback, and persistence stay vanilla.
    static void registerLegacyRuleAlias(com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher) {
        var root = dispatcher.getRoot().getChild("gamerule");
        var rule = root.getChild(REPAIR_NETHERITE_WITH_DIAMONDS.getIdentifier().toString());
        var alias = net.minecraft.commands.Commands.literal("repairNetheriteWithDiamonds")
            .executes(rule.getCommand());
        for (var child : rule.getChildren()) {
            alias.then(child);
        }
        root.addChild(alias.build());
    }
}
