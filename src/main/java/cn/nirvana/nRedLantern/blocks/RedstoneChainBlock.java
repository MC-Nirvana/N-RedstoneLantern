package cn.nirvana.nRedLantern.blocks;

import cn.nirvana.nRedLantern.registries.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import org.jetbrains.annotations.NotNull;

public class RedstoneChainBlock extends ChainBlock {
    public static final BooleanProperty IS_POWERED = BooleanProperty.create("is_powered");

    public RedstoneChainBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_POWERED, false).setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_POWERED);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide) {
            // 当邻近方块发生变化时，安排一次刻度更新
            level.scheduleTick(pos, this, 1);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.isClientSide) {
            setPowerState(state, level, pos, shouldPower(level, pos));
        }
    }

    public boolean shouldPower(@NotNull Level level, @NotNull BlockPos pos) {
        // 如果上方是红石锁链，检查其激活状态
        BlockState aboveState = level.getBlockState(pos.above());
        if (aboveState.is(ModBlocks.REDSTONE_CHAIN.get())) {
            return aboveState.getValue(RedstoneChainBlock.IS_POWERED);
        }

        // 否则，检查所有相邻方块中最大的红石信号
        return level.getBestNeighborSignal(pos) > 0;
    }

    public void setPowerState(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, boolean poweredFromAbove) {
        boolean currentState = state.getValue(IS_POWERED);
        if (currentState == poweredFromAbove) {
            return;
        }

        level.setBlock(pos, state.setValue(IS_POWERED, poweredFromAbove), 2);

        // 向下传递信号
        Direction.Axis axis = state.getValue(AXIS);
        BlockPos otherEndPos;

        if (axis == Direction.Axis.Y) {
            otherEndPos = pos.below();
        } else if (axis == Direction.Axis.X) {
            otherEndPos = pos.east();
        } else { // axis == Direction.Axis.Z
            otherEndPos = pos.south();
        }

        BlockState otherEndState = level.getBlockState(otherEndPos);
        if (otherEndState.is(ModBlocks.REDSTONE_CHAIN.get())) {
            // 使用 scheduleTick 而不是直接调用方法
            level.scheduleTick(otherEndPos, otherEndState.getBlock(), 1);
        }
    }
}