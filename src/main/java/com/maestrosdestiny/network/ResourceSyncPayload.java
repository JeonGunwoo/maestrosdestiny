package com.maestrosdestiny.network;

import java.util.HashMap;
import java.util.Map;

import com.maestrosdestiny.MaestrosDestiny;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * S2C 리소스 동기화 패킷 (CLAUDE.md §4-1: 동기화는 S2C 단방향).
 *
 * <p>현재값 전체 스냅샷을 담는다. 서버는 ① 소모/획득 등 불연속 변경 시 즉시,
 * ② 2초마다 정기적으로 전송한다. 클라이언트는 그 사이 자연회복을 로컬 예측으로 그린다.</p>
 *
 * <p>※ 멀티 동기화: 이 패킷은 항상 서버 → 클라 단방향. 클라이언트는 절대 권위 값을 보내지 않는다.</p>
 */
public record ResourceSyncPayload(Map<Identifier, Double> values) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ResourceSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MaestrosDestiny.MODID, "resource_sync"));

    private static final StreamCodec<ByteBuf, Map<Identifier, Double>> VALUES_CODEC =
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.DOUBLE);

    public static final StreamCodec<ByteBuf, ResourceSyncPayload> STREAM_CODEC =
            VALUES_CODEC.map(ResourceSyncPayload::new, ResourceSyncPayload::values);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
