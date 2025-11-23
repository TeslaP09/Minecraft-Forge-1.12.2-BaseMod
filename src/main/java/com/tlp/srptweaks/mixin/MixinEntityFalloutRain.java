package com.tlp.srptweaks.mixin;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.WasteLeaves;
import com.hbm.blocks.generic.WasteLog;
import com.hbm.config.VersatileConfig;
import com.hbm.entity.effect.EntityFalloutRain;
import com.tlp.srptweaks.util.ModConfigManager;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;

import java.util.Random;

import static com.tlp.srptweaks.util.ModConfigManager.debugPrint;

@Pseudo
@Mixin(EntityFalloutRain.class)
public abstract class MixinEntityFalloutRain {
    static {
        debugPrint("MixinEntityFalloutRain loaded!");
    }

    @Shadow(remap = false) private int fallingRadius;
    @Shadow(remap = false) private double s0;
    @Shadow(remap = false) private double s1;
    @Shadow(remap = false) private double s2;
    @Shadow(remap = false) private double s3;
    @Shadow(remap = false) private double s4;
    @Shadow(remap = false) private double s5;
    @Shadow(remap = false) private double s6;
    @Shadow(remap = false) private double s7;

    @Shadow(remap = false) public abstract int getMaxStoneDepth(double dist);
    @Shadow(remap = false) public abstract void placeBlockFromDist(double dist, Block b, BlockPos pos);

    @Unique
    private static final Random rand_a = new Random();

    /**
     * Adds a config override option to hardcoded blocks that get converted on getting irradiated.
     * @author Tesla_P
     * @reason There is no other way to define irradiation of modded Blocks
     */
    @Overwrite(remap = false)
    private int[] doFallout(BlockPos.MutableBlockPos pos, double dist){

        debugPrint("Called doFallout();");

        World world = ((Entity) (Object) this).world;

        int stoneDepth = 0;
        int maxStoneDepth =getMaxStoneDepth(dist);

        boolean lastReachedStone = false;
        boolean reachedStone = false;
        int contactHeight = 420;
        int lastGapHeight = 420;
        boolean gapFound = false;

        IBlockState b;
        Block bblock;
        Material bmaterial;
        for(int y = 255; y >= 0; y--) {
            pos.setY(y);
            b = world.getBlockState(pos);
            bblock = b.getBlock();
            bmaterial = b.getMaterial();
            lastReachedStone = reachedStone;

            if(bblock != Blocks.AIR && contactHeight == 420)
                contactHeight = Math.min(y+1, 255);

            if(reachedStone && bmaterial != Material.AIR){
                stoneDepth++;
            } else {
                reachedStone = b.getMaterial() == Material.ROCK;
            }
            if(reachedStone && stoneDepth > maxStoneDepth){
                break;
            }

            if(bmaterial == Material.AIR || bmaterial.isLiquid()){
                if(y < contactHeight){
                    gapFound = true;
                    lastGapHeight = y;
                }
                continue;
            }



            if(bblock == Blocks.BEDROCK || bblock == ModBlocks.ore_bedrock_oil || bblock == ModBlocks.ore_bedrock_block){
                if(world.isAirBlock(pos.up())) world.setBlockState(pos.up(), ModBlocks.toxic_block.getDefaultState());
                break;
            }

            if(y == contactHeight-1 && bblock != ModBlocks.fallout && Math.abs(rand_a.nextGaussian() * (dist * dist) / (s0 * s0)) < 0.05 && rand_a.nextDouble() < 0.05 && ModBlocks.fallout.canPlaceBlockAt(world, pos.up())) {
                placeBlockFromDist(dist, ModBlocks.fallout, pos.up());
            }

            if(bblock == ModBlocks.waste_leaves){
                if(!(dist > s1 || (dist > fallingRadius && (world.rand.nextFloat() < (-5F*(fallingRadius/dist)+5F))))){
                    world.setBlockToAir(pos);
                }
                continue;
            }

            for (String string : ModConfigManager.falloutReplacementsString) {
                String[] replacement = string.replace("\"", "").split(",");
                ResourceLocation toReplace = new ResourceLocation(replacement[0]);
                ResourceLocation replaceWith = new ResourceLocation(replacement[1]);

                debugPrint("Going through falloutReplacementString, replace " + toReplace + " with " + replaceWith + " at " + Block.REGISTRY.getNameForObject(bblock));
                debugPrint(toReplace.toString());
                debugPrint(Block.REGISTRY.getNameForObject(bblock).toString());

                if (Block.REGISTRY.getNameForObject(bblock).equals(toReplace)) {
                    placeBlockFromDist(dist, Block.REGISTRY.getObject(replaceWith), pos);
                    debugPrint("Radiation block replacement match found");
                    break;
                }
            }

            if(bblock instanceof BlockLeaves && !(bblock instanceof WasteLeaves)) {
                BlockLeaves bLeaf = (BlockLeaves) bblock;
                if(dist > s1 || (dist > fallingRadius && (world.rand.nextFloat() < (-5F*(fallingRadius/dist)+5F)))){
                    BlockPlanks.EnumType type = null;
                    try {
                        type = bLeaf.getWoodType(bLeaf.getMetaFromState(b));
                    } catch(UnsupportedOperationException ignored) {
                        //TK bag programming catch
                    }
                    if(type == null) type = BlockPlanks.EnumType.OAK;
                    world.setBlockState(pos, ModBlocks.waste_leaves.getDefaultState().withProperty(WasteLeaves.VARIANT, type));
                } else {
                    world.setBlockToAir(pos);
                }
                continue;
            }

            if(bblock == Blocks.BROWN_MUSHROOM || bblock == Blocks.RED_MUSHROOM){
                if(dist < s0)
                    world.setBlockState(pos, ModBlocks.mush.getDefaultState());
                continue;
            }

            // if(b.getBlock() == Blocks.WATER) {
            // 	world.setBlockState(pos, ModBlocks.radwater_block.getDefaultState());
            // }

            if(bblock instanceof BlockOre && reachedStone && !lastReachedStone && dist < s1){
                world.setBlockState(pos, ModBlocks.toxic_block.getDefaultState());
                continue;
            }

            else if(bblock instanceof BlockStone || bblock == Blocks.COBBLESTONE) {
                double ranDist = dist * (1D + world.rand.nextDouble()*0.1D);
                if(ranDist > s1 || stoneDepth==maxStoneDepth)
                    world.setBlockState(pos, ModBlocks.sellafield_slaked.getStateFromMeta(world.rand.nextInt(4)));
                else if(ranDist > s2 || stoneDepth==maxStoneDepth-1)
                    world.setBlockState(pos, ModBlocks.sellafield_0.getStateFromMeta(world.rand.nextInt(4)));
                else if(ranDist > s3 || stoneDepth==maxStoneDepth-2)
                    world.setBlockState(pos, ModBlocks.sellafield_1.getStateFromMeta(world.rand.nextInt(4)));
                else if(ranDist > s4 || stoneDepth==maxStoneDepth-3)
                    world.setBlockState(pos, ModBlocks.sellafield_2.getStateFromMeta(world.rand.nextInt(4)));
                else if(ranDist > s5 || stoneDepth==maxStoneDepth-4)
                    world.setBlockState(pos, ModBlocks.sellafield_3.getStateFromMeta(world.rand.nextInt(4)));
                else if(ranDist > s6 || stoneDepth==maxStoneDepth-5)
                    world.setBlockState(pos, ModBlocks.sellafield_4.getStateFromMeta(world.rand.nextInt(4)));
                else if(ranDist <= s6 || stoneDepth==maxStoneDepth-6)
                    world.setBlockState(pos, ModBlocks.sellafield_core.getStateFromMeta(world.rand.nextInt(4)));
                else
                    break;
                continue;

            } else if(bblock instanceof BlockGrass) {
                placeBlockFromDist(dist, ModBlocks.waste_earth, pos);
                continue;

            } else if(bblock instanceof BlockGravel) {
                placeBlockFromDist(dist, ModBlocks.waste_gravel, pos);
                continue;

            } else if(bblock instanceof BlockDirt) {
                BlockDirt.DirtType meta = b.getValue(BlockDirt.VARIANT);
                if(meta == BlockDirt.DirtType.DIRT)
                    placeBlockFromDist(dist, ModBlocks.waste_dirt, pos);
                else if(meta == BlockDirt.DirtType.COARSE_DIRT)
                    placeBlockFromDist(dist, ModBlocks.waste_gravel, pos);
                else if(meta == BlockDirt.DirtType.PODZOL)
                    placeBlockFromDist(dist, ModBlocks.waste_mycelium, pos);
                continue;
            } else if(bblock == Blocks.FARMLAND) {
                placeBlockFromDist(dist, ModBlocks.waste_dirt, pos);
                continue;
            } else if(bblock instanceof BlockSnow) {
                placeBlockFromDist(dist, ModBlocks.waste_snow, pos);
                continue;

            } else if(bblock instanceof BlockSnowBlock) {
                placeBlockFromDist(dist, ModBlocks.waste_snow_block, pos);
                continue;

            } else if(bblock instanceof BlockIce) {
                world.setBlockState(pos, ModBlocks.waste_ice.getDefaultState());
                continue;

            } else if(bblock instanceof BlockBush) {
                if(world.getBlockState(pos.down()).getBlock() == Blocks.FARMLAND){
                    placeBlockFromDist(dist, ModBlocks.waste_dirt, pos.down());
                    placeBlockFromDist(dist, ModBlocks.waste_grass_tall, pos);
                } else if(world.getBlockState(pos.down()).getBlock() instanceof BlockGrass){
                    placeBlockFromDist(dist, ModBlocks.waste_earth, pos.down());
                    placeBlockFromDist(dist, ModBlocks.waste_grass_tall, pos);
                } else if(world.getBlockState(pos.down()).getBlock() == Blocks.MYCELIUM){
                    placeBlockFromDist(dist, ModBlocks.waste_mycelium, pos.down());
                    world.setBlockState(pos, ModBlocks.mush.getDefaultState());
                }
                continue;

            } else if(bblock == Blocks.MYCELIUM) {
                placeBlockFromDist(dist, ModBlocks.waste_mycelium, pos);
                continue;

            } else if(bblock == Blocks.SANDSTONE) {
                placeBlockFromDist(dist, ModBlocks.waste_sandstone, pos);
                continue;
            } else if(bblock == Blocks.RED_SANDSTONE) {
                placeBlockFromDist(dist, ModBlocks.waste_sandstone_red, pos);
                continue;
            } else if(bblock == Blocks.HARDENED_CLAY || bblock == Blocks.STAINED_HARDENED_CLAY) {
                placeBlockFromDist(dist, ModBlocks.waste_terracotta, pos);
                continue;
            } else if(bblock instanceof BlockSand) {
                BlockSand.EnumType meta = b.getValue(BlockSand.VARIANT);
                if(rand_a.nextInt(60) == 0) {
                    placeBlockFromDist(dist, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_trinitite : ModBlocks.waste_trinitite_red, pos);
                } else {
                    placeBlockFromDist(dist, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_sand : ModBlocks.waste_sand_red, pos);
                }
                continue;
            }

            else if(bblock == Blocks.CLAY) {
                world.setBlockState(pos, Blocks.HARDENED_CLAY.getDefaultState());
                continue;
            }

            else if(bblock == Blocks.MOSSY_COBBLESTONE) {
                world.setBlockState(pos, Blocks.COAL_ORE.getDefaultState());
                continue;
            }

            else if(bblock == Blocks.COAL_ORE) {
                if(dist < s5){
                    int ra = rand_a.nextInt(150);
                    if(ra < 7) {
                        world.setBlockState(pos, Blocks.DIAMOND_ORE.getDefaultState());
                    } else if(ra < 10) {
                        world.setBlockState(pos, Blocks.EMERALD_ORE.getDefaultState());
                    }
                }
                continue;
            }

            else if(bblock == Blocks.BROWN_MUSHROOM_BLOCK || bblock == Blocks.RED_MUSHROOM_BLOCK) {
                if(dist < s0){
                    BlockHugeMushroom.EnumType meta = b.getValue(BlockHugeMushroom.VARIANT);
                    if(meta == BlockHugeMushroom.EnumType.STEM) {
                        world.setBlockState(pos, ModBlocks.mush_block_stem.getDefaultState());
                    } else {
                        world.setBlockState(pos, ModBlocks.mush_block.getDefaultState());
                    }
                }
                continue;
            }

            else if(bblock instanceof BlockLog) {
                if(dist < s1)
                    world.setBlockState(pos, ((WasteLog)ModBlocks.waste_log).getSameRotationState(b));
                continue;
            }

            else if(bmaterial == Material.WOOD && bblock != ModBlocks.waste_log && bblock != ModBlocks.waste_planks) {
                if(dist < s1)
                    world.setBlockState(pos, ModBlocks.waste_planks.getDefaultState());
                continue;
            }
            else if(b.getBlock() == ModBlocks.sellafield_4) {
                world.setBlockState(pos, ModBlocks.sellafield_core.getStateFromMeta(world.rand.nextInt(4)));
                continue;
            }
            else if(b.getBlock() == ModBlocks.sellafield_3) {
                world.setBlockState(pos, ModBlocks.sellafield_4.getStateFromMeta(world.rand.nextInt(4)));
                continue;
            }
            else if(b.getBlock() == ModBlocks.sellafield_2) {
                world.setBlockState(pos, ModBlocks.sellafield_3.getStateFromMeta(world.rand.nextInt(4)));
                continue;
            }
            else if(b.getBlock() == ModBlocks.sellafield_1) {
                world.setBlockState(pos, ModBlocks.sellafield_2.getStateFromMeta(world.rand.nextInt(4)));
                continue;
            }
            else if(b.getBlock() == ModBlocks.sellafield_0) {
                world.setBlockState(pos, ModBlocks.sellafield_1.getStateFromMeta(world.rand.nextInt(4)));
                continue;
            }
            else if(b.getBlock() == ModBlocks.sellafield_slaked) {
                world.setBlockState(pos, ModBlocks.sellafield_0.getStateFromMeta(world.rand.nextInt(4)));
                continue;
            }
            else if(b.getBlock() == Blocks.VINE) {
                world.setBlockToAir(pos);
                continue;
            }
            else if(bblock == ModBlocks.ore_uranium) {
                if(dist <= s5){
                    if (rand_a.nextInt(VersatileConfig.getSchrabOreChance()) == 0 || dist < s7)
                        world.setBlockState(pos, ModBlocks.ore_schrabidium.getDefaultState());
                    else
                        world.setBlockState(pos, ModBlocks.ore_uranium_scorched.getDefaultState());
                }
                break;
            }

            else if(bblock == ModBlocks.ore_nether_uranium) {
                if(dist <= s5){
                    if(rand_a.nextInt(VersatileConfig.getSchrabOreChance()) == 0)
                        world.setBlockState(pos, ModBlocks.ore_nether_schrabidium.getDefaultState());
                    else
                        world.setBlockState(pos, ModBlocks.ore_nether_uranium_scorched.getDefaultState());
                }
                break;

            }

            else if(bblock == ModBlocks.ore_gneiss_uranium) {
                if(dist <= s4){
                    if(rand_a.nextInt(VersatileConfig.getSchrabOreChance()) == 0)
                        world.setBlockState(pos, ModBlocks.ore_gneiss_schrabidium.getDefaultState());
                    else
                        world.setBlockState(pos, ModBlocks.ore_gneiss_uranium_scorched.getDefaultState());
                }
                break;
                // this piece stops the "stomp" from reaching below ground
            }
            else if(bblock == ModBlocks.brick_concrete) {
                if(rand_a.nextInt(80) == 0)
                    world.setBlockState(pos, ModBlocks.brick_concrete_broken.getDefaultState());
                break;
                // this piece stops the "stomp" from reaching below ground
            }
            else if(bblock.getExplosionResistance(null) > 300){
                break;
            }
        }
        return new int[]{gapFound ? 1 : 0, lastGapHeight, contactHeight};

    }
}

