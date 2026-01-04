package com.tlp.srptweaks.mixin.core;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.orecruncher.dsurround.registry.sound.SoundRegistry;

@Pseudo
@Mixin(SoundRegistry.class)
public interface SoundRegistryAccessor {
    @Accessor("volumeControl")
    Object2FloatOpenHashMap<ResourceLocation> getVolumeControl();
}
