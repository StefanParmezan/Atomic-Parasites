package com.stefanparmezan.atomic_parasites.utils.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class FaceOverlayHandler {

    // Размер лица на экране (будет масштабировано)
    private static final int FACE_SIZE = 32;
    private static final int MARGIN = 6;

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
        ResourceLocation skin = player.getLocationSkin();

        int screenWidth = event.getResolution().getScaledWidth();
        int screenHeight = event.getResolution().getScaledHeight();

        // Позиция хотбара
        int hotbarLeft = screenWidth / 2 - 91;
        int hotbarTop = screenHeight - 22;

        // Позиция лица: слева от хотбара
        int x = hotbarLeft - FACE_SIZE - MARGIN;
        int y = hotbarTop - 10;

        // Привязываем текстуру скина
        mc.getTextureManager().bindTexture(skin);

        // Рисуем базовый слой лица с масштабированием
        // Параметры: x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight
        Gui.drawScaledCustomSizeModalRect(
                x, y,
                8.0f, 8.0f,     // UV координаты лица на скине
                8, 8,            // Размер лица на текстуре (8x8 пикселей)
                FACE_SIZE, FACE_SIZE, // Размер на экране
                64.0f, 64.0f     // Полный размер текстуры скина
        );

        // Рисуем слой шляпы с прозрачностью
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Gui.drawScaledCustomSizeModalRect(
                x, y,
                40.0f, 8.0f,    // UV координаты шляпы
                8, 8,
                FACE_SIZE, FACE_SIZE,
                64.0f, 64.0f
        );

        GL11.glDisable(GL11.GL_BLEND);
    }
}