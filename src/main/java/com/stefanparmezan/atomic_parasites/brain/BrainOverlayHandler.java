package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BrainOverlayHandler {

    private static final int BRAIN_SIZE = 40;
    private static final int MARGIN = 3;
    private static final int VERTICAL_OFFSET = 13;  // ИСПРАВЛЕНО: было 12, стало 8

    private static final float BRAIN_U = 0.0f;
    private static final float BRAIN_V = 0.0f;
    private static final float BRAIN_SRC_SIZE = 16.0f;
    private static final float BRAIN_TEX_SIZE = 16.0f;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.gameSettings.hideGUI || mc.gameSettings.showDebugInfo) return;

        AbstractClientPlayer player = (AbstractClientPlayer) mc.player;
        BrainManager.update(player);

        int screenWidth = event.getResolution().getScaledWidth();
        int screenHeight = event.getResolution().getScaledHeight();

        // Позиция: справа от хотбара, VERTICAL_OFFSET = 8
        int hotbarRight = screenWidth / 2 + 91;
        int hotbarTop = screenHeight - 22;
        int x = hotbarRight + MARGIN;
        int y = hotbarTop - VERTICAL_OFFSET;

        // Тряска при взрыве
        float shakeX = 0, shakeY = 0;
        if (BrainManager.isShaking()) {
            shakeX = (float) ((Math.random() - 0.5) * 10);
            shakeY = (float) ((Math.random() - 0.5) * 10);
        }

        ResourceLocation brainTexture = BrainManager.getCurrentBrainTexture();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        mc.getTextureManager().bindTexture(brainTexture);
        drawBrain((int)(x + shakeX), (int)(y + shakeY), BRAIN_SIZE);

        // === ПРОСТАЯ КРАСНАЯ ВСПЫШКА (как в FaceColorModifier) ===
        if (BrainManager.isFlashingRed()) {
            // Просто рисуем красный прямоугольник поверх мозга — без анимации
            Gui.drawRect(
                    (int)(x + shakeX), (int)(y + shakeY),
                    (int)(x + shakeX + BRAIN_SIZE), (int)(y + shakeY + BRAIN_SIZE),
                    0x99FF0000  // Красный с прозрачностью ~60%
            );
        }

        // Процент внутри мозга
        drawSanityPercentInside((int)(x + shakeX), (int)(y + shakeY), BRAIN_SIZE, BrainManager.getCurrentSanity());

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    private void drawBrain(int x, int y, int displaySize) {
        Gui.drawScaledCustomSizeModalRect(
                x, y, BRAIN_U, BRAIN_V,
                (int) BRAIN_SRC_SIZE, (int) BRAIN_SRC_SIZE,
                displaySize, displaySize,
                BRAIN_TEX_SIZE, BRAIN_TEX_SIZE
        );
    }

    private void drawSanityPercentInside(int x, int y, int size, float sanity) {
        String text = (int) sanity + "%";
        int w = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        int textX = x + (size - w) / 2;
        int textY = y + (size - 8) / 2;
        int color = sanity > 50 ? 0xFFFFFFFF : (sanity > 25 ? 0xFFFFAA00 : 0xFFFF0000);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, textX, textY, color);
    }
}