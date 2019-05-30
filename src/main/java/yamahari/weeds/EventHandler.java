package yamahari.weeds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAttachedStem;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yamahari.weeds.blocks.BlockDryFarmland;
import yamahari.weeds.entities.ai.EntityAIMakeSoil;
import yamahari.weeds.lists.BlockList;

import javax.swing.text.html.parser.Entity;

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
        IWorld world = event.getWorld();
        IBlockState state = event.getState();
        Block block = state.getBlock();
        BlockPos pos = event.getPos();

        event.setResult(Event.Result.DEFAULT);
        if(block instanceof BlockCrops || block instanceof BlockStem) {
            IBlockState soil = world.getBlockState(pos.down());
            Block soilBlock = soil.getBlock();
            if(soilBlock == BlockList.dry_farmland) {
                if(!soil.get(((BlockDryFarmland)soilBlock).getNutrition())) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCropGrowPost(final BlockEvent.CropGrowEvent.Post event) {
        IWorld world = event.getWorld();
        IBlockState blockState = event.getState();
        Block block = blockState.getBlock();
        BlockPos pos = event.getPos();

        if(block instanceof BlockCrops) {
            int age = blockState.get(((BlockCrops) block).getAgeProperty());
            int maxAge = ((BlockCrops) block).getMaxAge();
            if (age == maxAge) {
                IBlockState soil = world.getBlockState(pos.down());
                Block soilBlock = soil.getBlock();
                if (soilBlock == BlockList.dry_farmland) {
                    world.setBlockState(pos.down(), soil.with(((BlockDryFarmland) soilBlock).getNutrition(), Boolean.valueOf(false)), 1 | 2);
                }
            } else if (block != BlockList.weeds) {
                if (world.getRandom().nextInt(10) == 0) {
                    event.getWorld().setBlockState(pos, BlockList.weeds.getDefaultState().with(BlockStateProperties.AGE_0_7, (int)Math.floor(((float)age / (float)maxAge) * 7.f)), 1 | 2);
                }
            }
        } else if (block instanceof BlockStem) {
            if(blockState.get(BlockStateProperties.AGE_0_7) == 7) {
                IBlockState soil = world.getBlockState(pos.down());
                Block soilBlock = soil.getBlock();
                if (soilBlock == BlockList.dry_farmland) {
                    world.setBlockState(pos.down(), soil.with(((BlockDryFarmland) soilBlock).getNutrition(), Boolean.valueOf(false)), 1 | 2);
                }
            }
        } else if (block instanceof BlockAttachedStem) {
            IBlockState soil = world.getBlockState(pos.down());
            Block soilBlock = soil.getBlock();
            if (soilBlock == BlockList.dry_farmland) {
                world.setBlockState(pos.down(), soil.with(((BlockDryFarmland) soilBlock).getNutrition(), Boolean.valueOf(false)), 1 | 2);
            }
        }
        event.setResult(Event.Result.DEFAULT);
    }

    @SubscribeEvent
    public static void onUseHoeEvent(final UseHoeEvent event) {
        World world = event.getContext().getWorld();
        BlockPos pos = event.getContext().getPos();
        Block block = world.getBlockState(pos).getBlock();

        IBlockState blockState = null;
        if(block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.GRASS_PATH) {
            blockState = BlockList.dry_farmland.getDefaultState();
        } else if (block == Blocks.COARSE_DIRT) {
            blockState = Blocks.DIRT.getDefaultState();
        }

        if(blockState != null) {
            EntityPlayer entityPlayer = event.getContext().getPlayer();
            world.playSound(entityPlayer, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isRemote) {
                world.setBlockState(pos, blockState, 11);
                if (entityPlayer != null) {
                    event.getContext().getItem().damageItem(1, entityPlayer);
                }
            }
            event.setResult(Event.Result.ALLOW);
        } else {
            event.setResult(Event.Result.DENY);
            // event.setCanceled(true);
        }
        // event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        if(!event.getEntity().getEntityWorld().isRemote()) {
            if(event.getEntity() instanceof EntityPig) {
                EntityPig pig = (EntityPig)event.getEntity();
                pig.tasks.addTask(8, new EntityAIMakeSoil(pig));
            }
        }
    }
}
