package cn.nirvana.nRedLantern;

import cn.nirvana.nRedLantern.client.ClientSetup;
import cn.nirvana.nRedLantern.registries.ModBlocks;
import cn.nirvana.nRedLantern.registries.ModCreativeTabs;
import cn.nirvana.nRedLantern.registries.ModItems;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(NRedLantern.MOD_ID)
public class NRedLantern {
    public static final String MOD_ID = "n_redlantern";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public NRedLantern(IEventBus modEventBus) {
        // 将 ModBlocks 和 ModItems 的注册器添加到 MOD 事件总线中
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        // 新增：手动注册 ClientSetup 类到事件总线
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.register(ClientSetup.class);
        }

        // 添加模组加载完成的日志输出
        LOGGER.info("N-Redstone Lantern mod has been loaded successfully!");
    }
}