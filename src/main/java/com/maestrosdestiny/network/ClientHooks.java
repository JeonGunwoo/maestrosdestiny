package com.maestrosdestiny.network;

import java.util.function.Consumer;

/**
 * S2C 패킷의 클라이언트 수신 동작을 담는 위임 훅 (레이어링 유지용).
 *
 * <p>{@code network} 는 {@code client} 를 참조할 수 없다(의존 방향: client → network).
 * 그래서 S2C 핸들러 본문을 여기에 비워 두고, 클라이언트 셋업이 실제 동작을 주입한다.
 * S2C 패킷은 서버에서 수신될 일이 없으므로 이 훅의 본문은 클라이언트에서만 실행된다.</p>
 */
public final class ClientHooks {
    /** 리소스 스냅샷 수신 처리. 클라이언트가 셋업에서 실제 핸들러로 교체. 서버에선 no-op. */
    public static volatile Consumer<ResourceSyncPayload> resourceSync = payload -> {
    };

    private ClientHooks() {
    }
}
