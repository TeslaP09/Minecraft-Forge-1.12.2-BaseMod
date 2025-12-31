package com.tlp.srptweaks.mixin;

import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sedridor.B3M.B3M_GuiIngame;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

@SideOnly(Side.CLIENT)
@Pseudo
@Mixin(B3M_GuiIngame.class)
public abstract class MixinB3M_GuiIngame {

    @Inject(method="showHUD", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectShowHUD(CallbackInfo ci) {
        debugPrint("Ran MixinB3M_GuiIngame code");
        int mode = ModConfigManager.limitB3MHudWithItem;
        if (mode > 0) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player == null) return;

            String itemString = ModConfigManager.B3MHudItem;
            ResourceLocation rl = new ResourceLocation(itemString.isEmpty() ? "minecraft:clock" : itemString);
            Item item = Item.REGISTRY.getObject(rl);
            if (item == null) return;
            if (mode == 1) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() == item) {
                        return;
                    }
                }
                ci.cancel();
            } else if (mode == 2) {
                for (ItemStack stack : player.inventory.mainInventory) {
                    if (!stack.isEmpty() && stack.getItem() == item) {
                        return;
                    }
                }
                ci.cancel();
            }
        }
    }
}
