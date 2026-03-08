package com.stefanparmezan.atomic_parasites.brain;

public class BrainConfig {

    // === 🌑 Окружение ===
    public static final int DARKNESS_LIGHT_THRESHOLD = 8;
    public static final int BRIGHT_LIGHT_THRESHOLD = 11;
    public static final int ENV_DECAY_INTERVAL = 35;
    public static final int ENV_RECOVERY_INTERVAL = 130;

    // === 💔 Физика ===
    public static final int HP_LOSS_THRESHOLD = 4;
    public static final int SANITY_PENALTY = 1;
    public static final double MOTION_THRESHOLD = 0.8;
    public static final int EXPLOSION_COOLDOWN = 60;

    // === 💀 Смерть от рассудка ===
    public static final int SANITY_AFTER_DEATH = 30;
    public static final String[] DEATH_MESSAGES = {
            "%player% решил подчиниться голосам в голове",
            "%player% потерял рассудок окончательно",
            "%player% растворился в безумии",
            "Рассудок %player% не выдержал этого мира",
            "%player% решил поспорить с голосом в голове"
    };

    // === 😰 СТРАХ СМЕРТИ ===
    /** Здоровье для АКТИВАЦИИ страха (в полу-сердцах, 4 = 2 сердца) */
    public static final float FEAR_TRIGGER_HP = 4.0f;
    /** Здоровье для ДЕАКТИВАЦИИ страха (в полу-сердцах, 8 = 4 сердца) */
    public static final float FEAR_CLEAR_HP = 6.0f;
    /** Длительность эффекта "бесконечная" (управляем вручную через HP) */
    public static final int FEAR_DURATION = 10000;
    /** Потеря рассудка в секунду при страхе */
    public static final float FEAR_SANITY_DRAIN_PER_SEC = 1.3f;
    /** Множитель скорости при страхе (0.4 = +40%) */
    public static final double FEAR_SPEED_MULTIPLIER = 0.30;
    /** Интенсивность тряски камеры */
    public static final float FEAR_CAMERA_SHAKE_INTENSITY = 10.0f;
    /** Интервал между звуками дыхания (в тиках) */
    public static final int FEAR_SOUND_INTERVAL_TICKS = 14;
    /** Длительность эффекта слепоты при обновлении (в тиках) */
    public static final int FEAR_BLINDNESS_DURATION = 200;
    /** Порог длительности для обновления слепоты (чтобы не мерцала) */
    public static final int FEAR_BLINDNESS_REFRESH_THRESHOLD = 50;

    // === 🕯️ Светящиеся предметы ===
    /** Минимальный уровень света предмета для защиты */
    public static final int ITEM_LIGHT_MIN_LEVEL = 10;
    /** Порог для высокого множителя (13-15) */
    public static final int ITEM_LIGHT_HIGH_THRESHOLD = 13;
    /** Множитель для света 10-12 */
    public static final int ITEM_LIGHT_MULTIPLIER_LOW = 2;
    /** Множитель для света 13-15 */
    public static final int ITEM_LIGHT_MULTIPLIER_HIGH = 3;

    // === 🌸 Восстановление от цветов ===
    /** Радиус поиска цветов */
    public static final int FLOWER_RADIUS = 5;
    /** Минимальное количество цветов для бонуса */
    public static final int FLOWER_MIN_COUNT = 2;
    /** Бонус к множителю восстановления от цветов */
    public static final float FLOWER_RECOVERY_BONUS = 0.30f;
    /** Минимальный свет для активации бонуса цветов */
    public static final int FLOWER_MIN_LIGHT = 10;

    // === 😴 Сон ===
    /** Процент восстановления рассудка при пропуске ночи */
    public static final float SLEEP_RECOVERY_PERCENT = 0.25f;
    /** Порог времени для определения пропуска ночи (в тиках) */
    public static final int NIGHT_SKIP_THRESHOLD = 120;

    // === 💥 Визуальные эффекты ===
    /** Длительность красной вспышки (в тиках) */
    public static final int FLASH_DURATION = 20;
    /** Длительность тряски от урона (в тиках) */
    public static final int SHAKE_DURATION = 30;

    // === 📝 Вспомогательные методы ===
    public static String getRandomDeathMessage(String playerName) {
        String msg = DEATH_MESSAGES[(int)(Math.random() * DEATH_MESSAGES.length)];
        return msg.replace("%player%", playerName);
    }
}