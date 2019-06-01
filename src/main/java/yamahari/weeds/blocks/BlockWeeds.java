package yamahari.weeds.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.Properties;
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
        return super.canSustainBush(state) || state.getBlock() == BlockList.dry_farmland;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        for(EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos offset = pos.offset(facing);
            IBlockState blockState = worldIn.getBlockState(offset);
            Block block = blockState.getBlock();

            if(block instanceof BlockCrops && block != BlockList.weeds
            && blockState.getValue(AGE) < ((BlockCrops) block).getMaxAge()) {
                float f = getGrowthChance(block, worldIn, offset);
                if(rand.nextInt((int)(100.0f / f) + 1) == 0) {
                    if(!worldIn.isRemote) {
                        worldIn.setBlockState(offset, BlockList.weeds.getDefaultState());
                    }
                }
            }
        }
    }
}
