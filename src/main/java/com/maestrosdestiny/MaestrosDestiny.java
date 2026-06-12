package com.maestrosdestiny;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

/**
 * Maestro's Destiny — 메인 모드 진입점.
 *
 * <p>이 클래스는 공용(common) 측에서 로드된다. 클라이언트 전용 코드는
 * {@link MaestrosDestinyClient} 에 격리한다 (전용 서버 크래시 방지, CLAUDE.md §3-2).</p>
 *
 * <p>Phase 0: 빈 모드. 로드 확인 외 어떤 게임 로직도 없다.</p>
 */
// @Mod 값은 META-INF/neoforge.mods.toml 의 modId 와 일치해야 한다.
@Mod(MaestrosDestiny.MODID)
public class MaestrosDestiny {
    /** 모드 식별자. 절대 변경 금지 — 세이브 호환 파괴 (CLAUDE.md §1). */
    public static final String MODID = "maestrosdestiny";

    /** 공용 로거. */
    public static final Logger LOGGER = LogUtils.getLogger();

    // 모드 클래스 생성자는 모드 로딩 시 가장 먼저 실행되는 코드다.
    // FML 이 IEventBus, ModContainer 같은 파라미터 타입을 인식해 자동으로 주입한다.
    public MaestrosDestiny(IEventBus modEventBus, ModContainer modContainer) {
        // 모드 이벤트 버스에 공용 셋업 리스너 등록
        modEventBus.addListener(this::commonSetup);

        // 이 클래스의 @SubscribeEvent 핸들러를 게임 이벤트 버스에 등록
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Maestro's Destiny: common setup complete (Phase 0).");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Maestro's Destiny: server starting.");
    }
}
