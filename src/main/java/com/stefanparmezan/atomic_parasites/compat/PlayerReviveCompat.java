package com.stefanparmezan.atomic_parasites.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

public class PlayerReviveCompat {

    // ✅ Проверяем, загружен ли мод
    public static boolean isPlayerReviveLoaded() {
        return Loader.isModLoaded("playerrevive");
    }

    /**
     * Проверяет, находится ли игрок в состоянии "downed" (лежит, таймер тикает)
     * @param player игрок
     * @return true если игрок в состоянии ожидания ревива
     */
    public static boolean isPlayerDowned(EntityPlayer player) {
        if (!isPlayerReviveLoaded() || player == null) {
            return false;
        }

        try {
            // === СПОСОБ 1: Через Capability (если мод использует его) ===
            // PlayerRevive может предоставлять capability для проверки состояния
            // Если этот способ не работает — попробуй Способ 2

            // === СПОСОБ 2: Через отражение (универсальный) ===
            // Ищем метод/поле в классе игрока или его расширениях
            Class<?> playerClass = player.getClass();

            // Проверяем, есть ли метод isDowned() или similar
            try {
                java.lang.reflect.Method isDownedMethod = playerClass.getMethod("isDowned");
                if (isDownedMethod != null) {
                    return (Boolean) isDownedMethod.invoke(player);
                }
            } catch (NoSuchMethodException ignored) {}

            // Проверяем через NBT/Entity data (PlayerRevive может хранить флаг)
            if (player.hasTagCompound()) {
                if (player.getEntityData().hasKey("playerrevive:downed") ||
                        player.getEntityData().hasKey("isDowned") ||
                        player.getEntityData().hasKey("downed")) {
                    return player.getEntityData().getBoolean("playerrevive:downed") ||
                            player.getEntityData().getBoolean("isDowned") ||
                            player.getEntityData().getBoolean("downed");
                }
            }

            // === СПОСОБ 3: Проверка через событие/сеть (если другие не сработали) ===
            // PlayerRevive может использовать сетевые пакеты — тогда нужна подписка на события

        } catch (Exception e) {
            // Если что-то пошло не так — считаем, что игрок НЕ downed (безопасный фоллбэк)
            com.stefanparmezan.atomic_parasites.main.AtomicParasites.LOGGER.debug(
                    "[PlayerReviveCompat] Error checking downed state: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Безопасная проверка: можно ли применять логику рассудка к этому игроку?
     */
    public static boolean canApplySanityLogic(EntityPlayer player) {
        // Если игрок в творческом режиме — не применяем
        if (player != null && player.capabilities.isCreativeMode) {
            return false;
        }
        // Если игрок "downed" в PlayerRevive — приостанавливаем логику
        if (isPlayerDowned(player)) {
            return false;
        }
        return true;
    }
}