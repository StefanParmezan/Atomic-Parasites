package com.stefanparmezan.atomic_parasites.player_avatar;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

public class HealthOverlayManager {

    // Процентные пороги здоровья (в долях от 1.0)
    private static final double HP_80 = 0.80;
    private static final double HP_60 = 0.60;
    private static final double HP_40 = 0.40;
    private static final double HP_20 = 0.20;
    private static final double HP_DEATH = 0.15; // 15% и ниже = смертельная текстура

    // Время перехода между текстурами (в тиках, 20 тиков = 1 секунда)
    private static final int TRANSITION_TIME = 20;

    // Текстуры оверлеев
    public static final ResourceLocation OVERLAY_80 = new ResourceLocation("atomic_parasites", "textures/blocks/player_face_80pr.png");
    public static final ResourceLocation OVERLAY_60 = new ResourceLocation("atomic_parasites", "textures/blocks/player_face_60pr.png");
    public static final ResourceLocation OVERLAY_40 = new ResourceLocation("atomic_parasites", "textures/blocks/player_face_40pr.png");
    public static final ResourceLocation OVERLAY_20 = new ResourceLocation("atomic_parasites", "textures/blocks/player_face_20pr.png");
    public static final ResourceLocation OVERLAY_DEATH = new ResourceLocation("atomic_parasites", "textures/blocks/player_face_3hp.png");

    // Текущее состояние
    private static ResourceLocation currentOverlay = null;
    private static ResourceLocation targetOverlay = null;
    private static float transitionProgress = 0.0f;
    private static boolean isFadingOut = false;

    public static void update(AbstractClientPlayer player) {
        double currentHealth = player.getHealth();
        double maxHealth = player.getMaxHealth(); // <-- ИСПОЛЬЗУЕМ РЕАЛЬНОЕ МАКС. ЗДОРОВЬЕ

        // Защита от деления на ноль
        if (maxHealth <= 0) maxHealth = 20.0;

        // Процент здоровья (0.0 - 1.0)
        double healthPercent = currentHealth / maxHealth;

        // Определяем целевую текстуру на основе процента здоровья
        ResourceLocation newTarget;
        if (healthPercent <= HP_DEATH) {
            newTarget = OVERLAY_DEATH;
        } else if (healthPercent <= HP_20) {
            newTarget = OVERLAY_20;
        } else if (healthPercent <= HP_40) {
            newTarget = OVERLAY_40;
        } else if (healthPercent <= HP_60) {
            newTarget = OVERLAY_60;
        } else if (healthPercent <= HP_80) {
            newTarget = OVERLAY_80;
        } else {
            newTarget = null; // Нет оверлея при высоком здоровье
        }

        // Если целевая текстура изменилась — начинаем переход
        if (newTarget != targetOverlay) {
            targetOverlay = newTarget;
            transitionProgress = 0.0f;
            isFadingOut = (newTarget == null);
        }

        // Обновляем прогресс перехода
        if (transitionProgress < 1.0f) {
            if (isFadingOut) {
                transitionProgress = Math.max(0.0f, transitionProgress - (1.0f / TRANSITION_TIME));
                if (transitionProgress <= 0.0f) {
                    transitionProgress = 0.0f;
                    currentOverlay = null;
                    targetOverlay = null;
                }
            } else {
                transitionProgress = Math.min(1.0f, transitionProgress + (1.0f / TRANSITION_TIME));
                if (transitionProgress >= 1.0f) {
                    transitionProgress = 1.0f;
                    currentOverlay = targetOverlay;
                }
            }
        }
    }

    public static ResourceLocation getCurrentOverlay() {
        return currentOverlay;
    }

    public static float getTransitionProgress() {
        return transitionProgress;
    }

    public static boolean shouldRender() {
        return currentOverlay != null && transitionProgress > 0.01f;
    }

    public static void reset() {
        currentOverlay = null;
        targetOverlay = null;
        transitionProgress = 0.0f;
        isFadingOut = false;
    }
}