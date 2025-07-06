package dev.burin.template;

import com.tterrag.registrate.Registrate;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TemplateMod.MOD_ID)
public class TemplateMod {
    public static final String MOD_ID = "template";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);

    public TemplateMod() {
        REGISTRATE
            .block("template_block", Block::new)
            .simpleItem()
            .register();

        LOGGER.info("Mod loading");
    }
}
