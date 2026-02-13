package com.stefanparmezan.atomic_parasites.events;

import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = AtomicParasitesInfo.MOD_ID)
public class SpawnStructureHandler {

    private static final String DATA_NAME = AtomicParasitesInfo.MOD_ID + "_pod_placed";
    private static final String BARREL_BLOCK_NAME = "hbm:barrel_corroded"; // Полное название блока

    public static class ModWorldData extends WorldSavedData {
        private boolean podPlaced = false;

        public ModWorldData() { super(DATA_NAME); }
        public ModWorldData(String name) { super(name); }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            podPlaced = nbt.getBoolean("podPlaced");
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            compound.setBoolean("podPlaced", podPlaced);
            return compound;
        }

        public boolean isPodPlaced() { return podPlaced; }
        public void setPodPlaced(boolean value) { this.podPlaced = value; markDirty(); }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        World world = event.player.world;
        if (world.isRemote) return;
        if (world.provider.getDimension() != 0) return;
        if (!(world instanceof WorldServer)) return;

        WorldServer worldServer = (WorldServer) world;

        // Загружаем данные мира
        ModWorldData data = (ModWorldData) worldServer.getMapStorage()
                .getOrLoadData(ModWorldData.class, DATA_NAME);
        if (data == null) {
            data = new ModWorldData();
            worldServer.getMapStorage().setData(DATA_NAME, data);
        }

        // Если капсула ещё не размещена — размещаем
        if (!data.isPodPlaced()) {
            BlockPos barrelPos = placeCargoPod(worldServer);
            if (barrelPos != null) {
                // Телепортируем игрока на ржавую бочку
                event.player.setPositionAndUpdate(
                        barrelPos.getX() + 0.5,
                        barrelPos.getY() + 1, // На 1 блок выше бочки
                        barrelPos.getZ() + 0.5
                );

                System.out.println("[AtomicParasites] Игрок телепортирован на ржавую бочку: " + barrelPos);
            } else {
                System.out.println("[AtomicParasites] Ржавая бочка не найдена в структуре!");
            }
            data.setPodPlaced(true);
        }
    }

    /**
     * Размещает капсулу и возвращает координаты ржавой бочки (или null)
     */
    private static BlockPos placeCargoPod(WorldServer world) {
        // Загружаем шаблон
        ResourceLocation loc = new ResourceLocation(AtomicParasitesInfo.MOD_ID, "cargo_pod");
        Template template = world.getSaveHandler().getStructureTemplateManager()
                .getTemplate(world.getMinecraftServer(), loc);
        if (template == null) {
            System.out.println("[AtomicParasites] cargo_pod.nbt не найден!");
            return null;
        }

        BlockPos size = template.getSize();

        // Получаем точку спавна мира
        BlockPos spawnPoint = world.getSpawnPoint();

        // Вычисляем позицию для структуры (центрируем)
        BlockPos placementPos = new BlockPos(
                spawnPoint.getX() - size.getX() / 2,
                0,
                spawnPoint.getZ() - size.getZ() / 2
        );

        // Высота земли
        int groundY = getAverageGroundHeight(world, placementPos, size);

        // Заглубляем на 11 блоков
        placementPos = new BlockPos(placementPos.getX(), groundY - 11, placementPos.getZ());

        // Размещаем структуру
        PlacementSettings settings = new PlacementSettings()
                .setMirror(Mirror.NONE)
                .setRotation(Rotation.NONE)
                .setIgnoreEntities(false)
                .setChunk(null);

        template.addBlocksToWorld(world, placementPos, settings, 2 | 16);

        // Ищем ржавую бочку
        BlockPos barrelPos = findBarrel(world, placementPos, size);
        if (barrelPos != null) {
            // Меняем точку спавна мира (для будущих заходов)
            world.setSpawnPoint(new BlockPos(barrelPos.getX(), barrelPos.getY() + 1, barrelPos.getZ()));
            System.out.println("[AtomicParasites] Капсула размещена, бочка найдена на " + barrelPos);
        }

        return barrelPos;
    }

    private static int getAverageGroundHeight(WorldServer world, BlockPos start, BlockPos size) {
        int total = 0, count = 0;
        for (int x = start.getX(); x < start.getX() + size.getX(); x += 4) {
            for (int z = start.getZ(); z < start.getZ() + size.getZ(); z += 4) {
                BlockPos top = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
                total += top.getY();
                count++;
            }
        }
        return count == 0 ? 64 : total / count;
    }

    /**
     * Ищет ржавую бочку (hbm:barrel_coroded) в структуре
     */
    private static BlockPos findBarrel(WorldServer world, BlockPos start, BlockPos size) {
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockPos check = start.add(x, y, z);

                    // Получаем блок и проверяем его registry name
                    net.minecraft.block.Block block = world.getBlockState(check).getBlock();
                    net.minecraft.util.ResourceLocation registryName = block.getRegistryName();

                    if (registryName != null && registryName.toString().equals(BARREL_BLOCK_NAME)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}