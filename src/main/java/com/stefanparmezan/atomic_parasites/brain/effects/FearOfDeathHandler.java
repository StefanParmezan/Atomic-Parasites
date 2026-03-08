package com.stefanparmezan.atomic_parasites.brain.effects;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import com.stefanparmezan.atomic_parasites.brain.BrainConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FearOfDeathHandler {

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

        if (hp <= BrainConfig.FEAR_TRIGGER_HP && (isActive == null || !isActive)) {
            activateFear(player, id);
            return;
        }

        if (hp >= BrainConfig.FEAR_CLEAR_HP && isActive != null && isActive) {
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
        player.addPotionEffect(new PotionEffect(PotionFearOfDeath.INSTANCE, BrainConfig.FEAR_DURATION, 0, false, false));
        AtomicParasites.LOGGER.info("[Fear] 🔥 ACTIVATED for {} | HP: {} | Duration: {}",
                player.getName(), player.getHealth(), BrainConfig.FEAR_DURATION);
    }

    private static void deactivateFear(EntityPlayerMP player, UUID id) {
        fearStates.put(id, false);
        player.removePotionEffect(PotionFearOfDeath.INSTANCE);
        AtomicParasites.LOGGER.info("[Fear] ❄️ DEACTIVATED for {} | HP: {}",
                player.getName(), player.getHealth());
    }
}