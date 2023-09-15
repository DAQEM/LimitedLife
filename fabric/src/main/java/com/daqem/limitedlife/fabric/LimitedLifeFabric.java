package com.daqem.limitedlife.fabric;

import com.daqem.limitedlife.LimitedLife;
import net.fabricmc.api.ModInitializer;

public class LimitedLifeFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        LimitedLife.init();
    }
}
