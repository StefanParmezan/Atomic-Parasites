package com.stefanparmezan.atomic_parasites.events;

import net.minecraft.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.Map;

public class ParasitesPhaseEventHandler {

    // ================= НАСТРОЙКИ =================
    private static final boolean DEBUG_MODE = false;
    private static final long TEST_DELAY_TICKS = 600L;           // 30 секунд
    private static final long PRODUCTION_DELAY_TICKS = 48000L;   // 2 дня
    private static final int POINTS_TO_ADD = 150;

    private static final Map<Integer, Long> worldPhaseTimers = new HashMap<>();

    // === КРИТИЧЕСКИ ВАЖНО: Регистрируем на ПРАВИЛЬНОМ EventBus ===
    public static void register() {
        // TickEvent.WorldTickEvent регистрируется на FMLCommonHandler, а не на MinecraftForge!
        FMLCommonHandler.instance().bus().register(ParasitesPhaseEventHandler.class);
    }

    public static void startPhaseTimer(World world) {
        if (world.isRemote) return;
        int dimension = world.provider.getDimension();

        setPhase(world, -1);
        worldPhaseTimers.put(dimension, 0L);

    }

    private static void setPhase(World world, int phase) {
        MinecraftServer server = world.getMinecraftServer();
        if (server != null) {
            server.getCommandManager().executeCommand(server, "/srpevolution setphase " + phase);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;
        int dimension = event.world.provider.getDimension();

        if (worldPhaseTimers.containsKey(dimension)) {
            long delayTicks = DEBUG_MODE ? TEST_DELAY_TICKS : PRODUCTION_DELAY_TICKS;
            long currentTicks = worldPhaseTimers.get(dimension);

            if (currentTicks >= delayTicks) {
                MinecraftServer server = event.world.getMinecraftServer();
                if (server != null) {
                    // 👇 ИСПРАВЛЕНИЕ: amount перед dimension!
                    int result = server.getCommandManager().executeCommand(server,
                            "/srpevolution addpoints " + POINTS_TO_ADD + " " + dimension);

                    if (result == 0) {
                    } else {
                    }
                }
                worldPhaseTimers.remove(dimension);
            } else {
                worldPhaseTimers.put(dimension, currentTicks + 1);
            }
        }
    }

}