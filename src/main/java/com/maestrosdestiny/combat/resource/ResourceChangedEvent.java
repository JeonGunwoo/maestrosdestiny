package com.maestrosdestiny.combat.resource;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

/**
 * 플레이어 리소스가 불연속적으로 바뀌었을 때(소모/획득/디버그) 서버에서 발행된다.
 *
 * <p>레이어링 유지: {@code combat} 가 이벤트를 발행하고 {@code network} 가 구독해 S2C 동기화를
 * 보낸다(의존 방향 network → combat). 자연회복(틱당 연속 변경)은 발행하지 않는다 —
 * 클라이언트가 로컬 예측으로 그리고 정기 스냅샷이 보정한다.</p>
 */
public class ResourceChangedEvent extends Event {
    private final ServerPlayer player;

    public ResourceChangedEvent(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
