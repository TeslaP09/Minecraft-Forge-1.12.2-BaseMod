package com.tlp.srptweaks.util;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;

/**
 * To add a new configuration file,
 * simply copy "SRPTweaksConfig" and rename/modify it!
 */
public class ModConfigManager {

    public static boolean globalInvasionScenario;

    public static int sourceDimension;
    public static int targetDimension;
    public static int minPoints;
    public static int minPhase;
    public static int setPhase;
    public static int setPoints;
    public static int invasionToOverworldTime;
    public static boolean debug;
    public static boolean debugAttack;

    public static String[] parasiteWeaponsString = new String[]{};
    public static String[] parasiteWeaponsClass = new String[]{
            "com.existingeevee.swparasites.items.ItemBucklerShield",
            "com.existingeevee.swparasites.items.ItemImpalerShield",
            "com.existingeevee.swparasites.items.ItemLongBlade",
            "com.existingeevee.swparasites.items.ItemParasiteBoomerang",
            "com.existingeevee.swparasites.items.ItemParasiteCrossbow",
            "com.existingeevee.swparasites.items.ItemParasiteCrossbowNocube",
            "com.existingeevee.swparasites.items.ItemParasiteDagger",
            "com.existingeevee.swparasites.items.ItemParasiteGauntlet",
            "com.existingeevee.swparasites.items.ItemParasiteJavelin",
            "com.existingeevee.swparasites.items.ItemParasiteThrowingAxe",
            "com.existingeevee.swparasites.items.ItemParasiteThrowingKnife",
            "com.existingeevee.swparasites.items.ItemVilePlate",

            "com.dhanantry.scapeandrunparasites.item.tool.WeaponToolMeleeBase",
            "com.dhanantry.scapeandrunparasites.item.tool.WeaponToolRangeBase",

            "com.existingeevee.nocubesrptweaks.items.ItemTwistedMalletReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemTwistedGreatAxeReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemTwistedBowReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemPestilentMiasmReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemGoreRapierReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemGoreHatchetReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemGoreCombatBowReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemCarapaceShellbreakerReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemHostTentacleReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemBolsterClawReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemEvolutionAxeReplacement",
            "com.existingeevee.nocubesrptweaks.items.ItemEvolutionBowReplacement",

            "net.mcreator.nocubessrparmory.item.ItemCarapaceBroadsword$1",
            "net.mcreator.nocubessrparmory.item.ItemCarapaceShellbreaker$1",
            "net.mcreator.nocubessrparmory.item.ItemEvolutionAxe$1",
            "net.mcreator.nocubessrparmory.item.ItemEvolutionBow$1",
            "net.mcreator.nocubessrparmory.item.ItemEvolutionKnife$1",
            "net.mcreator.nocubessrparmory.item.ItemEvolutionSickle$1",
            "net.mcreator.nocubessrpsurvival.item.ItemFleshEater$1",
            "net.mcreator.nocubessrparmory.item.ItemGoreCombatBow$1",
            "net.mcreator.nocubessrparmory.item.ItemGoreHatchet$1",
            "net.mcreator.nocubessrparmory.item.ItemGoreRapier$1",
            "net.mcreator.nocubessrparmory.item.ItemHostTentacle$1",
            "net.mcreator.nocubessrparmory.item.ItemMimicBladeGreen$1",
            "net.mcreator.nocubessrparmory.item.ItemMimicBladeYellow$1",
            "net.mcreator.nocubessrparmory.item.ItemMimicBladePurple$1",
            "net.mcreator.nocubessrparmory.item.ItemMimicBladeRed$1",
            "net.mcreator.nocubessrparmory.item.ItemPestilentKnife$1",
            "net.mcreator.nocubessrparmory.item.ItemPestilentScythe$1",
            "net.mcreator.nocubessrparmory.item.ItemPestilentShuriken$RangedItem",
            "net.mcreator.nocubessrparmory.item.ItemTwistedDagger$1",
            "net.mcreator.nocubessrparmory.item.ItemTheReaper$1",
            "net.mcreator.nocubessrparmory.item.ItemTheReaperTrue$1"
    };
    public static ArrayList<Class<?>> parasiteWeaponsClassList = new ArrayList<>();
    public static boolean globalBedSpawnOverridesDimension;
    public static boolean disableDynamicLightsAR;
    public static int reduce;
    public static double reduceMulti;
    public static boolean onlyPlayerSource;
    public static int tempMode;
    public static double biomeDebuffTemp;
    public static double biomeOverheatTemp;
    public static double environmentDebuffTemp;
    public static double environmentOverheatTemp;
    public static double overheatDamage;
    public static String[] tempDebuffsString = new String[]{
            "minecraft:slowness,1",
            "minecraft:weakness,1",
    };
    public static String[] tempOverheatDebuffsString = new String[]{
            "minecraft:slowness,3",
            "minecraft:weakness,3",
            "srparasites:antimall,1",
    };
    public static String[] falloutReplacementsString = new String[]{
            "desirepaths:grass_worn_1,hbm:waste_earth",
            "desirepaths:grass_worn_2,hbm:waste_earth",
            "desirepaths:grass_worn_3,hbm:waste_earth",
            "desirepaths:grass_worn_4,hbm:waste_earth",
            "desirepaths:grass_worn_5,hbm:waste_dirt",
            "desirepaths:grass_worn_6,hbm:waste_dirt",
    };
    public static String[] preventItemUse = new String[]{};

    public static void SRPTweaksConfig() {
        Configuration config = new Configuration(new File("config/srptweaks/srptweaks.cfg"));
        config.load();
        globalInvasionScenario = config.get("Invasion", "Global Invasion Scenario", true, "Whether global invasion features are enabled").getBoolean();
        sourceDimension = config.get("Invasion", "Source Dimension", 997, "The dimension the invasion originates from, so the dimension to check for points and phases for global progress").getInt();
        targetDimension = config.get("Invasion", "Target Dimension", 111, "The first dimension the invasion targeted, should be equal to the config value set in SRPMeteorConfig.cfg").getInt();
        minPoints = config.get("Invasion", "End Invasion Below Points", 0, "If points in Source Dimension are below/equal this, set phase in every dimension to Set Phase To and points to Set Points To").getInt();
        minPhase = config.get("Invasion", "End Invasion Below Phase", -1, "If phase in Source Dimension is below/equal this, set phase in every dimension to Set Phase To and points to Set Points To, -3 to disable").getInt();
        setPhase = config.get("Invasion", "Set Phase To", -2, "If points in Source Dimension are below/equal End Invasion Below Points, set phase in every dimension to this and points to Set Points To").getInt();
        setPoints = config.get("Invasion", "Set Points To", -400, "If points in Source Dimension are below/equal End Invasion Below Points, set phase in every dimension to Set Phase To and points to this").getInt();
        invasionToOverworldTime = config.get("Invasion", "Invasion To Overworld Time", 2688000, "After this amount of ticks, the invasion will leak from Target Dimension into the overworld").getInt();
        debug = config.get("Debug", "Debug Mode", false, "If things aren't working correctly, debug mode will print out what the mod's doing at any time").getBoolean();
        debugAttack = config.get("Debug", "Debug Attacks", false, "If things aren't working correctly, print out debug about entities being attacked").getBoolean();
        globalBedSpawnOverridesDimension = config.get("Tweaks", "Global Bed Spawn Overrides Dimension", true, "If player can respawn in dim B but has a bed in dim A, should he respawn in dim A (tweaked, true) or in dim B at world spawn (vanilla, false)").getBoolean();
        disableDynamicLightsAR = config.get("Tweaks", "Disable Dynamic lights on AR Planets", true, "Dynamically disables dynamic lights by Atomic Stryker for items that are not waterproof according to the DL config on planets that have their atmosphere defined as low/no oxygen.").getBoolean();
        onlyPlayerSource = config.get("Tweaks", "Only Players As Source", true, "If true, only reduce damage if it comes from a player, so not e.g. a zombie, this should improve performance").getBoolean();
        reduce = config.get("Tweaks", "Reduce Damage Mode", 1, "If damaged entity is not parasite and damage source item in Parasite Weapons, 0 = Do nothing, 1 = Reduce damage, 2 = Cancel damage event").getInt();
        reduceMulti = config.get("Tweaks", "Reduce Multiplier", 0.3D, "If Reduce damage == 1, multiply damage by this").getDouble();
        tempMode = config.get("Tweaks", "Parasite Temperature Mode", 2, "Apply debuffs to parasite when temp is higher than, 0 = Do nothing, 1 = Vanilla Biome Temp, 2 = Simple Difficulty Environmental Temp if it is installed").getInt();
        biomeDebuffTemp = config.get("Tweaks", "Biome Debuff Temperature", 1.5D, "If Parasite Temperature Mode == 1 and Biome Temp is above this, apply Temperature Debuff Effects to parasites").getDouble();
        biomeOverheatTemp = config.get("Tweaks", "Biome Overheat Temperature", 2.0D, "If Parasite Temperature Mode == 1 and Biome Temp is above this, apply Temperature Overheating Debuff Effects to parasites").getDouble();
        environmentDebuffTemp = config.get("Tweaks", "Environmental Debuff Temperature", 18.0D, "If Parasite Temperature Mode == 2 and Environmental Temp is above this, apply Temperature Debuff Effects to parasites").getDouble();
        environmentOverheatTemp = config.get("Tweaks", "Environmental Overheat Temperature", 25.0D, "If Parasite Temperature Mode == 2 and Environmental Temp is above this, apply Temperature Overheating Debuff Effects to parasites").getDouble();
        overheatDamage = config.get("Tweaks", "Damage From Overheating", 1.0D, "If Parasite Temperature Mode == 1 or 2 and the temperature is above the overheating temperature threshold, apply this amount of damage every second while applying potion effects").getDouble();
        tempDebuffsString = config.getStringList("Temperature Debuff Effects" ,"Tweaks" , tempDebuffsString, "List of effects with amplifiers applied to parasites as a temperature penalty, temp check every five seconds");
        tempOverheatDebuffsString = config.getStringList("Temperature Overheat Debuff Effects" ,"Tweaks" , tempOverheatDebuffsString, "List of effects with amplifiers applied to parasites as a temperature overheating penalty, temp check every five seconds");
        parasiteWeaponsString = config.getStringList("Parasitic Weapons Strings" ,"Tweaks" , parasiteWeaponsString, "List of resource ids of weapons that are considered parasitic weapons and will thus deal less damage to non parasitic entities");
        parasiteWeaponsClass = config.getStringList("Parasitic Weapons Class Names" ,"Tweaks" , parasiteWeaponsClass, "List of class names of weapons that are considered parasitic weapons and will thus deal less damage to non parasitic entities");
        falloutReplacementsString = config.getStringList("HBM NTM Radioactive Fallout Replacement" ,"HBM" , falloutReplacementsString, "List of blocks that get replaced by the second block when radioactive fallout happens.");
        preventItemUse = config.getStringList("Prevent Item Use" ,"Tweaks" , preventItemUse, "List of resource ids of items that have their right click functionality removed.");
        config.save();
    }

    public static void createClassList() {
        for(String className : parasiteWeaponsClass) {
            try {
                parasiteWeaponsClassList.add(Class.forName(className));
            } catch (ClassNotFoundException classNotFoundException) {
                System.out.println("Class not found: " + className);
            }
        }
    }

    public static boolean isParasiteWeaponClass(Item item) {
        if (item != null) {
            for (Class<?> clazz : parasiteWeaponsClassList) {
                if (clazz.isInstance(item)) {
                    debugPrint(item.getClass().getName() + " matches");
                    return true;
                }
            }
            debugPrint(item.getClass().getName() + " doesn't match");
        }
        return false;

    }

    public static boolean isParasiteWeaponRid(Item item) {
        if (item != null) {
            ResourceLocation rl = item.getRegistryName();
            if (rl != null) {
                for (String string : parasiteWeaponsString) {
                    if (string.matches(rl.toString())) {
                        debugPrint(rl + " matches");
                        return true;
                    }
                }
            }
            debugPrint(rl + " doesn't match");
        }
        return false;
    }

    public static void debugPrint(String string) {
        if (debug) {
            System.out.println(string);
        }
    }
}
