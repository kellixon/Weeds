package yamahari.weeds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemHoe;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yamahari.weeds.blocks.BlockDryFarmland;
import yamahari.weeds.lists.BlockList;

import java.util.Random;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onBoneMealEvent(final BonemealEvent event) {
        if(event.getBlock().getBlock() != BlockList.dry_farmland) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
        }
        else {
            event.setResult(Event.Result.DEFAULT);
        }
    }

    @SubscribeEvent
    public static void onCropGrowPre(final BlockEvent.CropGrowEvent.Pre event) {
        World world = event.getWorld();
        IBlockState state = event.getState();
        Block block = state.getBlock();
        BlockPos pos = event.getPos();

        event.setResult(Event.Result.DEFAULT);
        if(block instanceof BlockCrops || block instanceof BlockStem) {
            IBlockState soil = world.getBlockState(pos.down());
            Block soilBlock = soil.getBlock();
            if(soilBlock == BlockList.dry_farmland) {
                if(!soil.getValue(((BlockDryFarmland)soilBlock).getNutrition())) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onUseHoeEvent(final UseHoeEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();

        IBlockState blockState = null;

        if(block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
            blockState = BlockList.dry_farmland.getDefaultState();
        }
        else if(block == Blocks.DIRT) {
            switch ((BlockDirt.DirtType)blockState.getValue(BlockDirt.VARIANT))
            {
                case DIRT:
                    blockState = BlockList.dry_farmland.getDefaultState();
                    break;
                case COARSE_DIRT:
                    blockState = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT);
            }
        }
        if(blockState != null) {
            EntityPlayer entityPlayer = event.getEntityPlayer();
            world.playSound(entityPlayer, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isRemote) {
                world.setBlockState(pos, blockState, 11);
                if (entityPlayer != null) {
                    event.getCurrent().damageItem(1, entityPlayer);
                }
            }
            event.setResult(Event.Result.ALLOW);
        } else {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onCropGrowPost(final BlockEvent.CropGrowEvent.Post event) {
        World world = event.getWorld();
        IBlockState blockState = event.getState();
        Block block = blockState.getBlock();
        BlockPos pos = event.getPos();

        if(block instanceof BlockCrops) {
            int age = blockState.getValue(((BlockCrops) block).AGE);
            int maxAge = ((BlockCrops) block).getMaxAge();
            if (age == maxAge) {
                IBlockState soil = world.getBlockState(pos.down());
                Block soilBlock = soil.getBlock();
                if (soilBlock == BlockList.dry_farmland) {
                    if(!world.isRemote)
                        world.setBlockState(pos.down(), soil.withProperty(((BlockDryFarmland) soilBlock).getNutrition(), Boolean.valueOf(false)),1 | 2);
                }
            } else if (block != BlockList.weeds) {
                if (world.rand.nextInt(10) == 0) {
                    if(!world.isRemote)
                        world.setBlockState(pos, BlockList.weeds.getDefaultState().withProperty(((BlockCrops) block).AGE, (int)Math.floor(((float)age / (float)maxAge) * 7.f)), 1 | 2);
                }
            }
        } else if (block instanceof BlockStem) {
            if (blockState.getValue(((BlockStem) block).AGE) == 7) {
                IBlockState soil = world.getBlockState(pos.down());
                Block soilBlock = soil.getBlock();
                if (soilBlock == BlockList.dry_farmland) {
                    if(!world.isRemote)
                        world.setBlockState(pos.down(), soil.withProperty(((BlockDryFarmland) soilBlock).getNutrition(), Boolean.valueOf(false)), 1 | 2);
                }
            }
        }
        event.setResult(Event.Result.ALLOW);
    }
}

/*
@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onFarmlandTrample(final BlockEvent.FarmlandTrampleEvent event) {
        if(event.getEntity() instanceof EntityPig) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        if(!event.getEntity().getEntityWorld().isRemote()) {
            if(event.getEntity() instanceof EntityPig) {
                EntityPig pig = (EntityPig)event.getEntity();
                pig.tasks.addTask(9, new EntityAIMakeSoil(pig));
            }
        }
    }
}*/
