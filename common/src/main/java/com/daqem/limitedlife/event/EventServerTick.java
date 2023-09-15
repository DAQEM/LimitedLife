package com.daqem.limitedlife.event;

import com.daqem.limitedlife.LimitedLife;
import com.daqem.limitedlife.handlers.TitleHandler;
import com.daqem.limitedlife.player.LimitedLifePlayer;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EventServerTick {

    private static int tickCount = 0;
    private static int secondsCount = 0;

    public static UUID BOOGEYMAN = null;

    public static void registerEvent() {
        TickEvent.ServerLevelTick.SERVER_POST.register(EventServerTick::onTick);
    }

    private static void onTick(MinecraftServer minecraftServer) {
        tickCount++;
        if (tickCount % 20 == 0) {

            minecraftServer.getPlayerList().getPlayers().forEach(player -> {
                if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                    limitedLifePlayer.removeSecondToLive();
                    limitedLifePlayer.sendTimeLeftMessage();
                    limitedLifePlayer.setNameColor();
                }
            });

            secondsCount++;
            if (LimitedLife.CONFIG.enableBoogeyman && canStartBoogeymanElection(minecraftServer)) {
                int secondsPerBoogeymanElection = LimitedLife.CONFIG.secondsPerBoogeymanElection;
                if (secondsCount % secondsPerBoogeymanElection == secondsPerBoogeymanElection - 62) {
                    oneMinuteTillBoogeyman(minecraftServer);
                }
                if (secondsCount % secondsPerBoogeymanElection == secondsPerBoogeymanElection - 12) {
                    aboutToElectBoogeyman(minecraftServer);
                }
                if (secondsCount % secondsPerBoogeymanElection == secondsPerBoogeymanElection - 5) {
                    secondsTillBoogeyman(minecraftServer, 3);
                }
                if (secondsCount % secondsPerBoogeymanElection == secondsPerBoogeymanElection - 4) {
                    secondsTillBoogeyman(minecraftServer, 2);
                }
                if (secondsCount % secondsPerBoogeymanElection == secondsPerBoogeymanElection - 3) {
                    secondsTillBoogeyman(minecraftServer, 1);
                }
                if (secondsCount % secondsPerBoogeymanElection == secondsPerBoogeymanElection - 2) {
                    youAreTitle(minecraftServer);
                }
                if (secondsCount % secondsPerBoogeymanElection == 0) {
                    electBoogeyman(minecraftServer);
                }
            }
        }
    }

    private static boolean canStartBoogeymanElection(MinecraftServer minecraftServer) {
        return minecraftServer.getPlayerList().getPlayers()
                .stream()
                .filter(serverPlayer -> {
                    if (serverPlayer instanceof LimitedLifePlayer limitedLifePlayer) {
                        return limitedLifePlayer.getSecondsToLive() > 0;
                    }
                    return false;
                })
                .toList().size() > 1;
    }

    private static void youAreTitle(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerList().getPlayers().forEach(player -> {
            if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                TitleHandler.clearTitle(limitedLifePlayer);
                TitleHandler.sendTitle(limitedLifePlayer, LimitedLife.translatable("you_are"), ChatFormatting.YELLOW);
            }
        });
    }

    private static void secondsTillBoogeyman(MinecraftServer minecraftServer, int seconds) {
        minecraftServer.getPlayerList().getPlayers().forEach(player -> {
            if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                TitleHandler.clearTitle(limitedLifePlayer);
                TitleHandler.sendTitle(limitedLifePlayer, LimitedLife.literal(String.valueOf(seconds)), seconds == 1 ? ChatFormatting.RED : seconds == 2 ? ChatFormatting.YELLOW : ChatFormatting.GREEN);
            }
        });
    }

    private static void aboutToElectBoogeyman(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerList().getPlayers().forEach(player -> {
            if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                limitedLifePlayer.getServerPlayer().sendSystemMessage(LimitedLife.translatable("about_to_elect_boogeyman").withStyle(ChatFormatting.RED), false);
            }
        });
    }

    private static void oneMinuteTillBoogeyman(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerList().getPlayers().forEach(player -> {
            if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                limitedLifePlayer.getServerPlayer().sendSystemMessage(LimitedLife.translatable("one_minute_till_boogeyman").withStyle(ChatFormatting.RED), false);
            }
        });
    }

    private static void electBoogeyman(MinecraftServer minecraftServer) {

        List<ServerPlayer> serverPlayers = minecraftServer.getPlayerList().getPlayers()
                .stream()
                .filter(serverPlayer -> {
                    if (serverPlayer instanceof LimitedLifePlayer limitedLifePlayer) {
                        return limitedLifePlayer.getSecondsToLive() > 0;
                    }
                    return false;
                })
                .toList();

        minecraftServer.getPlayerList().getPlayers().forEach(player -> {
            if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                if (limitedLifePlayer.isBoogeyman()) {
                    limitedLifePlayer.handleBoogeymanFail();
                }
            }
        });

        if (serverPlayers.size() <= 1) {
            BOOGEYMAN = null;
            minecraftServer.getPlayerList().getPlayers().forEach(player -> {
                if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                    if (limitedLifePlayer.getSecondsToLive() <= 0) {
                        TitleHandler.clearTitle(limitedLifePlayer);
                        TitleHandler.sendTitle(limitedLifePlayer, LimitedLife.literal("DEAD LOL"), ChatFormatting.LIGHT_PURPLE);
                    } else {
                        TitleHandler.clearTitle(limitedLifePlayer);
                        TitleHandler.sendTitle(limitedLifePlayer, LimitedLife.translatable("not_boogeyman"), ChatFormatting.GREEN);
                    }
                }
            });
        } else {
            BOOGEYMAN = serverPlayers.get(new Random().nextInt(serverPlayers.size())).getUUID();

            minecraftServer.getPlayerList().getPlayers().forEach(player -> {
                if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                    LimitedLife.LOGGER.error(limitedLifePlayer.getSecondsToLive() + " " + limitedLifePlayer.getServerPlayer().getUUID() + " " + serverPlayers.contains(limitedLifePlayer.getServerPlayer()));
                    if (limitedLifePlayer.getSecondsToLive() <= 0) {
                        TitleHandler.clearTitle(limitedLifePlayer);
                        TitleHandler.sendTitle(limitedLifePlayer, LimitedLife.literal("DEAD LOL"), ChatFormatting.LIGHT_PURPLE);
                    } else {
                        if (limitedLifePlayer.getServerPlayer().getUUID() == BOOGEYMAN) {
                            limitedLifePlayer.setBoogeyman(true);
                            TitleHandler.clearTitle(limitedLifePlayer);
                            TitleHandler.sendTitle(limitedLifePlayer, LimitedLife.translatable("boogeyman"), ChatFormatting.RED);
                        } else {
                            TitleHandler.clearTitle(limitedLifePlayer);
                            TitleHandler.sendTitle(limitedLifePlayer, LimitedLife.translatable("not_boogeyman"), ChatFormatting.GREEN);
                        }
                    }
                }
            });
        }
    }
}
