package ru.jgers.regenesis;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import ru.jgers.regenesis.capability.MarkOfCainCapabilities;
import ru.jgers.regenesis.capability.MarkOfCainDataProvider;
import ru.jgers.regenesis.event.client.LimboDimensionEffects;
import ru.jgers.regenesis.event.MobEventHandler;
import ru.jgers.regenesis.event.PlayerEventHandler;
import ru.jgers.regenesis.network.SyncPacket;

@Mod(RegenesisMod.MODID)
public class RegenesisMod {
    public static final String MODID = "regenesis";

    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(MODID, "network"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(version -> true)
            .serverAcceptedVersions(version -> true)
            .simpleChannel();


    public RegenesisMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(MobEventHandler.class);

        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.class);
        
        modEventBus.register(MarkOfCainCapabilities.class);

        NETWORK.messageBuilder(SyncPacket.class, 0)
                .encoder(SyncPacket::encode)
                .decoder(SyncPacket::decode)
                .consumerMainThread(SyncPacket::handle)
                .add();

        LOGGER.info("Register hit");

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(MarkOfCainCapabilities.CAPABILITY).isPresent()) {
                event.addCapability(ResourceLocation.fromNamespaceAndPath(MODID, "year_capability"), new MarkOfCainDataProvider(player));
            }
        }
    }

    @SubscribeEvent
    public void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        ResourceLocation limboId = ResourceLocation.fromNamespaceAndPath("markofcain", "limbo");
        event.register(limboId, new LimboDimensionEffects());
    }
}
