package com.tlp.srptweaks.mixin;

import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tinkersurvival.event.HarvestEventHandler;

import java.util.Set;

import static com.tlp.srptweaks.util.ModConfigManager.*;

@Pseudo
@Mixin(value = HarvestEventHandler.class, remap = false)
public abstract class MixinHarvestEventHandler {

    @Inject(method = "breakBlock", at = @At(value = "HEAD"), cancellable = true)
    private void injectBreakBlockCheck(BlockEvent.BreakEvent event, CallbackInfo ci) {
        if (!event.getWorld().isRemote) {
            EntityPlayer player = event.getPlayer();
            Block block = event.getState().getBlock();

            if (block.getRegistryName() == null) return;
            String name = block.getRegistryName().toString();
            debugPrint(name);

            for (String id : ModConfigManager.tinkersurvivalBlockWhitelist) {
                if (id.equals(name)) {
                    ci.cancel();
                    return;
                }
            }
        }
    }

    @Inject(method = "slowMining", at = @At("TAIL"), cancellable = false)
    private void onSlowMining(PlayerEvent.BreakSpeed event, CallbackInfo ci) {
        if (!ModConfigManager.tinkersurvivalHarderBranchMining) return;

        EntityPlayer player = event.getEntityPlayer();

        if (player == null || player instanceof FakePlayer || player.capabilities.isCreativeMode) {
            return;
        }

        IBlockState state = event.getState();
        Block block = state.getBlock();
        if (block.getRegistryName() == null) return;
        if (isBlockWhitelisted(block, state)) return;

        BlockPos pos = event.getPos();
        if (pos == null) return;

        double y = pos.getY();

        if (y > 48) return;

        float heightFactor = calculateHeightFactor(y);

        float currentSpeed = event.getNewSpeed();
        event.setNewSpeed(currentSpeed * heightFactor);
        debugPrint("Multiplied mining speed by " + heightFactor);
    }

    private float calculateHeightFactor(double y) {
        final double START_Y = ModConfigManager.tinkersurvivalHarderBranchMiningMaxY;
        final double MAX_EFFECT_Y = ModConfigManager.tinkersurvivalHarderBranchMiningMinY;
        final float MIN_MULTIPLIER = (float)ModConfigManager.tinkersurvivalHarderBranchMiningMinMulti;

        if (y >= START_Y) return 1.0f;

        if (y <= MAX_EFFECT_Y) return MIN_MULTIPLIER;

        double progress = (START_Y - y) / (START_Y - MAX_EFFECT_Y);
        return 1.0f - (1.0f - MIN_MULTIPLIER) * (float)progress;
    }

    private boolean isBlockWhitelisted(Block block, IBlockState state) {
        ResourceLocation blockId = block.getRegistryName();
        if (blockId == null) return false;

        String idStr = blockId.toString();
        int meta = block.getMetaFromState(state);

        if (blockWhitelistSimple.contains(idStr)) {
            return true;
        }

        Set<Integer> allowedMetas = blockWhitelistWithMeta.get(idStr);
        if (allowedMetas != null && allowedMetas.contains(meta)) {
            return true;
        }

        return false;
    }
}
