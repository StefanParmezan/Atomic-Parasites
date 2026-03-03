package com.stefanparmezan.atomic_parasites.utils.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FaceOverlayHandler {

    private static final int FACE_SIZE = 32;
    private static final int MARGIN = 6;

    private static final float FACE_U = 8.0f;
    private static final float FACE_V = 8.0f;
    private static final float HAT_U = 40.0f;
    private static final float HAT_V = 8.0f;
    private static final float FACE_SRC_SIZE = 8.0f;
    private static final float SKIN_TEX_SIZE = 64.0f;

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

        int screenWidth = event.getResolution().getScaledWidth();
        int screenHeight = event.getResolution().getScaledHeight();

        int hotbarLeft = screenWidth / 2 - 91;
        int hotbarTop = screenHeight - 22;

        int x = hotbarLeft - FACE_SIZE - MARGIN;
        int y = hotbarTop - 10;

        ResourceLocation skin = player.getLocationSkin();
        FaceColorModifier.ColorResult colors = FaceColorModifier.calculate(player);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        mc.getTextureManager().bindTexture(skin);

        if (colors.hasEffect) {
            drawFaceWithVariation(x, y, colors);
        } else {
            drawBaseFace(x, y);
        }

        drawBloodSpots(x, y, FACE_SIZE);

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    private void drawFaceWithVariation(int x, int y, FaceColorModifier.ColorResult colors) {
        GlStateManager.color(colors.baseR, colors.baseG, colors.baseB, 1.0f);
        drawBaseFace(x, y);

        GlStateManager.color(colors.darkR, colors.darkG, colors.darkB, 0.5f);
        drawFaceWithUVOffset(x, y, 0.5f, 0.5f);

        GlStateManager.color(colors.darkR, colors.darkG, colors.darkB, 0.3f);
        drawFaceWithUVOffset(x, y, -0.5f, 0.5f);

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
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

    private void drawFaceWithUVOffset(int x, int y, float uOffset, float vOffset) {
        Gui.drawScaledCustomSizeModalRect(
                x, y,
                FACE_U + uOffset, FACE_V + vOffset,
                (int) FACE_SRC_SIZE, (int) FACE_SRC_SIZE,
                FACE_SIZE, FACE_SIZE,
                SKIN_TEX_SIZE, SKIN_TEX_SIZE
        );
        Gui.drawScaledCustomSizeModalRect(
                x, y,
                HAT_U + uOffset, HAT_V + vOffset,
                (int) FACE_SRC_SIZE, (int) FACE_SRC_SIZE,
                FACE_SIZE, FACE_SIZE,
                SKIN_TEX_SIZE, SKIN_TEX_SIZE
        );
    }

    private void drawBloodSpots(int faceX, int faceY, int faceSize) {
        for (BloodEffectManager.BloodSpot spot : BloodEffectManager.getActiveSpots()) {
            int spotX = faceX + (int) (spot.x * faceSize);
            int spotY = faceY + (int) (spot.y * faceSize);
            int spotPxSize = (int) (spot.size * 3.0f);

            // Тёмно-красный цвет (запёкшаяся кровь)
            // Формат ARGB: Alpha(0xE6) Red(0x88) Green(0x00) Blue(0x00)
            int bloodColor = 0xE6880000;

            Gui.drawRect(
                    spotX - spotPxSize / 2,
                    spotY - spotPxSize / 2,
                    spotX + spotPxSize / 2,
                    spotY + spotPxSize / 2,
                    bloodColor
            );

            // Хаотичные отростки
            for (int i = 0; i < spot.points; i++) {
                int offsetX = (int) (spot.offsetsX[i] * spotPxSize);
                int offsetY = (int) (spot.offsetsY[i] * spotPxSize);
                int subSize = spotPxSize / 3 + BloodEffectManager.RAND.nextInt(spotPxSize / 3);

                Gui.drawRect(
                        spotX + offsetX - subSize / 2,
                        spotY + offsetY - subSize / 2,
                        spotX + offsetX + subSize / 2,
                        spotY + offsetY + subSize / 2,
                        0xE6660000 // Ещё темнее для краёв
                );
            }

            // Брызги
            if (BloodEffectManager.RAND.nextFloat() < 0.3f) {
                int splashX = spotX + (BloodEffectManager.RAND.nextInt(spotPxSize * 2) - spotPxSize);
                int splashY = spotY + (BloodEffectManager.RAND.nextInt(spotPxSize * 2) - spotPxSize);
                int splashSize = spotPxSize / 4;

                Gui.drawRect(
                        splashX - splashSize / 2,
                        splashY - splashSize / 2,
                        splashX + splashSize / 2,
                        splashY + splashSize / 2,
                        0xE6550000 // Очень тёмный
                );
            }
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
}