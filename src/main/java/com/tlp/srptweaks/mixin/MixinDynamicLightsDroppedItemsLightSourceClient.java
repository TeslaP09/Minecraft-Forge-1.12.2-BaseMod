package com.tlp.srptweaks.mixin;

import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

@Pseudo
@Mixin(targets = "atomicstryker.dynamiclights.client.modules.DroppedItemsLightSource$EntityItemAdapter", remap = false)
public abstract class MixinDynamicLightsDroppedItemsLightSourceClient {

    @Shadow private EntityItem entity;
    @Shadow private int lightLevel;
    @Shadow private boolean enabled;
    @Shadow private boolean notWaterProof;
    @Invoker("disableLight") protected abstract void invokeDisableLight();

    @Inject(method="onTick",at = @At("TAIL"), remap = false)
    private void injectOnTick(CallbackInfo ci) {
        if (ModConfigManager.disableDynamicLightsAR && Loader.isModLoaded("advancedrocketry") && notWaterProof) {
            WorldProvider provider = entity.getEntityWorld().provider;
            if (provider != null) {
                AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(provider.getDimension());
                if (atmhandler != null) {
                    BlockPos pos = new BlockPos(MathHelper.floor(entity.posX), MathHelper.floor(entity.posY), MathHelper.floor(entity.posZ));
                    IAtmosphere at = atmhandler.getAtmosphereType(pos);
                    if ((at == AtmosphereType.HIGHPRESSURENOO2) || (at == AtmosphereType.NOO2) || (at == AtmosphereType.LOWOXYGEN) || (at == AtmosphereType.SUPERHEATEDNOO2) || (at == AtmosphereType.SUPERHIGHPRESSURENOO2) || (at == AtmosphereType.VACUUM) || at == (AtmosphereType.VERYHOTNOO2)) {
                        lightLevel = 0;
                        enabled = false;
                        invokeDisableLight();
                        debugPrint("Ran item disable light code");
                    }
                }
            }
        }

    }
}
