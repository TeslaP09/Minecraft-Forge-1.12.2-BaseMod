package com.tlp.srptweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import com.maidarch.srpcalamity.util.config.CalamityConfig;
import net.minecraftforge.common.config.Configuration;

import static com.maidarch.srpcalamity.util.config.CalamityConfig.*;

@Pseudo
@Mixin(value = CalamityConfig.class, remap = false)
public abstract class MixinSRPCalamityConfig {

    /**
     * @author Tesla_P
     * @reason The config file generates in a wrong way
     * Each config entry string has a double new line in the end, causing the file to error on every game startup.
     * This was fixed in versions for SRParasites 1.10+, but as we're still on 1.9.21, I'm going to  fix it like this...
     * THIS FIX IS FOR SRPCALAMITY 0.3.0 ONLY!!!
     */
    @Overwrite
    private static void initPropertiesConfigPious(Configuration cfg) {
        piousCap = cfg.getInt("Version Pious Damage Cap", "parasite_properties_pious", piousCap, 1, 100, "Minimum number of hits required to kill Pious versions.");
        piousdespawn = cfg.getBoolean("Version Pious Despawn", "parasite_properties_pious", piousdespawn, "Set to true for Pious versions to despawn.");
        piousFollow = cfg.getFloat("Version Pious Follow Range", "parasite_properties_pious", (float)piousFollow, 0.0F, 128.0F, "Follow range.");
        piousMinDamage = cfg.getFloat("Version Pious Minimum Damage", "parasite_properties_pious", piousMinDamage, 0.0F, 1024.0F, "Minimum Damage for Pious versions.");
        piousOneMindDeathV = cfg.getInt("Version Pious Scent Death Value", "parasite_properties_pious", piousOneMindDeathV, 1, 1000, "Death value set in EntityParasiticScent, used if parasite_collective_consciousness is enabled.");
        piousXPValue = cfg.getInt("Version Pious XP Value", "parasite_properties_pious", piousXPValue, 1, 50000, "XP value.");
        piousRemainValue = cfg.getInt("Version Pious Remain Value", "parasite_properties_pious", piousRemainValue, 1, 50000, "Life points required to be rebuilt.");
        piousLoosingEPValue = cfg.getInt("Version Pious Death Penalty Evolution Value", "parasite_properties_pious", piousLoosingEPValue, 0, 1000000000, "How many points parasites will lose when it is killed.");
        piousSneakPen = cfg.getFloat("Version Pious Sneak Penalty", "parasite_properties_pious", (float)piousSneakPen, 0.0F, 1.0F, "Penalty for parasites when someone is sneaking, the lower the value the higher the chance of not being seen, vanilla value is 0.800000011920929.");
        piousInviPen = cfg.getFloat("Version Pious Invisible Penalty", "parasite_properties_pious", piousInviPen, 0.0F, 1.0F, "Penalty for parasites when someone is invisible, the lower the value the higher the chance of not being seen, vanilla value is 0.7.");
    }
}
