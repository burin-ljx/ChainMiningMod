package dev.burin.chainMining.event;

import dev.burin.chainMining.ChainMiningMod;
import dev.burin.chainMining.network.ChainMiningPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ChainMiningMod.MOD_ID)
public class RegisterPayloadEventListener {
    @SubscribeEvent
    public static void registerPayload(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
            ChainMiningPacket.TYPE,
            ChainMiningPacket.STREAM_CODEC,
            ChainMiningPacket.HANDLER
        );
    }
}
