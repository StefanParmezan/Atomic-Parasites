package com.stefanparmezan.atomic_parasites.brain.effects;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FearOfDeathHandler {

    private static final float TRIGGER_HP_THRESHOLD = 4.0f;   // 2 сердца
    private static final float CLEAR_HP_THRESHOLD = 8.0f;     // 4 сердца
    private static final int FEAR_DURATION = 10000;           // Очень долго (~8 минут)

    private static final Map<UUID, Boolean> fearStates = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.world.isRemote) return;
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        UUID id = player.getUniqueID();
        float hp = player.getHealth();
        Boolean isActive = fearStates.get(id);

        // === АКТИВАЦИЯ ===
        if (hp <= TRIGGER_HP_THRESHOLD && (isActive == null || !isActive)) {
            activateFear(player, id);
            return;
        }

        // === ДЕАКТИВАЦИЯ ===
        if (hp >= CLEAR_HP_THRESHOLD && isActive != null && isActive) {
            deactivateFear(player, id);
            return;
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        fearStates.remove(event.player.getUniqueID());
    }

    private static void activateFear(EntityPlayerMP player, UUID id) {
        fearStates.put(id, true);

        // Добавляем эффект с ОЧЕНЬ долгой длительностью
        player.addPotionEffect(new PotionEffect(PotionFearOfDeath.INSTANCE, FEAR_DURATION, 0, false, false));

        AtomicParasites.LOGGER.info("[Fear] 🔥 ACTIVATED for {} | HP: {} | Duration: {}",
                player.getName(), player.getHealth(), FEAR_DURATION);
    }

    private static void deactivateFear(EntityPlayerMP player, UUID id) {
        fearStates.put(id, false);
        // Удаляем эффект вручную
        player.removePotionEffect(PotionFearOfDeath.INSTANCE);

        AtomicParasites.LOGGER.info("[Fear] ❄️ DEACTIVATED for {} | HP: {}",
                player.getName(), player.getHealth());
    }
}