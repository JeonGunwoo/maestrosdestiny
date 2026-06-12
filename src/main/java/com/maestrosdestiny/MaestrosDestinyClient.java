package com.maestrosdestiny;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 클라이언트 전용 진입점. 전용 서버에서는 로드되지 않으므로
 * 여기서 클라이언트 측 코드에 접근하는 것은 안전하다 (CLAUDE.md §3-2).
 *
 * <p>Phase 0: 빈 모드. 클라이언트 로드 확인용.</p>
 */
@Mod(value = MaestrosDestiny.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MaestrosDestiny.MODID, value = Dist.CLIENT)
public class MaestrosDestinyClient {
    public MaestrosDestinyClient(ModContainer container) {
        // Phase 0: 등록할 클라이언트 확장 지점 없음.
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MaestrosDestiny.LOGGER.info("Maestro's Destiny: client setup complete (Phase 0).");
    }
}
