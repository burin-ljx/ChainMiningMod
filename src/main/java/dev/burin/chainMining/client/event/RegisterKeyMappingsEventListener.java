package dev.burin.chainMining.client.event;

import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.burin.chainMining.ChainMiningMod;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ChainMiningMod.MOD_ID, value = Dist.CLIENT)
public class RegisterKeyMappingsEventListener {
    public static final Lazy<KeyMapping> CHAIN_MINING = register("chain_mining", KeyConflictContext.IN_GAME, Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT);

    public static Lazy<KeyMapping> register(String name, KeyConflictContext context, Type type, int keyCode) {
        return Lazy.of(() -> new KeyMapping("key.chain_mining." + name, context, type, keyCode, "key.categories.chain_mining"));
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(CHAIN_MINING.get());
    }
}
