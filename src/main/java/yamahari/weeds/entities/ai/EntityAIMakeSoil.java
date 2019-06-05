package yamahari.weeds.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import yamahari.weeds.blocks.BlockDryFarmland;
import yamahari.weeds.config.Configuration;
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
        if(this.soilMakerEntity.getRNG().nextInt(100) < (this.soilMakerEntity.isChild() ? Configuration.pig_child_digging_chance : Configuration.pig_digging_chance)) {
            IBlockState blockState = this.entityWorld.getBlockState(new BlockPos(this.soilMakerEntity.posX, Math.ceil(this.soilMakerEntity.posY), this.soilMakerEntity.posZ).down());
            Block block = blockState.getBlock();
            return block == Blocks.GRASS
                    || block == Blocks.DIRT
                    || block == BlockList.dry_farmland;
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
    public void updateTask() {
        this.makingSoilTimer = Math.max(0, this.makingSoilTimer - 1);
        if(this.makingSoilTimer == 4) {
            BlockPos pos = new BlockPos(this.soilMakerEntity.posX, Math.ceil(this.soilMakerEntity.posY), this.soilMakerEntity.posZ).down();
            IBlockState blockState = this.entityWorld.getBlockState(pos);
            Block block = blockState.getBlock();

            if(ForgeEventFactory.getMobGriefingEvent(this.entityWorld, this.soilMakerEntity)) {
                if(block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
                    this.entityWorld.playEvent(2001, pos, Block.getIdFromBlock(block));
                    this.entityWorld.setBlockState(pos, BlockList.dry_farmland.getDefaultState(), 2);
                }
                else if(block == Blocks.DIRT) {
                    switch (blockState.getValue(BlockDirt.VARIANT))
                    {
                        case DIRT:
                            this.entityWorld.playEvent(2001, pos, Block.getIdFromBlock(block));
                            this.entityWorld.setBlockState(pos, BlockList.dry_farmland.getDefaultState(), 2);
                            break;
                        case COARSE_DIRT:
                            this.entityWorld.playEvent(2001, pos, Block.getIdFromBlock(block));
                            this.entityWorld.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
                            break;
                        default:
                            break;
                    }
                }
                else if(block == BlockList.dry_farmland && !blockState.getValue(((BlockDryFarmland) block).getNutrition())) {
                    this.entityWorld.playEvent(2001, pos, Block.getIdFromBlock(block));
                    this.entityWorld.setBlockState(pos, blockState.withProperty(((BlockDryFarmland) block).getNutrition(), Boolean.valueOf(true)), 2);
                }
            }
        }
    }
}
