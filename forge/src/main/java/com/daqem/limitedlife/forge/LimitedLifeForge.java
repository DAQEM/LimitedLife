package com.daqem.limitedlife.forge;

import com.daqem.limitedlife.LimitedLife;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LimitedLife.MOD_ID)
public class LimitedLifeForge {
    public LimitedLifeForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(LimitedLife.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        DistExecutor.safeRunForDist(
                () -> SideProxyForge.Client::new,
                () -> SideProxyForge.Server::new
        );
    }
}
