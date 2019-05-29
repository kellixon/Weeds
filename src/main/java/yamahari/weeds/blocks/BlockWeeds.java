package yamahari.weeds.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yamahari.weeds.lists.BlockList;
import yamahari.weeds.lists.ItemList;

import java.util.Random;

public class BlockWeeds extends BlockCrops {
    public BlockWeeds(Properties builder) {
        super(builder);
    }

    protected IItemProvider getSeedsItem() { return ItemList.weed; }

    protected IItemProvider getCropsItem() { return ItemList.weed; }

    public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
        super.tick(state, worldIn, pos, random);
        for(EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos offset = pos.offset(facing);
            IBlockState blockState = worldIn.getBlockState(offset);
            Block block = blockState.getBlock();
            if(block instanceof BlockCrops && block != BlockList.weeds
                    && blockState.get(((BlockCrops)block).getAgeProperty()) < ((BlockCrops)block).getMaxAge()) {
                float f = getGrowthChance(block, worldIn, offset);
                if(random.nextInt((int)(100.0F / f) + 1) == 0) {
                    worldIn.setBlockState(offset, BlockList.weeds.getDefaultState());

                }
            }
        }
    }
}
