package com.maestrosdestiny.combat.resource;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * 서버 틱마다 접속 중인 모든 플레이어의 리소스 자연회복을 처리한다.
 *
 * <p>멀티 동기화: 서버에서만 회복을 판정한다. 클라이언트의 자연회복 표시는 Slice 2 에서
 * 로컬 예측으로 그리며, 서버 스냅샷이 진실이다 (§4-1).</p>
 */
public final class ResourceTickHandler {
    private ResourceTickHandler() {
    }

    public static void onServerTickPost(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            ResourceManager.tickRegen(player);
        }
    }
}
