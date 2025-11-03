package dev.burin.chainMining;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ChainMiningMod.MOD_ID)
public class ChainMiningMod {
    public static final String MOD_ID = "chain_mining";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ChainMiningMod() {
        LOGGER.info("Mod loading");
    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
