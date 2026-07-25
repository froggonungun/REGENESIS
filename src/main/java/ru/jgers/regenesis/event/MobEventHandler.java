package ru.jgers.regenesis.event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static ru.jgers.regenesis.event.PlayerEventHandler.LIMBO;

public class MobEventHandler {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        if (!(event.getEntity() instanceof Mob mob)) return;

        if (event.getEntity() instanceof Player) return;

        if (mob.level().dimension().location().equals(LIMBO) && !ForgeRegistries.ENTITY_TYPES.getKey(mob.getType()).getNamespace().equals("distantfriends")) {
            mob.discard();
        }
    }
}
