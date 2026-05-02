package com.tlp.srptweaks.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.food.FoodValues;

import static com.tlp.srptweaks.util.ModConfigManager.*;

public class HungerHandler {

    @SubscribeEvent
    public void onFoodStatsAddition(FoodEvent.FoodStatsAddition event) {
        if (!enableHungerTweaks) return;

        event.setCanceled(true);

        FoodValues fs = event.foodValuesToBeAdded;
        int hungerFood = fs.hunger;
        float saturationFood = 2 * fs.saturationModifier * hungerFood;

        if (hungerFood < 0 || saturationFood < 0) return;

        EntityPlayer player = event.player;
        FoodStats foodStats = event.player.getFoodStats();

        int maxHunger = AppleCoreAPI.accessor.getMaxHunger(player);

        int currentHunger = foodStats.getFoodLevel();
        float currentSaturation = foodStats.getSaturationLevel();

        int hungerToBeAdded = hungerFood;
        float saturationToBeAdded = saturationFood;
        float saturationOverflow = 0.0f;

        int hungerToFull = Math.max(0, maxHunger - currentHunger);
        float saturationToFull = Math.max(0, maxHunger - currentSaturation);

        if (hungerToBeAdded > hungerToFull) {
            saturationToBeAdded += (float) (hungerSaturationConversionRate * (hungerToBeAdded - hungerToFull));
            hungerToBeAdded = hungerToFull;
        }

        if (saturationToBeAdded > saturationToFull) {
            saturationOverflow = saturationToBeAdded - saturationToFull;
            saturationToBeAdded = saturationToFull;
        }

        debugPrint("Max Hunger: " +  maxHunger + ", hungerFood: " + hungerFood + ", saturationFood: " + saturationFood + ", hungerToBeAdded: " + hungerToBeAdded + ", saturationToBeAdded: " + saturationToBeAdded + ", saturationOverflow: " + saturationOverflow +  ", currentHunger: " + currentHunger + ", currentSaturation: " +  currentSaturation);

        AppleCoreAPI.mutator.setHunger(player, currentHunger + hungerToBeAdded);

        if (oversaturationMode == 0) {
            AppleCoreAPI.mutator.setSaturation(player, currentSaturation + saturationToBeAdded);
        } else if (oversaturationMode == 1) {
            AppleCoreAPI.mutator.setSaturation(player, currentSaturation + saturationToBeAdded + saturationOverflow);
        } else if (oversaturationMode == 2) {
            AppleCoreAPI.mutator.setSaturation(player, currentSaturation + saturationToBeAdded);
            if (foodStats.getFoodLevel() >= maxHunger && foodStats.getSaturationLevel() >= maxHunger) {
                int duration = Math.min(Math.round(saturationOverflow * saturationTicksPerOverflownSaturation), saturationDurationCap);
                PotionEffect effect = new PotionEffect(MobEffects.SATURATION, duration, 0, false, false);
                player.addPotionEffect(effect);
            }
        }
    }
}
