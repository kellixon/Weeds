package yamahari.weeds.blocks;

import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

public class BlockDryFarmland extends BlockFarmland implements IGrowable {
    public static final PropertyBool NUTRITION = PropertyBool.create("nutrition");

    public BlockDryFarmland() {
        super();
        this.setDefaultState(this.getDefaultState().withProperty(NUTRITION, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(0.6f);
        this.setResistance(0.6f);
        this.setSoundType(SoundType.GROUND);
        this.setTickRandomly(true);
    }



    public PropertyBool getNutrition() {
        return NUTRITION;
    }

    public boolean isNutrated(IBlockState state) {
        return state.getValue(this.getNutrition());
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return plantable.getPlantType(world, pos.offset(direction)) == EnumPlantType.Crop || plantable instanceof BlockStem;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MOISTURE, NUTRITION);
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return !this.isNutrated(state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return !this.isNutrated(state);
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        worldIn.setBlockState(pos, state.withProperty(NUTRITION, Boolean.valueOf(true)));
    }
}
