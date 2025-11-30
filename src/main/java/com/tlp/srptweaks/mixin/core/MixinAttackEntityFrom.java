package com.tlp.srptweaks.mixin.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.tlp.srptweaks.util.ModConfigManager.debugAttack;
import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

@Mixin(net.minecraft.entity.EntityLivingBase.class)
public class MixinAttackEntityFrom {
    static {
        debugPrint("MixinAttackEntityFrom loaded!");
    }

    @Inject(method="func_70097_a", at = @At("HEAD"), remap = true)
    public void injectAttackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (debugAttack) {
            EntityLivingBase target = (EntityLivingBase) (Object) this;

            System.out.println("=== ATTACK DEBUG ===");
            System.out.println("DamageSource: " + source);
            System.out.println("DamageType: " + source.getDamageType());
            System.out.println("Amount: " + amount);
            System.out.println("Target: " + target.getName());

            // Check if it's an EntityDamageSource to get the attacker
            if (source instanceof EntityDamageSource) {
                EntityDamageSource entitySource = (EntityDamageSource) source;
                Entity attacker = entitySource.getTrueSource();
                if (attacker != null) {
                    System.out.println("Attacker: " + attacker.getName());
                }
                Entity immediateSource = entitySource.getImmediateSource();
                if (immediateSource != null && immediateSource != attacker) {
                    System.out.println("Immediate Source: " + immediateSource.getName());
                }
            }

            // Only print stack trace for the problematic 1.0 damage to avoid spam
            if (amount == 1.0f && source.toString().contains("61e795d2")) {
                System.out.println("=== STACK TRACE FOR 1.0 DAMAGE ===");
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                for (int i = 0; i < Math.min(8, stack.length); i++) {
                    System.out.println("  " + stack[i]);
                }
            }
            System.out.println("====================");
        }
    }

}
