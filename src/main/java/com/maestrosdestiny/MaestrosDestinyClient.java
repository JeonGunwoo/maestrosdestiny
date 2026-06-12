package com.maestrosdestiny;

import com.maestrosdestiny.client.ClientResourceHandlers;
import com.maestrosdestiny.client.ResourceHudLayer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 클라이언트 전용 진입점. 전용 서버에서는 로드되지 않으므로
 * 여기서 클라이언트 측 코드에 접근하는 것은 안전하다 (CLAUDE.md §3-2).
 */
@Mod(value = MaestrosDestiny.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MaestrosDestiny.MODID, value = Dist.CLIENT)
public class MaestrosDestinyClient {
    public MaestrosDestinyClient(ModContainer container, IEventBus modEventBus) {
        // 클라이언트 리소스 배선: S2C 수신 훅 + 자연회복 예측 (게임 이벤트 버스)
        ClientResourceHandlers.init(NeoForge.EVENT_BUS);

        // HUD 레이어 등록 (모드 버스). 리소스 정의 → 바 자동 생성.
        modEventBus.addListener(ResourceHudLayer::onRegisterGuiLayers);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MaestrosDestiny.LOGGER.info("Maestro's Destiny: client setup complete.");
    }
}
