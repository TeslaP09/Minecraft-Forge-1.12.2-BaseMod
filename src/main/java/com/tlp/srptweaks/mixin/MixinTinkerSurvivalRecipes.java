package com.tlp.srptweaks.mixin;

import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tinkersurvival.recipe.TinkerSurvivalRecipes;
import tinkersurvival.world.TinkerSurvivalWorld;

import java.util.Objects;

import static com.tlp.srptweaks.util.OreDictHelper.hasOreDictName;


@Mixin(TinkerSurvivalRecipes.class)
public class MixinTinkerSurvivalRecipes {

    @Inject(method="addKnifeRecipe", at=@At("HEAD"), remap = false, cancellable = true)
    private static void injectAddKnifeRecipe(ItemStack input, ItemStack output, String tool, CallbackInfo ci) {
        boolean disableFlintRecipes = ModConfigManager.tinkersurvivalDisableFlintRecipes;
        boolean inputFlintStone = input.getItem() == TinkerSurvivalWorld.rockStone || input.getItem() == Items.FLINT;
        boolean outputFlintShard = output.getItem() == TinkerSurvivalWorld.flintShard;
        boolean toolKnife = Objects.equals(tool, "crudeKnife") || Objects.equals(tool, "ticKnife");

        if (disableFlintRecipes && inputFlintStone && outputFlintShard && toolKnife) {
            ci.cancel();
        }

        boolean disableSaplingStickRecipes = ModConfigManager.tinkersurvivalDisableSaplingStickRecipes;
        boolean outputStick = output.getItem() == Items.STICK;
        boolean inputSapling = hasOreDictName(input, "treeSapling");
        boolean inputSaplingAlt = false;
        for (String name : ModConfigManager.tinkersurvivalDisableSaplingStickRecipesContains) {
            if (Objects.requireNonNull(input.getItem().getRegistryName()).toString().contains(name)) {
                inputSaplingAlt = true;
                break;
            }
        }

        if (disableSaplingStickRecipes && (inputSapling || inputSaplingAlt) && outputStick && toolKnife) {
            ci.cancel();
        }
    }

    @Inject(method="registerRecipe", at=@At("HEAD"), remap = false, cancellable = true)
    private static void injectRegisterRecipe(ItemStack output, ItemStack input, String tool, CallbackInfo ci) {
        boolean disableRecipes = ModConfigManager.tinkersurvivalDisablePlankStickRecipes;
        boolean outputStick = output.getItem() == Items.STICK;
        boolean inputPlank = hasOreDictName(input, "plankWood");
        boolean inputPlankAlt = false;
        for (String name : ModConfigManager.tinkersurvivalDisablePlankStickRecipesContains) {
            if (Objects.requireNonNull(input.getItem().getRegistryName()).toString().contains(name)) {
                inputPlankAlt = true;
                break;
            }
        }
        boolean toolSaw = (Objects.equals(tool, "crudeSaw") || Objects.equals(tool, "ticSaw"));

        if (disableRecipes && outputStick && (inputPlank || inputPlankAlt) && toolSaw) {
            ci.cancel();
        }
    }
}