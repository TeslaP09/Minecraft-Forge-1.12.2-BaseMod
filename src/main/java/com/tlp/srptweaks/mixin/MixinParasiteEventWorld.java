package com.tlp.srptweaks.mixin;

import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

@Mixin(ParasiteEventWorld.class)
public abstract class MixinParasiteEventWorld {
    static {
        debugPrint("MixinEntityParasiteBase loaded!");
    }

    @Inject(method="SpreadBiome", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSpreadBiome(final World worldIn, final BlockPos pos, final int age, CallbackInfo ci) {
        debugPrint("Called SpreadBiome()");
        if (ModConfigManager.tempMode == 1) {
            double biomeTempHere = worldIn.getBiome(pos).getTemperature(pos);
            double differenceConfig = ModConfigManager.biomeOverheatTemp - ModConfigManager.biomeDebuffTemp;
            double differenceWorld = ModConfigManager.biomeOverheatTemp - biomeTempHere;
            if (differenceConfig <= 0) {
                if (biomeTempHere >= ModConfigManager.biomeOverheatTemp) {
                    debugPrint("Biome temp limit: " + ModConfigManager.biomeOverheatTemp + ", biomeTempHere: " + biomeTempHere);
                    debugPrint("Biome spread prevented at " + pos);
                    ci.cancel();
                }
            } else {
                double chanceToCancel = 1 - Math.max(0, Math.min(1, (differenceWorld / differenceConfig)));
                debugPrint("BiomeDebuffTemp: " + ModConfigManager.biomeDebuffTemp + ", biomeOverheatTemp: " + ModConfigManager.biomeOverheatTemp + ", biomeTempHere: " + biomeTempHere);
                if (ThreadLocalRandom.current().nextDouble() < chanceToCancel) {
                    debugPrint("Biome spread prevented at " + pos + ", chanceToCancel of " + chanceToCancel + " succeeded.");
                    ci.cancel();
                }
            }
        } else if (ModConfigManager.tempMode == 2) {
            float cumulative = 0f;
            for (ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
                float result = modifier.getWorldInfluence(worldIn, pos);
                cumulative += result;
            }
            double differenceConfig = ModConfigManager.environmentOverheatTemp - ModConfigManager.environmentDebuffTemp;
            double differenceWorld = ModConfigManager.environmentOverheatTemp - cumulative;
            if (differenceConfig <= 0) {
                if (cumulative >= ModConfigManager.environmentOverheatTemp) {
                    debugPrint("Environment temp limit: " + ModConfigManager.environmentOverheatTemp + ", temp here: " + cumulative);
                    debugPrint("Biome spread prevented at " + pos);
                    ci.cancel();
                }
            } else {
                double chanceToCancel = 1 - Math.max(0, Math.min(1, (differenceWorld / differenceConfig)));
                debugPrint("EnvironmentDebuffTemp: " + ModConfigManager.environmentDebuffTemp + ", environmentOverheatTemp: " + ModConfigManager.environmentOverheatTemp + ", temp here: " + cumulative + " at " + pos);
                if (ThreadLocalRandom.current().nextDouble() < chanceToCancel) {
                    debugPrint("Biome spread prevented at " + pos + ", chanceToCancel of " + chanceToCancel + " succeeded.");
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method="canBiomeStillExist", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectCanBiomeStillExist(final World worldIn, final BlockPos pos, final boolean spread, CallbackInfoReturnable<Integer> ci) {
        debugPrint("Called canBiomeStillExist()");
        if (ModConfigManager.tempMode == 1) {
            double biomeTempHere = worldIn.getBiome(pos).getTemperature(pos);
            double differenceConfig = ModConfigManager.biomeOverheatTemp - ModConfigManager.biomeDebuffTemp;
            double differenceWorld = ModConfigManager.biomeOverheatTemp - biomeTempHere;
            if (differenceConfig <= 0) {
                if (biomeTempHere >= ModConfigManager.biomeOverheatTemp) {
                    debugPrint("Biome temp limit: " + ModConfigManager.biomeOverheatTemp + ", biomeTempHere: " + biomeTempHere);
                    debugPrint("Spread prevented at " + pos);
                    ci.setReturnValue(-1);
                    ci.cancel();
                }
            } else {
                double chanceToCancel = 1 - Math.max(0, Math.min(1, (differenceWorld / differenceConfig)));
                debugPrint("BiomeDebuffTemp: " + ModConfigManager.biomeDebuffTemp + ", biomeOverheatTemp: " + ModConfigManager.biomeOverheatTemp + ", biomeTempHere: " + biomeTempHere);
                if (ThreadLocalRandom.current().nextDouble() < chanceToCancel) {
                    debugPrint("Spread prevented at " + pos + ", chanceToCancel of " + chanceToCancel + " succeeded.");
                    ci.setReturnValue(-1);
                    ci.cancel();
                }
            }
        } else if (ModConfigManager.tempMode == 2) {
            float cumulative = 0f;
            for (ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
                float result = modifier.getWorldInfluence(worldIn, pos);
                cumulative += result;
            }
            double differenceConfig = ModConfigManager.environmentOverheatTemp - ModConfigManager.environmentDebuffTemp;
            double differenceWorld = ModConfigManager.environmentOverheatTemp - cumulative;
            if (differenceConfig <= 0) {
                if (cumulative >= ModConfigManager.environmentOverheatTemp) {
                    debugPrint("Environment temp limit: " + ModConfigManager.environmentOverheatTemp + ", temp here: " + cumulative);
                    debugPrint("Spread prevented at " + pos);
                    ci.setReturnValue(-1);
                    ci.cancel();
                }
            } else {
                double chanceToCancel = 1 - Math.max(0, Math.min(1, (differenceWorld / differenceConfig)));
                debugPrint("EnvironmentDebuffTemp: " + ModConfigManager.environmentDebuffTemp + ", environmentOverheatTemp: " + ModConfigManager.environmentOverheatTemp + ", temp here: " + cumulative);
                if (ThreadLocalRandom.current().nextDouble() < chanceToCancel) {
                    debugPrint("Spread prevented at " + pos + ", chanceToCancel of " + chanceToCancel + " succeeded.");
                    ci.setReturnValue(-1);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method="canInfestBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectCanInfestBlock(final World worldIn, final BlockPos pos, final Random rand, final int stage, final boolean fromVenkrol, CallbackInfo ci) {
        debugPrint("Called canInfestBlock()");
        if (ModConfigManager.tempMode == 1) {
            double biomeTempHere = worldIn.getBiome(pos).getTemperature(pos);
            double differenceConfig = ModConfigManager.biomeOverheatTemp - ModConfigManager.biomeDebuffTemp;
            double differenceWorld = ModConfigManager.biomeOverheatTemp - biomeTempHere;
            if (differenceConfig <= 0) {
                if (biomeTempHere >= ModConfigManager.biomeOverheatTemp) {
                    debugPrint("Biome temp limit: " + ModConfigManager.biomeOverheatTemp + ", biomeTempHere: " + biomeTempHere);
                    debugPrint("Spread prevented at " + pos);
                    ci.cancel();
                }
            } else {
                double chanceToCancel = 1 - Math.max(0, Math.min(1, (differenceWorld / differenceConfig)));
                debugPrint("BiomeDebuffTemp: " + ModConfigManager.biomeDebuffTemp + ", biomeOverheatTemp: " + ModConfigManager.biomeOverheatTemp + ", biomeTempHere: " + biomeTempHere);
                if (ThreadLocalRandom.current().nextDouble() < chanceToCancel) {
                    debugPrint("Spread prevented at " + pos + ", chanceToCancel of " + chanceToCancel + " succeeded.");
                    ci.cancel();
                }
            }
        } else if (ModConfigManager.tempMode == 2) {
            float cumulative = 0f;
            for (ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
                float result = modifier.getWorldInfluence(worldIn, pos);
                cumulative += result;
            }
            double differenceConfig = ModConfigManager.environmentOverheatTemp - ModConfigManager.environmentDebuffTemp;
            double differenceWorld = ModConfigManager.environmentOverheatTemp - cumulative;
            if (differenceConfig <= 0) {
                if (cumulative >= ModConfigManager.environmentOverheatTemp) {
                    debugPrint("Environment temp limit: " + ModConfigManager.environmentOverheatTemp + ", temp here: " + cumulative);
                    debugPrint("Spread prevented at " + pos);
                    ci.cancel();
                }
            } else {
                double chanceToCancel = 1 - Math.max(0, Math.min(1, (differenceWorld / differenceConfig)));
                debugPrint("EnvironmentDebuffTemp: " + ModConfigManager.environmentDebuffTemp + ", environmentOverheatTemp: " + ModConfigManager.environmentOverheatTemp + ", temp here: " + cumulative);
                if (ThreadLocalRandom.current().nextDouble() < chanceToCancel) {
                    debugPrint("Spread prevented at " + pos + ", chanceToCancel of " + chanceToCancel + " succeeded.");
                    ci.cancel();
                }
            }
        }
    }
}
