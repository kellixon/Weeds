package yamahari.weeds.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import yamahari.weeds.Weeds;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

    public boolean hasNutrition(IBlockState state) {
        return state.get(this.getNutrition());
    }

    @Override
    public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
        Method hasWaterMethod = null;
        Method hasCropsMethod = null;
        try {
            hasWaterMethod = BlockFarmland.class.getDeclaredMethod("hasWater", new Class<?>[]{IWorldReaderBase.class, BlockPos.class});
            hasCropsMethod = BlockFarmland.class.getDeclaredMethod("hasCrops", new Class<?>[]{IBlockReader.class, BlockPos.class});
        }
        catch(Exception e) {
            Weeds.logger.error(e.getMessage());
        }
        hasWaterMethod.setAccessible(true);
        hasCropsMethod.setAccessible(true);
        boolean water = false;
        boolean crops = false;
        try {
            water = (boolean) hasWaterMethod.invoke(this, worldIn, pos);
            crops = (boolean) hasCropsMethod.invoke(this, worldIn, pos);
        }
        catch(Exception e) {
            Weeds.logger.error(e.getMessage());
        }

        if (!state.isValidPosition(worldIn, pos)) {
            turnToDirt(state, worldIn, pos);
        } else {
            int i = state.get(MOISTURE);
            if (!water && !worldIn.isRainingAt(pos.up())) {
                if (i > 0) {
                    worldIn.setBlockState(pos, state.with(MOISTURE, Integer.valueOf(i - 1)), 2);
                } else if (!crops && !this.hasNutrition(state)) {
                    turnToDirt(state, worldIn, pos);
                }
            } else if (i < 7) {
                worldIn.setBlockState(pos, state.with(MOISTURE, Integer.valueOf(7)), 2);
            }
        }
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return !this.hasNutrition(state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        worldIn.setBlockState(pos, state.with(NUTRITION, true));
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing facing, net.minecraftforge.common.IPlantable plantable) {
        IBlockState plant = plantable.getPlant(world, pos.offset(facing));
        net.minecraftforge.common.EnumPlantType type = plantable.getPlantType(world, pos.offset(facing));

        return type == EnumPlantType.Crop ? this.hasNutrition(state) : false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NUTRITION);
    }
}
