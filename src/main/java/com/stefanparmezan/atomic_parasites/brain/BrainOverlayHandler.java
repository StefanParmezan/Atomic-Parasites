package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BrainOverlayHandler {

    // === НАСТРОЙКИ ПОЗИЦИИ И РАЗМЕРА ===
    private static final int BRAIN_SIZE = 40;
    private static final int MARGIN = 3;
    private static final int VERTICAL_OFFSET = 13;

    // === UV-координаты и размеры текстуры ===
    private static final float BRAIN_U = 0.0f;
    private static final float BRAIN_V = 0.0f;
    private static final float BRAIN_SRC_SIZE = 16.0f;
    private static final float BRAIN_TEX_SIZE = 16.0f;

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.gameSettings.hideGUI || mc.gameSettings.showDebugInfo) {
            return;
        }

        AbstractClientPlayer player = (AbstractClientPlayer) mc.player;

        // === ОБНОВЛЕНИЕ ЛОГИКИ (урон, взрывы, рассудок) ===
        BrainEventHandler.tick(player);

        // === ВЫЧИСЛЕНИЕ ПОЗИЦИИ (всегда справа) ===
        int screenWidth = event.getResolution().getScaledWidth();
        int screenHeight = event.getResolution().getScaledHeight();

        int hotbarRight = screenWidth / 2 + 91;
        int hotbarTop = screenHeight - 22;
        int x = hotbarRight + MARGIN;
        int y = hotbarTop - VERTICAL_OFFSET;

        // === ЭФФЕКТ ТРЯСКИ ===
        float shakeX = 0, shakeY = 0;
        if (BrainManager.isShaking()) {
            shakeX = (float) ((Math.random() - 0.5) * 10);
            shakeY = (float) ((Math.random() - 0.5) * 10);
        }

        ResourceLocation brainTexture = BrainManager.getCurrentBrainTexture();

        // === НАСТРОЙКА OpenGL ДЛЯ ОТРИСОВКИ ПОВЕРХ ВСЕГО ===
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth(); // Отключаем Z-буфер
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Привязываем текстуру мозга
        mc.getTextureManager().bindTexture(brainTexture);

        // === КРАСНЫЙ ФЛЭШ ПРИ УРОНЕ ===
        if (BrainManager.isFlashingRed()) {
            GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
        }

        // Рисуем мозг (с учётом тряски)
        drawBrain((int)(x + shakeX), (int)(y + shakeY), BRAIN_SIZE);

        // Сбрасываем цвет для текста
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        // Рисуем процент рассудка внутри мозга
        drawSanityPercentInside((int)(x + shakeX), (int)(y + shakeY), BRAIN_SIZE, BrainManager.getCurrentSanity());

        // === ВОССТАНОВЛЕНИЕ СОСТОЯНИЯ OpenGL ===
        GlStateManager.enableDepth(); // Включаем Z-буфер обратно
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // Сброс цвета
        GlStateManager.popMatrix();
    }

    /**
     * Отрисовка текстуры мозга с масштабированием
     */
    private void drawBrain(int x, int y, int displaySize) {
        Gui.drawScaledCustomSizeModalRect(
                x, y,
                BRAIN_U, BRAIN_V,
                (int) BRAIN_SRC_SIZE, (int) BRAIN_SRC_SIZE,
                displaySize, displaySize,
                BRAIN_TEX_SIZE, BRAIN_TEX_SIZE
        );
    }

    /**
     * Отрисовка процента рассудка внутри мозга
     */
    private void drawSanityPercentInside(int x, int y, int size, float sanity) {
        String text = (int) sanity + "%";
        Minecraft mc = Minecraft.getMinecraft();
        int textWidth = mc.fontRenderer.getStringWidth(text);

        // Центрирование текста внутри мозга
        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - 8) / 2; // 8 — примерная высота шрифта

        // Цвет текста в зависимости от уровня рассудка
        int color;
        if (sanity > 50) {
            color = 0xFFFFFFFF; // Белый
        } else if (sanity > 25) {
            color = 0xFFFFAA00; // Оранжевый
        } else {
            color = 0xFFFF0000; // Красный
        }

        mc.fontRenderer.drawStringWithShadow(text, textX, textY, color);
    }
}