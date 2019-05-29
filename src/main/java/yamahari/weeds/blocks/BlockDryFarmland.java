package yamahari.weeds.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import java.util.Random;

public class BlockDryFarmland extends BlockFarmland implements IGrowable {
    public static final BooleanProperty NUTRITION = BooleanProperty.create("nutrition");

    public BlockDryFarmland(Properties builder) {
        super(builder);
        this.setDefaultState(this.getDefaultState().with(NUTRITION, Boolean.valueOf(false)));
    }

    public BooleanProperty getNutrition() {
        return NUTRITION;
    }

    public boolean isNutrated(IBlockState state) {
        return state.get(this.getNutrition());
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return !this.isNutrated(state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return !this.isNutrated(state);
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        worldIn.setBlockState(pos, state.with(NUTRITION, Boolean.valueOf(true)));
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing, net.minecraftforge.common.IPlantable plantable) {
        return plantable.getPlantType(world, pos.offset(facing)) == EnumPlantType.Crop;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NUTRITION);
    }
}
