package com.daqem.limitedlife.event;

import com.daqem.limitedlife.player.LimitedLifePlayer;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;

public class EventServerStop {

    public static void registerEvent() {
        LifecycleEvent.SERVER_STOPPING.register(EventServerStop::onServerStop);
    }

    private static void onServerStop(MinecraftServer minecraftServer) {
        minecraftServer.getPlayerList().getPlayers().forEach(player -> {
            if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                limitedLifePlayer.setBoogeyman(false);
            }
        });
    }
}
