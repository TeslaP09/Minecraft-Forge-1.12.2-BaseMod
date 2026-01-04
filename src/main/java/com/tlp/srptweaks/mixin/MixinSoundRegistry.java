package com.tlp.srptweaks.mixin;

import org.orecruncher.dsurround.registry.sound.SoundRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SoundRegistry.class)
public abstract class MixinSoundRegistry {

    @ModifyConstant(
            method = "preInit",
            constant = @Constant(floatValue = 4.0F),
            remap = false
    )
    private float modifyMaxSoundFactor(float original) {
        // Intentionally only modifying the internal max value, not the GUI slider, as that would get way too imprecise.
        // This is really just if you want to mess around.
        return 100.0F;
    }
}
