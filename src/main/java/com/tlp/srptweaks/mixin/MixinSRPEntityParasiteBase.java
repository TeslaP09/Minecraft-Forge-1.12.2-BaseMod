package com.tlp.srptweaks.mixin;

import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

@Mixin(EntityParasiteBase.class)
public abstract class MixinSRPEntityParasiteBase {
    private boolean overheating = false;
    static {
        debugPrint("MixinSRPEntityParasiteBase loaded!");
    }

    @Inject(method = "func_70636_d", at = @At("TAIL"), remap = false)
    private void injectOnLivingUpdate(CallbackInfo ci) {

        EntityParasiteBase parasite = (EntityParasiteBase)(Object) this;
        World world = parasite.world;

        if (!world.isRemote && (parasite.ticksExisted % 100 == 0)) {
            overheating = false;
            debugPrint("Applying temperature check for parasite at tick: " + parasite.ticksExisted);

            BlockPos pos = parasite.getPosition();

            if (ModConfigManager.tempMode == 1) {
                debugPrint("Using biome temperature mode");
                Biome biome = world.getBiome(pos);
                float temperature = biome.getTemperature(pos);
                debugPrint("Biome temperature: " + temperature + ", Thresholds: " + ModConfigManager.biomeDebuffTemp + ", " + ModConfigManager.biomeOverheatTemp);

                if (temperature >= ModConfigManager.biomeDebuffTemp) {
                    debugPrint("Applying biome debuff temperature effects");
                    applyConfiguredDebuffEffects(parasite);
                }
                if (temperature >= ModConfigManager.biomeOverheatTemp) {
                    debugPrint("Applying biome overheat temperature effects");
                    overheating = true;
                    applyConfiguredOverheatEffects(parasite);
                }

            } else if (ModConfigManager.tempMode == 2 && Loader.isModLoaded("simpledifficulty")) {
                debugPrint("Using Simple Difficulty temperature mode");
                float cumulative = 0f;
                for (ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
                    float result = modifier.getWorldInfluence(world, pos);
                    cumulative += result;
                }
                debugPrint("Cumulative temperature: " + cumulative + ", Threshold: " + ModConfigManager.environmentDebuffTemp + ", " + ModConfigManager.biomeOverheatTemp);

                if (cumulative >= ModConfigManager.environmentDebuffTemp) {
                    debugPrint("Applying environment debuff temperature effects");
                    applyConfiguredDebuffEffects(parasite);
                }
                if (cumulative >= ModConfigManager.environmentOverheatTemp) {
                    debugPrint("Applying environment overheat temperature effects");
                    overheating = true;
                    applyConfiguredOverheatEffects(parasite);
                }
            }
        }

        if (!world.isRemote && (parasite.ticksExisted % 20 == 0) && overheating) {
            parasite.attackEntityFrom(DamageSource.IN_FIRE, (float) ModConfigManager.overheatDamage);
        }
    }

    private void applyConfiguredDebuffEffects(EntityParasiteBase parasite) {
        debugPrint("Applying configured debuff effects");
        for (String effect : ModConfigManager.tempDebuffsString) {
            String[] parts = effect.split(",");
            if (parts.length == 2) {
                try {
                    String potionName = parts[0].trim();
                    int amplifier = Integer.parseInt(parts[1].trim());
                    Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionName));

                    if (potion != null) {
                        debugPrint("Adding potion effect: " + potionName + " with amplifier " + (amplifier - 1));
                        parasite.addPotionEffect(new PotionEffect(potion, 100, amplifier - 1, false, false));
                    } else {
                        debugPrint("Potion not found: " + potionName);
                    }
                } catch (NumberFormatException e) {
                    debugPrint("Invalid amplifier in config entry: " + effect);
                }
            } else {
                debugPrint("Invalid potion entry format: " + effect);
            }
        }
    }

    private void applyConfiguredOverheatEffects(EntityParasiteBase parasite) {
        debugPrint("Applying configured overheating effects");
        for (String effect : ModConfigManager.tempOverheatDebuffsString) {
            String[] parts = effect.split(",");
            if (parts.length == 2) {
                try {
                    String potionName = parts[0].trim();
                    int amplifier = Integer.parseInt(parts[1].trim());
                    Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionName));

                    if (potion != null) {
                        debugPrint("Adding potion effect: " + potionName + " with amplifier " + (amplifier - 1));
                        parasite.addPotionEffect(new PotionEffect(potion, 100, amplifier - 1, false, false));
                    } else {
                        debugPrint("Potion not found: " + potionName);
                    }
                } catch (NumberFormatException e) {
                    debugPrint("Invalid amplifier in config entry: " + effect);
                }
            } else {
                debugPrint("Invalid potion entry format: " + effect);
            }
        }
    }
}