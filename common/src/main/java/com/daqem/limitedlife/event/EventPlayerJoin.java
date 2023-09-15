package com.daqem.limitedlife.event;

import com.daqem.limitedlife.LimitedLife;
import com.daqem.limitedlife.player.LimitedLifePlayer;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public class EventPlayerJoin {

    public static void registerEvent() {
        PlayerEvent.PLAYER_JOIN.register(EventPlayerJoin::onPlayerJoin);
    }

    private static void onPlayerJoin(ServerPlayer serverPlayer) {
        if (serverPlayer instanceof LimitedLifePlayer limitedLifePlayer) {
            LimitedLife.LOGGER.info("Player joined: " + limitedLifePlayer.getServerPlayer().getUUID());
            LimitedLife.LOGGER.info("Boogeyman: " + EventServerTick.BOOGEYMAN);
            LimitedLife.LOGGER.info("Is boogeyman: " + limitedLifePlayer.isBoogeyman());
            if (limitedLifePlayer.isBoogeyman() && EventServerTick.BOOGEYMAN != limitedLifePlayer.getServerPlayer().getUUID()) {
                limitedLifePlayer.handleBoogeymanFail();
            }
            if (limitedLifePlayer.getSecondsToLive() <= 0) {
                limitedLifePlayer.handleTimeUp();
            }
        }
    }
}
