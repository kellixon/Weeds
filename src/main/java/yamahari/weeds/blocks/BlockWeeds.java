package yamahari.weeds.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import yamahari.weeds.config.Configuration;
import yamahari.weeds.lists.BlockList;
import yamahari.weeds.lists.ItemList;

import java.util.Random;

public class BlockWeeds extends BlockCrops {
    @Override
    protected Item getSeed() {
        return ItemList.weed;
    }

    @Override
    protected Item getCrop() {
        return ItemList.weed;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == Blocks.FARMLAND || state.getBlock() == BlockList.dry_farmland;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        EnumFacing facing = EnumFacing.Plane.HORIZONTAL.random(worldIn.rand);
        BlockPos offset = pos.offset(facing);
        IBlockState blockState = worldIn.getBlockState(offset);
        Block block = blockState.getBlock();
        if(block instanceof BlockCrops || block instanceof BlockStem) {
           if(block != BlockList.weeds && ((IGrowable)block).canGrow(worldIn, offset, blockState, false)) {
               if(worldIn.rand.nextDouble() < Configuration.spread_weed_chance) {
                   if(!worldIn.isRemote) {
                       worldIn.setBlockState(offset, BlockList.weeds.getDefaultState(),2);
                   }
               }
           }
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }
}
