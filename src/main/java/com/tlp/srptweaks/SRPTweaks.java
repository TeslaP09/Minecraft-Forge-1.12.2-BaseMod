package com.tlp.srptweaks;

import com.tlp.srptweaks.handlers.SubscriptionHandlerParasites;
import com.tlp.srptweaks.util.ModConfigManager;
import com.tlp.srptweaks.util.Resource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

@Mod(modid = Resource.MOD_ID, name = Resource.NAME, version = Resource.VERSION, dependencies = Resource.DEPENDENCIES)
public class SRPTweaks {

    @Instance
    public static SRPTweaks instance;
    private static Logger logger;

    /** This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.*/
    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("PreInit");
    }

    /** This is the second initialization event. Register custom recipes*/
    @EventHandler
    public static void init(FMLInitializationEvent event) {
        logger.info("Init");
        logger.info("Saving / loading mod configuration");
        // we need to call our function here, in order to execute the save / load
        ModConfigManager.SRPTweaksConfig();
        ModConfigManager.createClassList();
        logger.info("Finished saving / loading mod configuration");
        if (Loader.isModLoaded("srparasites")) {
            MinecraftForge.EVENT_BUS.register(new SubscriptionHandlerParasites());
        }
    }

    /** This is the final initialization event. Register actions from other mods here*/
    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        logger.info("PostInit");
    }
}