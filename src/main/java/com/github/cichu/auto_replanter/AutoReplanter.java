package com.github.cichu.auto_replanter;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoReplanter implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("auto_replanter");

    @Override
    public void onInitialize() {
        LOGGER.info("Auto replanter mod initialized");
    }
}
