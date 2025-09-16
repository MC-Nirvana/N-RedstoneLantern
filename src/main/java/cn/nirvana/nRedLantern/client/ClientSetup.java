package cn.nirvana.nRedLantern.client;

import cn.nirvana.nRedLantern.NRedLantern;
import cn.nirvana.nRedLantern.registries.ModBlocks;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = NRedLantern.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 使用 event.enqueueWork() 确保在正确的线程上执行渲染注册
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.REDSTONE_LANTERN.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.REDSTONE_CHAIN.get(), RenderType.cutout());
        });
    }
}
