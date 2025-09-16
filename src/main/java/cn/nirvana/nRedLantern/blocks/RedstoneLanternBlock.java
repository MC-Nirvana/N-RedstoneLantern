package cn.nirvana.nRedLantern.blocks;

import cn.nirvana.nRedLantern.registries.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneLanternBlock extends LanternBlock {
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public RedstoneLanternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0).setValue(LIT, false).setValue(HANGING, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER, LIT);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }
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
            setPowerState(state, level, pos, shouldPower(state, level, pos));
        }
    }

    public void setPowerState(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, boolean isPowered) {
        level.setBlock(pos, state.setValue(LIT, isPowered).setValue(POWER, isPowered ? 15 : 0), 2);
    }

    public boolean shouldPower(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        boolean isHanging = state.getValue(HANGING);

        if (isHanging) {
            // 悬挂时，检查上方
            BlockState aboveState = level.getBlockState(pos.above());
            // 检查上方是否是激活的红石锁链
            if (aboveState.is(ModBlocks.REDSTONE_CHAIN.get())) {
                return aboveState.getValue(RedstoneChainBlock.IS_POWERED);
            }
        }

        // 检查所有相邻方块中最大的红石信号
        return level.getBestNeighborSignal(pos) > 0;
    }
}