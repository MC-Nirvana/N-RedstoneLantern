package cn.nirvana.nRedLantern.registries;

import cn.nirvana.nRedLantern.NRedLantern;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(NRedLantern.MOD_ID);

    public static final Supplier<Item> REDSTONE_LANTERN_ITEM = ITEMS.register("redstone_lantern", () -> new BlockItem(ModBlocks.REDSTONE_LANTERN.get(), new Item.Properties()));

    public static final Supplier<Item> REDSTONE_CHAIN_ITEM = ITEMS.register("redstone_chain", () -> new BlockItem(ModBlocks.REDSTONE_CHAIN.get(), new Item.Properties()));
}