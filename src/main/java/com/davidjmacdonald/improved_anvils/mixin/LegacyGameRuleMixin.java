package com.davidjmacdonald.improved_anvils.mixin;

import com.davidjmacdonald.improved_anvils.LegacyGameRuleMigration;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.world.level.gamerules.GameRuleMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRuleMap.class)
public abstract class LegacyGameRuleMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE",
        target = "Lcom/mojang/serialization/Codec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static Codec<GameRuleMap> migrateLegacyRule(Codec<GameRuleMap> original) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<GameRuleMap, T>> decode(DynamicOps<T> ops, T input) {
                return original.decode(ops, LegacyGameRuleMigration.migrate(new Dynamic<>(ops, input)).getValue());
            }

            @Override
            public <T> DataResult<T> encode(GameRuleMap value, DynamicOps<T> ops, T prefix) {
                return original.encode(value, ops, prefix);
            }
        };
    }
}
