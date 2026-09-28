package com.davidjmacdonald.improved_anvils;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRuleMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnvilRuntimeTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        new ImprovedAnvils().onInitialize();
    }

    @Test
    void legacyRuleDecodesThroughActualMixin() {
        assertTrue(ImprovedAnvils.REPAIR_NETHERITE_WITH_DIAMONDS.defaultValue());
        for (boolean expected : new boolean[]{true, false}) {
            var json = new JsonObject();
            json.addProperty(LegacyGameRuleMigration.LEGACY, Boolean.toString(expected));
            var decoded = GameRuleMap.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
            assertEquals(expected, decoded.get(ImprovedAnvils.REPAIR_NETHERITE_WITH_DIAMONDS));
            var saved = GameRuleMap.CODEC.encodeStart(JsonOps.INSTANCE, decoded).getOrThrow().getAsJsonObject();
            assertFalse(saved.has(LegacyGameRuleMigration.LEGACY));
            assertEquals(expected, saved.get(LegacyGameRuleMigration.CANONICAL).getAsBoolean());
        }
    }

    @Test
    void legacyCommandAliasSharesVanillaExecutors() {
        var dispatcher = new com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack>();
        var root = net.minecraft.commands.Commands.literal("gamerule")
            .requires(source -> false); // The alias must not bypass the existing root's permission check.
        root.then(net.minecraft.commands.Commands.literal(LegacyGameRuleMigration.CANONICAL)
            .executes(context -> 1)
            .then(net.minecraft.commands.Commands.argument("value", com.mojang.brigadier.arguments.BoolArgumentType.bool())
                .executes(context -> 2)));
        dispatcher.register(root);
        ImprovedAnvils.registerLegacyRuleAlias(dispatcher);
        var ruleRoot = dispatcher.getRoot().getChild("gamerule");
        var canonical = ruleRoot.getChild(LegacyGameRuleMigration.CANONICAL);
        var alias = ruleRoot.getChild(LegacyGameRuleMigration.LEGACY);
        assertSame(canonical.getCommand(), alias.getCommand());
        assertSame(canonical.getChild("value"), alias.getChild("value"));
        assertFalse(ruleRoot.canUse(null));
    }

    @Test
    void menuUsesPointCostsAndNuggetRepairs() {
        // No world or player is needed to calculate an iron-repair result.
        // 26.2 normally binds item components while loading server data packs.
        // This isolated menu fixture binds only the two items needed here.
        Items.SHIELD.builtInRegistryHolder().bindComponents(net.minecraft.core.component.DataComponentMap.builder()
            .addAll(net.minecraft.core.component.DataComponents.COMMON_ITEM_COMPONENTS)
            .set(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE, 1)
            .set(net.minecraft.core.component.DataComponents.MAX_DAMAGE, 672)
            .set(net.minecraft.core.component.DataComponents.DAMAGE, 0)
            .set(net.minecraft.core.component.DataComponents.ITEM_NAME, net.minecraft.network.chat.Component.literal("Shield"))
            .build());
        Items.IRON_NUGGET.builtInRegistryHolder().bindComponents(net.minecraft.core.component.DataComponents.COMMON_ITEM_COMPONENTS);
        var menu = new AnvilMenu(0, new Inventory(null, new EntityEquipment()));
        var shield = new ItemStack(Items.SHIELD);
        shield.setDamageValue(600);
        menu.getSlot(0).set(shield);
        menu.getSlot(1).set(new ItemStack(Items.IRON_NUGGET, 9));
        assertEquals(0, menu.getSlot(2).getItem().getDamageValue());
        assertEquals(150, menu.getCost()); // Above vanilla's 40-level cap, but still valid.
        assertFalse(menu.getSlot(2).getItem().isEmpty());
        menu.getSlot(1).set(ItemStack.EMPTY);
        menu.setItemName("Named shield");
        assertEquals(1, menu.getCost());
        menu.getSlot(0).set(ItemStack.EMPTY);
        assertEquals(0, menu.getCost());
        assertTrue(menu.getSlot(2).getItem().isEmpty());
    }
}
