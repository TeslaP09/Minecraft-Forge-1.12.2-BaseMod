package com.tlp.srptweaks.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.tlp.srptweaks.util.InvasionSaveData;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

public class SubscriptionHandlerParasites {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        long time = world.getWorldTime();
        if (time % 20 == 0) {

            if (world.isRemote) return;
            if (event.phase != TickEvent.Phase.END) return;

            InvasionSaveData invasionData = InvasionSaveData.get(world);
            SRPSaveData srpData = SRPSaveData.get(world);

            InvasionHandler.invasionEndCheck(world, srpData, invasionData, ModConfigManager.sourceDimension, ModConfigManager.minPoints, ModConfigManager.minPhase, ModConfigManager.setPhase, ModConfigManager.setPoints);
        }

        if (time % ModConfigManager.invasionToOverworldTime == 0) {
            InvasionSaveData invasionData = InvasionSaveData.get(world);
            SRPSaveData srpData = SRPSaveData.get(world);

            InvasionHandler.leakIntoDimension(world, srpData, invasionData, 0);
        }

    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            boolean onlyPlayerSource = ModConfigManager.onlyPlayerSource;
            if (onlyPlayerSource && (!(attacker instanceof EntityPlayer))) {
                return;
            }
            EntityLivingBase entity = event.getEntityLiving();
            int reduce = ModConfigManager.reduce;
            double reduceMulti = ModConfigManager.reduceMulti;
            ItemStack stack;
            Item item;
            float amount = event.getAmount();


            if (reduce != 0) {
                stack = (attacker != null) ? attacker.getHeldItemMainhand() : ItemStack.EMPTY;
                item = !stack.isEmpty() ? stack.getItem() : null;

                if (!(entity instanceof EntityParasiteBase) && (ModConfigManager.isParasiteWeaponClass(item) || ModConfigManager.isParasiteWeaponRid(item))) {
                    switch (reduce) {
                        case 1:
                            event.setAmount((float) (amount * reduceMulti));
                            break;
                        case 2:
                            event.setCanceled(true);
                            break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        ResourceLocation name = stack.getItem().getRegistryName();
        if (name == null) return;

        String id = name.toString();

        for (String blocked : ModConfigManager.preventItemUse) {
            if (id.equals(blocked)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntityPlayer().world.isRemote) return;
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) return;
        if (!event.shouldSetSpawn()) return;

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();

        // Get bed location from vanilla system
        BlockPos bed = player.getBedLocation(player.dimension);
        if (bed == null) return;

        // Verify it's actually a bed
        World world = player.world;
        IBlockState state = world.getBlockState(bed);
        if (!state.getBlock().isBed(state, world, bed, player)) {
            return;
        }

        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persist = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        NBTTagCompound spawn = new NBTTagCompound();
        spawn.setInteger("Dim", player.dimension);
        spawn.setLong("Pos", bed.toLong());

        persist.setTag("GlobalSpawn", spawn);
        data.setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);

        debugPrint("Saved global spawn: " + player.dimension + ", " + bed.toString());
    }

    @SubscribeEvent
    public void onSetSpawn(PlayerSetSpawnEvent event) {
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();

            BlockPos pos = event.getNewSpawn();
            int dim = player.dimension;

            if (pos != null) {
                NBTTagCompound data = player.getEntityData();
                NBTTagCompound persist = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

                NBTTagCompound spawn = new NBTTagCompound();
                spawn.setInteger("Dim", dim);
                spawn.setLong("Pos", pos.toLong());

                persist.setTag("GlobalSpawn", spawn);
                data.setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);

                debugPrint("Saved GlobalSpawn: " + persist.getKeySet());
            }
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.world.isRemote) return;

        NBTTagCompound persist = player.getEntityData()
                .getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        if ((!persist.hasKey("GlobalSpawn")) || !(ModConfigManager.globalBedSpawnOverridesDimension)) return;

        NBTTagCompound spawn = persist.getCompoundTag("GlobalSpawn");
        int targetDim = spawn.getInteger("Dim");
        BlockPos pos = BlockPos.fromLong(spawn.getLong("Pos"));

        debugPrint("GlobalSpawn: " + targetDim + ", " + pos.toString());

        // If player already respawned in correct dimension, do nothing
        if (player.dimension == targetDim) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        WorldServer targetWorld = server.getWorld(targetDim);
        if (targetWorld == null) return;

        // Check if this is actually a valid bed at the position
        IBlockState state = targetWorld.getBlockState(pos);
        Block block = state.getBlock();

        // Check if it's a bed block
        if (!block.isBed(state, targetWorld, pos, player)) {
            debugPrint("No valid bed found at position: " + pos);
            return;
        }

        // Get the actual bed spawn position (handles bed orientation)
        BlockPos spawnPos = EntityPlayer.getBedSpawnLocation(targetWorld, pos, false);
        if (spawnPos == null) {
            debugPrint("Bed spawn location check failed");
            return;
        }

        debugPrint("Teleporting to bed at: " + spawnPos);

        player.getServer().addScheduledTask(() -> {
            player.changeDimension(targetDim, new ITeleporter() {
                @Override
                public void placeEntity(World world, Entity entity, float yaw) {
                    debugPrint("Post-respawn teleporter placing at: " + pos.toString());
                    entity.setPositionAndUpdate(
                            pos.getX() + 0.5,
                            pos.getY() + 0.6,
                            pos.getZ() + 0.5
                    );
                }
            });

            player.connection.setPlayerLocation(
                    pos.getX() + 0.5,
                    pos.getY() + 0.6,
                    pos.getZ() + 0.5,
                    0, 0
            );

            // Also set the position server-side
            player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);
        });
    }

    /*@SubscribeEvent(priority = EventPriority.LOWEST) // Run after everything else
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.world.isRemote) return;

        NBTTagCompound persist = player.getEntityData()
                .getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        if (!persist.hasKey("GlobalSpawn") || !ModConfigManager.globalBedSpawnOverridesDimension) {
            return;
        }

        NBTTagCompound spawn = persist.getCompoundTag("GlobalSpawn");
        int targetDim = spawn.getInteger("Dim");
        BlockPos pos = BlockPos.fromLong(spawn.getLong("Pos"));

        System.out.println("Post-respawn check:");
        System.out.println("  Global spawn target: " + targetDim + " at " + pos);
        System.out.println("  Player current: " + player.dimension + " at " + player.getPosition());

        // Schedule correction for next tick (after Minecraft's respawn logic)
        player.getServer().addScheduledTask(() -> {
            System.out.println("Executing post-respawn correction...");
            correctPlayerSpawn(player, targetDim, pos);
        });
    }

    private void correctPlayerSpawn(EntityPlayerMP player, int targetDim, BlockPos pos) {
        System.out.println("Correction started - Player at: " + player.getPosition() + " in dim " + player.dimension);

        // If player is already in the correct dimension
        if (player.dimension == targetDim) {
            // Check if player is at the correct position (with some tolerance)
            double distance = player.getPosition().distanceSq(pos);
            if (distance > 25) { // More than 5 blocks away from bed
                System.out.println("Player is in correct dimension but wrong position (" + distance + " blocks away)");
                System.out.println("Moving to bed at " + pos);
                player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            } else {
                System.out.println("Player is already at correct position");
            }
            return;
        }

        // Player is in wrong dimension - need to change
        System.out.println("Player is in wrong dimension. Changing from " + player.dimension + " to " + targetDim);

        WorldServer targetWorld = player.getServer().getWorld(targetDim);
        if (targetWorld == null) {
            System.out.println("Target world is null");
            return;
        }

        // Change dimension with teleporter
        player.changeDimension(targetDim, new ITeleporter() {
            @Override
            public void placeEntity(World world, Entity entity, float yaw) {
                System.out.println("Post-respawn teleporter placing at: " + pos);
                entity.setPositionAndUpdate(
                        pos.getX() + 0.5,
                        pos.getY() + 0.1,
                        pos.getZ() + 0.5
                );
            }
        });

        player.connection.setPlayerLocation(
                pos.getX() + 0.5,
                pos.getY() + 0.1,
                pos.getZ() + 0.5,
                0, 0
        );

        // Also set the position server-side
        player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
    }*/
}
