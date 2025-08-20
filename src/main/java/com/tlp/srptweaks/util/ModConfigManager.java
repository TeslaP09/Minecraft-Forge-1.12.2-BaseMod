package com.tlp.srptweaks.util;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.File;

/**
 * This class is used to generate our config! to add a new configuration file,
 * simply copy "WorldGenerationConfig" and rename/modify it!
 */
public class ModConfigManager {

    // int / float / string / boolean variables we are using in our Config!
    public static boolean globalInvasionScenario;
    public static int sourceDimension;
    public static int minPoints;
    public static int setPhase;

    public static void SRPTweaksConfig(FMLInitializationEvent event) {
        // Creating a new Configuration file
        Configuration config = new Configuration(new File("config/srptweaks/srptweaks.cfg"));
        // function to load our config (very important!)
        config.load();
        // Adding items to our config with: Category, Name of the item, Default value
        globalInvasionScenario = config.get("Invasion", "Global Invasion Scenario", true, "Whether global invasion features are enabled").getBoolean();
        sourceDimension = config.get("Invasion", "Source Dimension", 997, "The dimension the invasion originates from, so the dimension to check for points and phases for global progress").getInt();
        minPoints = config.get("Invasion", "End Invasion Below Points", 0, "If points in Source Dimension are below this, set phase in every dimension to Set Phase To").getInt();
        setPhase = config.get("Invasion", "Set Phase To", -2, "If points in Source Dimension are below End Invasion Below Points, set phase in every dimension to this").getInt();
        // function to save our config (very important!)
        config.save();
    }
}
