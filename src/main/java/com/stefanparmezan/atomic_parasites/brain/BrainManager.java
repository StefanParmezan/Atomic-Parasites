package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public class BrainManager {

    public static final ResourceLocation BRAIN_100 = new ResourceLocation("atomic_parasites", "textures/brain/brain100pr.png");
    public static final ResourceLocation BRAIN_90 = new ResourceLocation("atomic_parasites", "textures/brain/brain90pr.png");
    public static final ResourceLocation BRAIN_80 = new ResourceLocation("atomic_parasites", "textures/brain/brain80pr.png");
    public static final ResourceLocation BRAIN_70 = new ResourceLocation("atomic_parasites", "textures/brain/brain70pr.png");
    public static final ResourceLocation BRAIN_60 = new ResourceLocation("atomic_parasites", "textures/brain/brain60pr.png");
    public static final ResourceLocation BRAIN_50 = new ResourceLocation("atomic_parasites", "textures/brain/brain50pr.png");
    public static final ResourceLocation BRAIN_40 = new ResourceLocation("atomic_parasites", "textures/brain/brain40pr.png");
    public static final ResourceLocation BRAIN_30 = new ResourceLocation("atomic_parasites", "textures/brain/brain30pr.png");
    public static final ResourceLocation BRAIN_20 = new ResourceLocation("atomic_parasites", "textures/brain/brain20pr.png");
    public static final ResourceLocation BRAIN_10 = new ResourceLocation("atomic_parasites", "textures/brain/brain10pr.png");

    private static final int DECAY_INTERVAL = 60;
    private static final int LIGHT_THRESHOLD = 5;

    private static float currentSanity = 100.0f;
    private static int darknessTimer = 0;
    private static ResourceLocation currentTexture = BRAIN_100;

    // Эффекты
    private static boolean isFlashingRed = false;
    private static int flashTimer = 0;
    private static final int FLASH_DURATION = 20;

    private static boolean isShaking = false;
    private static int shakeTimer = 0;
    private static final int SHAKE_DURATION = 30;

    // Для обнаружения урона/взрыва
    private static float lastHealth = -1;
    private static double lastMotionX = 0, lastMotionY = 0, lastMotionZ = 0;
    private static int explosionCooldown = 0;

    public static void update(AbstractClientPlayer player) {
        float currentHealth = player.getHealth();

        // === ОБНАРУЖЕНИЕ УРОНА (сравнение здоровья) ===
        if (lastHealth > 0 && currentHealth < lastHealth - 0.1f) {
            triggerFlash();
        }
        lastHealth = currentHealth;

        // === ОБНАРУЖЕНИЕ ВЗРЫВА (резкое изменение motion) ===
        if (explosionCooldown <= 0) {
            double dx = Math.abs(player.motionX - lastMotionX);
            double dy = Math.abs(player.motionY - lastMotionY);
            double dz = Math.abs(player.motionZ - lastMotionZ);
            double motionChange = dx + dy + dz;

            if (motionChange > 0.8 && !player.onGround) {
                triggerShake();
                explosionCooldown = 60;
            }
        } else {
            explosionCooldown--;
        }
        lastMotionX = player.motionX;
        lastMotionY = player.motionY;
        lastMotionZ = player.motionZ;

        // === Обновление эффектов ===
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

        // === Снижение рассудка в темноте ===
        if (isInDarkness(player)) {
            darknessTimer++;
            if (darknessTimer >= DECAY_INTERVAL && currentSanity > 0) {
                currentSanity = Math.max(0, currentSanity - 1);
                darknessTimer = 0;
                updateTexture();
            }
        } else {
            darknessTimer = 0;
        }
    }

    private static boolean isInDarkness(AbstractClientPlayer player) {
        BlockPos headPos = new BlockPos(player.posX, player.posY + 1.6, player.posZ);
        if (!player.world.isBlockLoaded(headPos)) return false;
        int blockLight = player.world.getLightFromNeighbors(headPos);
        int skyLight = player.world.getLightFor(EnumSkyBlock.SKY, headPos);
        boolean isNight = skyLight < 4;
        return (isNight && blockLight <= LIGHT_THRESHOLD) || blockLight <= 2;
    }

    private static void updateTexture() {
        int s = (int) currentSanity;
        if (s >= 91)      currentTexture = BRAIN_100;
        else if (s >= 81) currentTexture = BRAIN_90;
        else if (s >= 71) currentTexture = BRAIN_80;
        else if (s >= 61) currentTexture = BRAIN_70;
        else if (s >= 51) currentTexture = BRAIN_60;
        else if (s >= 41) currentTexture = BRAIN_50;
        else if (s >= 31) currentTexture = BRAIN_40;
        else if (s >= 21) currentTexture = BRAIN_30;
        else if (s >= 11) currentTexture = BRAIN_20;
        else              currentTexture = BRAIN_10;
    }

    // === ТРИГГЕРЫ ЭФФЕКТОВ ===
    private static void triggerFlash() {
        isFlashingRed = true;
        flashTimer = 0;
    }

    private static void triggerShake() {
        isShaking = true;
        shakeTimer = 0;
    }

    // === ПУБЛИЧНЫЕ МЕТОДЫ ===
    public static boolean isFlashingRed() { return isFlashingRed; }
    public static boolean isShaking() { return isShaking; }
    public static float getCurrentSanity() { return currentSanity; }
    public static ResourceLocation getCurrentBrainTexture() { return currentTexture; }
    public static void setSanity(float sanity) { currentSanity = Math.max(0, Math.min(100, sanity)); updateTexture(); }
    public static void reset() {
        currentSanity = 100.0f; darknessTimer = 0; currentTexture = BRAIN_100;
        isFlashingRed = false; isShaking = false; flashTimer = 0; shakeTimer = 0;
        lastHealth = -1; lastMotionX = lastMotionY = lastMotionZ = 0; explosionCooldown = 0;
    }
}