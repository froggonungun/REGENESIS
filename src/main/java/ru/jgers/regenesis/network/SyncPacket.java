package ru.jgers.regenesis.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import ru.jgers.regenesis.capability.MarkOfCainCapabilities;

import java.util.function.Supplier;

import static ru.jgers.regenesis.RegenesisMod.NETWORK;

public class SyncPacket {
    private final int year;
    private final int tick;

    public SyncPacket(int year, int tick) {
        this.year = year;
        this.tick = tick;
    }

    public static void encode(SyncPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.year);
        buf.writeInt(packet.tick);
    }

    public static SyncPacket decode(FriendlyByteBuf buf) {
        return new SyncPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(SyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(MarkOfCainCapabilities.CAPABILITY).ifPresent(data -> {
                    data.setYearCount(packet.year);
                    data.setTickCount(packet.tick);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sync(Player player) {
        if (!player.level().isClientSide) {
            player.getCapability(MarkOfCainCapabilities.CAPABILITY).ifPresent(data -> {
                NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncPacket(data.getYearCount(), data.getTickCount()));
            });
        }
    }
}