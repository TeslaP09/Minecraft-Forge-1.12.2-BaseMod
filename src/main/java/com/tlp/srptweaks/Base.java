package com.tlp.srptweaks;

import com.tlp.srptweaks.util.ModConfigManager;
import com.tlp.srptweaks.util.Resource;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = Resource.MOD_ID, name = Resource.NAME, version = Resource.VERSION)
public class Base {

    @Instance
    public static Base instance;
    private static Logger logger; // used to print messages to our console output

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
        ModConfigManager.SRPTweaksConfig(event);
        logger.info("Finished saving / loading mod configuration");
    }

    /** This is the final initialization event. Register actions from other mods here*/
    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
    }
}