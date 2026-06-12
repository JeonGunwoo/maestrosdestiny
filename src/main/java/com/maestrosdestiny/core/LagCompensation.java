package com.maestrosdestiny.core;

/**
 * 서버 권위 + 입력 타임스탬프 소급 보정의 단일 유틸 (CLAUDE.md §3-1).
 *
 * <p>소급 한도 150ms = 3틱(@20tps). 모든 {@link com.maestrosdestiny.network.TimestampedInput}
 * 처리는 이 클램프를 거쳐 과거 틱으로 보정된다 (Phase 2 의 링버퍼 소급 판정에서 사용).</p>
 */
public final class LagCompensation {
    /** 소급 보정 한도(틱). 150ms / 50ms = 3. */
    public static final int MAX_ROLLBACK_TICKS = 3;

    private LagCompensation() {
    }

    /**
     * 클라이언트가 주장한 입력 틱을 서버가 신뢰 가능한 범위로 클램프한다.
     *
     * @param serverTick 현재 서버 틱
     * @param clientTick 입력 패킷이 주장하는 클라이언트 틱
     * @return [serverTick - MAX_ROLLBACK_TICKS, serverTick] 로 클램프된 틱
     */
    public static long clampClientTick(long serverTick, long clientTick) {
        long earliest = serverTick - MAX_ROLLBACK_TICKS;
        if (clientTick < earliest) {
            return earliest;
        }
        if (clientTick > serverTick) {
            // 미래 입력(시계 오차·치팅)은 현재 틱으로 끌어내린다.
            return serverTick;
        }
        return clientTick;
    }
}
