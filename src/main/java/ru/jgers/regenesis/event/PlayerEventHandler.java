package ru.jgers.regenesis.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.jgers.regenesis.RegenesisMod;
import ru.jgers.regenesis.capability.MarkOfCainCapabilities;
import ru.jgers.regenesis.network.SyncPacket;

import java.util.EnumSet;
import java.util.Random;


public class PlayerEventHandler {
    public static final ResourceLocation LIMBO = ResourceLocation.fromNamespaceAndPath(RegenesisMod.MODID, "limbo");
    public static final int LIMBO_TIME = 2400;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        if (event.type != TickEvent.Type.PLAYER || player.level().isClientSide()) return;

        player.getCapability(MarkOfCainCapabilities.CAPABILITY).ifPresent(data -> {
            if (player.level().dimension().location().equals(LIMBO) || data.getTickCount() >= LIMBO_TIME) {
                data.incrementTickCount();
                SyncPacket.sync(player);
                if (data.getTickCount() >= LIMBO_TIME) {
                    respawnPlayer(player);
                    if (data.getTickCount() >= LIMBO_TIME + 200) {
                        data.setTickCount(0);
                        SyncPacket.sync(player);
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        ServerLevel limbo = player.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, PlayerEventHandler.LIMBO));

        if (limbo == null || player.getLastDeathLocation().isEmpty()) return;

        BlockPos pos = player.getLastDeathLocation().get().pos();

        if (!player.level().isClientSide()) {
            player.teleportTo(limbo, pos.getX(), pos.getY(), pos.getZ(),
                    EnumSet.noneOf(RelativeMovement.class), player.getYRot(), player.getXRot());

            player.getCapability(MarkOfCainCapabilities.CAPABILITY).ifPresent(data -> {
                data.incrementYearCount();
                SyncPacket.sync(player);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(MarkOfCainCapabilities.CAPABILITY).ifPresent(oldStore -> {
                event.getEntity().getCapability(MarkOfCainCapabilities.CAPABILITY).ifPresent(newStore -> {
                    newStore.setYearCount(oldStore.getYearCount());
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onLogged(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        SyncPacket.sync(player);
    }

    public static void respawnPlayer(Player player) {
        BlockPos spawnPos = ((ServerPlayer) player).getRespawnPosition();
        if (spawnPos != null) {
            player.teleportTo(player.getServer().overworld(), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(),
                    EnumSet.noneOf(RelativeMovement.class), player.getYRot(), player.getXRot());
        }
        else {
            BlockPos worldPos = player.level().getSharedSpawnPos();
            if (player.getServer() != null && player.getServer().getLevel(Level.OVERWORLD) != null) {
                buildGrave(worldPos, player.getServer().getLevel(Level.OVERWORLD));
            }
            player.teleportTo(player.getServer().overworld(), worldPos.getCenter().x, worldPos.getY() - 2, worldPos.getCenter().z,
                    EnumSet.noneOf(RelativeMovement.class), player.getYRot(), player.getXRot());
        }
    }

    public static void buildGrave(BlockPos pos, Level level) {
        if (level.isClientSide) return;

        Random random = new Random();

        level.setBlock(pos.above(1), Blocks.AIR.defaultBlockState(), 0);

        level.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 11);
        placeDirt(pos.below(), level);

        level.setBlock(pos.below(2), Blocks.AIR.defaultBlockState(), 11);
        placeDirt(pos.below(2), level);
        level.getLightEngine().checkBlock(pos.below(2));

        level.setBlock(pos.below(3), Blocks.DIRT.defaultBlockState(), 11);

        Direction[] horizontals = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        if (level.getBlockState(pos.relative(horizontals[0])).is(Blocks.COBBLESTONE) ||
                level.getBlockState(pos.relative(horizontals[1])).is(Blocks.COBBLESTONE) ||
                level.getBlockState(pos.relative(horizontals[2])).is(Blocks.COBBLESTONE) ||
                level.getBlockState(pos.relative(horizontals[3])).is(Blocks.COBBLESTONE)) {
            return;
        }

        Direction randomDir = horizontals[random.nextInt(horizontals.length)];
        BlockPos stonePos = pos.relative(randomDir);
        level.setBlock(stonePos, Blocks.COBBLESTONE.defaultBlockState(), 11);

    }

    public static void placeDirt(BlockPos pos, Level level) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            level.setBlock(pos.relative(direction), Blocks.DIRT.defaultBlockState(), 11);
        }
    }
}
