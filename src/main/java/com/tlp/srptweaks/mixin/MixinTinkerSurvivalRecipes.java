package com.tlp.srptweaks.mixin;

import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import tinkersurvival.world.TinkerSurvivalWorld;
import tinkersurvival.recipe.TinkerSurvivalRecipes;


@Mixin(TinkerSurvivalRecipes.class)
public class MixinTinkerSurvivalRecipes {

    @Shadow(remap = false)
    private static void addKnifeRecipe(ItemStack input, ItemStack output, String tool) {}

    @Redirect(
            method = "initKnifeRecipes",
            at = @At(value = "INVOKE",
                    target = "Ltinkersurvival/recipe/TinkerSurvivalRecipes;addKnifeRecipe(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Ljava/lang/String;)V"),
            remap = false
    )
    private static void skipRockAndFlint(ItemStack input, ItemStack output, String tool) {
        if (!ModConfigManager.tinkersurvivalDisableFlintRecipes) {
            addKnifeRecipe(input, output, tool);
        } else {
            boolean isRock = input.getItem() == TinkerSurvivalWorld.rockStone;
            boolean isFlint = input.getItem() == Items.FLINT;

            if (isRock || isFlint) {
                return;
            }

            addKnifeRecipe(input, output, tool);
        }
    }
}