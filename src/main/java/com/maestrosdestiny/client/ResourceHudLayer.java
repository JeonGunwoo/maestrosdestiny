package com.maestrosdestiny.client;

import com.maestrosdestiny.MaestrosDestiny;
import com.maestrosdestiny.combat.resource.ModResources;
import com.maestrosdestiny.combat.resource.ResourceType;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;

/**
 * 리소스 HUD 바를 그리는 GuiLayer (CLAUDE.md §3-4 #5: 리소스 추가 = HUD 바 자동 생성).
 *
 * <p>{@link ModResources} 정의를 순회하며 각 리소스마다 바를 한 줄씩 자동 생성한다. 표시값은
 * {@link ClientResourceStore} 의 예측값(서버 스냅샷이 진실). 클라이언트 전용 — 전용 서버는 로드하지 않는다.</p>
 *
 * <p>26.1: 드로잉은 {@code GuiGraphics} 가 아닌 {@link GuiGraphicsExtractor} 로 한다
 * ({@code fill}, {@code text}, {@code guiHeight}).</p>
 */
public final class ResourceHudLayer implements GuiLayer {
    private static final Identifier LAYER_ID =
            Identifier.fromNamespaceAndPath(MaestrosDestiny.MODID, "resource_bars");

    private static final int LEFT = 10;
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 6;
    private static final int ROW_SPACING = 12;
    private static final int BOTTOM_MARGIN = 40;
    private static final int BG_COLOR = 0xC0101010;

    /** RegisterGuiLayersEvent(모드 버스, 클라) 에서 호출해 레이어를 등록. */
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, new ResourceHudLayer());
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        int row = 0;
        for (ResourceType type : ModResources.all()) {
            if (!ClientResourceStore.has(type.id())) {
                continue;
            }
            double current = ClientResourceStore.get(type.id());
            double fraction = type.max() <= 0.0 ? 0.0 : Math.max(0.0, Math.min(1.0, current / type.max()));

            int y = guiGraphics.guiHeight() - BOTTOM_MARGIN - row * ROW_SPACING;

            // 배경
            guiGraphics.fill(LEFT - 1, y - 1, LEFT + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, BG_COLOR);
            // 채움
            int fillWidth = (int) Math.round(BAR_WIDTH * fraction);
            guiGraphics.fill(LEFT, y, LEFT + fillWidth, y + BAR_HEIGHT, type.hudColor());
            // 수치 텍스트
            String label = (int) Math.floor(current) + "/" + (int) type.max();
            guiGraphics.text(mc.font, Component.literal(label), LEFT + BAR_WIDTH + 4, y - 1, 0xFFFFFFFF);

            row++;
        }
    }
}
