package com.tlp.srptweaks.mixin;

import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tinkersurvival.util.ItemUse;

@Pseudo
@Mixin(value=ItemUse.class, remap = false)
public class MixinTinkerSurvivalItemUse {

    @Inject(method = "isWhitelistItem", at=@At("HEAD"), cancellable = true)
    private static void injectIsWhitelistItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfigManager.tinkersurvivalMainhandAttackFix && stack.isEmpty()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
