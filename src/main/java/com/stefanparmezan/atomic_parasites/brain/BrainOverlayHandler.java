package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BrainOverlayHandler {

    private static final int BRAIN_SIZE = 40;
    private static final int MARGIN = 3;
    private static final int VERTICAL_OFFSET = 13;

    private static final float BRAIN_U = 0;
    private static final float BRAIN_V = 0;
    private static final float BRAIN_SRC = 16;
    private static final float BRAIN_TEX = 16;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != ElementType.ALL) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.gameSettings.hideGUI || mc.gameSettings.showDebugInfo) return;

        AbstractClientPlayer player = (AbstractClientPlayer) mc.player;

        // === 📍 ПОЗИЦИЯ ===
        int sw = event.getResolution().getScaledWidth();
        int sh = event.getResolution().getScaledHeight();
        int x = sw / 2 + 91 + MARGIN;
        int y = sh - 22 - VERTICAL_OFFSET;

        // === 🎨 РАСЧЁТ ЦВЕТА МОЗГА ===
        BrainColorModifier.BrainColorResult color = BrainColorModifier.calculate(player);

        // === 🎨 ОТРИСОВКА ===
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        // ✅ ПРИМЕНЯЕМ ЦВЕТ
        GlStateManager.color(color.baseR, color.baseG, color.baseB, 1.0f);

        mc.getTextureManager().bindTexture(BrainManager.getCurrentBrainTexture());
        Gui.drawScaledCustomSizeModalRect(
                x, y,
                BRAIN_U, BRAIN_V,
                (int) BRAIN_SRC, (int) BRAIN_SRC,
                BRAIN_SIZE, BRAIN_SIZE,
                BRAIN_TEX, BRAIN_TEX
        );

        // Текст с процентами (всегда белый для читаемости)
        GlStateManager.color(1, 1, 1, 1);
        drawSanityText(x, y, BRAIN_SIZE, BrainManager.getCurrentSanity());

        // Восстановление состояния
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
    }
    private void drawSanityText(int x, int y, int size, float sanity) {
        Minecraft mc = Minecraft.getMinecraft();
        String text = (int) sanity + "%";
        int tw = mc.fontRenderer.getStringWidth(text);
        int tx = x + (size - tw) / 2;
        int ty = y + (size - 8) / 2;

        // Цвет текста: белый > жёлтый > красный
        int col = sanity > 50 ? 0xFFFFFFFF : (sanity > 25 ? 0xFFFFAA00 : 0xFFFF0000);
        mc.fontRenderer.drawStringWithShadow(text, tx, ty, col);
    }
}