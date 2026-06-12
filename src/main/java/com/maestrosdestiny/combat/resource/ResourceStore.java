package com.maestrosdestiny.combat.resource;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;

/**
 * 플레이어별 리소스 현재값 저장소 (서버 진실 — CLAUDE.md §3-1). Attachment 로 직렬화된다.
 *
 * <p>모든 Attachment·월드 데이터는 {@code schemaVersion} 필드 + 마이그레이션 훅을 가진다
 * (§3-2, Phase 1 부터 적용).</p>
 *
 * <p>동기화: 이 객체는 서버 권위 데이터다. 클라이언트로의 전송은 Slice 2 의 명시적
 * S2C {@code ResourceSync} 패킷이 담당한다 ("어쩌다 동기화" 금지).</p>
 */
public final class ResourceStore {
    /** 현재 스키마 버전. 저장 포맷 변경 시 증가시키고 {@link #migrate()} 에 업그레이드 단계 추가. */
    public static final int CURRENT_SCHEMA = 1;

    public static final MapCodec<ResourceStore> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("schemaVersion", CURRENT_SCHEMA).forGetter(s -> s.schemaVersion),
            Codec.unboundedMap(Identifier.CODEC, Codec.DOUBLE).optionalFieldOf("values", Map.of()).forGetter(ResourceStore::valuesView)
    ).apply(instance, ResourceStore::deserialize));

    private int schemaVersion;
    private final Map<Identifier, Double> values;

    public ResourceStore() {
        this.schemaVersion = CURRENT_SCHEMA;
        this.values = new HashMap<>();
    }

    private static ResourceStore deserialize(int schemaVersion, Map<Identifier, Double> values) {
        ResourceStore store = new ResourceStore();
        store.schemaVersion = schemaVersion;
        store.values.putAll(values);
        store.migrate();
        return store;
    }

    /** schemaVersion 마이그레이션 훅 (§3-2). 과거 버전 데이터를 현재 포맷으로 끌어올린다. */
    private void migrate() {
        if (schemaVersion < CURRENT_SCHEMA) {
            // 향후 스키마 변경 시 (schemaVersion == N) 단계별 업그레이드를 여기에 추가.
            schemaVersion = CURRENT_SCHEMA;
        }
    }

    public boolean has(Identifier id) {
        return values.containsKey(id);
    }

    public double get(Identifier id) {
        return values.getOrDefault(id, 0.0);
    }

    public void put(Identifier id, double amount) {
        values.put(id, amount);
    }

    private Map<Identifier, Double> valuesView() {
        return values;
    }
}
