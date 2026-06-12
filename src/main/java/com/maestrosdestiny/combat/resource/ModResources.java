package com.maestrosdestiny.combat.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.maestrosdestiny.MaestrosDestiny;

import net.minecraft.resources.Identifier;

/**
 * 리소스 정의 레지스트리 (코드 우선 — CLAUDE.md §3-4 #3 의 datapack 이관 전 단계).
 *
 * <p>모든 ID는 네임스페이스 포함 (§3-2). 추후 datapack 로더가 이 레지스트리를 채우는 방식으로
 * 자연스럽게 교체할 수 있다.</p>
 */
public final class ModResources {
    private static final Map<Identifier, ResourceType> REGISTRY = new LinkedHashMap<>();

    /** 첫 리소스: 스태미나. max 100, 틱당 0.5 회복(≈10/s), 음수 허용(§4-1). */
    public static final ResourceType STAMINA = register(
            new ResourceType(id("stamina"), 100.0, 0.5, true));

    private ModResources() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MaestrosDestiny.MODID, path);
    }

    private static ResourceType register(ResourceType type) {
        REGISTRY.put(type.id(), type);
        return type;
    }

    public static ResourceType get(Identifier id) {
        return REGISTRY.get(id);
    }

    public static boolean contains(Identifier id) {
        return REGISTRY.containsKey(id);
    }

    public static Collection<ResourceType> all() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static Set<Identifier> ids() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }
}
