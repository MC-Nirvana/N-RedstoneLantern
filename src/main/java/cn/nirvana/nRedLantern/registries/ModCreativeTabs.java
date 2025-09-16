package cn.nirvana.nRedLantern.registries;

import cn.nirvana.nRedLantern.NRedLantern;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.function.Supplier;

@EventBusSubscriber(modid = NRedLantern.MOD_ID)
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NRedLantern.MOD_ID);

    public static final Supplier<CreativeModeTab> N_RED_LANTERN_TAB = CREATIVE_MODE_TABS.register("n_redlantern_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + NRedLantern.MOD_ID + ".n_redlantern_tab"))
            .icon(() -> ModItems.REDSTONE_LANTERN_ITEM.get().getDefaultInstance())
            .displayItems((displayFeatures, output) -> {
                output.accept(ModItems.REDSTONE_LANTERN_ITEM.get());
                output.accept(ModItems.REDSTONE_CHAIN_ITEM.get());
            })
            .build());

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        // 使用 getTabKey() 与 ResourceKey 进行比较
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            // 使用 get() 方法获取实际的 Item 实例
            event.accept(ModItems.REDSTONE_LANTERN_ITEM.get());
            event.accept(ModItems.REDSTONE_CHAIN_ITEM.get());
        }
    }
}