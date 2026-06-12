package com.maestrosdestiny.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * 모든 C2S 입력 패킷의 공용 베이스 (CLAUDE.md §3-4 #2).
 *
 * <p>입력은 클라이언트 틱을 실어 보내고, 서버는 {@link com.maestrosdestiny.core.LagCompensation}
 * 으로 소급 보정한 뒤 상태머신·리소스 검증을 수행한다. 소급 보정 로직을 단일화하기 위한 계약.</p>
 *
 * <p>Phase 1 에서는 베이스만 확립한다. 구체 입력 패킷(회피/패링 등)은 Phase 2 에서 이 인터페이스를
 * 구현하며 추가된다.</p>
 */
public interface TimestampedInput extends CustomPacketPayload {
    /** 이 입력이 발생했다고 클라이언트가 주장하는 틱. 서버에서 클램프 대상. */
    long clientTick();
}
