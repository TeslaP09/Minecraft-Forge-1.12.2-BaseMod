package com.tlp.srptweaks.mixin;

import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierAltitude;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = ModifierAltitude.class, priority = 2000, remap = false)
public abstract class MixinModifierAltitude {
    @Inject(method="getWorldInfluence", at=@At("HEAD"), cancellable = true)
    public void injectGetWorldInfluence(World world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (ModConfigManager.changeSDAltitudeHandling) {
            if (!world.provider.isSurfaceWorld()) {
                cir.setReturnValue(0.0f);
                return;
            }

            int seaLevel = ModConfigManager.SDAlditudeModifierSeaLevel;
            int altitudeMod = ModConfig.server.temperature.altitudeMultiplier;
            float altitudeModAboveBelow = (pos.getY() > seaLevel) ? (float) ModConfigManager.SDAlditudeModifierAbove : (float) ModConfigManager.SDAlditudeModifierBelow;

            // Compared to sea level, bedrock is -multiplier, 256 is about -3.sth multiplier
            cir.setReturnValue(-1.0f * (Math.abs((float) (seaLevel - pos.getY()) / seaLevel * altitudeMod * altitudeModAboveBelow)));
        }
    }
}
