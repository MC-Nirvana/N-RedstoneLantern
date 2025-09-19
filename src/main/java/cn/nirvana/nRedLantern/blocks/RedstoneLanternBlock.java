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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.BlockAndTintGetter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneLanternBlock extends LanternBlock {
    // LIT 属性，用于表示灯笼是否点亮
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public RedstoneLanternBlock(Properties properties) {
        super(properties);
        // 设置默认状态：未点亮，非悬挂，非水下
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(HANGING, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
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
            setPowerState(state, level, pos, shouldPower(state, level, pos));
        }
    }

    /**
     * 根据灯笼的悬挂状态来判断是否接收到红石信号
     */
    public boolean shouldPower(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        boolean isHanging = state.getValue(HANGING);

        if (isHanging) {
            // 如果是悬挂状态，检查正上方是否有红石信号
            BlockState aboveState = level.getBlockState(pos.above());
            // 检查上方是否是激活的红石锁链或能提供红石充能的方块
            boolean poweredByChain = aboveState.is(ModBlocks.REDSTONE_CHAIN.get()) && aboveState.getValue(RedstoneChainBlock.IS_POWERED);
            boolean poweredByBlock = level.getSignal(pos.above(), Direction.DOWN) > 0;
            return poweredByChain || poweredByBlock;
        }

        // 如果是放置在地面上，检查正下方是否有红石信号
        return level.getSignal(pos.below(), Direction.UP) > 0;
    }

    /**
     * 更新灯笼的点亮状态
     */
    public void setPowerState(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, boolean isPowered) {
        // 如果当前状态与目标状态相同，则不进行更新
        if (state.getValue(LIT) == isPowered) {
            return;
        }
        // 更新 LIT 属性
        level.setBlock(pos, state.setValue(LIT, isPowered), 2);
    }

    /**
     * 返回方块发出的光照级别
     */
    public int getLightEmission(@NotNull BlockState state, @NotNull BlockAndTintGetter world, @NotNull BlockPos pos) {
        // 如果灯笼点亮，发出15级光
        return state.getValue(LIT) ? 15 : 0;
    }
}