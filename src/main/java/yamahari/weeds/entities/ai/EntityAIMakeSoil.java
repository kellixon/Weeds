package yamahari.weeds.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import yamahari.weeds.blocks.BlockDryFarmland;
import yamahari.weeds.lists.BlockList;

public class EntityAIMakeSoil extends EntityAIBase {
    private final EntityLiving soilMakerEntity;

    private final World entityWorld;

    private int makingSoilTimer;

    public EntityAIMakeSoil(EntityLiving soilMakerEntity) {
        this.soilMakerEntity = soilMakerEntity;
        this.entityWorld = soilMakerEntity.world;
        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        if(this.soilMakerEntity.getRNG().nextInt(this.soilMakerEntity.isChild() ? 50 : 100) != 0) {
            return false;
        }
        else {
            IBlockState blockState = this.entityWorld.getBlockState(this.soilMakerEntity.getPosition().down());
            return blockState.getBlock() == Blocks.GRASS_BLOCK
                    || blockState.getBlock() == Blocks.DIRT
                    || blockState.getBlock() == BlockList.dry_farmland;
        }
    }

    @Override
    public void startExecuting() {
        this.makingSoilTimer = 40;
        this.entityWorld.setEntityState(this.soilMakerEntity, (byte)10);
        this.soilMakerEntity.getNavigator().clearPath();
    }

    @Override
    public void resetTask() {
        this.makingSoilTimer = 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.makingSoilTimer > 0;
    }

    public int getMakingSoilTimer() {
        return this.makingSoilTimer;
    }

    @Override
    public void tick() {
        this.makingSoilTimer = Math.max(0, this.makingSoilTimer - 1);
        if(this.makingSoilTimer == 4) {
            BlockPos pos = new BlockPos(this.soilMakerEntity).down();
            IBlockState blockState = this.entityWorld.getBlockState(pos);
            if(blockState.getBlock() == Blocks.GRASS_BLOCK
            || blockState.getBlock() == Blocks.DIRT) {
                if (ForgeEventFactory.getMobGriefingEvent(this.entityWorld, this.soilMakerEntity)) {
                    this.entityWorld.playEvent(2001, pos, Block.getStateId(blockState.getBlock().getDefaultState()));
                    this.entityWorld.setBlockState(pos, BlockList.dry_farmland.getDefaultState(), 2);
                }
            } else if(blockState.getBlock() == BlockList.dry_farmland) {
                if(!blockState.get(((BlockDryFarmland)blockState.getBlock()).getNutrition())) {
                    if (ForgeEventFactory.getMobGriefingEvent(this.entityWorld, this.soilMakerEntity)) {
                        this.entityWorld.playEvent(2001, pos, Block.getStateId(blockState.getBlock().getDefaultState()));
                        int moisture = blockState.get(BlockStateProperties.MOISTURE_0_7);
                        if (moisture > 0) {
                            this.entityWorld.setBlockState(pos, BlockList.dry_farmland.getDefaultState().with(((BlockDryFarmland) blockState.getBlock()).getNutrition(), Boolean.valueOf(true)).with(BlockStateProperties.MOISTURE_0_7, moisture), 2);
                        }
                        else {
                            this.entityWorld.setBlockState(pos, BlockList.dry_farmland.getDefaultState().with(((BlockDryFarmland) blockState.getBlock()).getNutrition(), Boolean.valueOf(true)), 2);
                        }
                    }
                }
            }
        }
    }
}
