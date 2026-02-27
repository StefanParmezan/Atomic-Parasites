package com.stefanparmezan.atomic_parasites.events;

import net.minecraft.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class ParasitesPhaseEventHandler {

    private static final long PHASE_TRANSITION_TICKS = 500L;

    private static final Map<Integer, Long> worldPhaseTimers = new HashMap<>();

    public static void startPhaseTimer(World world) {
        if (world.isRemote) return;

        int dimension = world.provider.getDimension();

        setPhase(world, -1);
        worldPhaseTimers.putIfAbsent(dimension, 0L);
    }

    /**
     * Устанавливает фазу через команду SRP.
     */
    private static void setPhase(World world, int phase) {
        MinecraftServer server = world.getMinecraftServer();
        if (server != null) {
            // Команда без dimension — применяет ко всем или к текущему
            server.getCommandManager().executeCommand(server,
                    "/srpevolution setphase " + phase);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;

        int dimension = event.world.provider.getDimension();

        if (worldPhaseTimers.containsKey(dimension)) {
            long currentTicks = worldPhaseTimers.get(dimension);

            if (currentTicks >= PHASE_TRANSITION_TICKS) {
                addEvolutionPoints(event.world, 150);
                worldPhaseTimers.remove(dimension);
            } else {
                worldPhaseTimers.put(dimension, currentTicks + 1);
            }
        }
    }

    /**
     * Добавляет очки эволюции.
     */
    private static void addEvolutionPoints(World world, int points) {
        MinecraftServer server = world.getMinecraftServer();
        if (server != null) {
            server.getCommandManager().executeCommand(server,
                    "/srpevolution addpoints " + points);
        }
    }

    public static void resetTimer(int dimension) {
        worldPhaseTimers.remove(dimension);
    }

    public static boolean isTimerActive(int dimension) {
        return worldPhaseTimers.containsKey(dimension);
    }
}