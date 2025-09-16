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
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import org.jetbrains.annotations.NotNull;

public class RedstoneChainBlock extends ChainBlock {
    public static final BooleanProperty IS_POWERED = BooleanProperty.create("is_powered");
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    public RedstoneChainBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_POWERED, false).setValue(AXIS, Direction.Axis.Y).setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_POWERED, POWER);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide) {
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
        BlockState aboveState = level.getBlockState(pos.above());

        boolean isPoweredFromAboveChain = aboveState.is(ModBlocks.REDSTONE_CHAIN.get()) && aboveState.getValue(IS_POWERED);
        boolean hasSignalFromAbove = aboveState.getSignal(level, pos.above(), Direction.DOWN) > 0;

        return isPoweredFromAboveChain || hasSignalFromAbove;
    }

    public void setPowerState(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, boolean isPowered) {
        boolean currentState = state.getValue(IS_POWERED);
        if (currentState == isPowered) {
            return;
        }

        level.setBlock(pos, state.setValue(IS_POWERED, isPowered).setValue(POWER, isPowered ? 15 : 0), 2);

        BlockPos belowPos = pos.below();

        // 无论锁链是激活还是失活，都通知下方的方块
        // 这样可以确保信号断开时，锁链也能正确熄灭
        level.updateNeighborsAt(belowPos, this);
        level.scheduleTick(belowPos, level.getBlockState(belowPos).getBlock(), 1);
    }

    public boolean isSignalSource(@NotNull BlockState state) {
        return state.getValue(IS_POWERED);
    }

    public int getSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        return state.getValue(IS_POWERED) && direction == Direction.DOWN ? 15 : 0;
    }

    public int getLightEmission(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockAndTintGetter world, @NotNull BlockPos pos) {
        return state.getValue(IS_POWERED) ? 7 : 0;
    }
}