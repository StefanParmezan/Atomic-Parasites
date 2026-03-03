package com.stefanparmezan.atomic_parasites.utils.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffectHandler {

    private static final Logger LOGGER = LogManager.getLogger("PotionEffect");
    private static boolean shouldHidePotions = false;

    public PotionEffectHandler() {
        LOGGER.info("PotionEffectHandler initialized");
    }

    // Отключаем рендер иконок зелий когда инвентарь открыт
    @SubscribeEvent
    public void onRenderPotionIcons(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            if (shouldHidePotions) {
                LOGGER.debug("Hiding potion icons - inventory is open");
                event.setCanceled(true);
            }
        }
    }

    // Включаем/выключаем скрытие при открытии/закрытии инвентаря
    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiInventory) {
            shouldHidePotions = true;
            LOGGER.debug("Inventory opened - hiding potion effects");
        }
    }

    @SubscribeEvent
    public void onGuiClose(GuiScreenEvent event) {
        if (event.getGui() instanceof GuiInventory) {
            shouldHidePotions = false;
            LOGGER.debug("Inventory closed - showing potion effects");
        }
    }
}