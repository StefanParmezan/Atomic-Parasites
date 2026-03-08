package com.stefanparmezan.atomic_parasites.util;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoundHelper {

    private static final float DEFAULT_VOLUME = 1.0f;
    private static final float DEFAULT_PITCH = 1.0f;
    private static final boolean DEBUG_ENABLED = true;

    // === 🔁 ОТСЛЕЖИВАНИЕ ПОСЛЕДНЕГО ВОСПРОИЗВЕДЕНИЯ (чтобы не накладывались) ===
    private static final Map<UUID, Long> lastPlayTime = new HashMap<>();
    private static final int MIN_INTERVAL_MS = 800; // Мин. интервал между звуками (мс)

    // === 🔊 ВОСПРОИЗВЕДЕНИЕ ЗВУКА ИГРОКУ (с защитой от наложения) ===
    public static void playSoundToPlayer(EntityPlayer player, SoundEvent sound) {
        playSoundToPlayer(player, sound, DEFAULT_VOLUME, DEFAULT_PITCH);
    }

    public static void playSoundToPlayer(EntityPlayer player, SoundEvent sound, float volume, float pitch) {
        if (player == null || sound == null) {
            logError("playSoundToPlayer: player or sound is null");
            return;
        }

        // === ПРОВЕРКА: не слишком ли часто играем звук этому игроку? ===
        UUID id = player.getUniqueID();
        long now = System.currentTimeMillis();
        Long lastTime = lastPlayTime.get(id);

        if (lastTime != null && (now - lastTime) < MIN_INTERVAL_MS) {
            if (DEBUG_ENABLED) {
                AtomicParasites.LOGGER.debug("[SoundHelper] ⏭️ Skipping sound (cooldown): {}ms remaining",
                        MIN_INTERVAL_MS - (now - lastTime));
            }
            return; // Пропускаем, если кулдаун не прошёл
        }

        // Запоминаем время воспроизведения
        lastPlayTime.put(id, now);

        if (DEBUG_ENABLED) {
            AtomicParasites.LOGGER.info("[SoundHelper] 🔊 Playing '{}' to {} | Volume: {} | Pitch: {}",
                    sound.getRegistryName(), player.getName(), volume, pitch);
        }

        try {
            player.playSound(sound, volume, pitch);
            if (DEBUG_ENABLED) {
                AtomicParasites.LOGGER.debug("[SoundHelper] ✅ playSound() called successfully");
            }
        } catch (Exception e) {
            logError("Failed to play sound '{}' to player '{}': {}",
                    sound.getRegistryName(), player.getName(), e.getMessage());
        }
    }

    // === 🌍 ВОСПРОИЗВЕДЕНИЕ ЗВУКА В МИРЕ ===
    public static void playSoundAtPosition(World world, double x, double y, double z, SoundEvent sound) {
        playSoundAtPosition(world, x, y, z, sound, DEFAULT_VOLUME, DEFAULT_PITCH);
    }

    public static void playSoundAtPosition(World world, double x, double y, double z,
                                           SoundEvent sound, float volume, float pitch) {
        if (world == null || sound == null) {
            logError("playSoundAtPosition: world or sound is null");
            return;
        }

        if (DEBUG_ENABLED) {
            AtomicParasites.LOGGER.info("[SoundHelper] 🔊 Playing '{}' at ({}, {}, {}) | Volume: {} | Pitch: {}",
                    sound.getRegistryName(), x, y, z, volume, pitch);
        }

        try {
            world.playSound(null, x, y, z, sound, SoundCategory.HOSTILE, volume, pitch);
            if (DEBUG_ENABLED) {
                AtomicParasites.LOGGER.debug("[SoundHelper] ✅ world.playSound() called successfully");
            }
        } catch (Exception e) {
            logError("Failed to play sound '{}' at ({}, {}, {}): {}",
                    sound.getRegistryName(), x, y, z, e.getMessage());
        }
    }

    // === 🎲 ЗВУК С РАНДОМИЗИРОВАННЫМ ПИТЧЕМ ===
    public static void playSoundWithRandomPitch(EntityPlayer player, SoundEvent sound, float volume, float pitchVariation) {
        if (player == null || sound == null) return;
        float pitch = 1.0f + (player.getRNG().nextFloat() - 0.5f) * pitchVariation;
        playSoundToPlayer(player, sound, volume, pitch);
    }

    // === 🎲 ЗВУК С РАНДОМИЗИРОВАННОЙ ГРОМКОСТЬЮ ===
    public static void playSoundWithRandomVolume(EntityPlayer player, SoundEvent sound, float volumeVariation, float pitch) {
        if (player == null || sound == null) return;
        float volume = DEFAULT_VOLUME + (player.getRNG().nextFloat() - 0.5f) * volumeVariation;
        volume = Math.max(0.0f, Math.min(1.0f, volume));
        playSoundToPlayer(player, sound, volume, pitch);
    }

    // === 🎲 ПОЛНЫЙ РАНДОМ (питч + громкость) ===
    public static void playSoundWithRandomness(EntityPlayer player, SoundEvent sound,
                                               float baseVolume, float volumeVariation,
                                               float basePitch, float pitchVariation) {
        if (player == null || sound == null) return;

        float volume = Math.max(0.0f, Math.min(1.0f, baseVolume + (player.getRNG().nextFloat() - 0.5f) * volumeVariation));
        float pitch = basePitch + (player.getRNG().nextFloat() - 0.5f) * pitchVariation;

        playSoundToPlayer(player, sound, volume, pitch);
    }

    // === 🧪 ПРОВЕРКА: загружен ли звук ===
    public static boolean isSoundLoaded(SoundEvent sound) {
        if (sound == null) return false;
        boolean loaded = sound.getRegistryName() != null;
        if (DEBUG_ENABLED) {
            AtomicParasites.LOGGER.debug("[SoundHelper] 🧪 Sound '{}' loaded: {}",
                    sound.getRegistryName(), loaded);
        }
        return loaded;
    }

    // === 🗑️ ОЧИСТКА ДАННЫХ ПРИ ВЫХОДЕ ИГРОКА ===
    public static void onPlayerLogout(EntityPlayer player) {
        if (player != null) {
            lastPlayTime.remove(player.getUniqueID());
        }
    }

    // === 📝 ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===
    private static void logError(String message, Object... args) {
        AtomicParasites.LOGGER.error("[SoundHelper] ❌ " + message, args);
    }

    private static void logDebug(String message, Object... args) {
        if (DEBUG_ENABLED) {
            AtomicParasites.LOGGER.debug("[SoundHelper] 🔍 " + message, args);
        }
    }

    public static void setDebugEnabled(boolean enabled) {}
}