package yamahari.weeds;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yamahari.weeds.blocks.BlockDryFarmland;
import yamahari.weeds.entities.ai.EntityAIMakeSoil;
import yamahari.weeds.lists.BlockList;

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
            if(soilBlock == BlockList.dry_farmland && !soil.getValue(BlockDryFarmland.NUTRITION)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onUseHoeEvent(final UseHoeEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        IBlockState state = null;

        if(block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
            state = BlockList.dry_farmland.getDefaultState();
        }
        else if(block == Blocks.DIRT) {
            switch (blockState.getValue(BlockDirt.VARIANT))
            {
                case DIRT:
                    state = BlockList.dry_farmland.getDefaultState();
                    break;
                case COARSE_DIRT:
                    state = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT);
                    break;
                default:
                    break;
            }
        }
        if(state != null) {
            EntityPlayer entityPlayer = event.getEntityPlayer();
            world.playSound(entityPlayer, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isRemote) {
                world.setBlockState(pos, state, 11);
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
        BlockPos blockPos = event.getPos();

        if(block instanceof BlockCrops || block instanceof BlockStem) {
            if(!((IGrowable)block).canGrow(world, blockPos, blockState, false)) {
                denutrate(world, blockPos);
            }
            else if(block != BlockList.weeds && world.rand.nextInt(16) == 0) {
                world.setBlockState(blockPos, BlockList.weeds.getDefaultState(), 1 | 2);
            }
        } else if (block instanceof BlockMelon || block instanceof BlockPumpkin) {
            for(EnumFacing facing : EnumFacing.HORIZONTALS) {
                BlockPos pos = blockPos.offset(facing.getOpposite());
                if(world.getBlockState(pos).getBlock() instanceof BlockStem) {
                    denutrate(world, pos);
                    break;
                }
            }
        }
    }

    private static void denutrate(World world, BlockPos blockPos) {
        IBlockState soil = world.getBlockState(blockPos.down());
        if (soil.getBlock() == BlockList.dry_farmland && soil.getValue(BlockDryFarmland.NUTRITION)) {
            if (!world.isRemote) {
                world.setBlockState(blockPos.down(), soil.withProperty(BlockDryFarmland.NUTRITION, Boolean.valueOf(false)), 1 | 2);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if(!entity.getEntityWorld().isRemote) {
            if(entity instanceof EntityPig) {
                ((EntityPig) entity).tasks.addTask(9, new EntityAIMakeSoil((EntityPig)entity));
            }
        }
    }
}
