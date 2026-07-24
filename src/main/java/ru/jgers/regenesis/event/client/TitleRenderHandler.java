package ru.jgers.regenesis.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.jgers.regenesis.RegenesisMod;
import ru.jgers.regenesis.capability.MarkOfCainCapabilities;;
import ru.jgers.regenesis.event.PlayerEventHandler;

@Mod.EventBusSubscriber(modid = RegenesisMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TitleRenderHandler {

    @SubscribeEvent
    public static void renderTitle(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type() || player == null) return;

        player.getCapability(MarkOfCainCapabilities.CAPABILITY).ifPresent(data -> {
            if (!player.getPersistentData().getBoolean(PlayerEventHandler.LIMBO_SHOW_TITLE)) return;

            GuiGraphics graphics = event.getGuiGraphics();
            Font font = mc.font;

            int year = data.getYearCount();

            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();

            int x = screenWidth / 2;
            int y = screenHeight / 2;
            float scale = 2.5f;

            Component milestone = getMilestone(year);
            int xMilestone = font.width(milestone) / 2;

            PoseStack poseStack = graphics.pose();

            poseStack.pushPose();
            poseStack.translate(x - font.width(String.valueOf(year)) * scale / 2, y - 3 * font.lineHeight * scale, 0);
            poseStack.scale(scale, scale, 1);
            graphics.drawString(font, String.valueOf(year), 0, 0, 0xFF2400, true);

            poseStack.popPose();

            scale = 2;

            poseStack.pushPose();
            poseStack.translate(x - font.width(Component.translatable("regenesis.limbo.title")) * scale / 2, y - 2 * font.lineHeight * scale, 0);
            poseStack.scale(scale, scale, 1);
            graphics.drawString(font, Component.translatable("regenesis.limbo.title"), 0, 0, 0xFFFFFF, true);

            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(x - xMilestone, y - font.lineHeight, 0);
            graphics.drawString(font, milestone, 0, 0, 0x999999, true);
            poseStack.popPose();
        });
    }

    public static Component getMilestone(int total_year) {
        if (total_year >= 17840) {
            return Component.translatable("regenesis.milestones.title.chernobyl");
        }
        if (total_year >= 1815) {
            return Component.translatable("regenesis.milestones.title.data");
        }
        if (total_year >= 1340) {
            return Component.translatable("regenesis.milestones.title.polar");
        }
        if (total_year >= 1023) {
            return Component.translatable("regenesis.milestones.title.humanity");
        }
        if (total_year >= 840) {
            return Component.translatable("regenesis.milestones.title.pillars");
        }
        if (total_year >= 440) {
            return Component.translatable("regenesis.milestones.title.plastic");
        }
        if (total_year >= 150) {
            return Component.translatable("regenesis.milestones.title.voyager");
        }
        if (total_year >= 60) {
            return Component.translatable("regenesis.milestones.title.pluto");
        }
        if (total_year >= 30) {
            return Component.translatable("regenesis.milestones.title.atmosphere");
        }

        return Component.empty();
    }
}
