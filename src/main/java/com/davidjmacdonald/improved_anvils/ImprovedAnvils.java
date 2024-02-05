package com.davidjmacdonald.improved_anvils;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImprovedAnvils implements ModInitializer {
    public static final String MOD_ID = "improved_anvils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello World!");
    }
}
