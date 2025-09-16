package cn.nirvana.nRedLantern.registries;

import cn.nirvana.nRedLantern.NRedLantern;
import cn.nirvana.nRedLantern.blocks.RedstoneChainBlock;
import cn.nirvana.nRedLantern.blocks.RedstoneLanternBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(NRedLantern.MOD_ID);

    public static final Supplier<Block> REDSTONE_LANTERN = BLOCKS.register("redstone_lantern", () -> new RedstoneLanternBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_BLACK)
            .strength(3.5F)
            .sound(SoundType.LANTERN)
            .lightLevel(state -> state.getValue(RedstoneLanternBlock.LIT) ? 15 : 0)));

    public static final Supplier<Block> REDSTONE_CHAIN = BLOCKS.register("redstone_chain", () -> new RedstoneChainBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(5.0F, 6.0F)
            .sound(SoundType.CHAIN)
            .lightLevel(state -> state.getValue(RedstoneChainBlock.IS_POWERED) ? 7 : 0)
            .noOcclusion()));
}