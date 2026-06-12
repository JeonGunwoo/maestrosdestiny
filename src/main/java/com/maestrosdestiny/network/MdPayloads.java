package com.maestrosdestiny.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 모드 패킷 카탈로그의 등록 진입점 (CLAUDE.md §5: 패킷 카탈로그를 한 곳에 집중).
 *
 * <p>S2C 핸들러는 공용으로 등록하되 본문을 {@link ClientHooks} 로 위임한다 — 서버는
 * 클라이언트 바운드 패킷을 수신하지 않으므로 그 본문은 클라이언트에서만 실행된다.</p>
 */
public final class MdPayloads {
    /** 네트워크 프로토콜 버전. 비호환 변경 시 증가. */
    public static final String VERSION = "1";

    private MdPayloads() {
    }

    public static void onRegister(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);

        // S2C: 리소스 스냅샷. 수신 동작은 ClientHooks 로 위임(클라 전용 실행).
        registrar.playToClient(
                ResourceSyncPayload.TYPE,
                ResourceSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientHooks.resourceSync.accept(payload)));
    }
}
