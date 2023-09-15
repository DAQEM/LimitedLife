package com.daqem.limitedlife.command;

import com.daqem.limitedlife.player.LimitedLifePlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class LimitedLifeCommand {

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands
                .literal("limitedlife")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(0))
                .then(Commands.literal("debug")
                        .then(Commands.argument("target_player", EntityArgument.player())
                                .executes(context -> debug(context.getSource(), EntityArgument.getPlayer(context, "target_player")))
                        )
                        .executes(context -> debug(context.getSource(), context.getSource().getPlayer()))
                )
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target_player", EntityArgument.player())
                                .then(Commands.argument("seconds", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(context -> setSeconds(context.getSource(), EntityArgument.getPlayer(context, "target_player"), IntegerArgumentType.getInteger(context, "seconds")))
                                )
                        )
                )
        );
    }

    private static int setSeconds(CommandSourceStack source, ServerPlayer targetPlayer, int seconds) {
        if (targetPlayer instanceof LimitedLifePlayer limitedLifePlayer) {
            limitedLifePlayer.setSecondsToLive(seconds);
            source.sendSuccess(limitedLifePlayer.getServerPlayer().getDisplayName().copy().withStyle(ChatFormatting.DARK_GREEN).append(" now has " + limitedLifePlayer.getTimeLeftAsString() + " time to live.").withStyle(ChatFormatting.GREEN), false);
        }
        return 1;
    }

    private static int debug(CommandSourceStack source, ServerPlayer target) {
        if (target instanceof LimitedLifePlayer limitedLifePlayer) {
            source.sendSuccess(limitedLifePlayer.getServerPlayer().getDisplayName().copy().withStyle(ChatFormatting.DARK_GREEN).append(" has " + limitedLifePlayer.getTimeLeftAsString() + " time left to live.").withStyle(ChatFormatting.GREEN), false);
        }
        return 1;
    }
}
