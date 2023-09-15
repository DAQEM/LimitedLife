package com.daqem.limitedlife.player;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;

public interface LimitedLifePlayer {

    int getSecondsToLive();

    void setSecondsToLive(int seconds);

    void removeSecondToLive();

    void addSecondsToLive(int seconds);

    void removeSecondsToLive(int seconds);

    ChatFormatting getNameColor();

    void setNameColor();

    boolean canAttack(LimitedLifePlayer target);

    void handleTimeUp();

    void handleBoogeymanFail();

    void setBoogeyman(boolean isBoogeyman);

    boolean isBoogeyman();

    String getTimeLeftAsString();

    ServerPlayer getServerPlayer();

    void sendTimeLeftMessage();
}
