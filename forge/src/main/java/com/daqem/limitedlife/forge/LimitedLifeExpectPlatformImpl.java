package com.daqem.limitedlife.forge;

import com.daqem.limitedlife.LimitedLifeExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class LimitedLifeExpectPlatformImpl {
    /**
     * This is our actual method to {@link LimitedLifeExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
