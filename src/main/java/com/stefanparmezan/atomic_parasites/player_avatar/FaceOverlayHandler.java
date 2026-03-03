package com.stefanparmezan.atomic_parasites.player_avatar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.stefanparmezan.atomic_parasites.player_avatar.HealthOverlayManager;

public class FaceOverlayHandler {

    private static final int FACE_SIZE = 32;
    private static final int MARGIN = 6;
    private static final int PIXEL_SIZE = FACE_SIZE / 8;

    private static final float FACE_U = 8.0f;
    private static final float FACE_V = 8.0f;
    private static final float HAT_U = 40.0f;
    private static final float HAT_V = 8.0f;
    private static final float FACE_SRC_SIZE = 8.0f;
    private static final float SKIN_TEX_SIZE = 64.0f;

    // Размер текстуры оверлея (8x8 пикселей)
    private static final int OVERLAY_TEX_SIZE = 8;

    private static final int WATER_COLOR = 0x802B3BCC;
    private static final int BLOOD_COLOR = 0xFFAA0000;
    private static final int BURN_COLOR = 0xFF555555;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.player == null || mc.gameSettings.hideGUI || mc.gameSettings.showDebugInfo) {
            return;
        }

        AbstractClientPlayer player = (AbstractClientPlayer) mc.player;

        BloodEffectManager.update(player);
        WaterEffectManager.update(player);
        HealthOverlayManager.update(player);

        int screenWidth = event.getResolution().getScaledWidth();
        int screenHeight = event.getResolution().getScaledHeight();

        int hotbarLeft = screenWidth / 2 - 91;
        int hotbarTop = screenHeight - 22;

        int x = hotbarLeft - FACE_SIZE - MARGIN;
        int y = hotbarTop - 10;

        ResourceLocation skin = player.getLocationSkin();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        // 1. Рисуем базовое лицо (скин игрока)
        mc.getTextureManager().bindTexture(skin);
        drawBaseFace(x, y);

        // 2. Вода — синий оверлей
        if (WaterEffectManager.isPlayerInWater()) {
            Gui.drawRect(x, y, x + FACE_SIZE, y + FACE_SIZE, WATER_COLOR);
        }

        // 3. Отравление — зелёный оверлей
        PotionEffect poison = player.getActivePotionEffect(MobEffects.POISON);
        if (poison != null) {
            drawColoredOverlay(x, y, FACE_SIZE, 0x0000FF00, poison.getAmplifier());
        }

        // 4. Иссушение — фиолетовый оверлей
        PotionEffect wither = player.getActivePotionEffect(MobEffects.WITHER);
        if (wither != null) {
            drawColoredOverlay(x, y, FACE_SIZE, 0x00880088, wither.getAmplifier());
        }

        // 5. Кровь и ожоги — отдельные пиксели
        drawEffectSpots(x, y);

        // 6. Оверлей здоровья — плавный переход между текстурами
        if (HealthOverlayManager.shouldRender()) {
            drawHealthOverlay(x, y, FACE_SIZE);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    private void drawBaseFace(int x, int y) {
        Gui.drawScaledCustomSizeModalRect(
                x, y, FACE_U, FACE_V,
                (int) FACE_SRC_SIZE, (int) FACE_SRC_SIZE,
                FACE_SIZE, FACE_SIZE,
                SKIN_TEX_SIZE, SKIN_TEX_SIZE
        );
        Gui.drawScaledCustomSizeModalRect(
                x, y, HAT_U, HAT_V,
                (int) FACE_SRC_SIZE, (int) FACE_SRC_SIZE,
                FACE_SIZE, FACE_SIZE,
                SKIN_TEX_SIZE, SKIN_TEX_SIZE
        );
    }

    private void drawColoredOverlay(int x, int y, int faceSize, int baseColor, int amplifier) {
        float intensity = Math.min(1.0f, 0.4f + amplifier * 0.15f);
        int alpha = (int) (intensity * 0.5f * 255);
        int color = (alpha << 24) | (baseColor & 0x00FFFFFF);
        Gui.drawRect(x, y, x + faceSize, y + faceSize, color);
    }

    private void drawEffectSpots(int faceX, int faceY) {
        for (BloodEffectManager.EffectSpot spot : BloodEffectManager.getActiveSpots()) {
            int pixelX = faceX + (spot.gridX * PIXEL_SIZE);
            int pixelY = faceY + (spot.gridY * PIXEL_SIZE);

            float opacity = spot.getOpacity();
            int alpha = (int) (opacity * 255);

            int spotColor;
            if (spot.type == BloodEffectManager.SpotType.BLOOD) {
                spotColor = (alpha << 24) | (BLOOD_COLOR & 0x00FFFFFF);
            } else {
                spotColor = (alpha << 24) | (BURN_COLOR & 0x00FFFFFF);
            }

            Gui.drawRect(pixelX, pixelY, pixelX + PIXEL_SIZE, pixelY + PIXEL_SIZE, spotColor);
        }
    }

    /**
     * Рисует оверлей здоровья с плавным переходом прозрачности
     */
    private void drawHealthOverlay(int x, int y, int displaySize) {
        ResourceLocation overlay = HealthOverlayManager.getCurrentOverlay();
        if (overlay == null) return;

        float progress = HealthOverlayManager.getTransitionProgress();

        // Привязываем текстуру оверлея
        Minecraft.getMinecraft().getTextureManager().bindTexture(overlay);

        // Сохраняем состояние OpenGL
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();

        // Применяем прозрачность на основе прогресса перехода
        GlStateManager.color(1.0f, 1.0f, 1.0f, progress);

        // Рисуем текстуру оверлея (8x8 -> displaySize x displaySize)
        Gui.drawScaledCustomSizeModalRect(
                x, y,
                0.0f, 0.0f,                    // UV координаты (начало текстуры)
                OVERLAY_TEX_SIZE, OVERLAY_TEX_SIZE,  // Размер области на текстуре (8x8)
                displaySize, displaySize,      // Размер отображения на экране
                OVERLAY_TEX_SIZE, OVERLAY_TEX_SIZE   // Полный размер текстуры (8x8)
        );

        // Восстанавливаем состояние
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
}