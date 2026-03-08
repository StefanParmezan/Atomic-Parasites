package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

public class BrainManager {

    // === 🖼️ ТЕКСТУРЫ МОЗГА ===
    public static final ResourceLocation BRAIN_100 = new ResourceLocation("atomic_parasites", "textures/brain/brain100pr.png");
    public static final ResourceLocation BRAIN_90  = new ResourceLocation("atomic_parasites", "textures/brain/brain90pr.png");
    public static final ResourceLocation BRAIN_80  = new ResourceLocation("atomic_parasites", "textures/brain/brain80pr.png");
    public static final ResourceLocation BRAIN_70  = new ResourceLocation("atomic_parasites", "textures/brain/brain70pr.png");
    public static final ResourceLocation BRAIN_60  = new ResourceLocation("atomic_parasites", "textures/brain/brain60pr.png");
    public static final ResourceLocation BRAIN_50  = new ResourceLocation("atomic_parasites", "textures/brain/brain50pr.png");
    public static final ResourceLocation BRAIN_40  = new ResourceLocation("atomic_parasites", "textures/brain/brain40pr.png");
    public static final ResourceLocation BRAIN_30  = new ResourceLocation("atomic_parasites", "textures/brain/brain30pr.png");
    public static final ResourceLocation BRAIN_20  = new ResourceLocation("atomic_parasites", "textures/brain/brain20pr.png");
    public static final ResourceLocation BRAIN_10  = new ResourceLocation("atomic_parasites", "textures/brain/brain10pr.png");

    // === 📊 СОСТОЯНИЕ ===
    private static float currentSanity = 100.0f;
    private static ResourceLocation currentTexture = BRAIN_100;
    private static boolean isFlashingRed = false;
    private static int flashTimer = 0;
    private static boolean isShaking = false;
    private static int shakeTimer = 0;
    private static boolean deathTriggered = false;

    // === ⏱️ ДЛИТЕЛЬНОСТИ ИЗ КОНФИГА ===
    private static final int FLASH_DURATION = BrainConfig.FLASH_DURATION;
    private static final int SHAKE_DURATION = BrainConfig.SHAKE_DURATION;

    // === 📥 GETTERS (без аргументов - для GUI) ===
    public static float getCurrentSanity() { return currentSanity; }
    public static ResourceLocation getCurrentBrainTexture() { return currentTexture; }
    public static boolean isFlashingRed() { return isFlashingRed; }
    public static boolean isShaking() { return isShaking; }
    public static boolean isDeathTriggered() { return deathTriggered; }

    // === 📥 GETTERS (с игроком - для совместимости) ===
    public static float getCurrentSanity(EntityPlayer player) { return getCurrentSanity(); }
    public static void setSanity(EntityPlayer player, float sanity) { setSanity(sanity); }

    // === ⚙️ SETTERS ===
    public static void setSanity(float sanity) {
        currentSanity = Math.max(0, Math.min(100, sanity));
        updateTexture();
        if (currentSanity <= 0 && !deathTriggered) triggerSanityDeath();
    }

    public static void addSanity(float amount) { setSanity(currentSanity + amount); }

    // === 🔴 ВСПЫШКА ===
    public static void triggerFlash() {
        isFlashingRed = true;
        flashTimer = 0;
    }

    // === 📳 ТРЯСКА ===
    public static void triggerShake() {
        isShaking = true;
        shakeTimer = 0;
    }

    // === 🔄 ОБНОВЛЕНИЕ ЭФФЕКТОВ ===
    public static void updateEffects() {
        if (isFlashingRed && ++flashTimer >= FLASH_DURATION) {
            isFlashingRed = false;
            flashTimer = 0;
        }
        if (isShaking && ++shakeTimer >= SHAKE_DURATION) {
            isShaking = false;
            shakeTimer = 0;
        }
    }

    // === 🎨 ОБНОВЛЕНИЕ ТЕКСТУРЫ ===
    private static void updateTexture() {
        int s = (int) currentSanity;
        if (s >= 91) currentTexture = BRAIN_100;
        else if (s >= 81) currentTexture = BRAIN_90;
        else if (s >= 71) currentTexture = BRAIN_80;
        else if (s >= 61) currentTexture = BRAIN_70;
        else if (s >= 51) currentTexture = BRAIN_60;
        else if (s >= 41) currentTexture = BRAIN_50;
        else if (s >= 31) currentTexture = BRAIN_40;
        else if (s >= 21) currentTexture = BRAIN_30;
        else if (s >= 11) currentTexture = BRAIN_20;
        else currentTexture = BRAIN_10;
    }

    // === 💀 СМЕРТЬ ОТ РАССУДКА ===
    private static void triggerSanityDeath() {
        deathTriggered = true;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.player.capabilities.isCreativeMode) return;

        String name = mc.player.getName();
        if (mc.isSingleplayer() && mc.getIntegratedServer() != null) {
            EntityPlayerMP serverPlayer = mc.getIntegratedServer().getPlayerList()
                    .getPlayerByUUID(mc.player.getUniqueID());
            if (serverPlayer != null) {
                serverPlayer.setHealth(0);
                TextComponentString msg = new TextComponentString(BrainConfig.getRandomDeathMessage(name));
                mc.getIntegratedServer().getPlayerList().sendMessage(msg);
            }
        }
    }

    // === 🔄 СБРОС ПОСЛЕ СМЕРТИ ===
    public static void resetAfterDeath() {
        currentSanity = BrainConfig.SANITY_AFTER_DEATH;
        deathTriggered = false;
        updateTexture();
        isFlashingRed = false;
        isShaking = false;
        flashTimer = 0;
        shakeTimer = 0;
    }

    // === 🔄 ПОЛНЫЙ СБРОС ===
    public static void reset() {
        currentSanity = 100.0f;
        deathTriggered = false;
        updateTexture();
        isFlashingRed = false;
        isShaking = false;
        flashTimer = 0;
        shakeTimer = 0;
    }
}