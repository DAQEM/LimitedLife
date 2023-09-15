package com.daqem.limitedlife.handlers;

import com.daqem.limitedlife.LimitedLife;
import com.daqem.limitedlife.player.LimitedLifePlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TitleHandler {

    public static void sendTitle(LimitedLifePlayer player, Component message, ChatFormatting color) {
        if (LimitedLife.CONFIG.enableTitles) {
            ServerPlayer serverPlayer = player.getServerPlayer();
            if (serverPlayer != null) {
                if (serverPlayer.getServer() != null) {
                    serverPlayer.getServer().getCommands().performPrefixedCommand(
                            serverPlayer.getServer().createCommandSourceStack(),
                            "/title " + serverPlayer.getDisplayName().getString() + " title {\"text\":\"" + message.getString() + "\", \"color\":\"" + color.getName() + "\"}");
                }
            }
        }
    }

    public static void clearTitle(LimitedLifePlayer player) {
        if (LimitedLife.CONFIG.enableTitles) {
            ServerPlayer serverPlayer = player.getServerPlayer();
            if (serverPlayer != null) {
                if (serverPlayer.getServer() != null) {
                    serverPlayer.getServer().getCommands().performPrefixedCommand(
                            serverPlayer.getServer().createCommandSourceStack(),
                            "/title " + serverPlayer.getDisplayName().getString() + " clear");
                }
            }
        }
    }
}
