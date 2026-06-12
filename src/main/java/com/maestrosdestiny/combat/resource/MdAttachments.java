package com.maestrosdestiny.combat.resource;

import java.util.function.Supplier;

import com.maestrosdestiny.MaestrosDestiny;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 모드의 Attachment 타입 등록 (서버 진실 상태 저장 — CLAUDE.md §5 플레이어 Attachment).
 *
 * <p>Slice 1: 리소스 현재값 저장소만. 이후 투자 스탯·친숙도·생존 상태 등이 추가된다.</p>
 */
public final class MdAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MaestrosDestiny.MODID);

    /** 플레이어 리소스 현재값. 직렬화 + 사망 시 복사 보존(Phase 3/4 에서 수면 스냅샷으로 정교화). */
    public static final Supplier<AttachmentType<ResourceStore>> RESOURCES =
            ATTACHMENT_TYPES.register("resources", () -> AttachmentType
                    .builder(ResourceStore::new)
                    .serialize(ResourceStore.CODEC)
                    .copyOnDeath()
                    .build());

    private MdAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
