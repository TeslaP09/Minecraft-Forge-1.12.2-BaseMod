package com.tlp.srptweaks.mixin;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.WasteLeaves;
import com.hbm.config.GeneralConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.handler.RadiationSystemNT;
import com.hbm.handler.RadiationSystemNT.RadPocket;
import com.hbm.handler.RadiationWorldHandler;
import com.hbm.main.MainRegistry;
import com.hbm.saveddata.RadiationSaveStructure;
import com.hbm.saveddata.RadiationSavedData;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

import java.util.Map.Entry;

import java.util.Collection;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

@Pseudo
@Mixin(RadiationWorldHandler.class)
public class MixinHBMRadiationWorldHandler {
    static {
        debugPrint("MixinHBMRadiationWorldHandler loaded!");
    }

    /**
     * Adds a config override option to hardcoded blocks that get converted on getting irradiated.
     * @author Tesla_P
     * @reason There is no other way to define irradiation of modded Blocks
     */
    @Overwrite(remap = false)
    public static void handleWorldDestruction(World world) {

        debugPrint("Called handleWorldDestruction();");

        //TODO fix this up for new radiation system
        if(!(world instanceof WorldServer))
            return;
        if(!RadiationConfig.worldRadEffects || !GeneralConfig.enableRads)
            return;

        int count = 50;//MainRegistry.worldRad;
        int threshold = 5;//MainRegistry.worldRadThreshold;

        if(GeneralConfig.advancedRadiation) {
            if(GeneralConfig.enableDebugMode) {
                MainRegistry.logger.info("[Debug] Starting world destruction processing");
            }

            Collection<RadPocket> activePockets = RadiationSystemNT.getActiveCollection(world);
            if(activePockets.isEmpty())
                return;
            int randIdx = world.rand.nextInt(activePockets.size());
            int itr = 0;
            for(RadPocket p : activePockets){
                if(itr == randIdx){
                    if(p.radiation < threshold)
                        return;
                    BlockPos startPos = p.getSubChunkPos();
                    RadPocket[] pocketsByBlock = p.parent.pocketsByBlock;

                    for(int i = 0; i < 16; i ++){
                        for(int j = 0; j < 16; j ++){
                            for(int k = 0; k < 16; k ++){
                                if(world.rand.nextInt(3) != 0)
                                    continue;
                                if(pocketsByBlock != null && pocketsByBlock[i*16*16+j*16+k] != p){
                                    continue;
                                }
                                BlockPos pos = startPos.add(i, j, k);
                                IBlockState b = world.getBlockState(pos);
                                Block bblock = b.getBlock();

                                if(!world.isAirBlock(pos)){
                                    for (String string : ModConfigManager.falloutReplacementsString) {
                                        String[] replacement = string.replace("\"", "").split(",");
                                        ResourceLocation toReplace = new ResourceLocation(replacement[0]);
                                        ResourceLocation replaceWith = new ResourceLocation(replacement[1]);

                                        debugPrint("Going through falloutReplacementString, replace " + toReplace + " with " + replaceWith + " at " + Block.REGISTRY.getNameForObject(bblock));
                                        debugPrint(toReplace.toString());
                                        debugPrint(Block.REGISTRY.getNameForObject(bblock).toString());

                                        if (Block.REGISTRY.getNameForObject(bblock).equals(toReplace)) {
                                            world.setBlockState(pos, Block.REGISTRY.getObject(replaceWith).getDefaultState());
                                            debugPrint("Radiation block replacement match found");
                                            return;
                                        }
                                    }

                                    if(bblock == Blocks.GRASS) {
                                        world.setBlockState(pos, ModBlocks.waste_earth.getDefaultState());

                                    } else if(bblock == Blocks.DIRT || bblock == Blocks.FARMLAND) {
                                        world.setBlockState(pos, ModBlocks.waste_dirt.getDefaultState());
                                    } else if(bblock == Blocks.SANDSTONE) {
                                        world.setBlockState(pos, ModBlocks.waste_sandstone.getDefaultState());
                                    } else if(bblock == Blocks.RED_SANDSTONE) {
                                        world.setBlockState(pos, ModBlocks.waste_sandstone_red.getDefaultState());
                                    } else if(bblock == Blocks.HARDENED_CLAY || bblock == Blocks.STAINED_HARDENED_CLAY) {
                                        world.setBlockState(pos, ModBlocks.waste_terracotta.getDefaultState());
                                    } else if(bblock == Blocks.SAND) {
                                        BlockSand.EnumType meta = b.getValue(BlockSand.VARIANT);
                                        world.setBlockState(pos, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_sand.getDefaultState() : ModBlocks.waste_sand_red.getDefaultState());
                                    } else if(bblock == Blocks.GRAVEL) {
                                        world.setBlockState(pos, ModBlocks.waste_gravel.getDefaultState());

                                    } else if(bblock == Blocks.MYCELIUM) {
                                        world.setBlockState(pos, ModBlocks.waste_mycelium.getDefaultState());

                                    } else if(bblock instanceof BlockSnow) {
                                        world.setBlockState(pos, ModBlocks.waste_snow.getDefaultState());

                                    } else if(bblock instanceof BlockSnowBlock) {
                                        world.setBlockState(pos, ModBlocks.waste_snow_block.getDefaultState());

                                    } else if(bblock instanceof BlockIce) {
                                        world.setBlockState(pos, ModBlocks.waste_ice.getDefaultState());

                                    } else if(bblock instanceof BlockBush) {
                                        world.setBlockState(pos, ModBlocks.waste_grass_tall.getDefaultState());

                                    } else if(bblock == ModBlocks.waste_leaves) {
                                        if(world.rand.nextInt(8) == 0) {
                                            world.setBlockToAir(pos);
                                        }

                                    } else if(bblock instanceof BlockLeaves && !(bblock instanceof WasteLeaves)) {
                                        BlockLeaves bLeaf = (BlockLeaves) bblock;
                                        BlockPlanks.EnumType type = null;
                                        try {
                                            type = bLeaf.getWoodType(bLeaf.getMetaFromState(b));
                                        } catch(UnsupportedOperationException ignored) {
                                            //TK bag programming catch
                                        }
                                        if(type == null) type = BlockPlanks.EnumType.OAK;
                                        world.setBlockState(pos, ModBlocks.waste_leaves.getDefaultState().withProperty(WasteLeaves.VARIANT, type));
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                itr ++;
            }
            if(GeneralConfig.enableDebugMode) {
                MainRegistry.logger.info("[Debug] Finished world destruction processing");
            }
            return;
        }

        WorldServer serv = (WorldServer)world;

        RadiationSavedData data = RadiationSavedData.getData(serv);
        ChunkProviderServer provider = (ChunkProviderServer) serv.getChunkProvider();

        Object[] entries = data.contamination.entrySet().toArray();

        if(entries.length == 0)
            return;

        Entry<ChunkPos, RadiationSaveStructure> randEnt = (Entry<ChunkPos, RadiationSaveStructure>) entries[world.rand.nextInt(entries.length)];

        ChunkPos coords = randEnt.getKey();


        if(randEnt == null || randEnt.getValue().radiation < threshold)
            return;

        if(provider.chunkExists(coords.x, coords.z)) {

            for(int a = 0; a < 16; a ++) {
                for(int b = 0; b < 16; b ++) {

                    if(world.rand.nextInt(3) != 0)
                        continue;

                    int x = coords.getXStart() + a;
                    int z = coords.getZStart() + b;
                    int y = world.getHeight(x, z) - world.rand.nextInt(2);
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState c = world.getBlockState(pos);
                    Block bblock = c.getBlock();

                    if(!world.isAirBlock(pos)){
                        for (String string : ModConfigManager.falloutReplacementsString) {
                            String[] replacement = string.replace("\"", "").split(",");
                            ResourceLocation toReplace = new ResourceLocation(replacement[0]);
                            ResourceLocation replaceWith = new ResourceLocation(replacement[1]);

                            debugPrint("Going through falloutReplacementString, replace " + toReplace + " with " + replaceWith + " at " + Block.REGISTRY.getNameForObject(bblock));
                            debugPrint(toReplace.toString());
                            debugPrint(Block.REGISTRY.getNameForObject(bblock).toString());

                            if (Block.REGISTRY.getNameForObject(bblock).equals(toReplace)) {
                                world.setBlockState(pos, Block.REGISTRY.getObject(replaceWith).getDefaultState());
                                debugPrint("Radiation block replacement match found");
                                return;
                            }
                        }

                        if(bblock == Blocks.GRASS) {
                            world.setBlockState(pos, ModBlocks.waste_earth.getDefaultState());

                        } else if(bblock == Blocks.DIRT) {
                            world.setBlockState(pos, ModBlocks.waste_dirt.getDefaultState());

                        } else if(bblock == Blocks.SAND) {
                            BlockSand.EnumType meta = c.getValue(BlockSand.VARIANT);
                            if(world.rand.nextInt(60) == 0) {
                                world.setBlockState(pos, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_trinitite.getDefaultState() : ModBlocks.waste_trinitite_red.getDefaultState());
                            } else {
                                world.setBlockState(pos, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_sand.getDefaultState() : ModBlocks.waste_sand_red.getDefaultState());
                            }
                        } else if(bblock == Blocks.SANDSTONE) {
                            world.setBlockState(pos, ModBlocks.waste_sandstone.getDefaultState());
                        } else if(bblock == Blocks.RED_SANDSTONE) {
                            world.setBlockState(pos, ModBlocks.waste_sandstone_red.getDefaultState());
                        } else if(bblock == Blocks.HARDENED_CLAY || bblock == Blocks.STAINED_HARDENED_CLAY) {
                            world.setBlockState(pos, ModBlocks.waste_terracotta.getDefaultState());
                        } else if(bblock == Blocks.GRAVEL) {
                            world.setBlockState(pos, ModBlocks.waste_gravel.getDefaultState());

                        } else if(bblock == Blocks.MYCELIUM) {
                            world.setBlockState(pos, ModBlocks.waste_mycelium.getDefaultState());

                        } else if(bblock instanceof BlockSnow) {
                            world.setBlockState(pos, ModBlocks.waste_snow.getDefaultState());

                        } else if(bblock instanceof BlockSnowBlock) {
                            world.setBlockState(pos, ModBlocks.waste_snow_block.getDefaultState());

                        } else if(bblock instanceof BlockIce) {
                            world.setBlockState(pos, ModBlocks.waste_ice.getDefaultState());

                        } else if(bblock instanceof BlockBush) {
                            world.setBlockState(pos, ModBlocks.waste_grass_tall.getDefaultState());

                        } else if(bblock == ModBlocks.waste_leaves) {
                            if(world.rand.nextInt(8) == 0) {
                                world.setBlockToAir(pos);
                            }

                        } else if(bblock instanceof BlockLeaves && !(bblock instanceof WasteLeaves)) {
                            BlockLeaves bLeaf = (BlockLeaves) bblock;
                            BlockPlanks.EnumType type = null;
                            try {
                                type = bLeaf.getWoodType(bLeaf.getMetaFromState(c));
                            } catch(UnsupportedOperationException ignored) {
                                //TK bag programming catch
                            }
                            if(type == null) type = BlockPlanks.EnumType.OAK;
                            world.setBlockState(pos, ModBlocks.waste_leaves.getDefaultState().withProperty(WasteLeaves.VARIANT, type));
                        }
                    }
                }
            }
        }
    }
}