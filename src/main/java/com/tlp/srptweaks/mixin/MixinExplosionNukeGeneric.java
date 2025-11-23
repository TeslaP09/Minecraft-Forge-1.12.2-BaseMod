package com.tlp.srptweaks.mixin;

import com.hbm.explosion.ExplosionNukeGeneric;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

@Pseudo
@Mixin(ExplosionNukeGeneric.class)
public class MixinExplosionNukeGeneric {

    static {
        debugPrint("MixinExplosionNukeGeneric loaded!");
    }

    @Inject(method = "wasteDest", at = @At("HEAD"), remap = false)
    private static void injectWasteDest(World world, BlockPos pos, CallbackInfo ci) {

        debugPrint("Called wasteDest();");

        if (!world.isRemote) {
            int rand;
            IBlockState bs = world.getBlockState(pos);
            Block b = bs.getBlock();
            if (b == Blocks.AIR) {
                return;
            } else {
                for (String string : ModConfigManager.falloutReplacementsString) {
                    String[] replacement = string.replace("\"", "").split(",");
                    ResourceLocation toReplace = new ResourceLocation(replacement[0]);
                    ResourceLocation replaceWith = new ResourceLocation(replacement[1]);

                    debugPrint("Going through falloutReplacementString, replace " + toReplace + " with " + replaceWith + " at " + Block.REGISTRY.getNameForObject(b));
                    debugPrint(toReplace.toString());
                    debugPrint(Block.REGISTRY.getNameForObject(b).toString());

                    if (Block.REGISTRY.getNameForObject(b).equals(toReplace)) {
                        world.setBlockState(pos, Block.REGISTRY.getObject(replaceWith).getDefaultState());
                        debugPrint("Radiation block replacement match found");
                        return;
                    }
                }
            }
        }
    }

    @Inject(method = "wasteDestNoSchrab", at = @At("HEAD"), remap = false)
    private static void injectWasteDestNoSchrab(World world, BlockPos pos, CallbackInfo ci) {

        debugPrint("Called wasteDestNoSchrab();");

        if (!world.isRemote) {
            int rand;
            Block b = world.getBlockState(pos).getBlock();
            if(b == Blocks.AIR){
                return;
            } else {
                for (String string : ModConfigManager.falloutReplacementsString) {
                    String[] replacement = string.replace("\"", "").split(",");
                    ResourceLocation toReplace = new ResourceLocation(replacement[0]);
                    ResourceLocation replaceWith = new ResourceLocation(replacement[1]);

                    debugPrint("Going through falloutReplacementString, replace " + toReplace + " with " + replaceWith + " at " + Block.REGISTRY.getNameForObject(b));
                    debugPrint(toReplace.toString());
                    debugPrint(Block.REGISTRY.getNameForObject(b).toString());

                    if (Block.REGISTRY.getNameForObject(b).equals(toReplace)) {
                        world.setBlockState(pos, Block.REGISTRY.getObject(replaceWith).getDefaultState());
                        debugPrint("Radiation block replacement match found");
                        return;
                    }
                }
            }
        }
    }
}
