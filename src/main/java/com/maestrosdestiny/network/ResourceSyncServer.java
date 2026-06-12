package com.maestrosdestiny.network;

import com.maestrosdestiny.combat.resource.ResourceChangedEvent;
import com.maestrosdestiny.combat.resource.ResourceManager;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 서버 사이드 리소스 동기화 송신 (CLAUDE.md §4-1: 즉시 + 2초 스냅샷).
 *
 * <p>의존 방향 network → combat: combat 의 {@link ResourceChangedEvent} 와 {@link ResourceManager}
 * 스냅샷을 읽어 S2C 패킷으로 포장해 보낸다.</p>
 */
public final class ResourceSyncServer {
    /** 정기 스냅샷 주기(틱). 2초 = 40틱(@20tps). */
    private static final int SNAPSHOT_INTERVAL_TICKS = 40;

    private ResourceSyncServer() {
    }

    /** 불연속 변경 시 즉시 해당 플레이어에게 송신. */
    public static void onResourceChanged(ResourceChangedEvent event) {
        send(event.getPlayer());
    }

    /** 2초마다 전체 플레이어에게 정기 스냅샷(자연회복 예측 드리프트 보정). */
    public static void onServerTickPost(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % SNAPSHOT_INTERVAL_TICKS != 0) {
            return;
        }
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            send(player);
        }
    }

    /** 접속 시 초기 스냅샷으로 클라이언트 상태를 초기화. */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            send(player);
        }
    }

    private static void send(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new ResourceSyncPayload(ResourceManager.snapshot(player)));
    }
}
