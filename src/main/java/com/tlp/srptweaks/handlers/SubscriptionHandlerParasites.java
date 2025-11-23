package com.tlp.srptweaks.handlers;

import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.tlp.srptweaks.util.InvasionSaveData;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SubscriptionHandlerParasites {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        long time = world.getTotalWorldTime();
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
