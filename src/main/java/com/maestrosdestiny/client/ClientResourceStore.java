package com.maestrosdestiny.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.maestrosdestiny.combat.resource.ModResources;
import com.maestrosdestiny.combat.resource.ResourceType;
import com.maestrosdestiny.network.ResourceSyncPayload;

import net.minecraft.resources.Identifier;

/**
 * 클라이언트의 리소스 표시값 저장소 (연출용 — CLAUDE.md §3-1: 클라 예측은 언제든 취소 가능).
 *
 * <p>서버 스냅샷이 진실이다. 그 사이 자연회복만 로컬 예측으로 그리고, 다음 스냅샷이 덮어쓴다(§4-1).
 * 모든 접근은 클라이언트 메인 스레드(패킷 처리 enqueueWork + 클라 틱 + HUD 렌더)에서 일어난다.</p>
 */
public final class ClientResourceStore {
    private static final Map<Identifier, Double> VALUES = new HashMap<>();

    private ClientResourceStore() {
    }

    /** 서버 스냅샷으로 표시값을 교체(권위 보정). */
    public static void applySnapshot(ResourceSyncPayload payload) {
        VALUES.clear();
        VALUES.putAll(payload.values());
    }

    /** 1 클라이언트 틱의 자연회복 로컬 예측. 서버 정기 스냅샷이 드리프트를 보정. */
    public static void predictTick() {
        for (Map.Entry<Identifier, Double> entry : VALUES.entrySet()) {
            ResourceType type = ModResources.get(entry.getKey());
            if (type == null || type.naturalRegenPerTick() == 0.0) {
                continue;
            }
            double current = entry.getValue();
            if (current < type.max()) {
                entry.setValue(Math.min(type.max(), current + type.naturalRegenPerTick()));
            }
        }
    }

    public static boolean has(Identifier id) {
        return VALUES.containsKey(id);
    }

    public static double get(Identifier id) {
        return VALUES.getOrDefault(id, 0.0);
    }

    /** HUD 렌더가 읽는 읽기 전용 뷰(Slice 3). */
    public static Map<Identifier, Double> view() {
        return Collections.unmodifiableMap(VALUES);
    }
}
