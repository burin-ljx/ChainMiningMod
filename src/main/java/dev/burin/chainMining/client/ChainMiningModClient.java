package dev.burin.chainMining.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = ChainMiningModClient.MOD_ID, dist = Dist.CLIENT)
public class ChainMiningModClient {
    public static final String MOD_ID = "chain_mining";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ChainMiningModClient() {
        LOGGER.info("Mod client loading");
    }
}
