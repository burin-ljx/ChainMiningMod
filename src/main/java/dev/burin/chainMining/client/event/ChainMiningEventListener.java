package dev.burin.chainMining.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import dev.burin.chainMining.ChainMiningMod;
import dev.burin.chainMining.network.ChainMiningPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(modid = ChainMiningMod.MOD_ID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ChainMiningEventListener {
    private static boolean onPressed = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onKeyPress(InputEvent.Key event) {
        int key = event.getKey();
        int action = event.getAction();
        if (key == RegisterKeyMappingsEventListener.CHAIN_MINING.get().getKey().getValue()) {
            if (action == InputConstants.PRESS) {
                onPressed = true;
            } else if (action == InputConstants.RELEASE) {
                onPressed = false;
            }
            ChainMiningPacket packet = new ChainMiningPacket(onPressed);
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                connection.send(packet);
            }
        }
    }
}
