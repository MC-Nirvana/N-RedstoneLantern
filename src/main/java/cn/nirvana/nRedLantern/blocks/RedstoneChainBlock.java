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
    // IS_POWERED 属性，表示锁链是否通电
    public static final BooleanProperty IS_POWERED = BooleanProperty.create("is_powered");
    // POWER 属性，表示红石信号强度，为了兼容红石线而保留
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
            // 方块放置时，立即安排一个tick，检查其状态
            level.scheduleTick(pos, this, 1);
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide) {
            // 当相邻方块发生变化时，安排一个tick，重新检查状态
            level.scheduleTick(pos, this, 1);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.isClientSide) {
            // 在tick时，根据 shouldPower 的结果更新状态
            setPowerState(state, level, pos, shouldPower(level, pos));
        }
    }

    /**
     * 检查方块是否应该通电
     */
    public boolean shouldPower(@NotNull Level level, @NotNull BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());

        // 检查上方是否是通电的红石锁链
        boolean isPoweredFromAboveChain = aboveState.is(ModBlocks.REDSTONE_CHAIN.get()) && aboveState.getValue(IS_POWERED);

        // 检查上方方块是否能提供任何红石信号（包括红石块、拉杆、红石火把等）
        // 使用 level.getSignal 来获取信号，这个方法会更全面地检测信号源
        boolean hasSignalFromAbove = level.getSignal(pos.above(), Direction.DOWN) > 0;

        return isPoweredFromAboveChain || hasSignalFromAbove;
    }

    /**
     * 更新方块的通电状态
     */
    public void setPowerState(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, boolean isPowered) {
        boolean currentState = state.getValue(IS_POWERED);
        if (currentState == isPowered) {
            return;
        }

        level.setBlock(pos, state.setValue(IS_POWERED, isPowered).setValue(POWER, isPowered ? 15 : 0), 2);

        BlockPos belowPos = pos.below();

        // 无论锁链是激活还是失活，都通知下方的方块
        // 这样可以确保信号断开时，锁链也能正确熄灭，并传递信号给下方方块
        level.updateNeighborsAt(belowPos, this);
        level.scheduleTick(belowPos, level.getBlockState(belowPos).getBlock(), 1);
    }

    /**
     * 返回方块输出的红石信号强度
     */
    public int getSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        // 只有当方块通电且信号方向为下方时，才输出15级信号
        return state.getValue(IS_POWERED) && direction == Direction.DOWN ? 15 : 0;
    }

    /**
     * 返回方块输出的强充能信号强度
     * 返回0，防止其强充能旁边的方块
     */
    public int getDirectSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        return 0;
    }

    /**
     * 返回方块发出的光照级别
     */
    public int getLightEmission(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockAndTintGetter world, @NotNull BlockPos pos) {
        // 当锁链通电时，发出7级光
        return state.getValue(IS_POWERED) ? 7 : 0;
    }
}