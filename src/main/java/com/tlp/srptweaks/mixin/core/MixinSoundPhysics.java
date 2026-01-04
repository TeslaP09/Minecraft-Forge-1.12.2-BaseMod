package com.tlp.srptweaks.mixin.core;

import com.sonicether.soundphysics.SoundPhysics;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = SoundPhysics.class, priority = 1001)
public abstract class MixinSoundPhysics {

    @Unique
    private static final Logger LOGGER = LogManager.getLogger("SRPTweaks");

    @Unique
    private static Object2FloatOpenHashMap<ResourceLocation> dsVolumeControl = null;

    @Unique
    private static boolean dsInitialized = false;

    // ThreadLocal to store sound name per thread (audio is multithreaded!)
    @Unique
    private static final ThreadLocal<String> currentSoundName = ThreadLocal.withInitial(() -> "");

    @Unique
    private static synchronized void ensureInitialized() {
        if (dsInitialized) return;

        try {
            Class<?> registryManagerClass = Class.forName("org.orecruncher.dsurround.registry.RegistryManager");
            Object soundRegistry = registryManagerClass.getField("SOUND").get(null);

            if (soundRegistry != null) {
                // Use reflection to access the volumeControl map
                java.lang.reflect.Field volumeControlField =
                        soundRegistry.getClass().getDeclaredField("volumeControl");
                volumeControlField.setAccessible(true);
                dsVolumeControl = (Object2FloatOpenHashMap<ResourceLocation>) volumeControlField.get(soundRegistry);
                dsInitialized = true;
                LOGGER.debug("[SRPTweaks] Dynamic Surroundings integration ready");

                // DEBUG: Log some entries
                if (dsVolumeControl != null) {
                    int nonDefaultCount = 0;
                    for (Object2FloatOpenHashMap.Entry<ResourceLocation> entry : dsVolumeControl.object2FloatEntrySet()) {
                        if (Math.abs(entry.getFloatValue() - 1.0f) > 0.001f) {
                            nonDefaultCount++;
                            if (nonDefaultCount <= 5) {
                                LOGGER.debug("[SRPTweaks] DS Config: {} = {}", entry.getKey(), entry.getFloatValue());
                            }
                        }
                    }
                    LOGGER.debug("[SRPTweaks] DS has {} non-default volume entries", nonDefaultCount);
                }
            } else {
                LOGGER.warn("[SRPTweaks] DS SoundRegistry is null");
                dsInitialized = true;
            }
        } catch (ClassNotFoundException e) {
            LOGGER.debug("[SRPTweaks] Dynamic Surroundings not installed");
            dsInitialized = true;
        } catch (Exception e) {
            LOGGER.error("[SRPTweaks] Failed to initialize DS integration: {}", e.getMessage());
            e.printStackTrace();
            dsInitialized = true;
        }
    }

    @Inject(method = "evaluateEnvironment", at = @At("HEAD"), remap = false)
    private static void onEvaluateEnvironmentStart(int sourceID, float posX, float posY, float posZ,
                                                   SoundCategory category, String name,
                                                   ISound.AttenuationType attType, CallbackInfo ci) {
        currentSoundName.set(name);

        ensureInitialized();
    }

    @ModifyArgs(method = "evaluateEnvironment",
            at = @At(value = "INVOKE",
                    target = "Lcom/sonicether/soundphysics/SoundPhysics;setEnvironment(IFFFFFFFFFFF)V"), remap = false)
    private static void modifyMainEnvironmentArgs(Args args) {
        String soundName = currentSoundName.get();

        if (!dsInitialized || dsVolumeControl == null || soundName == null || soundName.isEmpty()) {
            return;
        }

        float dsMultiplier = getDynamicSurroundingsVolumeMultiplier(soundName);

        if (Math.abs(dsMultiplier - 1.0f) > 0.1f) {
            LOGGER.debug("[SRPTweaks] Applying realistic DS multiplier {} to {}", dsMultiplier, soundName);

            float originalDirectGain = args.get(10);
            float originalDirectCutoff = args.get(9);
            float originalAirAbsorption = args.get(11);

            if (dsMultiplier > 1.0f) {
                // LOUDER SOUND physics:
                // 1. More gain (if headroom available)
                // 2. Better penetration (less filtering)
                // 3. Less air absorption (travels further)
                // 4. Reverb scales WITH volume (same proportion)

                // 1. Gain: Increase but respect OpenAL 1.0 limit
                // At source: 1.0 * 4.0 = 4.0 → clamp to 1.0 (no change)
                // Behind block: 0.5 * 4.0 = 2.0 → clamp to 1.0 (can increase!)
                float newDirectGain = originalDirectGain * dsMultiplier;
                newDirectGain = Math.min(1.0f, newDirectGain);
                args.set(10, newDirectGain);

                // 2. Penetration: Louder sounds penetrate obstacles better
                // At source: 1.0 * 4.0 = 4.0 → clamp to 1.0 (no change)
                // Behind block: 0.3 * 4.0 = 1.2 → clamp to 1.0 (less filtering!)
                float newDirectCutoff = originalDirectCutoff * dsMultiplier * dsMultiplier;
                newDirectCutoff = Math.min(1.0f, newDirectCutoff);
                args.set(9, newDirectCutoff);

                // 3. Air absorption: Louder sounds lose less high-freq over distance
                // Higher multiplier = LESS absorption (inverse relationship)
                // airAbsorption=2.0, multiplier=4.0 → 2.0 / 4.0 = 0.5 (less absorption)
                float newAirAbsorption = originalAirAbsorption / dsMultiplier;
                newAirAbsorption = Math.max(0.0f, newAirAbsorption); // Can't be negative
                args.set(11, newAirAbsorption);

                // 4. Reverb: Scale WITH volume (same proportion of reverb to direct)
                // sendGain0-3 (indices 1-4): multiply by dsMultiplier
                for (int i = 1; i <= 4; i++) {
                    float sendGain = args.get(i);
                    float newSendGain = sendGain * dsMultiplier;
                    newSendGain = Math.min(1.0f, newSendGain); // Clamp to 1.0
                    args.set(i, newSendGain);
                }

                LOGGER.debug("[SRPTweaks] Louder: Gain {}->{}, Cutoff {}->{}, AirAbs {}->{}",
                        originalDirectGain, newDirectGain,
                        originalDirectCutoff, newDirectCutoff,
                        originalAirAbsorption, newAirAbsorption);

            } else if (dsMultiplier < 1.0f) {
                // QUIETER SOUND physics:
                // 1. Less gain
                // 2. Worse penetration (more filtering)
                // 3. More air absorption (loses highs faster)
                // 4. Reverb scales DOWN with volume

                // 1. Gain: Direct reduction (safe since multiplier < 1.0)
                float newDirectGain = originalDirectGain * dsMultiplier;
                args.set(10, newDirectGain);

                // 2. Penetration: Quieter sounds penetrate worse
                float newDirectCutoff = originalDirectCutoff * dsMultiplier;
                args.set(9, newDirectCutoff);

                // 3. Air absorption: Quieter sounds lose highs faster
                float newAirAbsorption = originalAirAbsorption / dsMultiplier;
                newAirAbsorption = Math.min(10.0f, newAirAbsorption); // OpenAL max
                args.set(11, newAirAbsorption);

                // 4. Reverb: Scale down with volume
                for (int i = 1; i <= 4; i++) {
                    float sendGain = args.get(i);
                    float newSendGain = sendGain * dsMultiplier;
                    args.set(i, newSendGain);
                }

                LOGGER.debug("[SRPTweaks] Quieter: Gain {}->{}, Cutoff {}->{}, AirAbs {}->{}",
                        originalDirectGain, newDirectGain,
                        originalDirectCutoff, newDirectCutoff,
                        originalAirAbsorption, newAirAbsorption);
            }
        }

        currentSoundName.remove();
    }

    @Unique
    private static float getDynamicSurroundingsVolumeMultiplier(String soundName) {
        try {
            String resourceString;
            if (soundName.contains("|")) {
                resourceString = soundName.split("\\|")[0];
            } else {
                resourceString = soundName;
            }

            ResourceLocation resource = new ResourceLocation(resourceString);

            LOGGER.debug("[SRPTweaks] Looking up DS multiplier for: {}", resource);

            float multiplier = dsVolumeControl.getFloat(resource);

            LOGGER.debug("[SRPTweaks] Found multiplier: {} for {}", multiplier, resource);

            return Math.max(0.0f, Math.min(100.0f, multiplier));

        } catch (Exception e) {
            LOGGER.warn("[SRPTweaks] Error getting multiplier for {}: {}", soundName, e.getMessage());
            return 1.0f;
        }
    }
}