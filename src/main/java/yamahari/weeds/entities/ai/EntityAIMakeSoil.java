package yamahari.weeds.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
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
        if(this.soilMakerEntity.getRNG().nextInt(this.soilMakerEntity.isChild() ? 50 : 100) == 0) {
            IBlockState blockState = this.entityWorld.getBlockState(new BlockPos(this.soilMakerEntity.posX, Math.ceil(this.soilMakerEntity.posY), this.soilMakerEntity.posZ).down());
            Block block = blockState.getBlock();
            return block == Blocks.GRASS_BLOCK
                    || block == Blocks.DIRT
                    || block == BlockList.dry_farmland
                    || block == Blocks.COARSE_DIRT;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        this.makingSoilTimer = 40;
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
            BlockPos pos = new BlockPos(this.soilMakerEntity.posX, Math.ceil(this.soilMakerEntity.posY), this.soilMakerEntity.posZ).down();
            IBlockState blockState = this.entityWorld.getBlockState(pos);

            if(ForgeEventFactory.getMobGriefingEvent(this.entityWorld, this.soilMakerEntity)) {
                if(blockState.getBlock() == Blocks.GRASS_BLOCK || blockState.getBlock() == Blocks.DIRT) {
                    this.entityWorld.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getStateId(blockState.getBlock().getDefaultState()));
                    this.entityWorld.setBlockState(pos, BlockList.dry_farmland.getDefaultState(), Constants.BlockFlags.NOTIFY_LISTENERS);
                }
                else if(blockState.getBlock() == Blocks.COARSE_DIRT) {
                    this.entityWorld.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getStateId(blockState.getBlock().getDefaultState()));
                    this.entityWorld.setBlockState(pos, Blocks.DIRT.getDefaultState(), Constants.BlockFlags.NOTIFY_LISTENERS);
                }
                else if(blockState.getBlock() == BlockList.dry_farmland && !blockState.get(((BlockDryFarmland) blockState.getBlock()).getNutrition())) {
                    this.entityWorld.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getStateId(blockState.getBlock().getDefaultState()));
                    this.entityWorld.setBlockState(pos, blockState.with(((BlockDryFarmland) blockState.getBlock()).getNutrition(), Boolean.valueOf(true)), Constants.BlockFlags.NOTIFY_LISTENERS);
                }
            }
        }
    }
}
