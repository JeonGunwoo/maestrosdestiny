package com.maestrosdestiny.client;

import com.maestrosdestiny.network.ClientHooks;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * 클라이언트 리소스 배선: S2C 수신 훅 주입 + 자연회복 로컬 예측 틱 (CLAUDE.md §4-1).
 *
 * <p>{@code client → network} 방향이므로 여기서 {@link ClientHooks} 에 실제 수신 동작을 주입한다.</p>
 */
public final class ClientResourceHandlers {
    private ClientResourceHandlers() {
    }

    public static void init(IEventBus gameEventBus) {
        // S2C 리소스 스냅샷 수신 동작 주입(서버에선 절대 실행되지 않는 본문).
        ClientHooks.resourceSync = ClientResourceStore::applySnapshot;

        // 매 클라이언트 틱 자연회복 예측.
        gameEventBus.addListener(ClientResourceHandlers::onClientTickPost);
    }

    private static void onClientTickPost(ClientTickEvent.Post event) {
        ClientResourceStore.predictTick();
    }
}
