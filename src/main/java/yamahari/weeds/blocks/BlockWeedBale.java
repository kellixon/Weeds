package yamahari.weeds.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHay;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yamahari.weeds.Weeds;
import yamahari.weeds.config.Configuration;
import yamahari.weeds.lists.BlockList;

import java.util.Random;

public class BlockWeedBale extends BlockRotatedPillar {

    public static final PropertyInteger DRYING_STAGE = PropertyInteger.create("drying_stage", 0, 2);

    public BlockWeedBale() {
        super(Material.GRASS, MapColor.GREEN);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y).withProperty(DRYING_STAGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(0.5f);
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        entityIn.fall(fallDistance, 0.2F);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if(worldIn.canBlockSeeSky(pos.up()) && 0 <= worldIn.getWorldTime() && worldIn.getWorldTime() <= 12000) {
            if(rand.nextDouble() < Configuration.weed_bale_drying_chance) {
                if(!worldIn.isRemote) {
                    int age = state.getValue(DRYING_STAGE);
                    if (age != 2) {
                        worldIn.setBlockState(pos, state.withProperty(DRYING_STAGE, Integer.valueOf(age + 1)), 1 | 2);
                    } else {
                        worldIn.setBlockState(pos, BlockList.dried_weed_bale.getDefaultState().withProperty(BlockRotatedPillar.AXIS, state.getValue(BlockRotatedPillar.AXIS)), 1 | 2);
                    }
                }
            }
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, DRYING_STAGE);
    }
}
