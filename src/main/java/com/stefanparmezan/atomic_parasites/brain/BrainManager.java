package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.util.ResourceLocation;

public class BrainManager {

    // === Текстуры мозга ===
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

    // === Состояние ===
    private static float currentSanity = 100.0f;
    private static ResourceLocation currentTexture = BRAIN_100;

    // === Эффекты ===
    private static boolean isFlashingRed = false;
    private static int flashTimer = 0;
    private static final int FLASH_DURATION = 20; // 1 секунда

    private static boolean isShaking = false;
    private static int shakeTimer = 0;
    private static final int SHAKE_DURATION = 30; // 1.5 секунды

    // === Геттеры ===
    public static float getCurrentSanity() { return currentSanity; }
    public static ResourceLocation getCurrentBrainTexture() { return currentTexture; }
    public static boolean isFlashingRed() { return isFlashingRed; }
    public static boolean isShaking() { return isShaking; }

    // === Сеттеры и утилиты ===
    public static void setSanity(float sanity) {
        currentSanity = Math.max(0, Math.min(100, sanity));
        updateTexture();
    }

    public static void addSanity(float amount) {
        setSanity(currentSanity + amount);
    }

    public static void triggerFlash() {
        isFlashingRed = true;
        flashTimer = 0;
    }

    public static void triggerShake() {
        isShaking = true;
        shakeTimer = 0;
    }

    public static void updateEffects() {
        if (isFlashingRed) {
            flashTimer++;
            if (flashTimer >= FLASH_DURATION) {
                isFlashingRed = false;
                flashTimer = 0;
            }
        }
        if (isShaking) {
            shakeTimer++;
            if (shakeTimer >= SHAKE_DURATION) {
                isShaking = false;
                shakeTimer = 0;
            }
        }
    }

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

    public static void reset() {
        currentSanity = 100.0f;
        currentTexture = BRAIN_100;
        isFlashingRed = false;
        isShaking = false;
        flashTimer = 0;
        shakeTimer = 0;
    }
}