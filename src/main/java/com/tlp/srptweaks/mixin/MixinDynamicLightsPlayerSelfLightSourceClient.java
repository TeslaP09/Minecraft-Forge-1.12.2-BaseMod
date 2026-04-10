package com.tlp.srptweaks.mixin;

import atomicstryker.dynamiclights.client.modules.PlayerSelfLightSource;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraftforge.fml.common.Loader;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;

@Pseudo
@Mixin(PlayerSelfLightSource.class)
public abstract class MixinDynamicLightsPlayerSelfLightSourceClient {

    @Inject(method="checkPlayerWater", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectCheckPlayerWater(EntityPlayer thePlayer, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfigManager.disableDynamicLightsAR && Loader.isModLoaded("advancedrocketry")) {
            WorldProvider provider = thePlayer.getEntityWorld().provider;
            AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(provider.getDimension());
            BlockPos pos = new BlockPos(MathHelper.floor(thePlayer.posX + 0.5D), MathHelper.floor(thePlayer.posY + thePlayer.getEyeHeight()), MathHelper.floor(thePlayer.posZ + 0.5D));
            if (atmhandler != null && provider != null) {
                IAtmosphere at = atmhandler.getAtmosphereType(pos);
                if ((at == AtmosphereType.HIGHPRESSURENOO2) || (at == AtmosphereType.NOO2) || (at == AtmosphereType.LOWOXYGEN) || (at == AtmosphereType.SUPERHEATEDNOO2) || (at == AtmosphereType.SUPERHIGHPRESSURENOO2) || (at == AtmosphereType.VACUUM) || at == (AtmosphereType.VERYHOTNOO2)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

}
