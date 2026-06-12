package com.maestrosdestiny.combat.resource;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 리소스 판정·조작의 서버 사이드 단일 진입점 (CLAUDE.md §3-1 모든 게임 로직은 서버 판정).
 *
 * <p>불연속 변경(소모/획득/디버그)은 {@link ResourceChangedEvent} 를 발행해 즉시 S2C 동기화를
 * 유발한다. 자연회복은 {@code notify=false} 로 변경만 하고 패킷을 보내지 않는다 — 클라이언트가
 * 로컬 예측으로 그리고 정기 스냅샷이 보정한다(§4-1). 클라이언트는 절대 이 클래스로 권위 값을 바꾸지 않는다.</p>
 */
public final class ResourceManager {
    private ResourceManager() {
    }

    public static ResourceStore store(Player player) {
        return player.getData(MdAttachments.RESOURCES);
    }

    /** 현재값. 저장소에 없으면 가득 찬 상태(max)로 간주 — 신규 플레이어는 풀 상태로 시작. */
    public static double get(Player player, ResourceType type) {
        ResourceStore store = store(player);
        return store.has(type.id()) ? store.get(type.id()) : type.max();
    }

    /** 불연속 변경. 변경분을 즉시 동기화(ResourceChangedEvent 발행). */
    public static void set(Player player, ResourceType type, double amount) {
        set(player, type, amount, true);
    }

    /**
     * 값을 설정한다. 상한은 max 로 클램프, 하한은 allow_negative 여부에 따른다.
     *
     * @param notify true 면 서버 플레이어에게 즉시 동기화 이벤트를 발행. 자연회복은 false 로 호출.
     */
    public static void set(Player player, ResourceType type, double amount, boolean notify) {
        double clamped = Math.min(amount, type.max());
        if (!type.allowNegative()) {
            clamped = Math.max(0.0, clamped);
        }
        ResourceStore store = store(player);
        store.put(type.id(), clamped);
        // Attachment 변경을 더티로 표시 (메모리 연결에서도 저장 보장).
        player.setData(MdAttachments.RESOURCES, store);

        if (notify && player instanceof ServerPlayer serverPlayer) {
            NeoForge.EVENT_BUS.post(new ResourceChangedEvent(serverPlayer));
        }
    }

    public static void add(Player player, ResourceType type, double delta) {
        set(player, type, get(player, type) + delta);
    }

    /**
     * 비용을 소모한다. allow_negative 면 잔량이 0보다 클 때만 발동하고 결과는 음수까지 허용한다
     * (§4-1 몰아쓰기의 손맛). 그 외엔 잔량이 비용 이상일 때만 발동한다.
     *
     * @return 발동(소모)했으면 true
     */
    public static boolean consume(Player player, ResourceType type, double cost) {
        double current = get(player, type);
        if (type.allowNegative()) {
            if (current <= 0.0) {
                return false;
            }
        } else if (current < cost) {
            return false;
        }
        set(player, type, current - cost);
        return true;
    }

    /** 모든 리소스에 1틱 자연회복 적용. 동기화하지 않음(notify=false) — 클라가 예측. */
    public static void tickRegen(Player player) {
        for (ResourceType type : ModResources.all()) {
            if (type.naturalRegenPerTick() == 0.0) {
                continue;
            }
            double current = get(player, type);
            if (current < type.max()) {
                set(player, type, current + type.naturalRegenPerTick(), false);
            }
        }
    }

    /** 전체 리소스의 현재값 스냅샷. 동기화 패킷 구성에 사용(network 레이어가 호출). */
    public static Map<Identifier, Double> snapshot(Player player) {
        Map<Identifier, Double> out = new LinkedHashMap<>();
        for (ResourceType type : ModResources.all()) {
            out.put(type.id(), get(player, type));
        }
        return out;
    }
}
