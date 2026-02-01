package com.tlp.srptweaks.handlers;


import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.tlp.srptweaks.util.InvasionSaveData;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;


public class InvasionHandler {
    public static void invasionEndCheck(final World world, final SRPSaveData srpData, final InvasionSaveData invasionData, final int dimId, final int minPoints, final int minPhase, final int setPhase, final int setPoints) {
        try {
            final Field dim = srpData.getClass().getDeclaredField("dimEPid");
            final Field points = srpData.getClass().getDeclaredField("dimEPtotalKills");
            final Field phase = srpData.getClass().getDeclaredField("dimEPevolution");

            dim.setAccessible(true);
            points.setAccessible(true);
            phase.setAccessible(true);

            final ArrayList<Integer> dimEPid = (ArrayList<Integer>)dim.get(srpData);
            final ArrayList<Integer> dimEPtotalKills = (ArrayList<Integer>)points.get(srpData);
            final ArrayList<Byte> dimEPevolution = (ArrayList<Byte>)phase.get(srpData);

            debugPrint("SRPTweaks SRP main arrays fetched.");

            for (int i = 0; i < dimEPid.size(); ++i) {
                debugPrint("SRPTweaks looping through dimEPid, now at position" + i + ".");

                if (dimEPid.get(i) == dimId) {
                    if (((dimEPtotalKills.get(i) <= minPoints) || (dimEPevolution.get(i) <= minPhase)) && (!invasionData.getInvasionEnded())) {
                        System.out.println("=== INVASION DEFEATED ===");
                        dimEPtotalKills.set(i, setPoints);
                        dimEPevolution.set(i, (byte)setPhase);
                        srpData.setDirty(true);

                        invasionData.setInvasionEnded(true);

                        for (EntityPlayerMP player : world.getMinecraftServer().getPlayerList().getPlayers()) {
                            debugPrint("SRPTweaks looping through players, trying to play sound right now");
                            player.connection.sendPacket(
                                    new SPacketSoundEffect(
                                            SoundEvents.ENTITY_ENDERDRAGON_DEATH,
                                            SoundCategory.MASTER,
                                            player.posX,
                                            player.posY,
                                            player.posZ,
                                            1.0F,
                                            1.0F
                                    )
                            );

                            Advancement advancement = player.getServer().getAdvancementManager().getAdvancement(new ResourceLocation("srptweaks", "invasion_end"));

                            if (advancement != null) {
                                player.getAdvancements().grantCriterion(advancement, "dummy");
                                debugPrint("SRPTweaks advancement granted (theoretically)");
                            }

                        }

                        killAllParasitesInDimension(ModConfigManager.sourceDimension);

                    }
                    break;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void killAllParasitesInDimension(int dimensionId) {
        WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimensionId);
        if (world == null) return;
        List<Entity> entities = new ArrayList<>(world.loadedEntityList);

        for (Entity entity : entities) {
            if (entity instanceof EntityParasiteBase) {
                entity.setDead();
            }
        }

        debugPrint("SRPTweaks killed all parasites in dimension " +  dimensionId + ".");
    }

    public static void leakIntoDimension(final World world, final SRPSaveData srpData, final InvasionSaveData invasionData, final int dimId) {
        try {
            final Field dim = srpData.getClass().getDeclaredField("dimEPid");
            final Field points = srpData.getClass().getDeclaredField("dimEPtotalKills");
            final Field phase = srpData.getClass().getDeclaredField("dimEPevolution");

            dim.setAccessible(true);
            points.setAccessible(true);
            phase.setAccessible(true);

            final ArrayList<Integer> dimEPid = (ArrayList<Integer>) dim.get(srpData);
            final ArrayList<Integer> dimEPtotalKills = (ArrayList<Integer>)points.get(srpData);
            final ArrayList<Byte> dimEPevolution = (ArrayList<Byte>) phase.get(srpData);

            boolean allowLeak = false;

            debugPrint("SRPTweaks SRP main arrays fetched.");

            for (int i = 0; i < dimEPid.size(); ++i) {
                debugPrint("SRPTweaks looping through dimEPid, now at position" + i + ".");
                if (dimEPid.get(i) == ModConfigManager.targetDimension) {
                    if ((int)(dimEPevolution.get(i)) >= ModConfigManager.leakMinPhase) {
                        allowLeak = true;
                        break;
                    }
                }
            }

            if (!allowLeak) return;

            if (invasionData.getInvasionEnded()) return;

            for (int i = 0; i < dimEPid.size(); ++i) {
                debugPrint("SRPTweaks looping through dimEPid, now at position" + i + ".");

                if (dimEPid.get(i) == dimId) {
                    if (!invasionData.getInvasionEnded() && (int)(dimEPevolution.get(i)) < 0) {
                        System.out.println("=== INVASION LEAKED INTO " + dimId + " ===");
                        dimEPtotalKills.set(i, 0);
                        dimEPevolution.set(i, (byte)0);
                        srpData.setDirty(true);

                        for (EntityPlayerMP player : world.getMinecraftServer().getPlayerList().getPlayers()) {
                            debugPrint("SRPTweaks looping through players, trying to play sound and send message right now");

                            if (player.world.provider.getDimension() == 0) {
                                player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "You sense a strange interdimensional force, ... OH NO!! They ... are here ... giving me deja vus already."));
                                player.connection.sendPacket(
                                        new SPacketSoundEffect(
                                                SRPSounds.DODSIII,
                                                SoundCategory.MASTER,
                                                player.posX,
                                                player.posY,
                                                player.posZ,
                                                1.0F,
                                                1.0F
                                        )
                                );
                            } else if (player.world.provider.getDimension() == ModConfigManager.targetDimension) {
                                player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "You sense a strange interdimensional force, ... OH NO!! The dimension seems to be leaking... Where are they going??? Kinda giving me flashbacks."));
                                player.connection.sendPacket(
                                        new SPacketSoundEffect(
                                                SRPSounds.ANCIENT_POD,
                                                SoundCategory.MASTER,
                                                player.posX,
                                                player.posY,
                                                player.posZ,
                                                1.0F,
                                                1.0F
                                        )
                                );
                            }
                        }
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
