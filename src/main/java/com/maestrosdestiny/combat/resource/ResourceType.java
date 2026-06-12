package com.maestrosdestiny.combat.resource;

import net.minecraft.resources.Identifier;

/**
 * 범용 리소스(기력) 정의. 스태미나는 이 시스템의 첫 인스턴스일 뿐이다 (CLAUDE.md §4-1).
 *
 * <p>Phase 1(Slice 1)에서는 코드로 정의한다. 추후 datapack {@code resource_type} 스키마로
 * 이관할 수 있도록 순수 데이터 객체로 유지한다 (CLAUDE.md §3 하드코딩 금지 — 차후 적용).</p>
 *
 * <p>regen 3형(natural/buildup/charge) 중 Slice 1은 {@code natural}만 구현한다.
 * buildup/charge 및 복합 비용·scope·HUD 정보는 후속 슬라이스에서 확장한다.</p>
 *
 * @param id                  네임스페이스 포함 식별자 (예: {@code maestrosdestiny:stamina})
 * @param max                 최대치
 * @param naturalRegenPerTick 틱당 자연회복량 (0 = 자연회복 없음)
 * @param allowNegative       true면 잔량이 0보다 클 때 비용을 초과 지불해 음수까지 허용 (§4-1 몰아쓰기)
 * @param hudColor            HUD 바 채움 색(ARGB). 리소스에 HUD 정보를 담아 바를 자동 생성한다 (§3-4 #5)
 */
public record ResourceType(Identifier id, double max, double naturalRegenPerTick, boolean allowNegative, int hudColor) {
}
