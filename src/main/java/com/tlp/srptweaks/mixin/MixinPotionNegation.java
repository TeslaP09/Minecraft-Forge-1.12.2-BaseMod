package com.tlp.srptweaks.mixin;

import cursedflames.bountifulbaubles.baubleeffect.PotionNegation;
import net.minecraftforge.event.entity.living.PotionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = PotionNegation.class, remap=false)
public abstract class MixinPotionNegation {
    @Inject(method="potionApply", at=@At("HEAD"), cancellable = true)
    private static void injectPotionApply(PotionEvent.PotionApplicableEvent event, CallbackInfo ci) {
        if (event.getPotionEffect().getPotion().getRegistryName() == null) {
            ci.cancel();
        }
    }
}
