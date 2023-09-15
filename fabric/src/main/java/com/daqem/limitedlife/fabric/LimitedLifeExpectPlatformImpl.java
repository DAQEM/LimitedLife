package com.daqem.limitedlife.fabric;

import com.daqem.limitedlife.LimitedLifeExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class LimitedLifeExpectPlatformImpl {
    /**
     * This is our actual method to {@link LimitedLifeExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
